package annotation.generator;

import static util.Utils.checkCorrectMethods;
import static util.Utils.validate;
import annotation.BaseElement;
import annotation.generator.interfaces.ElementCollector;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import lombok.RequiredArgsConstructor;
import model.Collector;
import model.Element;
import util.Logger;

@RequiredArgsConstructor
public class ElementCollectorImpl implements ElementCollector {

    private final RoundEnvironment roundEnv;
    private final Logger log;
    private final Collector collector;

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

        roundEnv.getElementsAnnotatedWith(annotation.Element.class)
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
                checkCorrectMethods(publicElementMethods);
                elements.add(new Element(element.asType(), new ArrayList<>(publicElementMethods)));
            });
        validate(elements, annotation.Element.class);
        collector.setElements(elements);
        log.debug("Finished collectElements");
        return elements;
    }

    /*
    Получение всех методов из класса аннотированного BaseElement
     */
    private List<ExecutableElement> getBaseMethods() {
        List<ExecutableElement> baseMethods = new ArrayList<>();
        try {
            baseMethods = getPublicMethods(roundEnv.getElementsAnnotatedWith(BaseElement.class)
                .stream().toList().get(0));
            log.debug("Base methods collected successfully");
        } catch (Exception e) {
            log.error("Error collecting base methods: " + e.getMessage());
        }
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
        long baseElementCount = roundEnv.getElementsAnnotatedWith(BaseElement.class).size();
        log.debug("Number of BaseElement annotations found: " + baseElementCount);
        if (baseElementCount != 1) {
            throw new RuntimeException(
                "Ожидается, что будет одна аннотация BaseElement но их: " + baseElementCount);
        }
    }
}
