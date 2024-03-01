package util;

import annotation.Action;

import annotation.BasePageObject;
import annotation.PageElementGen;
import com.squareup.javapoet.ParameterSpec;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
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
     * Костыль для заполнения value в степе аллюра
     * Если элемент не помечен PageElementGen - ищем этот же элемент в BaseScreen и берем value у него
     */
    public static String getAnnotationValue(RoundEnvironment env, VariableElement field) {
        return getBaseScreenFields(env)
            .filter(fields -> fields.getSimpleName().equals(field.getSimpleName()))
            .map(annotation -> annotation.getAnnotation(PageElementGen.class).value())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Поле не объявлено в BaseScreen"));
    }

    public static String getFieldAnnotationValue(RoundEnvironment env, VariableElement field) {
        if (field.getAnnotationMirrors().toString().contains(PageElementGen.class.getName())) {
            return field.getAnnotation(PageElementGen.class).value();
        } else {
            return getAnnotationValue(env, field);
        }
    }

    /**
     * Метод для получения филдов из BaseScreen
     */
    public static Stream<VariableElement> getBaseScreenFields(RoundEnvironment env) {
        return env.getElementsAnnotatedWith(BasePageObject.class)
            .stream()
            .flatMap(baseScreenFields -> ElementFilter.fieldsIn(baseScreenFields.getEnclosedElements()).stream());
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

    /**
     * Метод позволяет заменить в заданной строке подстроку на основе регулярного выражения.
     *
     * @param target    заданная строка;
     * @param regexp    регулярное выражение, на основании которого будет выполнен поиск подстроки;
     * @param text текст, на который будет замена найденная по регулярному выражению строка;
     * @return полученная итоговая строка;
     */
    public static String replaceSubstring(String target, String regexp, String text) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = Pattern.compile(regexp).matcher(target);

        while (matcher.find()) {
            result.append(matcher.group());
        }

        return result.isEmpty() ? target : target.replace(result.toString(), text);
    }
}
