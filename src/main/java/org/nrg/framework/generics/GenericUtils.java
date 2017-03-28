/*
 * framework: org.nrg.framework.generics.GenericUtils
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.generics;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class GenericUtils {
    public static <T> List<T> convertToTypedList(final Iterable<?> objects, final Class<T> clazz) {
        final List<T> list = Lists.newArrayList();
        if (objects != null) {
            for (final Object object : objects) {
                if (object != null && clazz.isInstance(object)) {
                    list.add(clazz.cast(object));
                }
            }
        }
        return list;
    }

    public static <K, V> Map<K, V> convertToTypedMap(final Map<?, ?> objects, final Class<K> keyClazz, final Class<V> valueClazz) {
        final Map<K, V> map = Maps.newHashMap();
        if (objects != null) {
            for (final Map.Entry<?, ?> entry : objects.entrySet()) {
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                if (keyClazz.isInstance(key) && valueClazz.isInstance(value)) {
                    map.put(keyClazz.cast(key), valueClazz.cast(value));
                }
            }
        }
        return map;
    }
}
