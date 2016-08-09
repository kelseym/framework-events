package org.nrg.framework.utilities;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicXnatResourceLocator implements XnatResourceLocator {

    private BasicXnatResourceLocator(final String pattern) {
        _pattern = pattern;
    }

    public static Resource getResource(final String pattern) throws IOException {
        return new BasicXnatResourceLocator(pattern).getResource();
    }

    public static List<Resource> getResources(final String pattern) throws IOException {
        return new BasicXnatResourceLocator(pattern).getResources();
    }

    @Override
    public Resource getResource() throws IOException {
        return _resolver.getResource(_pattern);
    }

    @Override
    public List<Resource> getResources() throws IOException {
        return new ArrayList<>(Arrays.asList(_resolver.getResources(_pattern)));
    }

    private final PathMatchingResourcePatternResolver _resolver = new PathMatchingResourcePatternResolver();
    private final String _pattern;
}
