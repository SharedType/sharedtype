package org.sharedtype.support.exception;

import org.sharedtype.support.github.RepositoryInfo;

/**
 * Indicate an error.
 *
 * @author Cause Chung
 */
public final class SharedTypeInternalError extends Error {
    public SharedTypeInternalError(String message) {
        super(format(message));
    }

    public SharedTypeInternalError(String message, Throwable cause) {
        super(format(message), cause);
    }

    private static String format(String message) {
        return String.format("%s (this could be an implementation error, please post an issue at %s/issues)", message, RepositoryInfo.PROJECT_REPO_URL);
    }
}
