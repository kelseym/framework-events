/*
 * org.nrg.framework.utilities.AbstractXnatResourceLocator
 * XNAT http://www.xnat.org
 * Copyright (c) 2016, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public abstract class AbstractXnatResourceLocator implements XnatResourceLocator {
    protected AbstractXnatResourceLocator() {
    }

    protected AbstractXnatResourceLocator(final String defaultPattern) {
        _patterns.add(defaultPattern);
    }

    protected AbstractXnatResourceLocator(final List<String> patterns) {
        _patterns.addAll(patterns);
    }

    public List<String> getPatterns() {
        return new ArrayList<>(_patterns);
    }

    public void setPatterns(final List<String> patterns) {
        _patterns.clear();
        _patterns.addAll(patterns);
    }

    public void addPattern(final String pattern) {
        _patterns.add(pattern);
    }

    public void removePattern(final String pattern) {
        _patterns.remove(pattern);
    }

    @Override
    public Resource getResource() throws IOException {
        if (_patterns.size() == 0) {
            if (_log.isDebugEnabled()) {
                _log.debug("Tried to retrieve resource, but no patterns are specified.");
            }
            return null;
        } else if (_patterns.size() > 1) {
            if (_log.isDebugEnabled()) {
                _log.debug("Resource requested, but multiple patterns are specified. Only retrieving resource matching first pattern: " + _patterns.get(0));
            }
        }
        return _resolver.getResource(_patterns.get(0));
    }

    @Override
    public List<Resource> getResources() throws IOException {
        final List<Resource> resources = new ArrayList<>();
        for (final String pattern : _patterns) {
            final Resource[] retrieved = _resolver.getResources(pattern);
            if (_log.isDebugEnabled()) {
                _log.debug("Retrieved resources with pattern {}, found {} results.", pattern, retrieved.length);
            }
            resources.addAll(Arrays.asList(retrieved));
        }
        return resources;
    }

    private static final Logger _log = LoggerFactory.getLogger(AbstractXnatResourceLocator.class);

    private final PathMatchingResourcePatternResolver _resolver = new PathMatchingResourcePatternResolver();
    private final List<String>                        _patterns = new ArrayList<>();
}
