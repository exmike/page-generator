package annotation.generator;

import static util.Utils.PACKAGE_NAME;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
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
import javax.lang.model.element.TypeElement;
import lombok.SneakyThrows;
import model.Page;
import model.WidgetModel;
import util.Logger;

@SupportedAnnotationTypes("annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class PageProcessor extends AbstractProcessor {

    private int roundCount = 0;

    @Override
    @SneakyThrows
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundCount++;
        Logger log = new Logger(processingEnv.getMessager());
        PageGenerator pageGenerator = new PageGenerator(log, roundEnv, new SpecsCreator());

        if (roundCount == 2) {
            pageGenerator.generateScreenManager(processingEnv);
            return true;
        }
        if (roundCount > 2){
            return true;
        }
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

        //добавление всех методов из baseElement к остальным widget'am
        pageGenerator.addBaseMethodsToEachWidget(baseElement.get(), widgets);

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
    - Нужен механизм для генерации методов в определенные пейджи на основе полей из BaseScreen
    - Нужен механизм для возможности итераций по раундам (kek done)
    - Генерировать screenManager'a на основе уже сгенерированных классов (done)
    - Генерировать WidgetAction на основе доступных widget'ов в пакете
    - Генерить к каждому виджету методы из BaseElement (done)
    - Доработать механизм передачи параметров в сгенерированный метод(done)
    - В проекте избавиться от оверрайда в виджетах (done)
    - В проекте избавиться от реализаций методов верифай
    - Реализовать полноценный перенос всех методов и экстендов скринов
    - Реализовать полноценный перенос аннотаций с полей(done)
    - *Добавить @Step к сгенеренным методам
    - *Добавить логгирование аналогичное собранному значению в @Step'e
    - Помолиться
     */