package annotation.generator;

import static util.Utils.checkCorrectMethods;
import static util.Utils.validate;
import annotation.BaseElement;
import annotation.generator.interfaces.ElementCollector;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import model.Collector;
import model.Element;
import util.GeneratorConfig;
import util.Logger;
import util.Utils;

public record ElementCollectorImpl(
    RoundEnvironment roundEnv,
    Logger log,
    Collector collector,
    ProcessingEnvironment processingEnvironment,
    GeneratorConfig config) implements ElementCollector {

    /**
     * Метод, который собирает все элементы проаннотированные Element и к каждому Element добавляет все методы из
     * BaseElement
     */
    @Override
    public List<Element> collectElements() {
        log.debug("Starting collectElements");
        validateBaseElement();
        List<Element> elements = new ArrayList<>();
        List<ExecutableElement> baseMethods = getBaseMethods();

        findAnnotated(annotation.Element.class)
            .forEach(element -> {
                if (element.getAnnotation(annotation.Element.class).value().isEmpty()) {
                    throw new RuntimeException(
                        String.format("Значение в аннотации Element %s не должно быть пустым",
                            element.getSimpleName()));
                }
                List<ExecutableElement> publicElementMethods = new ArrayList<>(getPublicMethods(element));
                /*
                Проверка, что у бейз элемента есть суперкласс, который не Object
                для таких не будут генерироваться методы из BaseElement
                 */
                if (!((TypeElement) element).getSuperclass().toString().equals(Object.class.getName())) {
                    publicElementMethods.addAll(baseMethods);
                }
                publicElementMethods = publicElementMethods.stream()
                    .filter(publicMethod -> !Utils.containsIgnoreCase("getCollection",
                        publicMethod.getSimpleName().toString())).toList();
                checkCorrectMethods(publicElementMethods);
                elements.add(new Element(element.asType(), new ArrayList<>(publicElementMethods)));
            });
        validate(elements, annotation.Element.class);
        collector.setElements(elements);
        log.debug("Finished collectElements");
        return elements;
    }

    /**
     * Ищет классы с заданной аннотацией.
     *
     * <p>По умолчанию — в исходниках текущего модуля, как и раньше. Если задана опция
     * {@link GeneratorConfig#OPTION_ELEMENTS_PACKAGE}, поиск идёт по указанному пакету через модель
     * компиляции, поэтому обёртки элементов могут лежать в отдельном модуле и приходить сюда уже
     * скомпилированными.
     */
    private Set<? extends javax.lang.model.element.Element> findAnnotated(Class<? extends Annotation> annotation) {
        if (!config.hasElementsPackage()) {
            return roundEnv.getElementsAnnotatedWith(annotation);
        }
        PackageElement elementsPackage =
            processingEnvironment.getElementUtils().getPackageElement(config.elementsPackage());
        if (elementsPackage == null) {
            throw new RuntimeException(String.format(
                "Пакет '%s' из опции %s не найден. Проверь, что модуль с обёртками элементов подключён "
                    + "как зависимость этого модуля", config.elementsPackage(), GeneratorConfig.OPTION_ELEMENTS_PACKAGE));
        }
        return ElementFilter.typesIn(elementsPackage.getEnclosedElements())
            .stream()
            .filter(type -> !Utils.isNotAnnotated(type, annotation))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /*
    Получение всех методов из класса аннотированного BaseElement
     */
    private List<ExecutableElement> getBaseMethods() {
        /*
        validateBaseElement() уже гарантировал ровно один @BaseElement, поэтому здесь не глотаем
        исключения: без базовых методов страницы сгенерируются без checkVisible и подобных, и
        падение произойдёт позже и в другом месте
         */
        javax.lang.model.element.Element baseElement = findAnnotated(BaseElement.class).iterator().next();
        List<ExecutableElement> baseMethods = getPublicMethods(baseElement);
        log.debug("Base methods collected successfully: " + baseMethods.size());
        return baseMethods;
    }

    /*
    Получение всех публичных методов из виджета
     */
    private List<ExecutableElement> getPublicMethods(javax.lang.model.element.Element element) {
        return ElementFilter.methodsIn(element.getEnclosedElements())
            .stream()
            .filter(method -> method.getModifiers().contains(Modifier.PUBLIC))
            .toList();
    }

    /*
    Проверка наличия BaseElement в единственном экземпляре
     */
    private void validateBaseElement() {
        long baseElementCount = findAnnotated(BaseElement.class).size();
        log.debug("Number of BaseElement annotations found: " + baseElementCount);
        if (baseElementCount != 1) {
            throw new RuntimeException(
                "Ожидается, что будет одна аннотация BaseElement но их: " + baseElementCount);
        }
    }
}
