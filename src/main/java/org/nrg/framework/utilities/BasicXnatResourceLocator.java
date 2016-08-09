package org.nrg.framework.utilities;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicXnatResourceLocator extends AbstractXnatResourceLocator {

    private BasicXnatResourceLocator(final String pattern) {
        super(pattern);
    }

    public static Resource getResource(final String pattern) throws IOException {
        return new BasicXnatResourceLocator(pattern).getResource();
    }

    public static List<Resource> getResources(final String pattern) throws IOException {
        return new BasicXnatResourceLocator(pattern).getResources();
    }
}
