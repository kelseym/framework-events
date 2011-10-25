/**
 * JaxbBasedMarshallerCacheService
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 * 
 * Released under the Simplified BSD License
 * Created on Oct 21, 2011
 */
package org.nrg.framework.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nrg.framework.services.MarshallerCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.MarshallingFailureException;
import org.springframework.oxm.UncategorizedMappingException;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.ValidationFailureException;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.util.xml.StaxUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

// TODO: Auto-generated Javadoc
/**
 * The Class JaxbBasedMarshallerCacheService.
 */
@Service
public final class JaxbBasedMarshallerCacheService implements MarshallerCacheService {
    /**
     * Gets the list of packages that can be searched for classes that support
     * submitted XML root element values.
     * 
     * @return An array
     */
    @Override
    public List<String> getMarshalablePackages() {
        return _packages;
    }

    /**
     * Sets the list of packages that can be searched for classes that support
     * submitted XML root element values.
     * 
     * @param packages
     *            The list of package names that can be searched.
     */
    @Override
    public void setMarshalablePackages(List<String> packages) {
        _packages = packages;
    }

    /**
     * Indicates whether the requested class is supported for marshalling
     * operations.
     * 
     * @param clazz
     *            The class to test for marshalling support.
     * @return <b>true</b> if the class is supported for marshalling,
     *         <b>false</b> if not.
     */
    @Override
    public boolean supports(Class<?> clazz) {
        if (_log.isDebugEnabled()) {
            _log.debug("Asking if " + clazz.getName() + " is supported");
        }
        return clazz.isAnnotationPresent(XmlRootElement.class);
    }

    /**
     * Marshals the submitted object ({@link #supports(Class) if supported} to
     * a string.
     * 
     * @param object
     *            The object to be marshaled.
     * @return The resulting XML.
     */
    @Override
    public String marshal(Object object) {
        StreamResult result = new StreamResult(new ByteArrayOutputStream());
        marshal(object, result);
        return result.getOutputStream().toString();
    }

    /**
     * Marshals the object to a {@link Document} object.
     */
    @Override
    public Document marshalToDocument(Object object) {
        try {
            DOMResult result = new DOMResult(getDocument());
            marshal(object, result);
            return (Document) result.getNode();
        } catch (ParserConfigurationException exception) {
            throw new UncategorizedMappingException("An error occurred creating a Document object", exception);
        }
    }

