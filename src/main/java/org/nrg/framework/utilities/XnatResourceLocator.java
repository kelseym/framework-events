package org.nrg.framework.utilities;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public interface XnatResourceLocator {
    List<Resource> getResources() throws IOException;
}
