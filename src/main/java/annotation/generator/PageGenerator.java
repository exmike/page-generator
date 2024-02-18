package annotation.generator;

import static enums.WidgetAction.getActionByClassName;
import static util.Utils.PACKAGE_NAME;
import annotation.BaseWidget;
import annotation.PageObject;
import annotation.Widget;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import enums.WidgetAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import lombok.AllArgsConstructor;
import model.Page;
import model.WidgetModel;
import util.Logger;

@AllArgsConstructor
public class PageGenerator {

    private Logger log;
    private RoundEnvironment roundEnv;
    private SpecsCreator specsCreator;

    /*
    Метод, который собирает все элементы проаннотированные Widget
     */
    public List<WidgetModel> collectWidgets() {
        List<WidgetModel> widgets = new ArrayList<>();

        for (Element widget : this.roundEnv.getElementsAnnotatedWith(Widget.class)) {

            List<ExecutableElement> methods = getPublicMethods(widget);
            if (!methods.isEmpty()) {
                widgets.add(
                    new WidgetModel(widget.asType(), methods, getActionByClassName(widget.getSimpleName().toString())));
            } else {
                log.warn("no public methods in ", widget.getSimpleName());
            }
        }
        return widgets;
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
            List<VariableElement> publicFields = fields.stream()
                .filter(field -> field.getModifiers().contains(Modifier.PUBLIC)).toList();
            if (!publicFields.isEmpty()) {
                publicFields.forEach(
                    variableElement -> log.error("Field %s in class %s must be private, not public ",
                        variableElement.toString(), page.getSimpleName()));
            }

            log.debug(page.getSimpleName() + " fields: " + fields);

            pages.add(new Page(page.getSimpleName().toString() + "Gen", fields, widgets));
        }
        return pages;
    }

    /*
    К каждой page генерируется пачка методов на основе доступных Widget'ов
     */
    public void generateMethodsToPage(List<Page> pages) {
        pages.forEach(page -> {
            List<MethodSpec> methodSpecs = new ArrayList<>();
            page.getFields().forEach(field -> {
                switch (WidgetAction.contains(field.getSimpleName().toString())) {
                    case BUTTON -> processMethodSpecsByAction(WidgetAction.BUTTON, methodSpecs, field, page);
                    case LABEL -> processMethodSpecsByAction(WidgetAction.LABEL, methodSpecs, field, page);
                }
            });
        });
    }

    private void processMethodSpecsByAction(WidgetAction action,
        List<MethodSpec> methodSpecs, VariableElement field, Page page) {
        WidgetModel widget = getWidgetByWidgetAction(page.getWidgets(), action);
        widget.getMethods().forEach(method -> methodSpecs.add(getMethodSpec(method, field, page, widget)));
        page.setMethodSpecs(methodSpecs);
    }

    private WidgetModel getWidgetByWidgetAction(List<WidgetModel> widgets, WidgetAction widgetAction) {
        return widgets.stream()
            .filter(widget -> widget.getWidgetAction() == widgetAction)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("getWidgetByWidgetAction"));
    }

    private MethodSpec getMethodSpec(ExecutableElement method, VariableElement field, Page page, WidgetModel widget) {
        return MethodSpec.methodBuilder(
                field.getSimpleName().toString() + "_" + method.getSimpleName().toString())
            .addModifiers(Modifier.PUBLIC)
            //todo потом реализовать добавление аннотации
//            .addAnnotation(AnnotationSpec.builder(Step.class)
//                .addMember("value", "$S",
//                    page.getPageName() + ". " + method.getSimpleName().toString() + " " + field.getAnnotation(
//                        PageElementGen.class).value())
//                .build())
            //todo
            .addStatement(
                "new $T(" + field.getSimpleName() + ")." + method.getSimpleName() + "()", widget.getType())

            .returns(ClassName.get(PACKAGE_NAME, page.getPageName()))
            .addStatement("return this")
            .build();
    }

    public List<TypeSpec> generateClasses(List<Page> pages) {
        List<TypeSpec> specs = new ArrayList<>();
        pages.forEach(page -> specs.add(specsCreator.getTypeSpecFromPage(page).build()));
        return specs;

    }

    private List<ExecutableElement> getPublicMethods(Element widget) {
        List<ExecutableElement> methods = ElementFilter.methodsIn(widget.getEnclosedElements());
        methods = methods.stream()
            .filter(method -> method.getModifiers().contains(Modifier.PUBLIC))
            .toList();
        log.debug(widget.getSimpleName() + " methods: " + methods);
        return methods;
    }

    public List<ExecutableElement> baseMethods(Element baseElement) {
        return getPublicMethods(baseElement);
    }

    public Optional<Element> checkBaseWidget() {
        List<? extends Element> baseElements = this.roundEnv.getElementsAnnotatedWith(BaseWidget.class).stream()
            .toList();
        if (baseElements.isEmpty()) {
            throw new RuntimeException("Can't generate no BaseWidget found ");
        }
        if (baseElements.size() > 1) {
            throw new RuntimeException("Can't generate BaseWidget need only 1");
        }
        return Optional.of(baseElements.get(0));
    }


}
