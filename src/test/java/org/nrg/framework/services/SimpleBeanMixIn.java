package org.nrg.framework.services;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class SimpleBeanMixIn {
    @JsonIgnore
    public abstract String getIgnoredField();
}
