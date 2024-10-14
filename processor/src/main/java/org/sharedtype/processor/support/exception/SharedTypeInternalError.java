package org.sharedtype.processor.support.exception;

import org.sharedtype.processor.support.github.RepositoryInfo;

public final class SharedTypeInternalError extends RuntimeException {
    public SharedTypeInternalError(String message) {
        super(format(message));
    }

    public SharedTypeInternalError(String message, Throwable cause) {
        super(format(message), cause);
    }

    private static String format(String message) {
        return String.format("%s (this is an implementation error, please post an issue at %s)", message, RepositoryInfo.PROJECT_REPO_URL);
    }
}
