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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class ApplicationLauncherTests {

    @Test
    public void testBasicTestApplication() throws ApplicationParameterException {
        BasicTestApplicationLauncher launcher = new BasicTestApplicationLauncher(new String[] { "prog", "-h" });
        assertNotNull(launcher);
        assertTrue(launcher.getHelp());
    }
}
