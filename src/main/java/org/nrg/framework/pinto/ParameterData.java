/**
 * ParameterData
 * (C) 2012 Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD License
 *
 * Created on 10/28/12 by rherri01
 */
package org.nrg.framework.pinto;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;

/**
 * Encapsulates all the data about a particular parameter extracted from a pinto bean instance.
 */
public class ParameterData {

    public ParameterData(final Method method, Parameter parameter) {
        if (_log.isDebugEnabled()) {
            _log.debug("Creating new parameter data object:");
            _log.debug(" *** Short option:  " + parameter.value());
            _log.debug(" *** Long option:   " + parameter.longOption());
            _log.debug(" *** Help text:     " + parameter.help());
            _log.debug(" *** Expected type: " + parameter.type());
        }

        _method = method;
        _shortOption = parameter.value();
        _longOption = parameter.longOption();
        _argCount = parameter.argCount();
        _exactArgCount = parameter.exactArgCount();
        _help = parameter.help();
    }

    public Method getMethod() {
        return _method;
    }

    public String getShortOption() {
        return _shortOption;
    }

    public boolean hasLongOption() {
        return !StringUtils.isBlank(_longOption);
    }

    public String getLongOption() {
        return _longOption;
    }

    public ArgCount getArgCount() {
        return _argCount;
    }

    public int getExactArgCount() {
        return _exactArgCount;
    }

    public String getHelp() {
        return _help;
    }

    private static final Log _log = LogFactory.getLog(ParameterData.class);

    private final Method _method;
    private final String _shortOption;
    private final String _longOption;
    private final ArgCount _argCount;
    private final int _exactArgCount;
    private final String _help;
}

