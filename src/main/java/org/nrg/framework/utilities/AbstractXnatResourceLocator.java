package org.nrg.framework.utilities;

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
    public List<Resource> getResources() throws IOException {
        final List<Resource> resources = new ArrayList<>();
        for (final String pattern : _patterns) {
            resources.addAll(Arrays.asList(_resolver.getResources(pattern)));
        }
        return resources;
    }

    private final PathMatchingResourcePatternResolver _resolver = new PathMatchingResourcePatternResolver();
    private final List<String>                        _patterns = new ArrayList<>();
}
