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

import org.nrg.framework.annotations.AcceptedArguments;
import org.nrg.framework.annotations.CommandLineParameter;

import java.net.URI;

public class BasicTestApplicationLauncher extends AbstractApplicationLauncher {
    public BasicTestApplicationLauncher(String[] arguments) throws ApplicationParameterException {
        super(arguments);
    }

    @CommandLineParameter(value = "h", longOption = "help", help = "Show the help text for the various options available for this application.")
    public void setHelp(boolean showHelp) {
        _showHelp = showHelp;
    }

    public boolean getHelp() {
        return _showHelp;
    }

    @CommandLineParameter(value = "n", longOption = "name", arguments = AcceptedArguments.OneArgument, help = "Sets the name for the operation.")
    public void setName(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @CommandLineParameter(value = "u", longOption = "url", arguments = AcceptedArguments.OneArgument, help = "Sets the URL to be used for the operation.")
    public void setUri(URI uri) {
        _uri = uri;
    }

    public URI getUri() {
        return _uri;
    }

    @CommandLineParameter(value = "c", longOption = "count", arguments = AcceptedArguments.OneArgument, help = "The count of items to display at one time.")
    public void setCount(int count) {
        _count = count;
    }

    public int getCount() {
        return _count;
    }

    @CommandLineParameter(value = "t", longOption = "targets", arguments = AcceptedArguments.OneToN, help = "Indicates the targets for this item.")
    public void setTargets(String... targets) {
        _targets = targets;
    }

    public String[] getTargets() {
        return _targets;
    }

    private boolean _showHelp;
    private String _name;
    private URI _uri;
    private int _count;
    private String[] _targets;
}
