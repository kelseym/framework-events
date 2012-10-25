/**
 * ApplicationLauncherTests
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/18/12 by rherri01
 */
package org.nrg.framework.application;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class ApplicationLauncherTests {

    private static final String TEST_URI = "http://www.yahoo.com";
    private static final String TEST_NAME = "foo";
    private static final int TEST_COUNT = 10;
    public static final String TEST_TARGET_1 = "one";
    public static final String TEST_TARGET_2 = "two";
    public static final String TEST_TARGET_3 = "three";
    private static final String[] TEST_PARAMS = { "prog", "-h", "-n", TEST_NAME, "-u", TEST_URI, "-c", Integer.toString(TEST_COUNT), "--targets", TEST_TARGET_1, TEST_TARGET_2, TEST_TARGET_3 };

    @Test
    public void testBasicTestApplication() throws ApplicationParameterException, URISyntaxException {
        BasicTestApplicationLauncher launcher = new BasicTestApplicationLauncher(TEST_PARAMS);
        assertNotNull(launcher);
        assertTrue(launcher.getHelp());
        assertFalse(StringUtils.isBlank(launcher.getName()));
        assertEquals("foo", launcher.getName());
        assertNotNull(launcher.getUri());
        assertEquals(new URI(TEST_URI), launcher.getUri());
        assertEquals(TEST_COUNT, launcher.getCount());
        assertNotNull(launcher.getTargets());
    }
}
