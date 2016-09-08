package org.nrg.framework.beans;

import org.apache.commons.lang3.StringUtils;
import org.nrg.framework.annotations.XnatDataModel;

import java.util.Properties;

public class XnatDataModelBean {
    public static final String PLUGIN_DATA_MODEL_PREFIX = "dataModel.";

    public XnatDataModelBean(final String type, final Properties properties) {
        _type = type;
        _prefix = getPrefix(type);
        final Properties init = new Properties();
        for (final String property : properties.stringPropertyNames()) {
            if (property.startsWith(_prefix)) {
                final String value = properties.getProperty(property);
                init.setProperty(property.substring(_prefix.length()), value);
            }
        }
        _secured = Boolean.parseBoolean(init.getProperty(XnatDataModel.DATA_MODEL_SECURED));
        _singular = init.getProperty(XnatDataModel.DATA_MODEL_SINGULAR);
        _plural = init.getProperty(XnatDataModel.DATA_MODEL_PLURAL);
        _code = init.getProperty(XnatDataModel.DATA_MODEL_CODE);
    }

    public XnatDataModelBean(final XnatDataModel dataModel) {
        _type = dataModel.value();
        _prefix = getPrefix(_type);
        _secured = dataModel.secured();
        _singular = dataModel.singular();
        _plural = dataModel.plural();
        _code = dataModel.code();
    }

    public String getType() {
        return _type;
    }

    public boolean isSecured() {
        return _secured;
    }

    public String getSingular() {
        return _singular;
    }

    public String getPlural() {
        return _plural;
    }

    public String getCode() {
        return _code;
    }

    public Properties asProperties() {
        final Properties properties = new Properties();
        properties.setProperty(_prefix + "secured", Boolean.toString(_secured));
        if (StringUtils.isNotBlank(_singular)) {
            properties.setProperty(_prefix + "singular", _singular);
        }
        if (StringUtils.isNotBlank(_plural)) {
            properties.setProperty(_prefix + "plural", _plural);
        }
        if (StringUtils.isNotBlank(_code)) {
            properties.setProperty(_prefix + "code", _code);
        }
        return properties;
    }

    private static String getPrefix(final String type) {
        return PLUGIN_DATA_MODEL_PREFIX + type.replace(":", ".") + ".";
    }

    private final String  _type;
    private final String  _prefix;
    private final boolean _secured;
    private final String  _singular;
    private final String  _plural;
    private final String  _code;
}
