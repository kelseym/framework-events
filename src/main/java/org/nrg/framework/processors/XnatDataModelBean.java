package org.nrg.framework.processors;

public class XnatDataModelBean {
    public String getType() {
        return _type;
    }

    public void setType(final String type) {
        _type = type;
    }

    public boolean isSecured() {
        return _secured;
    }

    public void setSecured(final boolean secured) {
        _secured = secured;
    }

    public String getSingular() {
        return _singular;
    }

    public void setSingular(final String singular) {
        _singular = singular;
    }

    public String getPlural() {
        return _plural;
    }

    public void setPlural(final String plural) {
        _plural = plural;
    }

    public String getCode() {
        return _code;
    }

    public void setCode(final String code) {
        _code = code;
    }

    private String  _type;
    private boolean _secured;
    private String  _singular;
    private String  _plural;
    private String  _code;
}
