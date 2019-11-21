package com.microsoft.onlineid;

import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Scopes;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.Uris;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SecurityScope implements ISecurityScope {
    private static final long serialVersionUID = 1;
    private final String _address;
    private final String _policy;

    public SecurityScope(String target, String policy) {
        Strings.verifyArgumentNotNullOrEmpty(target, "target");
        if (policy == null || !policy.equalsIgnoreCase(Scopes.TokenBrokerPolicyString)) {
            target = Uris.mapToSortedQueryString(Collections.singletonMap(Scopes.ScopeParameterName, Scopes.buildOffer(target, policy)));
            policy = Scopes.TokenBrokerPolicyString;
        }
        this._address = target;
        this._policy = policy;
    }

    public SecurityScope(String target, String policy, Map<String, String> parameters) {
        if (policy != null && policy.equalsIgnoreCase(Scopes.TokenBrokerPolicyString) && parameters != null) {
            throw new IllegalArgumentException("The parameters map cannot be applied to a scope already in TOKEN_BROKER format.");
        } else if (parameters.containsKey(Scopes.ScopeParameterName)) {
            throw new IllegalArgumentException("The parameters map cannot contain a 'scope' key.");
        } else {
            Map<String, String> parametersCopy = new HashMap(parameters);
            parametersCopy.put(Scopes.ScopeParameterName, Scopes.buildOffer(target, policy));
            this._address = Uris.mapToSortedQueryString(parametersCopy);
            this._policy = Scopes.TokenBrokerPolicyString;
        }
    }

    public String getTarget() {
        return this._address;
    }

    public String getPolicy() {
        return this._policy;
    }

    public int hashCode() {
        return Objects.hashCode(toString());
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ISecurityScope)) {
            return false;
        }
        ISecurityScope scope = (ISecurityScope) other;
        if (getTarget().equalsIgnoreCase(scope.getTarget()) && Strings.equalsIgnoreCase(getPolicy(), scope.getPolicy())) {
            return true;
        }
        return false;
    }

    public String toString() {
        return this._address + " / " + this._policy;
    }
}
