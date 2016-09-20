/*
 * org.nrg.framework.pinto.BasicTestPintoBean
 * XNAT http://www.xnat.org
 * Copyright (c) 2016, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.framework.pinto;

import java.net.URI;

public class BasicTestPintoBean extends AbstractPintoBean {

    public BasicTestPintoBean(Object parent, String[] arguments) throws PintoException {
        super(parent, arguments);
    }

    /**
     * Provides an opportunity for subclasses to validate the processed parameters and their arguments.
     *
     * @throws org.nrg.framework.pinto.PintoException
     *
     */
    @Override
    public void validate() throws PintoException {
        // Nothing here.
    }

    @Parameter(value = "n", longOption = "name", argCount = ArgCount.OneArgument, help = "Sets the name for the operation.")
    public void setName(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Parameter(value = "u", longOption = "url", argCount = ArgCount.OneArgument, help = "Sets the URL to be used for the operation.")
    public void setUri(URI uri) {
        _uri = uri;
    }

    public URI getUri() {
        return _uri;
    }

    @Parameter(value = "c", longOption = "count", argCount = ArgCount.OneArgument, help = "The count of items to display at one time.")
    public void setCount(int count) {
        _count = count;
    }

    public int getCount() {
        return _count;
    }

    @Parameter(value = "t", longOption = "targets", argCount = ArgCount.OneToN, help = "Indicates the targets for this item.")
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
