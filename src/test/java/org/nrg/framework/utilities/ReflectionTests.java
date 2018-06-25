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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Arrays.array;

public class ReflectionTests {
    @SuppressWarnings("unchecked")
    @Test
    public void testGetGetters() {
        final List<Method> allGetters = Reflection.getGetters(SuperClass3.class);
        assertThat(allGetters.size()).isEqualTo(16);
        final List<Method> upToClass2Getters = Reflection.getGetters(SuperClass3.class, SuperClass1.class);
        assertThat(upToClass2Getters.size()).isEqualTo(8);
        final List<Method> allSetters = Reflection.getSetters(SuperClass3.class);
        assertThat(allSetters.size()).isEqualTo(16);
        final List<Method> upToClass2Setters = Reflection.getSetters(SuperClass3.class, SuperClass1.class);
        assertThat(upToClass2Setters.size()).isEqualTo(8);
    }

    @Test
    public void testConstructorsAndMethodWithPrimitivesAndWrappers() {
        final ConstructorsAndMethodsWithPrimitivesAndWrappers instance1 = Reflection.constructObjectFromParameters(ConstructorsAndMethodsWithPrimitivesAndWrappers.class, 1, "bar");
        final ConstructorsAndMethodsWithPrimitivesAndWrappers instance2 = Reflection.constructObjectFromParameters(ConstructorsAndMethodsWithPrimitivesAndWrappers.class, 2, Arrays.asList("far", "car"));

        assertThat(instance1).isNotNull();
        assertThat(instance1.getFoo()).isNotNull();
        assertThat(instance1.getFoo()).isEqualTo(1);
        assertThat(instance1.getBar()).isNotNull();
        assertThat(instance1.getBar()).isNotEmpty();
        assertThat(instance1.getBar()).containsExactly("bar");
        assertThat(instance2).isNotNull();
        assertThat(instance2.getFoo()).isNotNull();
        assertThat(instance2.getFoo()).isEqualTo(2);
        assertThat(instance2.getBar()).isNotNull();
        assertThat(instance2.getBar()).isNotEmpty();
        assertThat(instance2.getBar()).containsExactly("far", "car");

        final int     intPrim   = 2;
        final Integer intObject = Integer.valueOf("3");
        final String  first     = (String) Reflection.callMethodForParameters(instance1, "getStringFromInteger", intPrim);
        final String  second    = (String) Reflection.callMethodForParameters(instance1, "getStringFromInteger", intObject);
        final String  third     = (String) Reflection.callMethodForParameters(instance1, "getStringFromPrimitiveInt", intPrim);
        final String  fourth    = (String) Reflection.callMethodForParameters(instance1, "getStringFromPrimitiveInt", intObject);

        assertThat(first).isNotBlank();
        assertThat(second).isNotBlank();
        assertThat(third).isNotBlank();
        assertThat(fourth).isNotBlank();
        assertThat(first).isEqualTo("2");
        assertThat(second).isEqualTo("3");
        assertThat(third).isEqualTo("2");
        assertThat(fourth).isEqualTo("3");
    }

    @Test
    public void testSimpleObjectConstruction() {
        final SimpleTestClass instance1 = Reflection.constructObjectFromParameters(SimpleTestClass.class);
        final SimpleTestClass instance2 = Reflection.constructObjectFromParameters(SimpleTestClass.class, "2");
        final SimpleTestClass instance3 = Reflection.constructObjectFromParameters(SimpleTestClass.class, 3);
        final SimpleTestClass instance4 = Reflection.constructObjectFromParameters(SimpleTestClass.class, "four", 4);

        assertThat(array(instance1, instance2, instance3, instance4)).doesNotContainNull();
        assertThat(instance1).hasFieldOrPropertyWithValue("foo", null);
        assertThat(instance1).hasFieldOrPropertyWithValue("bar", -1);
        assertThat(instance2).hasFieldOrPropertyWithValue("foo", "2");
        assertThat(instance2).hasFieldOrPropertyWithValue("bar", -1);
        assertThat(instance3).hasFieldOrPropertyWithValue("foo", null);
        assertThat(instance3).hasFieldOrPropertyWithValue("bar", 3);
        assertThat(instance4).hasFieldOrPropertyWithValue("foo", "four");
        assertThat(instance4).hasFieldOrPropertyWithValue("bar", 4);

        final LessSimpleTestClass instance5 = Reflection.constructObjectFromParameters(LessSimpleTestClass.class);
        final LessSimpleTestClass instance6 = Reflection.constructObjectFromParameters(LessSimpleTestClass.class, "2");
        final LessSimpleTestClass instance7 = Reflection.constructObjectFromParameters(LessSimpleTestClass.class, 3);
        final LessSimpleTestClass instance8 = Reflection.constructObjectFromParameters(LessSimpleTestClass.class, "four", 4);

        assertThat(array(instance5, instance6, instance7, instance8)).doesNotContainNull();
        assertThat(instance5).hasFieldOrPropertyWithValue("foo", null);
        assertThat(instance5).hasFieldOrPropertyWithValue("bar", -1);
        assertThat(instance6).hasFieldOrPropertyWithValue("foo", "2");
        assertThat(instance6).hasFieldOrPropertyWithValue("bar", -1);
        assertThat(instance7).hasFieldOrPropertyWithValue("foo", null);
        assertThat(instance7).hasFieldOrPropertyWithValue("bar", 3);
        assertThat(instance8).hasFieldOrPropertyWithValue("foo", "four");
        assertThat(instance8).hasFieldOrPropertyWithValue("bar", 4);
    }

    @SuppressWarnings("unused")
    private static class ConstructorsAndMethodsWithPrimitivesAndWrappers {
        public ConstructorsAndMethodsWithPrimitivesAndWrappers(final int foo, final String bar) {
            _foo = foo;
            _bar = Collections.singletonList(bar);
        }

        public ConstructorsAndMethodsWithPrimitivesAndWrappers(final Integer foo, final List<String> bar) {
            _foo = foo;
            _bar = bar;
        }

        public String getStringFromPrimitiveInt(final int foo) {
            return Integer.toString(foo);
        }

        public String getStringFromInteger(final Integer foo) {
            return foo.toString();
        }

        public Integer getFoo() {
            return _foo;
        }

        public List<String> getBar() {
            return _bar;
        }

        private final Integer      _foo;
        private final List<String> _bar;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class SimpleTestClass {
        public SimpleTestClass() {
            this(null, -1);
        }

        public SimpleTestClass(final String foo) {
            this(foo, -1);
        }

        public SimpleTestClass(final int bar) {
            this(null, bar);
        }

        public SimpleTestClass(final String foo, final int bar) {
            _foo = foo;
            _bar = bar;
        }

        public String getFoo() {
            return _foo;
        }

        public int getBar() {
            return _bar;
        }

        private final String _foo;
        private final int    _bar;
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    private static class LessSimpleTestClass {
        public LessSimpleTestClass() {
            this(null, -1);
        }

        public LessSimpleTestClass(final String foo) {
            this(foo, -1);
        }

        public LessSimpleTestClass(final Integer bar) {
            this(null, bar);
        }

        public LessSimpleTestClass(final String foo, final Integer bar) {
            _foo = foo;
            _bar = bar;
        }

        public String getFoo() {
            return _foo;
        }

        public int getBar() {
            return _bar;
        }

        private final String _foo;
        private final int    _bar;
    }
}
