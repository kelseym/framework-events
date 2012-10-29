/**
 * PintoBeanTests
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/18/12 by rherri01
 */
package org.nrg.framework.pinto;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class PintoBeanTests {

    private static final String TEST_URI = "http://www.yahoo.com";
    private static final String TEST_NAME = "foo";
    private static final int TEST_COUNT = 10;
    public static final String TEST_TARGET_1 = "one";
    public static final String TEST_TARGET_2 = "two";
    public static final String TEST_TARGET_3 = "three";
    private static final String[] TEST_PARAMS = { "prog", "-h", "-n", TEST_NAME, "-u", TEST_URI, "-c", Integer.toString(TEST_COUNT), "--targets", TEST_TARGET_1, TEST_TARGET_2, TEST_TARGET_3 };

    @Test
    public void testBasicTestApplication() throws PintoException, URISyntaxException {
        BasicTestPintoBean bean = new BasicTestPintoBean(this, TEST_PARAMS);
        assertNotNull(bean);
        assertTrue(bean.getHelp());
        assertFalse(StringUtils.isBlank(bean.getName()));
        assertEquals("foo", bean.getName());
        assertNotNull(bean.getUri());
        assertEquals(new URI(TEST_URI), bean.getUri());
        assertEquals(TEST_COUNT, bean.getCount());
        assertNotNull(bean.getTargets());
    }
}
