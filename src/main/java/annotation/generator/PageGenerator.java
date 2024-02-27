package annotation.generator;

import static util.Utils.PACKAGE_NAME;
import static util.Utils.getWidgetNameFromField;
import static util.Utils.getWidgetTypeName;
import static util.Utils.isAnnotated;
import annotation.AutoGenPage;
import annotation.BaseWidget;
import annotation.PageObject;
import annotation.Widget;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import model.Page;
import model.WidgetModel;
import org.apache.commons.lang3.StringUtils;
import util.Logger;

@AllArgsConstructor
public class PageGenerator {

    private Logger log;
    private RoundEnvironment roundEnv;
    private SpecsCreator specsCreator;
    private ProcessingEnvironment processingEnvironment;

    /*
    Метод, который собирает все элементы проаннотированные Widget
     */
    public List<WidgetModel> collectWidgets() {
        List<WidgetModel> widgets = new ArrayList<>();

        long baseWidget = roundEnv.getElementsAnnotatedWith(BaseWidget.class).size();
        if (baseWidget != 1) {
            throw new RuntimeException("Ожидается, что будет одна аннотация BaseWidget но их: " + baseWidget);
        }

        roundEnv.getElementsAnnotatedWithAny(Set.of(Widget.class, BaseWidget.class))
            .forEach(widget -> {
                List<ExecutableElement> publicMethods = getPublicMethods(widget);
                if (!isAnnotated(widget, BaseWidget.class)) {
                    widgets.add(new WidgetModel(widget.asType(), new ArrayList<>(publicMethods)));
                } else {
                    widgets.forEach(allWidgets -> {
                        List<ExecutableElement> widgetMethods = allWidgets.getMethods();
                        widgetMethods.addAll(publicMethods);
                        allWidgets.setMethods(widgetMethods);
                    });
                }
            });
        return widgets;
    }

    /*
    К каждой page генерируется пачка методов на основе доступных Widget'ов
     */
    public void generateMethodsToPage(List<Page> pages) {
        pages.forEach(page -> page.getFields().forEach(field -> processMethodSpecsByAction(field, page)));
    }

    /**
     * Метод собирает все пейджы, которые проаннотированны PageObject'ом, собирая public поля находящиеся в них
     */
    public List<Page> collectPages(List<WidgetModel> widgets) {
        List<Page> pages = new ArrayList<>();

        for (Element page : this.roundEnv.getElementsAnnotatedWith(PageObject.class)) {
            List<VariableElement> fields = ElementFilter.fieldsIn(page.getEnclosedElements());
            //todo проверка что на филде есть аннотация PageElement и она не пустая
            //проверка что поля в PageObject'ах имеют модификатор private
            List<VariableElement> notPrivateField = fields.stream()
                .filter(field -> !field.getModifiers().contains(Modifier.PRIVATE))
                .toList();
            if (!notPrivateField.isEmpty()) {
                notPrivateField.forEach(
                    variableElement -> log.error("Поле %s в классе %s должно быть private, а не %s ",
                        variableElement.toString(), page.getSimpleName(), variableElement.getModifiers()));
            }

            log.debug(page.getSimpleName() + " поля: " + fields);

            pages.add(new Page(page.getSimpleName().toString() + "Gen", fields, widgets));
        }
        return pages;
    }

    /*
    Метод для сохранения сгенерированных MethodSpec в каждый из объектов Page
     */
    private void processMethodSpecsByAction(VariableElement field, Page page) {
        String widgetTypeNameFromField = getWidgetNameFromField(field);

        WidgetModel widget = page.getWidgets().stream()
            .filter(currentWidget -> StringUtils.containsIgnoreCase(widgetTypeNameFromField,
                getWidgetTypeName(currentWidget)))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                String.format("У поля %s в классе %s не смогли определить тип, доступные типы: %s",
                    field.getSimpleName().toString(), page.getPageName(), page.getStringWidgets())));

        //todo rework
        widget.getMethods().forEach(method -> {
            if (method.getParameters().isEmpty() && method.getTypeParameters().isEmpty()) {
                page.addSpec(specsCreator.getMethodSpecWithoutParams(method, field, page, widget).build());
                return;
            }
            if (!method.getTypeParameters().isEmpty()) {
                page.addSpec(specsCreator.getMethodSpecWithTypeParams(method, field, page, widget).build());
                return;
            }
            if (!method.getParameters().isEmpty()) {
                page.addSpec(specsCreator.getMethodSpecWithParams(method, field, page, widget).build());
            }
        });
    }

    /*
    Метод для генерации всех классов на основе собранных объектов Page
     */
    public List<TypeSpec> generateClasses(List<Page> pages) {
        // todo а почему нельзя методреференс где статика
        return pages.stream()
            .map(page -> specsCreator.getTypeSpecFromPage(page))
            .toList();
    }

    /*
    Получение всех публичных методов из виджета
     */
    private List<ExecutableElement> getPublicMethods(Element widget) {
        return ElementFilter.methodsIn(widget.getEnclosedElements())
            .stream()
            .filter(method -> method.getModifiers().contains(Modifier.PUBLIC))
            .toList();
    }

    @SneakyThrows
    public void generateScreenManager() {
        List<MethodSpec> methodSpecs = this.roundEnv.getElementsAnnotatedWith(AutoGenPage.class).stream()
            .map(SpecsCreator::generateScreenMethods)
            .toList();

        TypeSpec screenManagerSpec = TypeSpec.classBuilder("ScreenManagerGen")
            .addModifiers(Modifier.PUBLIC)
            .addMethods(methodSpecs)
            .build();
        JavaFile.builder(PACKAGE_NAME, screenManagerSpec).build().writeTo(this.processingEnvironment.getFiler());
    }
}
