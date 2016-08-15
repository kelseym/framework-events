package org.nrg.framework.configuration;

import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Maintains a list of paths for configuration folders and provides utility methods for locating resources within those
 * folders.
 */
public class ConfigPaths extends ArrayList<Path> {
    /**
     * Creates a default instance of the class. This contains no paths to start.
     */
    public ConfigPaths() {
        _log.debug("Creating default instance of the ConfigPaths class.");
    }

    /**
     * Creates an instance of the class populated with the paths in the input parameter.
     *
     * @param paths The paths with which to populate the new instance
     */
    public ConfigPaths(final Collection<Path> paths) {
        super(paths);
        _log.debug("Creating instance of the ConfigPaths class with {} paths provided: ", paths.size(), Joiner.on(", ").join(paths));
    }

    /**
     * Attempts to find the indicated subpaths on the list of paths in this instance. The first instance in which a file
     * from the <b>subpaths</b> parameter list is found in one of the paths contained in the instance is returned. No
     * further searching is done. If you want to find instances of each of the subpaths, try calling the {@link
     * #findFiles(String...)} method instead.
     *
     * @param subpaths The subpaths to search for on the list of configuration paths.
     * @return The first instance found of one of the subpaths on one of the configuration paths.
     */
    public File findFile(final String... subpaths) {
        for (final String subpath : subpaths) {
            for (final Path path : this) {
                final File file = path.resolve(subpath).toFile();
                if (file.exists() && file.isFile()) {
                    return file;
                }
            }
        }
        return null;
    }

    /**
     * Attempts to find the indicated subpaths on the list of paths in this instance. The first instance in which a file
     * from each of the entries in the <b>subpaths</b> parameter list is found in one of the paths contained in the
     * instance is added to the list, but every entry is searched for.  If you want to find just the first instance
     * where one of the subpaths is found on one of the configuration paths, call the {@link #findFile(String...)}
     * method instead.
     *
     * @param subpaths The subpaths to search for on the list of configuration paths.
     * @return The first instance found of each of the subpaths on one of the configuration paths.
     */
    public List<File> findFiles(final String... subpaths) {
        final List<File> files = new ArrayList<>();
        for (final String subpath : subpaths) {
            for (final Path path : this) {
                final File file = path.resolve(subpath).toFile();
                if (file.exists() && file.isFile()) {
                    files.add(file);
                }
            }
        }
        return files;
    }

    private static final Logger _log = LoggerFactory.getLogger(ConfigPaths.class);
}
