package annotation.generator;

import static enums.WidgetAction.getActionByClassName;
import static util.Utils.PACKAGE_NAME;
import static util.Utils.replaceGen;
import annotation.Generated;
import annotation.PageObject;
import annotation.ScreenRouter;
import annotation.Widget;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import enums.WidgetAction;
import io.qameta.allure.Step;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
public class Creator {

    private Logger log;

    public List<WidgetModel> collectWidgets(RoundEnvironment roundEnv) {
        List<WidgetModel> widgets = new ArrayList<>();

        for (Element widget : roundEnv.getElementsAnnotatedWith(Widget.class)) {
            log.debug("Found @Widget at " + widget);
            List<ExecutableElement> methods = getPublicMethods(widget);
            if (methods.isEmpty()) {
                log.warn("no public methods is ", widget.getSimpleName());
            } else {
                widgets.add(
                    new WidgetModel(widget.asType(), methods, getActionByClassName(widget.getSimpleName().toString())));
            }
        }
        return widgets;
    }

    public List<Page> collectPages(RoundEnvironment roundEnv, List<WidgetModel> widgets) {
        List<Page> pages = new ArrayList<>();

        for (Element page : roundEnv.getElementsAnnotatedWith(PageObject.class)) {
            List<VariableElement> fields = ElementFilter.fieldsIn(page.getEnclosedElements());
            log.debug(page.getSimpleName() + " fields: " + fields);
            pages.add(new Page(page.getSimpleName().toString() + "Gen", fields, widgets));
        }

        addMethodSpecsToPage(pages);

        return pages;
    }

    public void addMethodSpecsToPage(List<Page> pages) {
        pages.forEach(page -> {
            List<MethodSpec> methodSpecs = new ArrayList<>();
            List<WidgetModel> widgets = page.getWidgets();
            page.getFields().forEach(field -> {
                switch (WidgetAction.contains(field.getSimpleName().toString())) {
                    case BUTTON -> processMethodSpecsByAction(WidgetAction.BUTTON, widgets, methodSpecs, field, page);
                    case LABEL -> processMethodSpecsByAction(WidgetAction.LABEL, widgets, methodSpecs, field, page);
                }
            });
            page.setMethodSpecs(methodSpecs);
        });
    }

    private void processMethodSpecsByAction(WidgetAction action, List<WidgetModel> widgets,
        List<MethodSpec> methodSpecs,
        VariableElement field, Page page) {
        WidgetModel widget = getWidgetByWidgetAction(widgets, action);
        widget.getMethods().forEach(method -> methodSpecs.add(getMethodSpec(method, field, page, widget)));
    }

    public WidgetModel getWidgetByWidgetAction(List<WidgetModel> widgets, WidgetAction widgetAction) {
        return widgets.stream()
            .filter(widget -> widget.getWidgetAction() == widgetAction)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("getWidgetByWidgetAction"));

    }

    private MethodSpec getMethodSpec(ExecutableElement method, VariableElement field, Page page, WidgetModel widget) {
        return MethodSpec.methodBuilder(
                field.getSimpleName().toString() + "_" + method.getSimpleName().toString())
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(AnnotationSpec.builder(Step.class).addMember("value", "$S", "todo").build())
            .addStatement("new $T(" +replaceGen(page.getPageName()) + field.getSimpleName()+")." + method.getSimpleName() + "()", widget.getType())
            .returns(ClassName.get(PACKAGE_NAME, page.getPageName()))
            .addStatement("return this")
            .build();
    }

    public List<TypeSpec> generateClasses(List<Page> pages, Element manager) {
        List<TypeSpec> specs = new ArrayList<>();
        pages.forEach(page -> specs.add(
            TypeSpec.classBuilder(page.getPageName())
                .superclass(manager.asType())
                .addModifiers(Modifier.PUBLIC)
                .addMethods(page.getMethodSpecs())
                .addAnnotation(Generated.class)
                .build()));
        return specs;

    }

    private List<ExecutableElement> getPublicMethods(Element widget) {
        List<ExecutableElement> methods = ElementFilter.methodsIn(widget.getEnclosedElements());
        methods = methods.stream()
            .filter(method -> method.getModifiers().contains(Modifier.PUBLIC))
            .collect(Collectors.toList());
        log.debug(widget.getSimpleName() + " methods: " + methods);
        return methods;
    }

    public Optional<Element> checkManager(RoundEnvironment environment) {
        List<? extends Element> managers = environment.getElementsAnnotatedWith(ScreenRouter.class).stream().toList();
        if (managers.isEmpty()) {
            throw new RuntimeException("Can't generate no ScreenRouter found ");
        }
        if (managers.size() > 1) {
            throw new RuntimeException("Can't generate ScreenRouter need only 1");
        }
        return Optional.ofNullable(managers.get(0));
    }


}
