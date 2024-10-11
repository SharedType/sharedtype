package org.jets.processor.support.exception;

import org.jets.processor.support.github.RepositoryInfo;

public final class JetsInternalError extends RuntimeException {
    public JetsInternalError(String message) {
        super(format(message));
    }

    public JetsInternalError(String message, Throwable cause) {
        super(format(message), cause);
    }

    private static String format(String message) {
        return String.format("%s (this is an implementation error, please post an issue at %s)", message, RepositoryInfo.PROJECT_REPO_URL);
    }
}
