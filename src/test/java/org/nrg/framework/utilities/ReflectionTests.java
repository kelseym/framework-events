/*
 * framework: org.nrg.framework.utilities.ReflectionTests
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.utilities;

import org.junit.Test;
import org.nrg.framework.utilities.beans.SuperClass1;
import org.nrg.framework.utilities.beans.SuperClass3;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ReflectionTests {
    @Test
    public void testGetGetters() {
        final List<Method> allGetters = Reflection.getGetters(SuperClass3.class);
        assertEquals(16, allGetters.size());
        final List<Method> upToClass2Getters = Reflection.getGetters(SuperClass3.class, SuperClass1.class);
        assertEquals(8, upToClass2Getters.size());
        final List<Method> allSetters = Reflection.getSetters(SuperClass3.class);
        assertEquals(16, allSetters.size());
        final List<Method> upToClass2Setters = Reflection.getSetters(SuperClass3.class, SuperClass1.class);
        assertEquals(8, upToClass2Setters.size());
    }
}
