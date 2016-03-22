package org.nrg.framework.processors;

import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import org.nrg.framework.annotations.XnatPlugin;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import java.util.Properties;

/**
 * Processes the {@link XnatPlugin} annotation and generates the plugin's properties file that used by XNAT for plugin
 * discovery. The basis for this code was adapted from <a href="http://kohsuke.org">Kohsuke Kawaguchi's</a> code for the
 * <a href="http://metainf-services.kohsuke.org">META-INF/services generator</a>. This does the same basic thing but
 * generates a file named "META-INF/xnat/id-plugin.properties", where the <i>id</i> is taken from the value set for the
 * {@link XnatPlugin#value()} attribute on the annotation.
 */
@MetaInfServices(Processor.class)
@SupportedAnnotationTypes("org.nrg.framework.annotations.XnatPlugin")
public class XnatPluginAnnotationProcessor extends NrgAbstractAnnotationProcessor<XnatPlugin> {

    @Override
    protected Properties processAnnotation(final TypeElement element, final XnatPlugin plugin) {
        final Properties properties = new Properties();
        properties.setProperty("id", plugin.value());
        if (StringUtils.isNotBlank(plugin.namespace())) {
            properties.setProperty("namespace", plugin.namespace());
        }
        properties.setProperty("name", plugin.name());
        if (StringUtils.isNotBlank(plugin.description())) {
            properties.setProperty("description", plugin.description());
        }
        if (StringUtils.isNotBlank(plugin.beanName())) {
            properties.setProperty("beanName", plugin.beanName());
        }
        final String config = getTypeElementValue(element, "config");
        if (StringUtils.isNotEmpty(config)) {
            properties.setProperty("config", config);
        }
        return properties;
    }

    @Override
    protected String getPropertiesName(final XnatPlugin plugin) {
        final String namespace = plugin.namespace();
        final String pluginId = plugin.value();
        if (StringUtils.isBlank(namespace)) {
            return String.format("META-INF/xnat/%s-plugin.properties", pluginId);
        }
        return String.format("META-INF/xnat/%s/%s-plugin.properties", namespace, pluginId);
    }
}
