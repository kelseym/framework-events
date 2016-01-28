package org.nrg.framework.processors;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XnatModule;
import org.kohsuke.MetaInfServices;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * Processes the {@link XnatModule} annotation and generates the module's properties file that used by XNAT for module
 * discovery. The basis for this code was adapted from <a href="http://kohsuke.org">Kohsuke Kawaguchi's</a> code for the
 * <a href="http://metainf-services.kohsuke.org">META-INF/services generator</a>. This does the same basic thing but
 * generates a file named "META-INF/xnat/id-module.properties", where the <i>id</i> is taken from the value set for the
 * {@link XnatModule#value()} attribute on the annotation.
 */
@MetaInfServices(Processor.class)
@SupportedAnnotationTypes("org.nrg.framework.annotations.XnatModule")
public class XnatModuleAnnotationProcessor extends AbstractProcessor {
    @Override
    public SourceVersion getSupportedSourceVersion() {
        try {
            return SourceVersion.valueOf("RELEASE_8");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            return SourceVersion.valueOf("RELEASE_7");
        } catch (IllegalArgumentException ignored) {
        }
        return SourceVersion.RELEASE_6;
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return false;
        }

        final Map<String, Map<String, XnatModuleBean>> modules = new HashMap<>();

        // discover services from the current compilation sources
        for (final Element element : roundEnv.getElementsAnnotatedWith(XnatModule.class)) {
            final XnatModule module = element.getAnnotation(XnatModule.class);
            if (module == null) {
                continue;
            }
            if (!element.getKind().isClass() && !element.getKind().isInterface()) {
                continue;
            }
            try {
                final String namespace = module.namespace();
                if (!modules.containsKey(namespace)) {
                    modules.put(namespace, new HashMap<String, XnatModuleBean>());
                }
                final XnatModuleBean bean = new XnatModuleBean(getPropertiesFromAnnotation((TypeElement) element, module));
                modules.get(namespace).put(bean.getId(), bean);
            } catch (ClassNotFoundException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Couldn't find a class referenced by the XnatModule annotation for module ID " + module.value() + " on the class " + element.getKind().name());
            } catch (InvalidClassException e) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "There was an error in the configuration of the XnatModule annotation for module ID " + module.value() + " on the class " + element.getKind().name());
            }
        }

        final Filer filer = processingEnv.getFiler();

        for (final String namespace : modules.keySet()) {
            final Map<String, XnatModuleBean> namespacedModules = modules.get(namespace);
            for (final Map.Entry<String, XnatModuleBean> module : namespacedModules.entrySet()) {
                final String         moduleId = module.getKey();
                final XnatModuleBean bean     = module.getValue();
                final String         path     = getModulePropertiesName(namespace, moduleId);

                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Writing resource to " + path);

                try (final PrintWriter writer = new PrintWriter(new OutputStreamWriter(filer.createResource(StandardLocation.CLASS_OUTPUT, "", path).openOutputStream(), "UTF-8"))) {
                    writer.println("id=" + moduleId);
                    if (StringUtils.isNotBlank(bean.getNamespace())) {
                        writer.println("namespace=" + bean.getNamespace());
                    }
                    writer.println("name=" + bean.getName());
                    if (StringUtils.isNotBlank(bean.getDescription())) {
                        writer.println("description=" + bean.getDescription());
                    }
                    if (StringUtils.isNotBlank(bean.getBeanName())) {
                        writer.println("beanName=" + bean.getBeanName());
                    }
                    if (bean.getConfig() != null) {
                        writer.println("config=" + bean.getConfig());
                    }
                    if (bean.getTargetType() != null) {
                        writer.println("targetType=" + bean.getTargetType());
                    }
                } catch (IOException x) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to write service definition files: " + x);
                }
            }
        }

        return false;
    }

    private Properties getPropertiesFromAnnotation(final TypeElement element, final XnatModule annotation) throws InvalidClassException {
        final Properties properties = new Properties();
        properties.setProperty("id", annotation.value());
        properties.setProperty("name", annotation.name());

        final String      namespace     = annotation.namespace();
        final String      description   = annotation.description();
        final String      beanName      = annotation.beanName();

        if (StringUtils.isNotBlank(namespace)) {
            properties.setProperty("namespace", namespace);
        }
        if (StringUtils.isNotBlank(description)) {
            properties.setProperty("description", description);
        }
        if (StringUtils.isNotBlank(beanName)) {
            properties.setProperty("beanName", beanName);
        }

        final AnnotationMirror mirror = getAnnotationMirror(element, XnatModule.class.getName());
        final TypeElement configElement = getAnnotationValueAsType(mirror, "config");
        if (configElement != null) {
            properties.setProperty("config", configElement.toString());
        }
        final TypeElement targetTypeElement = getAnnotationValueAsType(mirror, "targetTypeElement");
        if (targetTypeElement != null) {
            properties.setProperty("targetType", targetTypeElement.toString());
        }
        return properties;
    }

    private static String getModulePropertiesName(final String namespace, final String moduleId) {
        if (StringUtils.isBlank(namespace)) {
            return String.format("META-INF/xnat/%s-module.properties", moduleId);
        }
        return String.format("META-INF/xnat/%s/%s-module.properties", namespace, moduleId);
    }

    private static AnnotationMirror getAnnotationMirror(TypeElement typeElement, String className) {
        for(AnnotationMirror m : typeElement.getAnnotationMirrors()) {
            if(m.getAnnotationType().toString().equals(className)) {
                return m;
            }
        }
        return null;
    }

    private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for(final Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationMirror.getElementValues().entrySet() ) {
            if(entry.getKey().getSimpleName().toString().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private TypeElement getAnnotationValueAsType(AnnotationMirror annotationMirror, String key) throws InvalidClassException {
        AnnotationValue annotationValue = getAnnotationValue(annotationMirror, key);
        if(annotationValue == null) {
            return null;
        }
        if (annotationValue.getValue() instanceof List) {
            final List list = (List) annotationValue.getValue();
            if (list.size() == 0) {
                return null;
            }
            if (list.size() > 1) {
                throw new InvalidClassException("You should only specify a single class for the " + key + " attribute for XnatModule.");
            }
            final TypeMirror typeMirror = (TypeMirror) ((AnnotationValue) list.get(0)).getValue();
            if (typeMirror == null) {
                return null;
            }
            return (TypeElement) processingEnv.getTypeUtils().asElement(typeMirror);
        }
        return null;
    }

}
