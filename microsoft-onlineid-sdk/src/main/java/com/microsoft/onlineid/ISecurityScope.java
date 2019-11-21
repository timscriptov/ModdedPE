package com.microsoft.onlineid;

import java.io.Serializable;

public interface ISecurityScope extends Serializable {
    String getPolicy();

    String getTarget();
}
