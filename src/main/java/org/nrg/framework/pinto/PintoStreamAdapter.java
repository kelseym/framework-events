/**
 * PintoStreamAdapter
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 11/8/12 by rherri01
 */
package org.nrg.framework.pinto;

import java.io.PrintStream;

public interface PintoStreamAdapter {
    abstract public PrintStream getOutputStream();
}
