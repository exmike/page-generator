package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Retention CLASS — см. {@link Element}. Значение аннотации читается при генерации методов,
 * поэтому должно сохраняться в class-файле обёртки.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Action {

    String value();

}
