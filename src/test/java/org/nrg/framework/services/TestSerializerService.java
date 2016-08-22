/*
 * TestPropertiesService
 * (C) 2016 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 */
package org.nrg.framework.services;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestSerializerServiceConfiguration.class)
public class TestSerializerService {

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

        final Map<String, Double> deserialized = _serializer.deserializeJson(serialized, TYPE_REFERENCE);

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

    private final static TypeReference<HashMap<String, Double>> TYPE_REFERENCE = new TypeReference<HashMap<String, Double>>() {};

    @Inject
    private SerializerService _serializer;
}
