package annotation.generator;

import static util.Utils.PACKAGE_NAME;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import lombok.SneakyThrows;
import model.Page;
import model.WidgetModel;
import org.checkerframework.checker.units.qual.A;
import util.Logger;

@SupportedAnnotationTypes("annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class PageProcessor extends AbstractProcessor {

    @Override
    @SneakyThrows
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Logger log = new Logger(processingEnv.getMessager());
        PageGenerator pageGenerator = new PageGenerator(log, roundEnv, new SpecsCreator());
        log.warn(annotations.toString());
        //собрали доступные Widget'ы
        List<WidgetModel> widgets = pageGenerator.collectWidgets();
        if (widgets.isEmpty()) {
            log.warn("No widgets found");
            return true;
        }
        //получили baseWidget
        Optional<Element> baseElement = pageGenerator.checkBaseWidget();
        if (baseElement.isEmpty()) {
            log.warn("No Base widget found");
            return true;
        }

        //todo вынести и переделать, пока закостылил добавление всех методов из baseElement к остальным element
        List<ExecutableElement> executableElements = pageGenerator.baseMethods(baseElement.get());
        widgets.forEach(widgetModel -> {
            List<ExecutableElement> methods = new ArrayList<>(widgetModel.getMethods());
            methods.addAll(executableElements);
            widgetModel.setMethods(methods);
        });

        //собрали доступные PageObject'ы
        List<Page> pages = pageGenerator.collectPages(widgets);
        if (pages.isEmpty()) {
            log.warn("No pages found");
            return true;
        }
        //сгенерировали для каждой Page методы
        pageGenerator.generateMethodsToPage(pages);

        //сгенерировали классы на основе ранее сгенерированных pages
        List<TypeSpec> specs = pageGenerator.generateClasses(pages);

        //записали классы в filer
        for (TypeSpec spec : specs) {
            JavaFile.builder(PACKAGE_NAME, spec).build().writeTo(processingEnv.getFiler());
        }
        return true;
    }
}

    /*
    todo list
    - Генерить к каждому виджету методы из BaseElement (done)
    - Доработать механизм передачи параметров в сгенерированный метод
    - В проекте избавиться от оверрайда в виджетах
    - Реализовать полноценный перенос аннотаций с полей(done)
    - Добавить @Step к сгенеренным методам
    - Добавить логгирование аналогичное собранному значению в @Step'e
    - Помолиться

     */

