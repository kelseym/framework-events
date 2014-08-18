package org.nrg.framework.constants;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum Scope {
    Site("site"),
    Project("prj"),
    Subject("subj"),
    User("user");

    Scope(final String code) {
        _code = code;
    }

    public String code() {
        return _code;
    }

    public static Scope getScope(final String code) {
        if (_scopes.isEmpty()) {
            synchronized (Scope.class) {
                for (Scope scope : values()) {
                    _scopes.put(scope.code(), scope);
                }
            }
        }
        return _scopes.get(code);
    }

    public static Set<String> getCodes() {
        if (_scopes.isEmpty()) {
            getScope("site");
        }
        return _scopes.keySet();
    }

    private static final Map<String, Scope> _scopes = new ConcurrentHashMap<String, Scope>();
    private final String _code;
}
