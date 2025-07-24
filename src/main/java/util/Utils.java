package util;

import annotation.Action;
import annotation.PageElement;
import com.squareup.javapoet.ParameterSpec;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import model.Collector;
import model.Element;

public class Utils {

    public static final String PACKAGE_NAME = "page.generated";
    public static final String WHITESPACE = " ";

    //[aboba], [kek] -> aboba, kek
    public static String formatParamListToString(List<ParameterSpec> parameterSpecs) {
        return parameterSpecs.stream()
            .map(spec -> spec.name)
            .collect(Collectors.joining(", "));
    }

    /**
     * Костыль для заполнения value в степе аллюра Если элемент не помечен PageElement - ищем этот же элемент в
     * BaseScreen и берем value у него
     */
    public static String getAnnotationValue(VariableElement field) {
        return Collector.getInstance().getBaseScreenFields().stream()
            .filter(fields -> fields.getSimpleName().equals(field.getSimpleName()))
            .map(annotation -> annotation.getAnnotation(PageElement.class).value())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Поле не объявлено в BaseScreen"));
    }

    public static String getFieldAnnotationValue(VariableElement field) {
        return field.getAnnotationMirrors().toString().contains(PageElement.class.getName())
            ? field.getAnnotation(PageElement.class).value()
            : getAnnotationValue(field);
    }


    /**
     * Метод для проверки есть ли на классе специфическая аннотация
     */
    public static boolean isNotAnnotated(javax.lang.model.element.Element element, Class<? extends Annotation> clazz) {
        return element.getAnnotation(clazz) == null;
    }

    /**
     * Метод для получения типа виджета из пакета test.model.Button -> Button
     */
    public static String getElementTypeName(Element model) {
        return (((DeclaredType) model.getType()).asElement()).getSimpleName().toString();
    }

    public static void checkCorrectMethods(List<? extends javax.lang.model.element.Element> elements) {
        checkDuplicates(elements);
        elements.forEach(method -> {
            if (isNotAnnotated(method, Action.class)) {
                throw new RuntimeException(
                    String.format("Метод с названием %s в классе %s должен быть с аннотацией Action",
                        method.getSimpleName(), method.getEnclosingElement().getSimpleName().toString()));
            }

            if (method.getAnnotation(Action.class).value().isEmpty()) {
                throw new RuntimeException(
                    String.format("Метод с названием %s в классе %s в аннотации Action должен иметь не пустое значение",
                        method.getSimpleName(), method.getEnclosingElement().getSimpleName().toString())
                );
            }
        });
    }

    /*
    Проверка на дубли методов, если есть оверрайд, то структура класса сгенерированного сломается
     */
    private static void checkDuplicates(List<? extends javax.lang.model.element.Element> elements) {
        Set<String> methodNames = elements.stream()
            .map(Object::toString)
            .collect(Collectors.toSet());

        if (elements.size() != methodNames.size()) {
            throw new RuntimeException(
                "Найдены дубликаты методов скорее всего в наследнике BaseElement переопределен один из методов");
        }
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
     * @param target заданная строка;
     * @param regexp регулярное выражение, на основании которого будет выполнен поиск подстроки;
     * @param text   текст, на который будет замена найденная по регулярному выражению строка;
     * @return полученная итоговая строка;
     */
    public static String replaceSubstring(String target, String regexp, String text) {
        StringBuilder result = new StringBuilder();
        Matcher matcher = Pattern.compile(regexp).matcher(target);

        while (matcher.find()) {
            result.append(matcher.group());
        }

        return result.isEmpty()
            ? target
            : target.replace(result.toString(), text);
    }

    /**
     * Метод позволяет получить подстроку в заданной строке по регулярному выражению.
     *
     * @param str    - заданная строка;
     * @param regexp - регулярное выражение, по которому происходит поиск в заданной строке;
     * @return возвращается первое совпадение, если его нет то пустая строка;
     */
    public static String getSubstring(String str, String regexp) {
        String result = "";

        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            result = matcher.group();
        }

        return result;
    }

    /*
    Метод для проверки сопоставления типа Element и значения из Field
    Пример -> Element = Button, field = nextButton, return - true;otherwise false
     */
    public static boolean isFieldTypeCorrect(Element element, VariableElement field) {
        String elementName = getElementTypeName(element);

        return !getSubstring(field.getSimpleName().toString(),
            elementName + "$").isEmpty();
    }

    /*
    Method from apache lang3
     */
    public static boolean containsIgnoreCase(final String str, final String search) {
        int len = search.length();
        int max = str.length() - len;

        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, search, 0, len)) {
                return true;
            }
        }
        return false;
    }

    /*
    Method from apache lang3
     */
    public static String uncapitalize(final String str) {
        final int strLen = str.length();
        if (strLen == 0) {
            return str;
        }

        final int firstCodePoint = str.codePointAt(0);
        final int newCodePoint = Character.toLowerCase(firstCodePoint);
        if (firstCodePoint == newCodePoint) {
            // already capitalized
            return str;
        }

        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint;
        for (int inOffset = Character.charCount(firstCodePoint); inOffset < strLen; ) {
            final int codePoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codePoint;
            inOffset += Character.charCount(codePoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

}
