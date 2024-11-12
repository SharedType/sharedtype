package online.sharedtype.processor.support.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicate a method is recursive. Recursive methods are hard to debug and have worse performance than plain loops, and should be avoided in favor of loops.
 * @author Cause Chung
 */
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface Recursive {
}
