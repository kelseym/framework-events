package org.nrg.framework.utilities;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface XnatResourceLocator {
    Resource getResource() throws IOException;
    List<Resource> getResources() throws IOException;
}
