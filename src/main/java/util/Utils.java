package util;

import com.squareup.javapoet.ParameterSpec;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import model.MobileElementModel;

public class Utils {

    public static final String PACKAGE_NAME = "page.generated";
    public static final String WHITESPACE = " ";

    //todo mb rework
    //[aboba],[kek] -> aboba, kek
    public static String formatParamListToString(List<ParameterSpec> parameterSpecs) {
        return parameterSpecs.stream()
            .map(parameterSpec -> parameterSpec.name)
            .toList()
            .toString()
            .replace("[", "").replace("]", "");
    }

    /**
     * Метод для проверки есть ли на классе специфическая аннотация
     */
    public static boolean isAnnotated(Element element, Class<? extends Annotation> clazz) {
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            if (annotation.getAnnotationType().toString().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Метод для получения типа виджета из пакета test.model.Button -> Button
     */
    public static String getMobileElementTypeName(MobileElementModel model) {
        return (((DeclaredType) model.getType()).asElement()).getSimpleName().toString();
    }

    /*
    Метод для получения типа виджета из филда пейджи titleLabel -> Label
    */
    public static String getMobileElementNameFromField(VariableElement field) {
        return Arrays.stream(field.getSimpleName().toString().split("(?=[A-Z])"))
            .reduce((head, tail) -> tail)
            .get();
    }

}
