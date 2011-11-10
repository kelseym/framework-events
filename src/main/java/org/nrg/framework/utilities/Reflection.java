/**
 * Reflection
 * (C) 2011 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 11/8/11 by rherri01
 */
package org.nrg.framework.utilities;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Reflection {
    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException    Signals that an I/O exception has occurred.
     */
    public static List<Class<?>> getClassesForPackage(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        assert loader != null;

        Enumeration<URL> resources = loader.getResources(packageName.replace('.', '/'));
        List<File> directories = new ArrayList<File>();
        List<URL> jarFiles = new ArrayList<URL>();
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();

            if (resource.getProtocol().equalsIgnoreCase("jar")) {
                jarFiles.add(resource);
            } else {
                directories.add(new File(URLDecoder.decode(resource.getFile(), "UTF-8")));
            }
        }

        for (URL jarFile : jarFiles) {
            classes.addAll(findClassesInJarFile(jarFile, packageName));
        }
        for (File directory : directories) {
            classes.addAll(findClasses(directory, packageName));
        }

        return classes;
    }

    /**
     * Find classes in jar file.
     *
     * @param jarFile     the jar file
     * @param packageName the package name
     * @return the collection<? extends class<?>>
     * @throws IOException    Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException the class not found exception
     */
    public static Collection<? extends Class<?>> findClassesInJarFile(URL jarFile, String packageName) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        JarURLConnection connection = (JarURLConnection) jarFile.openConnection();
        JarFile jar = connection.getJarFile();
        for (JarEntry entry : Collections.list(jar.entries())) {
            if (entry.getName().startsWith(packageName.replace('.', '/')) && entry.getName().endsWith(".class") && !entry.getName().contains("$")) {
                String className = entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException    Signals that an I/O exception has occurred.
     */
    public static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException, IOException {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();

        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                Class<?> _class;

                try {
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                } catch (ExceptionInInitializerError e) {
                    // This happens to classes which depend on Spring to inject
                    // some beans
                    // and fail if dependency is not fulfilled
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false, Thread.currentThread().getContextClassLoader());
                }

                classes.add(_class);
            }
        }

        return classes;
    }
}
