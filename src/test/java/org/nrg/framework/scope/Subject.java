package org.nrg.framework.scope;

import org.nrg.framework.constants.Scope;

public class Subject extends Wired {
    @Override
    public Scope getScope() {
        return Scope.Subject;
    }
}
