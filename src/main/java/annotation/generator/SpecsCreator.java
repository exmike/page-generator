package annotation.generator;

import static util.Utils.PACKAGE_NAME;
import static util.Utils.WHITESPACE;
import annotation.Action;
import annotation.AutoGenPage;
import annotation.PageElementGen;
import com.codeborne.selenide.appium.ScreenObject;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import io.qameta.allure.Step;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import model.Page;
import model.MobileElementModel;
import org.apache.commons.lang3.StringUtils;
import util.Utils;

public class SpecsCreator {

    /*
    Собираем спеку для создания классов в которой хранится вся информация собранная ранее
     */
    public TypeSpec getTypeSpecFromPage(Page page) {
        return TypeSpec.classBuilder(page.getPageName())
            .addModifiers(Modifier.PUBLIC)
            .addFields(generateFieldsSpecByPage(page.getFields()))
            .addMethods(page.getMethodSpecs())
            .addAnnotation(AutoGenPage.class)
            .build();
    }

    /*
    Генерирует метод спеку без параметров
     */
    public MethodSpec.Builder getMethodSpecWithoutParams(ExecutableElement method, VariableElement field, Page page,
        MobileElementModel mobileElement) {
        return defaultMethodSpecBuilder(method, field, page)
            .addStatement(
                "new $T(" + field.getSimpleName() + ")." + method.getSimpleName() + "()", mobileElement.getType())
            .addStatement("return this");
    }

    /*
    Генерирует метод спеку с параметрами
     */
    public MethodSpec.Builder getMethodSpecWithParams(ExecutableElement method, VariableElement field, Page page,
        MobileElementModel mobileElement) {
        List<ParameterSpec> parameterSpecs = paramSpec(method);
        return defaultMethodSpecBuilder(method, field, page)
            .addParameters(parameterSpecs)
            .addStatement(
                "new $T(" + field.getSimpleName() + ")." + method.getSimpleName() +
                    "(" + Utils.formatParamListToString(parameterSpecs) + ")", mobileElement.getType())
            .addStatement("return this");
    }

    /*
    Генерирует метод спеку с <TYPE> параметрами
     */
    public MethodSpec.Builder getMethodSpecWithTypeParams(ExecutableElement method, VariableElement field, Page page,
        MobileElementModel mobileElement) {
        return getMethodSpecWithParams(method, field, page, mobileElement)
            .addTypeVariables(getTypeParamsFromMethod(method.getTypeParameters()));
    }


    private MethodSpec.Builder defaultMethodSpecBuilder(ExecutableElement method, VariableElement field, Page page) {
        return MethodSpec.methodBuilder(
                field.getSimpleName().toString() + "_" + method.getSimpleName().toString())
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(stepAnnotationSpec(method, field, page))
            .returns(ClassName.get(PACKAGE_NAME, page.getPageName()));
    }

    private List<TypeVariableName> getTypeParamsFromMethod(List<? extends TypeParameterElement> typeParameterElements) {
        return typeParameterElements.stream()
            .map(TypeVariableName::get)
            .toList();
    }

    /*
    из метода берем все параметры и делаем из них спеки
     */
    private List<ParameterSpec> paramSpec(ExecutableElement method) {
        return method.getParameters().stream()
            .map(ParameterSpec::get)
            .toList();
    }

    /*
    Собираем поля класса с типом SelenideAppiumElement и добавляем к ним все аннотации, которые были на исходных элементах
     */
    private List<FieldSpec> generateFieldsSpecByPage(List<VariableElement> pageFields) {
        return pageFields.stream()
            .map(this::generateFieldSpecFromField)
            .toList();
    }

    /*
    Собираем дефолтную спеку для создания полей
     */
    private FieldSpec generateFieldSpecFromField(VariableElement field) {
        return FieldSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString())
            .addModifiers(Modifier.PRIVATE)
            .addAnnotations(annotationSpecsFromElement(field))
            .build();
    }

    /*
    Собираем все аннотации с поля в List AnnotationSpec
     */
    private List<AnnotationSpec> annotationSpecsFromElement(Element element) {
        return element.getAnnotationMirrors()
            .stream()
            .map(AnnotationSpec::get)
            .toList();
    }

    /*
    Собирает Аннотацию @Step со значением 'pageName methodAction fieldValue'
     */
    private AnnotationSpec stepAnnotationSpec(ExecutableElement method, VariableElement field, Page page) {
        return AnnotationSpec.builder(Step.class)
            .addMember("value", "$S",
                page.getPageName() + WHITESPACE + method.getAnnotation(Action.class).value() +
                    WHITESPACE + field.getAnnotation(PageElementGen.class).value())
            .build();
    }

    /*
    Генерирует методы для инизиализации скринов сгенерированных ранее
     */
    public MethodSpec generateScreenMethods(Element element) {
        return MethodSpec.methodBuilder(StringUtils.uncapitalize(element.getSimpleName().toString()))
            .addModifiers(Modifier.PUBLIC)
            .returns(ClassName.get(PACKAGE_NAME, element.getSimpleName().toString()))
            .addStatement("return $T.screen(" + element.getSimpleName().toString() + ".class)", ScreenObject.class)
            .build();
    }
}
