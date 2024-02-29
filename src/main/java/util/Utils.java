package util;

import annotation.Action;
import annotation.PageElementGen;
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
    public static boolean isNotAnnotated(Element element, Class<? extends Annotation> clazz) {
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            if (annotation.getAnnotationType().toString().equals(clazz.getName())) {
                return false;
            }
        }
        return true;
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
            .orElseThrow(() -> new RuntimeException("getMobileElementNameFromField"));
    }

    public static void checkCorrectFields(List<? extends Element> elements, Element page) {
        elements.forEach(field -> {
            if (isNotAnnotated(field, PageElementGen.class)) {
                throw new RuntimeException(String.format("Поле %s в классе %s должно быть c аннотацией PageElement",
                    field, page.getSimpleName()));
            }

            if (field.getAnnotation(PageElementGen.class).value().isEmpty()) {
                throw new RuntimeException(
                    String.format("Поле %s в классе %s в аннотации PageElement должно иметь не пустое значение",
                        field, page.getSimpleName())
                );
            }
        });
    }

    public static void checkCorrectMethods(List<? extends Element> elements) {
        elements.forEach(method -> {
            if (isNotAnnotated(method, Action.class)) {
                throw new RuntimeException(
                    String.format("Метод с названием %s в классе %s должен быть с аннотацией Action",
                        method.getSimpleName(), method.getEnclosingElement().getSimpleName().toString()));
            }

            if (method.getAnnotation(Action.class).value().isEmpty()) {
                throw new RuntimeException(
                    String.format("Метод с названием %s в классе %s в аннотации Action должно иметь не пустое значение",
                        method.getSimpleName(), method.getEnclosingElement().getSimpleName().toString())
                );
            }
        });
    }

    /*
    Валидация наличия обязательных аннотаций
     */
    public static <T> void validate(List<T> elements, Class<? extends Annotation> annotation) {
        if (elements.isEmpty()) {
            throw new RuntimeException("Не нашли классов аннотированных " + annotation.getSimpleName());
        }
    }
}
