package annotation.generator;

import static util.Utils.PACKAGE_NAME;
import static util.Utils.getMobileElementNameFromField;
import static util.Utils.getMobileElementTypeName;
import static util.Utils.isAnnotated;
import annotation.Action;
import annotation.AutoGenPage;
import annotation.BaseMobileElement;
import annotation.MobileElement;
import annotation.PageElementGen;
import annotation.PageObject;
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
import model.MobileElementModel;
import model.Page;
import org.apache.commons.lang3.StringUtils;
import util.Logger;

@AllArgsConstructor
public class PageGenerator {

    private Logger log;
    private RoundEnvironment roundEnv;
    private SpecsCreator specsCreator;
    private ProcessingEnvironment processingEnvironment;

    /**
     * Метод, который собирает все элементы проаннотированные MobileElement
     */
    public List<MobileElementModel> collectMobileElements() {
        List<MobileElementModel> mobileElements = new ArrayList<>();

        long baseMobileElement = roundEnv.getElementsAnnotatedWith(BaseMobileElement.class).size();
        if (baseMobileElement != 1) {
            throw new RuntimeException(
                "Ожидается, что будет одна аннотация BaseMobileElement но их: " + baseMobileElement);
        }

        roundEnv.getElementsAnnotatedWithAny(Set.of(MobileElement.class, BaseMobileElement.class))
            .forEach(element -> {
                List<ExecutableElement> publicMethods = getPublicMethods(element);
                checkCorrectMethods(publicMethods);
                if (!isAnnotated(element, BaseMobileElement.class)) {
                    mobileElements.add(new MobileElementModel(element.asType(), new ArrayList<>(publicMethods)));
                } else {
                    mobileElements.forEach(mobileElement -> {
                        List<ExecutableElement> elementMethods = mobileElement.getMethods();
                        elementMethods.addAll(publicMethods);
                        mobileElement.setMethods(elementMethods);
                    });
                }
            });
        return mobileElements;
    }

    /*
    К каждой page генерируется пачка методов на основе доступных MobileElement'ов
     */
    public void generateMethodsToPage(List<Page> pages) {
        pages.forEach(page -> page.getFields().forEach(field -> processMethodSpecsByAction(field, page)));
    }

    /**
     * Метод собирает все пейджы, которые проаннотированны PageObject'ом, собирая public поля находящиеся в них
     */
    public List<Page> collectPages(List<MobileElementModel> mobileElements) {
        List<Page> pages = new ArrayList<>();

        for (Element page : this.roundEnv.getElementsAnnotatedWith(PageObject.class)) {
            List<VariableElement> fields = ElementFilter.fieldsIn(page.getEnclosedElements());
            checkCorrectFields(fields, page);

            log.debug(page.getSimpleName() + " поля: " + fields);
            pages.add(new Page(page.getSimpleName().toString() + "Gen", fields, mobileElements));
        }
        return pages;
    }

    private void checkCorrectFields(List<? extends Element> elements, Element page) {
        elements.forEach(field -> {
            if (!isAnnotated(field, PageElementGen.class)) {
                throw new RuntimeException(String.format("Поле %s в классе %s должно быть c аннотацией PageElement",
                    field, page.getSimpleName()));
            }

            if (field.getAnnotation(PageElementGen.class).value().isEmpty()) {
                throw new RuntimeException(
                    String.format("Поле %s в классе %s в аннотации PageElement должно иметь не пустое значение",
                        field, page.getSimpleName())
                );
            }
        });
    }

    private void checkCorrectMethods(List<? extends Element> elements) {
        elements.forEach(method -> {
            if (!isAnnotated(method, Action.class)) {
                throw new RuntimeException(
                    String.format("Метод с названием %s в классе %s должен быть с аннотацией Action",
                        method.getSimpleName(), method.getEnclosingElement().getSimpleName().toString()));
            }

            if (method.getAnnotation(Action.class).action().isEmpty()) {
                throw new RuntimeException(
                    String.format("Метод с названием %s в классе %s в аннотации Action должно иметь не пустое значение",
                        method.getSimpleName(), method.getEnclosingElement().getSimpleName().toString())
                );
            }
        });
    }

    /*
    Метод для сохранения сгенерированных MethodSpec в каждый из объектов Page
     */
    private void processMethodSpecsByAction(VariableElement field, Page page) {
        String elementTypeNameFromField = getMobileElementNameFromField(field);

        MobileElementModel element = page.getMobileElements().stream()
            .filter(currentElement -> StringUtils.containsIgnoreCase(elementTypeNameFromField,
                getMobileElementTypeName(currentElement)))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(
                String.format("У поля %s в классе %s не смогли определить тип, доступные типы: %s",
                    field.getSimpleName().toString(), page.getPageName(), page.getStringMobileElements())));

        //todo rework
        element.getMethods().forEach(method -> {
            if (method.getParameters().isEmpty() && method.getTypeParameters().isEmpty()) {
                page.addSpec(specsCreator.getMethodSpecWithoutParams(method, field, page, element).build());
                return;
            }
            if (!method.getTypeParameters().isEmpty()) {
                page.addSpec(specsCreator.getMethodSpecWithTypeParams(method, field, page, element).build());
                return;
            }
            if (!method.getParameters().isEmpty()) {
                page.addSpec(specsCreator.getMethodSpecWithParams(method, field, page, element).build());
            }
        });
    }

    /*
    Метод для генерации всех классов на основе собранных объектов Page
     */
    public List<TypeSpec> generateClasses(List<Page> pages) {
        return pages.stream()
            .map(specsCreator::getTypeSpecFromPage)
            .toList();
    }

    /*
    Получение всех публичных методов из виджета
     */
    private List<ExecutableElement> getPublicMethods(Element mobileElement) {
        return ElementFilter.methodsIn(mobileElement.getEnclosedElements())
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
