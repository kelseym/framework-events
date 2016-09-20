/*
 * org.nrg.framework.processors.XnatMixInAnnotationProcessor
 * XNAT http://www.xnat.org
 * Copyright (c) 2016, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.processors;

import org.kohsuke.MetaInfServices;
import org.nrg.framework.annotations.XnatMixIn;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@inheritDoc}
 */
@MetaInfServices(Processor.class)
@SupportedAnnotationTypes("org.nrg.framework.annotations.XnatMixIn")
public class XnatMixInAnnotationProcessor extends NrgAbstractAnnotationProcessor<XnatMixIn> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Map<String, String> processAnnotation(final TypeElement element, final XnatMixIn mixIn) {
        final Map<String, String> properties = new LinkedHashMap<>();
        properties.put(element.getQualifiedName().toString(), getTypeElementValue(element, "value"));
        return properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getPropertiesName(final TypeElement element, final XnatMixIn mixIn) {
        return String.format("META-INF/xnat/serializers/%s-mixin.properties", element.getQualifiedName().toString());
    }
}
