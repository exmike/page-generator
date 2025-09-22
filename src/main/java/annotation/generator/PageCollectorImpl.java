package annotation.generator;

import static util.Utils.isNotAnnotated;
import static util.Utils.validate;
import annotation.PageElement;
import annotation.PageObject;
import annotation.generator.interfaces.PageCollector;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import model.Collector;
import model.Page;
import util.Logger;

public record PageCollectorImpl(
    RoundEnvironment roundEnv,
    Logger log,
    Collector collector,
    ProcessingEnvironment processingEnvironment) implements PageCollector {

    /**
     * Метод собирает все пейджы, которые проаннотированны PageObject'ом, собирая public поля находящиеся в них
     */
    @Override
    public List<Page> collectPages() {
        log.debug("Starting collectPages");
        List<Page> pages = this.roundEnv.getElementsAnnotatedWith(PageObject.class)
            .stream()
            .map(page -> {
                List<VariableElement> fields = ElementFilter.fieldsIn(page.getEnclosedElements())
                    .stream()
                    .filter(e -> isLocatorField(e, processingEnvironment))
                    .toList();
                List<ExecutableElement> methods = ElementFilter.methodsIn(page.getEnclosedElements());
                checkCorrectFields(fields, page);
                return new Page(page.getSimpleName().toString(), page.asType(), fields, methods);
            }).toList();
        validate(pages, PageObject.class);
        collector.setPages(pages);
        log.debug("Finished collectPages");
        return pages;
    }

    private boolean isLocatorField(VariableElement field, ProcessingEnvironment processingEnv) {
        Elements elements = processingEnv.getElementUtils();
        Types types = processingEnv.getTypeUtils();
        TypeElement locatorElement = elements.getTypeElement("com.microsoft.playwright.Locator");
        return types.isAssignable(field.asType(), locatorElement.asType());
    }

    private void checkCorrectFields(List<? extends Element> elements, Element page) {

        elements.forEach(field -> {

            if (isNotAnnotated(field, PageElement.class)) {
                throw new RuntimeException(String.format("Поле %s в классе %s должно быть c аннотацией PageElement",
                    field, page.getSimpleName()));
            }
            if (field.getAnnotation(PageElement.class).value().isEmpty()) {
                throw new RuntimeException(
                    String.format("Поле %s в классе %s в аннотации PageElement должно иметь не пустое значение",
                        field, page.getSimpleName()));
            }
        });
    }
}
