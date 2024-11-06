package online.sharedtype.support.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that the visibility of a method or class is greater than needed in compile scope code, but exposed for testing purpose.
 *
 * @author Cause Chung
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.SOURCE)
public @interface VisibleForTesting {

}
