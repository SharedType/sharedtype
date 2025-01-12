package online.sharedtype.processor.support.annotation;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Mark an issue number.
 *
 * @author Cause Chung
 */
@Retention(RetentionPolicy.SOURCE)
public @interface Issue {
    int value();
    String comment() default "";
}
