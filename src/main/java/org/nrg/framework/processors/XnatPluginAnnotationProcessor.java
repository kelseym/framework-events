package org.nrg.framework.processors;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import org.nrg.framework.annotations.XnatPlugin;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import java.util.Properties;

/**
 * Processes the {@link XnatPlugin} annotation and generates the module's properties file that used by XNAT for module
 * discovery. The basis for this code was adapted from <a href="http://kohsuke.org">Kohsuke Kawaguchi's</a> code for the
 * <a href="http://metainf-services.kohsuke.org">META-INF/services generator</a>. This does the same basic thing but
 * generates a file named "META-INF/xnat/id-module.properties", where the <i>id</i> is taken from the value set for the
 * {@link XnatPlugin#value()} attribute on the annotation.
 */
@MetaInfServices(Processor.class)
@SupportedAnnotationTypes("org.nrg.framework.annotations.XnatPlugin")
public class XnatPluginAnnotationProcessor extends NrgAbstractAnnotationProcessor<XnatPlugin> {

    @Override
    protected Properties processAnnotation(final TypeElement element, final XnatPlugin module) {
        final Properties properties = new Properties();
        properties.setProperty("id", module.value());
        if (StringUtils.isNotBlank(module.namespace())) {
            properties.setProperty("namespace", module.namespace());
        }
        properties.setProperty("name", module.name());
        if (StringUtils.isNotBlank(module.description())) {
            properties.setProperty("description", module.description());
        }
        if (StringUtils.isNotBlank(module.beanName())) {
            properties.setProperty("beanName", module.beanName());
        }
        final String config = getTypeElementValue(element, "config");
        if (StringUtils.isNotEmpty(config)) {
            properties.setProperty("config", config);
        }
        return properties;
    }

    @Override
    protected String getPropertiesName(final XnatPlugin module) {
        final String namespace = module.namespace();
        final String moduleId = module.value();
        if (StringUtils.isBlank(namespace)) {
            return String.format("META-INF/xnat/%s-module.properties", moduleId);
        }
        return String.format("META-INF/xnat/%s/%s-module.properties", namespace, moduleId);
    }
}