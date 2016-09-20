/*
 * org.nrg.framework.utilities.Reflection
 * XNAT http://www.xnat.org
 * Copyright (c) 2016, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.utilities;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import org.nrg.framework.annotations.XnatPlugin;
import org.nrg.framework.exceptions.NotConcreteTypeException;
import org.nrg.framework.exceptions.NotParameterizedTypeException;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public class Reflection {
    public static final Pattern                     PATTERN_GETTER            = Pattern.compile("^get[A-Z][A-z]+");
    public static final Pattern                     PATTERN_BOOL_GETTER       = Pattern.compile("^is[A-Z][A-z]+");
    public static final Pattern                     PATTERN_SETTER            = Pattern.compile("^set[A-Z][A-z]+");
    public static       Map<String, List<Class<?>>> CACHED_CLASSES_BY_PACKAGE = Maps.newHashMap();

    /**
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     *
     * @param packageName The base package
     *
     * @return The classes
     *
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException            Signals that an I/O exception has occurred.
     */
    public static List<Class<?>> getClassesForPackage(final String packageName) throws ClassNotFoundException, IOException {
        if (CACHED_CLASSES_BY_PACKAGE.containsKey(packageName)) {
            return CACHED_CLASSES_BY_PACKAGE.get(packageName);
        }
        if (_log.isInfoEnabled()) {
            _log.info("Identifying classes for " + packageName);
        }
        final ClassLoader loader = getClassLoader();
        assert loader != null;

        Enumeration<URL> resources = loader.getResources(packageName.replace('.', '/'));
        List<File> directories = new ArrayList<>();
        List<URL> jarFiles = new ArrayList<>();
        final List<Class<?>> classes = new ArrayList<>();

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

        CACHED_CLASSES_BY_PACKAGE.put(packageName, classes);

        return classes;
    }

    /**
     * Checks whether <b>clazz</b> or any of its superclasses are decorated with an annotation of the class
     * <b>annotationClass</b>. If so, the annotation instance is returned. If not, this method returns null. Note that
     * an instance of the annotation will not be found unless the annotation definition is retained for run-time
     * analysis, which requires the following annotation on the definition:
     *
     * <pre>@Retention(RetentionPolicy.RUNTIME)</pre>
     *
     * See {@link XnatPlugin} for an example of an annotation that includes this configuration.
     *
     * @param <T>                 The annotation class to check.
     * @param clazz               The top-level class to check.
     * @param annotationClass     The annotation definition to check for.
     *
     * @return The annotation instance if it exists on the class or any of its subtypes, null otherwise.
     */
    public static <T extends Annotation> T findAnnotationInClassHierarchy(final Class<?> clazz, final Class<T> annotationClass) {
        Class<?> current = clazz;
        while (current != null) {
            if (current.isAnnotationPresent(annotationClass)) {
                return current.getAnnotation(annotationClass);
            }
            current = clazz.getSuperclass();
        }
        return null;
    }

    public static void injectDynamicImplementations(final String _package, final boolean failOnException, Map<String, Object> params) throws Exception {
        List<Class<?>> classes = Reflection.getClassesForPackage(_package);
        if (params == null) {
            params = Maps.newHashMap();
        }
        if (classes != null && classes.size() > 0) {
            for (Class<?> clazz : classes) {
                try {
                    if (InjectableI.class.isAssignableFrom(clazz)) {
                        InjectableI action = (InjectableI) clazz.newInstance();
                        action.execute(params);
                    } else {
                        _log.error("Reflection: " + _package + "." + clazz.getName() + " is NOT an implementation of InjectableI");
                    }
                } catch (Throwable e) {
                    if (failOnException) {
                        throw e;
                    } else {
                        _log.error("", e);
                    }
                }
            }
        }
    }

    public static void injectDynamicImplementations(final String _package, final Map<String, Object> params) {
        try {
            injectDynamicImplementations(_package, false, params);
        } catch (Throwable ignored) {
            // Nothing to do here...
        }
    }

    public static List<Class<?>> getClassesFromParameterizedType(final Type type) throws NotParameterizedTypeException, NotConcreteTypeException {
        if (!(type instanceof ParameterizedType)) {
            throw new NotParameterizedTypeException(type, "The type " + type.toString() + " is not a parameterized type");
        }
        final List<Class<?>> classes = new ArrayList<>();
        final ParameterizedType parameterizedType = (ParameterizedType) type;
        for (final Type subtype : parameterizedType.getActualTypeArguments()) {
            if (subtype instanceof ParameterizedType) {
                throw new NotConcreteTypeException(type, "The type " + type.toString() + " can not be a parameterized type");
            }
            classes.add((Class<?>) subtype);
        }
        return classes;
    }

    public static boolean isGetter(final Method method) {
        return Modifier.isPublic(method.getModifiers()) &&
               method.getParameterTypes().length == 0 &&
               ((PATTERN_GETTER.matcher(method.getName()).matches() && !method.getReturnType().equals(Void.TYPE)) || (PATTERN_BOOL_GETTER.matcher((method.getName())).matches() && method.getReturnType().equals(Boolean.TYPE)));
    }

    public static boolean isSetter(final Method method) {
        return Modifier.isPublic(method.getModifiers()) && PATTERN_SETTER.matcher(method.getName()).matches() && method.getParameterTypes().length == 1;
    }

    public interface InjectableI {
        void execute(Map<String, Object> params);
    }

    /**
     * Find classes in jar file.
     *
     * @param jarFile     the jar file
     * @param packageName the package name
     *
     * @return the collection of classes in the jar file.
     *
     * @throws IOException            Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException the class not found exception
     */
    public static Collection<? extends Class<?>> findClassesInJarFile(final URL jarFile, final String packageName) throws IOException, ClassNotFoundException {
        final List<Class<?>> classes = new ArrayList<>();
        final JarURLConnection connection = (JarURLConnection) jarFile.openConnection();
        final JarFile jar = connection.getJarFile();
        for (final JarEntry entry : Collections.list(jar.entries())) {
            if (entry.getName().startsWith(packageName.replace('.', '/')) && entry.getName().endsWith(".class") && !entry.getName().contains("$")) {
                final String className = entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6);
                classes.add(Class.forName(className));
            }
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirectories.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     *
     * @return The classes
     *
     * @throws ClassNotFoundException the class not found exception
     * @throws IOException            Signals that an I/O exception has occurred.
     */
    public static List<Class<?>> findClasses(final File directory, final String packageName) throws ClassNotFoundException, IOException {
        final List<Class<?>> classes = new ArrayList<>();

        if (!directory.exists()) {
            return classes;
        }

        final File[] files = directory.listFiles();

        assert files != null;
        for (final File file : files) {
            final String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$")) {
                try {
                    classes.add(Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6)));
                } catch (ExceptionInInitializerError e) {
                    // This happens to classes which depend on Spring to inject
                    // some beans
                    // and fail if dependency is not fulfilled
                    final ClassLoader classLoader = getClassLoader();
                    assert classLoader != null;
                    classes.add(Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false, classLoader));
                }
            }
        }

        return classes;
    }

    public static ClassLoader getClassLoader() {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return contextClassLoader;
        }
        return Reflection.class.getClassLoader();
    }

    public static Properties getPropertiesForClass(final Class<?> parent) {
        final String bundle = "/" + parent.getName().replace(".", "/") + ".properties";
        Properties properties = new Properties();
        try {
            try (InputStream inputStream = parent.getResourceAsStream(bundle)) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            properties = null;
        }
        return properties;
    }

    /**
     * This method is to compensate for the fact that the default Java getConstructor() method doesn't match
     * constructors that have parameters that are superclasses of one of the submitted parameter classes. For example,
     * if an object submitted for the constructor is an <b>ArrayList</b>, <b>getConstructor()</b> will not match that to
     * a constructor that takes a <b>List</b>. This method checks for that downcast compatibility via the
     * <b>isAssignableFrom()</b> method.
     *
     * <b>Note:</b> This version of the method returns only publicly accessible constructors. If you need to find
     * protected or private constructors, call {@link #getConstructorForParameters(Class, int, Class[])}.
     *
     * @param target         The class that you want to inspect for compatible constructors.
     * @param parameterTypes The parameter types you want to submit to the constructor.
     * @param <T>            The parameterized type for this method.
     *
     * @return A matching constructor, if any, <b>null</b> otherwise.
     */
    public static <T> Constructor<T> getConstructorForParameters(final Class<T> target, final Class<?>... parameterTypes) {
        return getConstructorForParameters(target, Modifier.PUBLIC, parameterTypes);
    }

    /**
     * This method is to compensate for the fact that the default Java getConstructor() method doesn't match
     * constructors that have parameters that are superclasses of one of the submitted parameter classes. For example,
     * if an object submitted for the constructor is an <b>ArrayList</b>, <b>getConstructor()</b> will not match that to
     * a constructor that takes a <b>List</b>. This method checks for that downcast compatibility via the
     * <b>isAssignableFrom()</b> method.
     *
     * <b>Note:</b> This version of the method can return public, protected, and private constructors. If you want to
     * find only publicly accessible constructors, you can call {@link #getConstructorForParameters(Class, Class[])}.
     * To specify what access level of constructor you want to retrieve, you should specify the appropriate value from
     * the {@link Modifier} class:
     *
     * <ul>
     * <li>{@link Modifier#PUBLIC} indicates only publicly accessible constructors</li>
     * <li>{@link Modifier#PROTECTED} indicates publicly accessible or protected constructors</li>
     * <li>{@link Modifier#PRIVATE} indicates public, protected, or private constructors</li>
     * </ul>
     *
     * @param target          The class that you want to inspect for compatible constructors.
     * @param requestedAccess Indicates the desired access level.
     * @param parameterTypes  The parameter types you want to submit to the constructor.
     * @param <T>             The parameterized type for this method.
     *
     * @return A matching constructor, if any, <b>null</b> otherwise.
     */
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> getConstructorForParameters(final Class<T> target, final int requestedAccess, final Class<?>... parameterTypes) {
        // If there are no parameters specified, return the default constructor.
        if (parameterTypes == null || parameterTypes.length == 0) {
            try {
                Constructor<T> constructor = target.getConstructor();
                // If the default constructor isn't accessible
                return isAccessible(requestedAccess, constructor.getModifiers()) ? constructor : null;
            } catch (NoSuchMethodException e) {
                // If there was no default constructor, then return null.
                return null;
            }
        }
        // Try to return constructor that's an exact match for the parameter types.
        // If that doesn't exist, ignore the exception. We have more sophisticated
        // things that we can try to match superclasses, interfaces, etc.
        try {
            return target.getConstructor(parameterTypes);
        } catch (NoSuchMethodException ignored) {
            //
        }
        final Constructor<T>[] constructors = (Constructor<T>[]) target.getConstructors();
        // Honestly I don't even think this can happen, but just in case...
        if (constructors == null || constructors.length == 0) {
            return null;
        }
        // If we got through all the bits where there are no constructors or parameters, we can try to match the
        // submitted parameter types.
        for (final Constructor<T> candidate : constructors) {
            // If it's not accessible, don't even process.
            if (!isAccessible(requestedAccess, candidate.getModifiers())) {
                continue;
            }
            // Get all of the parameter types for the current candidate constructor.
            final Class<?>[] candidateParameterTypes = candidate.getParameterTypes();
            // If the number of parameter types don't match, then this isn't a match.
            // TODO: Check how this works with vararg constructors. It should be fine (should be Object[]), but still.
            if (candidateParameterTypes.length != parameterTypes.length) {
                continue;
            }
            // Let's assume the best.
            boolean match = true;
            // Now go through all of the parameters.
            for (int index = 0; index < candidateParameterTypes.length; index++) {
                // If we can't assign this submitted parameter to the candidate parameter...
                if (!(candidateParameterTypes[index].isAssignableFrom(parameterTypes[index]))) {
                    // We don't have a match.
                    match = false;
                    break;
                }
            }
            if (match) {
                return candidate;
            }
        }
        // If we made it through without returning, none of the constructor candidates match.
        return null;
    }

    @SuppressWarnings("unused")
    public static String findResource(final String resourcePackage, final String resourcePattern) {
        final Set<String> resources = findResources(resourcePackage, Pattern.compile(resourcePattern));
        if (resources.size() == 0) {
            return null;
        }
        if (resources.size() > 1) {
            throw new RuntimeException("You assumed there was only one resource with the package " + resourcePackage + " and the name " + resourcePattern + ", but that's not true: there are " + resources.size() + " of them (make sure your 'pattern' isn't actually a regex pattern but just a standard name): " + Joiner.on(", ").join(resources));
        }
        return (String) resources.toArray()[0];
    }

    public static Set<String> findResources(final String resourcePackage, final String resourcePattern) {
        return findResources(resourcePackage, Pattern.compile(resourcePattern));
    }

    public static Set<String> findResources(final String resourcePackage, final Pattern resourcePattern) {
        final Reflections reflections = getReflectionsForPackage(resourcePackage);
        return reflections.getResources(resourcePattern);
    }

    @SuppressWarnings("unused")
    public static URL getResourceUrl(final String resource) {
        final ClassLoader classLoader = getClassLoader();
        assert classLoader != null;
        return classLoader.getResource(resource);
    }

    private static boolean isAccessible(final int requestedAccess, final int modifiers) {
        // If they want private, they can have anything, so just say yes.
        if (requestedAccess == Modifier.PRIVATE) {
            return true;
        }

        // Find out if the target modifier is private or protected.
        boolean isPrivate = Modifier.isPrivate(modifiers);
        boolean isProtected = Modifier.isPrivate(modifiers);

        // If requested access is protected, then return true if the modifier is NOT private, otherwise it's public so
        // return true if the modifier is NOT private or protected.
        return requestedAccess == Modifier.PROTECTED ? !isPrivate : !(isPrivate || isProtected);
    }

    private static Reflections getReflectionsForPackage(final String resourcePackage) {
        if (!_reflectionsCache.containsKey(resourcePackage)) {
            _reflectionsCache.put(resourcePackage, new Reflections(resourcePackage, new ResourcesScanner()));
        }
        return _reflectionsCache.get(resourcePackage);
    }

    private static final Map<String, Reflections> _reflectionsCache = Collections.synchronizedMap(new HashMap<String, Reflections>());

    private static final Logger _log = LoggerFactory.getLogger(Reflection.class);
}