    /**
     * Implements the base <b>marshall()</b> method. This accepts the object and
     * returns the results of the marshalling operation in the <b>result</b>
     * parameter.
     * 
     * @param object
     *            The object to be marshalled.
     * @param result
     *            The results of the marshalling operation.
     */
    @Override
    public void marshal(Object object, Result result) {
        if (_log.isDebugEnabled()) {
            _log.debug("Attempting to marshall object of type: " + object.getClass().getName());
        }

        JAXBContext context = getInstance(object.getClass());

        try {
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(object, result);
        } catch (JAXBException exception) {
            throw convertJaxbException(exception);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.oxm.Unmarshaller#unmarshal(javax.xml.transform.Source
     * )
     */
    @Override
    public Object unmarshal(Source source) throws IOException, XmlMappingException {
        String xmlElementName = null;
        String xmlSource = null;

        try {
            if (source instanceof StreamSource) {
                InputStream stream = ((StreamSource) source).getInputStream();
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = builder.parse(stream);
                xmlElementName = document.getDocumentElement().getNodeName();
                xmlSource = getStringFromDocument(document);
            } else if (source instanceof DOMSource) {
                Node node = ((DOMSource) source).getNode();
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = builder.newDocument();
                document.adoptNode(node);
                document.appendChild(node);
                xmlElementName = document.getDocumentElement().getNodeName();
                xmlSource = getStringFromDocument(document);
            } else if (StaxUtils.isStaxSource(source)) {
                XMLStreamReader streamReader = StaxUtils.getXMLStreamReader(source);
                InputStream stream = new ByteArrayInputStream(streamReader.getText().getBytes());
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document document = builder.parse(stream);
                xmlElementName = document.getDocumentElement().getNodeName();
                xmlSource = getStringFromDocument(document);
            }
        } catch (ParserConfigurationException exception) {
            throw new UncategorizedMappingException("There was a parser configuration exception.", exception);
        } catch (TransformerException exception) {
            throw new UncategorizedMappingException("There was a transformer exception.", exception);
        } catch (SAXException exception) {
            throw new UncategorizedMappingException("There was a SAX exception.", exception);
        }

        JAXBContext context = getInstance(xmlElementName);

        Unmarshaller unmarshaller;
        try {
            InputStream inputstream = new ByteArrayInputStream(xmlSource.getBytes());
            StreamSource newSource = new StreamSource(inputstream);
            unmarshaller = context.createUnmarshaller();
            return unmarshaller.unmarshal(newSource);
        } catch (JAXBException exception) {
            throw convertJaxbException(exception);
        }
    }

    /**
     * Gets the string from document.
     * 
     * @param document
     *            the document
     * @return the string from document
     * @throws TransformerException
     *             the transformer exception
     */
    private String getStringFromDocument(Document document) throws TransformerException {
        DOMSource domSource = new DOMSource(document);
        Writer writer = new StringWriter();
        Result result = new StreamResult(writer);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    /**
     * Gets an instance of the {@link JAXBContext} class. This gets the
     * <b>JAXBContext</b> appropriate for the <b>clazz</b> parameter from the
     * internal cache.
     * 
     * @param clazz
     *            The class for which the <b>JAXBContext</b> needs to be
     *            retrieved.
     * @return The appropriate <b>JAXBContext</b> object.
     */
    private static JAXBContext getInstance(final Class<?> clazz) {
        final ContextDescriptor contextDescriptor = new ContextDescriptor(clazz.getName());

        if (cache.containsKey(contextDescriptor)) {
            return cache.get(contextDescriptor);
        }

        if (!clazz.isAnnotationPresent(XmlRootElement.class)) {
            throw new RuntimeException("The class specified is not declared with the XmlRootElement or XmlType annotation.");
        }

        final JAXBContext jaxbContext = newInstance(clazz);
        cacheContext(getXmlRootElementName(clazz), contextDescriptor, jaxbContext);

        return jaxbContext;
    }

    /**
     * Gets an instance of the {@link JAXBContext} class. This gets the
     * <b>JAXBContext</b> appropriate for the <b>clazz</b> parameter from the
     * internal cache.
     * 
     * @param xmlElementName
     *            The XML element name for which the <b>JAXBContext</b> needs to
     *            be retrieved.
     * @return The appropriate <b>JAXBContext</b> object.
     */
    private JAXBContext getInstance(String xmlElementName) {
        if (contextCache.containsKey(xmlElementName)) {
            return cache.get(contextCache.get(xmlElementName));
        }

        final Class<?> clazz = findClassForElement(xmlElementName);
        final ContextDescriptor contextDescriptor = new ContextDescriptor(clazz.getName());
        final JAXBContext jaxbContext = newInstance(clazz);

        cacheContext(xmlElementName, contextDescriptor, jaxbContext);

        return jaxbContext;
    }

    /**
     * Creates a new instance of a <b>JAXBContext</b> object for the submitted
     * class.
     * 
     * @param clazz
     *            The class for which a new <b>JAXBContext</b> needs to be
     *            created.
     * @return The new <b>JAXBContext</b> object.
     */
    private static JAXBContext newInstance(final Class<?> clazz) {
        try {
            return JAXBContext.newInstance(clazz);
        } catch (JAXBException exception) {
            throw new RuntimeException("Exception occured creating JAXB unmarshaller for context=" + clazz, exception);
        }
    }

    /**
     * This performs two caching operations for later lookups. The <b>clazz</b>
     * simple name is used to cache the context descriptor. The context
     * descriptor is used to cache the {@link JAXBContext}. This allows lookups
     * by both class name (generally for unmarshalling operations where you can
     * get the XML root element name)
     * 
     * @param xmlElementName
     *            the xml element name
     * @param contextDescriptor
     *            the context descriptor
     * @param jaxbContext
     *            the jaxb context
     */
    private static void cacheContext(final String xmlElementName, final ContextDescriptor contextDescriptor, final JAXBContext jaxbContext) {
        contextCache.put(xmlElementName, contextDescriptor);
        cache.put(contextDescriptor, jaxbContext);
    }

    /**
     * Find class for element.
     * 
     * @param xmlElementName
     *            the xml element name
     * @return the class
     */
    private Class<?> findClassForElement(String xmlElementName) {
        for (String target : _packages) {
            List<Class<?>> classes;
            try {
                classes = getClassesForPackage(target);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }

            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(XmlRootElement.class)) {
                    String name = clazz.getAnnotation(XmlRootElement.class).name();
                    if (name.equals("##default")) {
                        String simpleName = clazz.getSimpleName();
                        name = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                    }
                    if (name.equals(xmlElementName)) {
                        return clazz;
                    }
                }
            }
        }

        return null;
    }

