package annotation.generator;

import static util.Utils.PACKAGE_NAME;
import annotation.AutoGenPage;
import annotation.BaseWidget;
import annotation.PageObject;
import annotation.Widget;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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
    public List<WidgetModel> collectWidgets() { //todo проверка единственного BaseWidget + что он просто есть
        List<WidgetModel> widgets = new ArrayList<>();
        roundEnv.getElementsAnnotatedWithAny(Set.of(Widget.class, BaseWidget.class))
            .forEach(widget -> {
                List<ExecutableElement> publicMethods = getPublicMethods(widget);
                if (!publicMethods.isEmpty() && !isAnnotated(widget, BaseWidget.class)) {
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

    /**
     * Метод для проверки есть ли на классе специфическая аннотация
     */
    private boolean isAnnotated(Element element, Class<?> clazz) {
        List<? extends AnnotationMirror> annotations = element.getAnnotationMirrors();
        for (AnnotationMirror annotation : annotations) {
            if (annotation.getAnnotationType().toString().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }

    /*
    К каждой page генерируется пачка методов на основе доступных Widget'ов
    NB!!! необходимо добавлять новые case если появляются новые Widget'ы
     */
    public void generateMethodsToPage(List<Page> pages) {
        pages.forEach(page -> {
            List<MethodSpec> methodSpecs = new ArrayList<>();
            page.getFields().forEach(field -> {
                processMethodSpecsByAction(getWidgetFromField(field), methodSpecs, field, page);
            });
        });
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
                .filter(field -> field.getModifiers().contains(Modifier.PUBLIC))
                .toList();
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
    Метод для получения типа виджета из филда пейджи
    */
    private String getWidgetFromField(VariableElement field) {
        return Arrays.stream(field.getSimpleName().toString().split("(?=[A-Z])"))
            .reduce((head, tail) -> tail)
            .get();
    }

    /*
    Метод для сохранения сгенерированных MethodSpec в каждый из объектов Page
     */
    private void processMethodSpecsByAction(String widgetType, List<MethodSpec> methodSpecs, VariableElement field,
        Page page) {

        WidgetModel widget = page.getWidgets().stream()
            .filter(currentWidget -> getWidgetType(currentWidget).equals(widgetType))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Widget type mismatch"));
        widget.getMethods().forEach(method -> {
            if (method.getParameters().isEmpty() && method.getTypeParameters().isEmpty()) {
                methodSpecs.add(specsCreator.getMethodSpecWithoutParams(method, field, page, widget).build());
                return;
            }
            if (!method.getTypeParameters().isEmpty()) {
                methodSpecs.add(specsCreator.getMethodSpecWithTypeParams(method, field, page, widget).build());
                return;
            }
            if (!method.getParameters().isEmpty()) {
                methodSpecs.add(specsCreator.getMethodSpecWithParams(method, field, page, widget).build());
            }
        });
        page.setMethodSpecs(methodSpecs);
    }

    /*
    Метод для получения типа виджета из пакета
    test.model.Button -> Button
     */
    private String getWidgetType(WidgetModel model) {
        return model.getType().toString()
            .substring(model.getType().toString().lastIndexOf(".") + 1);
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
        List<ExecutableElement> methods = ElementFilter.methodsIn(widget.getEnclosedElements());
        methods = methods.stream()
            .filter(method -> method.getModifiers().contains(Modifier.PUBLIC))
            .toList();
        log.debug(widget.getSimpleName() + " methods: " + methods);
        return methods;
    }

    /*
    Метод для добавления всех методов из BaseWidget к остальным Widget'ам
     */
    @Deprecated
    public void addBaseMethodsToEachWidget(Element baseElement, List<WidgetModel> widgets) {
        List<ExecutableElement> publicBaseElementMethods = getPublicMethods(baseElement);
        widgets.forEach(widget -> {
            List<ExecutableElement> widgetMethods = widget.getMethods();
            widgetMethods.addAll(publicBaseElementMethods);
            widget.setMethods(widgetMethods);
        });
    }

    /*
    Проверка наличия обязательной аннотации BaseWidget
     */
    @Deprecated
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

    @SneakyThrows
    public void generateScreenManager(ProcessingEnvironment environment) {
        List<MethodSpec> methodSpecs = this.roundEnv.getElementsAnnotatedWith(AutoGenPage.class).stream()
            .map(SpecsCreator::generateScreenMethods)
            .toList();

        TypeSpec screenManagerSpec = TypeSpec.classBuilder("ScreenManagerGen")
            .addModifiers(Modifier.PUBLIC)
            .addMethods(methodSpecs)
            .build();
        JavaFile.builder(PACKAGE_NAME, screenManagerSpec).build().writeTo(environment.getFiler());

    }

}
