package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Retention CLASS, а не SOURCE: обёртки элементов могут лежать в отдельном модуле и попадать
 * в обработку уже скомпилированными — из class-файла аннотацию с retention SOURCE не прочитать.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Element {

    String value();

}
