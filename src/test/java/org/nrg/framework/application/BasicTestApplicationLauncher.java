/**
 * BasicTestApplicationLauncher
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/16/12 by rherri01
 */
package org.nrg.framework.application;

import org.nrg.framework.annotations.CommandLineParameter;
import org.nrg.framework.annotations.AcceptedArguments;

public class BasicTestApplicationLauncher extends AbstractApplicationLauncher {
    public BasicTestApplicationLauncher(String[] arguments) throws ApplicationParameterException {
        super(arguments);
    }

    @CommandLineParameter(value = "h", longOption = "help", help = "Show the help text for the various options available for this application.")
    public void setHelp(boolean showHelp) {
        _showHelp = showHelp;
    }

    @CommandLineParameter(value = "u", longOption = "url", help = "Sets the URL to be used for the operation.")
    public void setUrl(String url) {
        _url = url;
    }

    public boolean getHelp() {
        return _showHelp;
    }

    private boolean _showHelp;
    private String _url;
}
