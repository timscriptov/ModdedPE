package com.microsoft.onlineid.internal.storage;

public class StorageException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public StorageException(String message) {
        super(message);
    }

    public StorageException(Throwable cause) {
        super(cause);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
