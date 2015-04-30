/**
 * Reflection
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 11/8/11 by rherri01
 */
package org.nrg.framework.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Reflection {
    public static Map<String,List<Class<?>>> CACHED_CLASSES_BY_PACKAGE=Maps.newHashMap();
    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException    Signals that an I/O exception has occurred.
     */
    public static List<Class<?>> getClassesForPackage(String packageName) throws ClassNotFoundException, IOException {
    	List<Class<?>> classes=CACHED_CLASSES_BY_PACKAGE.get(packageName);
    	if(classes==null){
    		_log.info("Identifying classes for "+packageName);
	        ClassLoader loader = Thread.currentThread().getContextClassLoader();
	        assert loader != null;
	
	        Enumeration<URL> resources = loader.getResources(packageName.replace('.', '/'));
	        List<File> directories = new ArrayList<File>();
	        List<URL> jarFiles = new ArrayList<URL>();
	        classes = Lists.newArrayList();
	
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
    		CACHED_CLASSES_BY_PACKAGE.put(packageName, classes);
    	}

        return classes;
    }

    /**
     * Find classes in jar file.
     *
     * @param jarFile     the jar file
     * @param packageName the package name
     * @return the collection of classes in the jar file.
     * @throws IOException    Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException the class not found exception
     */
    public static Collection<? extends Class<?>> findClassesInJarFile(URL jarFile, String packageName) throws IOException, ClassNotFoundException {
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
     * Recursive method used to find all classes in a given directory and subdirectories.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException    Signals that an I/O exception has occurred.
     */
    public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException, IOException {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();

        assert files != null;
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

    public static Properties getPropertiesForClass(final Class<?> parent) {
        final String bundle = "/" + parent.getName().replace(".", "/") + ".properties";
        Properties properties = new Properties();
        final InputStream inputStream = parent.getResourceAsStream(bundle);
        try {
            try {
                properties.load(inputStream);
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            properties = null;
        }
        return properties;
    }

    /**
     * This method is to compensate for the fact that the default Java getConstructor() method doesn't match
     * constructors that have parameters that are superclasses of one of the submitted parameter classes. For example,
     * if an object submitted for the constructor is an <b>ArrayList</b>, <b>getConstructor()</b> will not match that to
     * a constructor that takes a <b>List</b>. This method checks for that downcast compatibility via the
     * <b>isAssignableFrom()</b> method.
     *
     * <b>Note:</b> This version of the method returns only publicly accessible constructors. If you need to find
     * protected or private constructors, call {@link #getConstructorForParameters(Class, int, Class[])}.
     *
     * @param target            The class that you want to inspect for compatible constructors.
     * @param parameterTypes    The parameter types you want to submit to the constructor.
     * @return A matching constructor, if any, <b>null</b> otherwise.
     */
    public static <T> Constructor<T> getConstructorForParameters(final Class<T> target, final Class<?>... parameterTypes) {
        return getConstructorForParameters(target, Modifier.PUBLIC, parameterTypes);
    }

    /**
     * This method is to compensate for the fact that the default Java getConstructor() method doesn't match
     * constructors that have parameters that are superclasses of one of the submitted parameter classes. For example,
     * if an object submitted for the constructor is an <b>ArrayList</b>, <b>getConstructor()</b> will not match that to
     * a constructor that takes a <b>List</b>. This method checks for that downcast compatibility via the
     * <b>isAssignableFrom()</b> method.
     *
     * <b>Note:</b> This version of the method can return public, protected, and private constructors. If you want to
     * find only publicly accessible constructors, you can call {@link #getConstructorForParameters(Class, Class[])}.
     * To specify what access level of constructor you want to retrieve, you should specify the appropriate value from
     * the {@link Modifier} class:
     *
     * <ul>
     *     <li>{@link Modifier#PUBLIC} indicates only publicly accessible constructors</li>
     *     <li>{@link Modifier#PROTECTED} indicates publicly accessible or protected constructors</li>
     *     <li>{@link Modifier#PRIVATE} indicates public, protected, or private constructors</li>
     * </ul>
     *
     * @param target            The class that you want to inspect for compatible constructors.
     * @param requestedAccess   Indicates the desired access level.
     * @param parameterTypes    The parameter types you want to submit to the constructor.
     * @return A matching constructor, if any, <b>null</b> otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructorForParameters(final Class<T> target, final int requestedAccess, final Class<?>... parameterTypes) {
        // If there are no parameters specified, return the default constructor.
        if (parameterTypes == null || parameterTypes.length == 0) {
            try {
                Constructor<T> constructor = target.getConstructor();
                // If the default constructor isn't accessible
                return isAccessible(requestedAccess, constructor.getModifiers()) ? constructor : null;
            } catch (NoSuchMethodException e) {
                // If there was no default constructor, then return null.
                return null;
            }
        }
        // Try to return constructor that's an exact match for the parameter types.
        // If that doesn't exist, ignore the exception. We have more sophisticated
        // things that we can try to match superclasses, interfaces, etc.
        try {
            return target.getConstructor(parameterTypes);
        } catch (NoSuchMethodException ignored) {
            //
        }
        final Constructor<T>[] constructors = (Constructor<T>[]) target.getConstructors();
        // Honestly I don't even think this can happen, but just in case...
        if (constructors == null || constructors.length == 0) {
            return null;
        }
        // If we got through all the bits where there are no constructors or parameters, we can try to match the
        // submitted parameter types.
        for (final Constructor<T> candidate : constructors) {
            // If it's not accessible, don't even process.
            if (!isAccessible(requestedAccess, candidate.getModifiers())) {
                continue;
            }
            // Get all of the parameter types for the current candidate constructor.
            final Class<?>[] candidateParameterTypes = candidate.getParameterTypes();
            // If the number of parameter types don't match, then this isn't a match.
            // TODO: Check how this works with vararg constructors. It should be fine (should be Object[]), but still.
            if (candidateParameterTypes.length != parameterTypes.length) {
                continue;
            }
            // Let's assume the best.
            boolean match = true;
            // Now go through all of the parameters.
            for (int index = 0; index < candidateParameterTypes.length; index++) {
                // If we can't assign this submitted parameter to the candidate parameter...
                if (!(candidateParameterTypes[index].isAssignableFrom(parameterTypes[index]))) {
                    // We don't have a match.
                    match = false;
                    break;
                }
            }
            // We made it through all of the candidates... If they all matched, this constructor matches so return it.
            return match ? candidate : null;
        }
        // If we made it through without returning, none of the constructor candidates match.
        return null;
    }

    private static boolean isAccessible(final int requestedAccess, final int modifiers) {
        // If they want private, they can have anything, so just say yes.
        if (requestedAccess == Modifier.PRIVATE) {
            return true;
        }

        // Find out if the target modifier is private or protected.
        boolean isPrivate = Modifier.isPrivate(modifiers);
        boolean isProtected = Modifier.isPrivate(modifiers);

        // If requested access is protected, then return true if the modifier is NOT private, otherwise it's public so
        // return true if the modifier is NOT private or protected.
        return requestedAccess == Modifier.PROTECTED ? !isPrivate : !(isPrivate || isProtected);
    }
    
    private static final Log _log = LogFactory.getLog(Reflection.class);
}
