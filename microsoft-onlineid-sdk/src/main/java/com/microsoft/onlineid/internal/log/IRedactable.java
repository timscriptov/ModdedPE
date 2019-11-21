package com.microsoft.onlineid.internal.log;

public interface IRedactable {
    String getRedactedString();

    String getUnredactedString();
}
