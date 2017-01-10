/*
 * framework: org.nrg.framework.services.TestSerializerService
 * XNAT http://www.xnat.org
 * Copyright (c) 2017, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestSerializerServiceConfiguration.class)
public class TestSerializerService {

    public static final String IGNORED  = "This shouldn't show up.";
    public static final String RELEVANT = "This should totally show up.";

    @Test
    public void testNaNAndInfinityHandling() throws IOException {
        final Map<String, Double> values = new HashMap<>();
        values.put("one", 3.14159);
        values.put("two", 723.12479145115);
        values.put("three", Double.MAX_VALUE);
        values.put("four", Double.MIN_VALUE);
        values.put("five", Double.MIN_NORMAL);
        values.put("six", Double.NaN);
        values.put("seven", Double.NEGATIVE_INFINITY);
        values.put("eight", Double.POSITIVE_INFINITY);

        final String serialized = _serializer.toJson(values);

        assertNotNull(serialized);

        final Map<String, Double> deserialized = _serializer.deserializeJson(serialized, SerializerService.TYPE_REF_MAP_STRING_DOUBLE);

        assertNotNull(deserialized);
        assertFalse(Double.isInfinite(deserialized.get("one")));
        assertFalse(Double.isInfinite(deserialized.get("two")));
        assertFalse(Double.isInfinite(deserialized.get("three")));
        assertFalse(Double.isInfinite(deserialized.get("four")));
        assertFalse(Double.isInfinite(deserialized.get("five")));
        assertFalse(Double.isNaN(deserialized.get("one")));
        assertFalse(Double.isNaN(deserialized.get("two")));
        assertFalse(Double.isNaN(deserialized.get("three")));
        assertFalse(Double.isNaN(deserialized.get("four")));
        assertFalse(Double.isNaN(deserialized.get("five")));
        assertTrue(Double.isNaN(deserialized.get("six")));
        assertTrue(Double.isInfinite(deserialized.get("seven")));
        assertTrue(Double.isInfinite(deserialized.get("eight")));
    }

    @Test
    public void testAnnotatedMixIn() throws IOException {
        final SimpleBean bean = new SimpleBean(RELEVANT, IGNORED);
        assertNotNull(bean);
        assertEquals(RELEVANT, bean.getRelevantField());
        assertEquals(IGNORED, bean.getIgnoredField());

        final String json = _serializer.toJson(bean);
        assertNotNull(json);
        final Map<String, String> map = _serializer.deserializeJsonToMapOfStrings(json);
        assertTrue(map.containsKey("relevantField"));
        assertFalse(map.containsKey("ignoredField"));
        assertEquals(RELEVANT, map.get("relevantField"));
    }

    @Inject
    private SerializerService _serializer;
}
