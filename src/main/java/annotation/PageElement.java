package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface PageElement {

    /*
    Значение, которое отвечает за название элемента
     */
    String value();

    /*
    Значение, которое отвечает за дополнительную генерацию ожидания элемента
    Если значение выставлено, то к каждому из методов будет дополнительно сгенерировано
    выставленное ожидание, указывается в секундах.
     */
    int timeout() default 0;
}
