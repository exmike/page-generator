package util;

import java.util.Map;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;

/**
 * Настройки генерации, приходящие из опций процессора аннотаций.
 *
 * <p>В Gradle задаются так:
 * <pre>
 * kapt {
 *     arguments {
 *         arg("pageGenerator.package", "page.generated.admin")
 *         arg("pageGenerator.managerName", "AdminScreenManager")
 *         arg("pageGenerator.elementsPackage", "com.doto.qa.element")
 *     }
 * }
 * </pre>
 *
 * <p>Все опции необязательны. Без них поведение остаётся прежним: классы генерируются в
 * {@code page.generated}, менеджер называется {@code ScreenManager}, а обёртки элементов ищутся
 * только в исходниках текущего модуля. Это позволяет обновить версию генератора, не меняя сборку.
 *
 * @param packageName     пакет, в который пишутся сгенерированные классы
 * @param managerName     имя класса-менеджера экранов
 * @param elementsPackage пакет с обёртками элементов; {@code null} — искать в текущем модуле
 */
public record GeneratorConfig(String packageName, String managerName, String elementsPackage) {

    public static final String OPTION_PACKAGE = "pageGenerator.package";
    public static final String OPTION_MANAGER_NAME = "pageGenerator.managerName";
    public static final String OPTION_ELEMENTS_PACKAGE = "pageGenerator.elementsPackage";

    public static final String DEFAULT_PACKAGE = "page.generated";
    public static final String DEFAULT_MANAGER_NAME = "ScreenManager";

    public static final Set<String> SUPPORTED_OPTIONS =
        Set.of(OPTION_PACKAGE, OPTION_MANAGER_NAME, OPTION_ELEMENTS_PACKAGE);

    public static GeneratorConfig from(ProcessingEnvironment processingEnvironment) {
        Map<String, String> options = processingEnvironment.getOptions();
        return new GeneratorConfig(
            option(options, OPTION_PACKAGE, DEFAULT_PACKAGE),
            option(options, OPTION_MANAGER_NAME, DEFAULT_MANAGER_NAME),
            option(options, OPTION_ELEMENTS_PACKAGE, null));
    }

    /**
     * Обёртки элементов нужно искать в отдельном пакете, а не только в исходниках текущего модуля.
     */
    public boolean hasElementsPackage() {
        return elementsPackage != null;
    }

    private static String option(Map<String, String> options, String key, String defaultValue) {
        String value = options.get(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
