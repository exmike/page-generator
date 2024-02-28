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
import java.lang.annotation.Annotation;
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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import model.MobileElementModel;
import model.Page;
import org.apache.commons.lang3.StringUtils;
import util.Logger;

@RequiredArgsConstructor
public class PageGenerator {

    private final Logger log;
    private final RoundEnvironment roundEnv;
    private final SpecsCreator specsCreator;
    private final ProcessingEnvironment processingEnvironment;

    private List<MobileElementModel> mobileElements;
    private List<Page> pages;

    /**
     * Метод, который собирает все элементы проаннотированные MobileElement и к каждому MobileElement добавляет все
     * методы из BaseMobileElement
     */
    public PageGenerator collectMobileElements() {
        validateBaseMobileElement();
        List<MobileElementModel> mobileElements = new ArrayList<>();

        roundEnv.getElementsAnnotatedWithAny(Set.of(MobileElement.class, BaseMobileElement.class))
            .forEach(element -> {
                List<ExecutableElement> publicMethods = getPublicMethods(element);
                checkCorrectMethods(publicMethods);
                if (!isAnnotated(element, BaseMobileElement.class)) {
                    mobileElements.add(new MobileElementModel(element.asType(), new ArrayList<>(publicMethods)));
                } else {
                    mobileElements.forEach(mobileElement -> mobileElement.getMethods().addAll(publicMethods));
                }
            });
        validate(mobileElements, MobileElement.class);
        this.mobileElements = mobileElements;
        return this;
    }

    /*
    К каждой page генерируется пачка методов на основе доступных MobileElement'ов
     */
    public PageGenerator generateMethodsToPage() {
        this.pages.forEach(page -> page.getFields().forEach(field -> processMethodSpecsByAction(field, page)));
        return this;
    }

    /**
     * Метод собирает все пейджы, которые проаннотированны PageObject'ом, собирая public поля находящиеся в них
     */
    public PageGenerator collectPages() {
        List<Page> pages = this.roundEnv.getElementsAnnotatedWith(PageObject.class)
            .stream()
            .map(page -> {
                List<VariableElement> fields = ElementFilter.fieldsIn(page.getEnclosedElements());
                checkCorrectFields(fields, page);
                return new Page(page.getSimpleName().toString() + "Gen", fields, this.mobileElements);
            }).toList();
        validate(pages, PageObject.class);
        this.pages = pages;
        return this;
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
    public List<TypeSpec> generateClasses() {
        return this.pages.stream()
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
        List<MethodSpec> methodSpecs = this.roundEnv.getElementsAnnotatedWith(AutoGenPage.class)
            .stream()
            .map(specsCreator::generateScreenMethods)
            .toList();

        TypeSpec screenManagerSpec = TypeSpec.classBuilder("ScreenManagerGen")
            .addModifiers(Modifier.PUBLIC)
            .addMethods(methodSpecs)
            .build();
        JavaFile.builder(PACKAGE_NAME, screenManagerSpec).build().writeTo(this.processingEnvironment.getFiler());
    }

    /*
    Основной метод для поэтапной генерации классов
     */
    public void generatePages() {
        //собрали доступные MobileElement'ы
        this.collectMobileElements()
            //собрали доступные PageObject'ы
            .collectPages()
            //сгенерировали для каждой Page методы
            .generateMethodsToPage()
            //сгенерировали классы на основе ранее сгенерированных pages и записал их в Filer
            .generateClasses()
            .forEach(this::writeClass);
    }

    @SneakyThrows
    private void writeClass(TypeSpec typeSpec) {
        JavaFile.builder(PACKAGE_NAME, typeSpec).build().writeTo(this.processingEnvironment.getFiler());
    }

    private <T> void validate(List<T> elements, Class<? extends Annotation> annotation) {
        if (elements.isEmpty()) {
            throw new RuntimeException("Не нашли классов аннотированных " + annotation.getSimpleName());
        }
    }

    private void validateBaseMobileElement() {
        long baseMobileElement = roundEnv.getElementsAnnotatedWith(BaseMobileElement.class).size();
        if (baseMobileElement != 1) {
            throw new RuntimeException(
                "Ожидается, что будет одна аннотация BaseMobileElement но их: " + baseMobileElement);
        }
    }
}
