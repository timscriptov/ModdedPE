package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Objects;
import java.util.HashMap;
import java.util.Map;

public class UserProperties {
    private final Map<UserProperty, String> _userProperties = new HashMap();

    public enum UserProperty {
        CID
    }

    public UserProperties put(UserProperty userProperty, String value) {
        this._userProperties.put(userProperty, value);
        return this;
    }

    public String get(UserProperty userProperty) {
        return (String) this._userProperties.get(userProperty);
    }

    public boolean has(UserProperty userProperty) {
        return this._userProperties.containsKey(userProperty);
    }

    public int hashCode() {
        return Objects.hashCode(this._userProperties);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof UserProperties)) {
            return false;
        }
        return Objects.equals(this._userProperties, ((UserProperties) o)._userProperties);
    }
}
