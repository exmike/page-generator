package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Retention CLASS — см. {@link Element}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface BaseElement {

}
