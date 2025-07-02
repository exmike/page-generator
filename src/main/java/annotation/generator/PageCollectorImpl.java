package annotation.generator;

import static util.Utils.isNotAnnotated;
import static util.Utils.validate;
import annotation.PageElement;
import annotation.PageObject;
import annotation.generator.interfaces.PageCollector;
import java.util.List;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import lombok.RequiredArgsConstructor;
import model.Collector;
import model.Page;
import util.Logger;

@RequiredArgsConstructor
public class PageCollectorImpl implements PageCollector {

    private final RoundEnvironment roundEnv;
    private final Logger log;
    private final Collector collector;

    /**
     * Метод собирает все пейджы, которые проаннотированны PageObject'ом, собирая public поля находящиеся в них
     */
    @Override
    public List<Page> collectPages() {
        log.debug("Starting collectPages");
        List<Page> pages = this.roundEnv.getElementsAnnotatedWith(PageObject.class)
            .stream()
            .map(page -> {
                List<VariableElement> fields = ElementFilter.fieldsIn(page.getEnclosedElements());
                List<ExecutableElement> methods = ElementFilter.methodsIn(page.getEnclosedElements());
                checkCorrectFields(fields, page);
                return new Page(page.getSimpleName().toString(), fields, methods);
            }).toList();
        validate(pages, PageObject.class);
        collector.setPages(pages);
        log.debug("Finished collectPages");
        return pages;
    }

    private void checkCorrectFields(List<? extends javax.lang.model.element.Element> elements,
        javax.lang.model.element.Element page) {
        List<String> basePageElementFields = collector.getBaseScreenFields().stream()
            .map(field -> field.getSimpleName().toString())
            .toList();

        elements.forEach(field -> {
            boolean isNotBasePageField = !basePageElementFields.contains(field.getSimpleName().toString());

            if (isNotBasePageField && isNotAnnotated(field, PageElement.class)) {
                throw new RuntimeException(String.format("Поле %s в классе %s должно быть c аннотацией PageElement",
                    field, page.getSimpleName()));
            }
            if (isNotBasePageField && field.getAnnotation(PageElement.class).value().isEmpty()) {
                throw new RuntimeException(
                    String.format("Поле %s в классе %s в аннотации PageElement должно иметь не пустое значение",
                        field, page.getSimpleName()));
            }
        });
    }
}
