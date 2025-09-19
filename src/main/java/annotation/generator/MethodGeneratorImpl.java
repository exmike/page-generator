package annotation.generator;

import annotation.generator.interfaces.MethodGenerator;
import com.squareup.javapoet.MethodSpec;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import lombok.RequiredArgsConstructor;
import model.Collector;
import model.Element;
import model.Page;
import util.Logger;
import util.Utils;

@RequiredArgsConstructor
public class MethodGeneratorImpl implements MethodGenerator {

    private final SpecsCreator specsCreator;
    private final Logger log;
    private final Collector collector;

    /*
    К каждой page генерируется пачка методов на основе доступных Element'ов
     */
    @Override
    public void generateMethodsToPage(List<Page> pages) {
        log.debug("Starting generateMethodsToPage");
        /*
        Генерация вложенных инициализаций пейджей
        фильтруем по методам, которые содержат в названии widget
         */
        pages.forEach(page -> {
            /*
            Генерация методов на основе доступных полей
             */
            page.getFields().forEach(field -> {
                log.debug(field.getSimpleName().toString() + "FIELDNAME");
                // Генерация методов на основе доступных полей
                generateMethodSpecToPage(field, page);
            });
        });
        log.debug("Finished generateMethodsToPage");
    }

    /*
    Метод для сохранения сгенерированных MethodSpec в каждый из объектов Page
     */
    private void generateMethodSpecToPage(VariableElement field, Page page) {
        Element element = findElementForField(field, page);
        log.debug(element.getMethods().toString() + "123456");
        element.getMethods().forEach(method -> {
            MethodSpec methodSpec = createMethodSpec(method, field, page, element);
            page.addSpec(methodSpec);
        });
    }

    private Element findElementForField(VariableElement field, Page page) {
        return collector.getElements().stream()
            .filter(el -> Utils.isFieldTypeCorrect(el, field))
            .findFirst()
            .orElseThrow(() -> createFieldTypeException(field, page));
    }

    private MethodSpec createMethodSpec(ExecutableElement method, VariableElement field, Page page, Element element) {
        if (method.getParameters().isEmpty() && method.getTypeParameters().isEmpty()) {
            return specsCreator.getMethodSpecWithoutParams(method, field, page, element).build();
        } else if (!method.getTypeParameters().isEmpty()) {
            return specsCreator.getMethodSpecWithTypeParams(method, field, page, element).build();
        } else if (!method.getParameters().isEmpty()) {
            return specsCreator.getMethodSpecWithParams(method, field, page, element).build();
        } else {
            throw new IllegalStateException("Unsupported method type: " + method);
        }
    }

    private RuntimeException createFieldTypeException(VariableElement field, Page page) {
        return new RuntimeException(
            String.format(
                "Не удалось определить тип поля %s в классе %s. Доступные типы: %s",
                field.getSimpleName(), page.getPageName(), collector.getStringElements()
            )
        );
    }
}