    /**
     * <b>Note:</b> Taken from ClassPathScanningCandidateComponentProvider.
     * Resolve the specified base package into a pattern specification for the
     * package search path.
     * <p>
     * The default implementation resolves placeholders against system
     * properties, and converts a "."-based package path to a "/"-based resource
     * path.
     * 
     * @param basePackage
     *            the base package as specified by the user
     * @return the pattern specification to be used for package searching
     */
    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    /**
     * Gets the xml root element name.
     * 
     * @param clazz
     *            the clazz
     * @return the xml root element name
     */
    private static String getXmlRootElementName(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(XmlRootElement.class)) {
            return null;
        }

        XmlRootElement annotation = clazz.getAnnotation(XmlRootElement.class);

        if (annotation.name().equals("##default")) {
            String simpleName = clazz.getSimpleName();
            return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
        } else {
            return annotation.name();
        }

    }

    /**
     * Convert the given <code>JAXBException</code> to a {@link TipException}
     * with an appropriate {@link TipErrorCode} value.
     * 
     * @param exception
     *            <code>JAXBException</code> that occured
     * @return the corresponding {@link TipException}
     */
    protected XmlMappingException convertJaxbException(JAXBException exception) {
        if (exception instanceof ValidationException) {
            return new ValidationFailureException("JAXB validation exception", exception);
        } else if (exception instanceof MarshalException) {
            return new MarshallingFailureException("JAXB marshalling exception", exception);
        } else if (exception instanceof UnmarshalException) {
            return new UnmarshallingFailureException("JAXB unmarshalling exception", exception);
        } else {
            return new UncategorizedMappingException("Unknown JAXB exception", exception);
        }
    }

    /**
     * Creating the JAXB Context ... javax.xml.bind.JAXBContext.newInstance() is
     * a very slow operation, you can improve your performance if you create a
     * cache of these instances, we do this by wrapping the call to newInstance
     * with the following code
     */
    private static class ContextDescriptor {

        /** The class name. */
        private final String className;

        /*
         * (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object o) {
            if (o instanceof ContextDescriptor) {
                final ContextDescriptor un = ((ContextDescriptor) o);
                return className.equals(un.className);
            }
            return false;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return (className).hashCode();
        }

        /**
         * Instantiates a new context descriptor.
         * 
         * @param className
         *            the class name
         */
        ContextDescriptor(String className) {
            this.className = className.trim();
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     * 
     * @param packageName
     *            The base package
     * @return The classes
     * @throws ClassNotFoundException
     *             the class not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static List<Class<?>> getClassesForPackage(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        assert loader != null;

        Enumeration<URL> resources = loader.getResources(packageName.replace('.', '/'));
        List<File> directories = new ArrayList<File>();
        List<URL> jarFiles = new ArrayList<URL>();
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();

            if (resource.getProtocol().equalsIgnoreCase("jar")) {
                jarFiles.add(resource);
            } else {
                directories.add(new File(URLDecoder.decode(resource.getFile(), "UTF-8")));
            }
        }

        for (URL jarFile : jarFiles) {
            classes.addAll(findClassesInJarFile(jarFile, packageName));
        }
        for (File directory : directories) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    /**
     * Find classes in jar file.
     * 
     * @param jarFile
     *            the jar file
     * @param packageName
     *            the package name
     * @return the collection<? extends class<?>>
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException
     *             the class not found exception
     */
    private static Collection<? extends Class<?>> findClassesInJarFile(URL jarFile, String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        JarURLConnection connection = (JarURLConnection) jarFile.openConnection();
        JarFile jar = connection.getJarFile();
        for (JarEntry entry : Collections.list(jar.entries())) {
            if (entry.getName().startsWith(packageName.replace('.', '/')) && entry.getName().endsWith(".class") && !entry.getName().contains("$")) {
                String className = entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     * 
     * @param directory
     *            The base directory
     * @param packageName
     *            The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     *             the class not found exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException, IOException {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();

        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                Class<?> _class;

                try {
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                } catch (ExceptionInInitializerError e) {
                    // This happens to classes which depend on Spring to inject
                    // some beans
                    // and fail if dependency is not fulfilled
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false, Thread.currentThread().getContextClassLoader());
                }

                classes.add(_class);
            }
        }

        return classes;
    }

    private Document getDocument() throws ParserConfigurationException {
        return _docBuilderFactory.newDocumentBuilder().newDocument();
    }

    private static final Log _log = LogFactory.getLog(JaxbBasedMarshallerCacheService.class);
    private static final Map<ContextDescriptor, JAXBContext> cache = new HashMap<ContextDescriptor, JAXBContext>();
    private static final Map<String, ContextDescriptor> contextCache = new HashMap<String, ContextDescriptor>();
    private DocumentBuilderFactory _docBuilderFactory = DocumentBuilderFactory.newInstance();
    
    @Autowired(required = false)
    @Qualifier("marshalablePackages")
    private List<String> _packages;
}
