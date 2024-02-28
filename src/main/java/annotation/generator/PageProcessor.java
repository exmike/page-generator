package annotation.generator;

import static util.Utils.PACKAGE_NAME;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import lombok.SneakyThrows;
import model.Page;
import model.MobileElementModel;
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
        PageGenerator pageGenerator = new PageGenerator(log, roundEnv, new SpecsCreator(), processingEnv);

        if (roundCount == 2) {
            pageGenerator.generateScreenManager();
            return true;
        }
        if (roundCount > 2) {
            return true;
        }

        //собрали доступные MobileElement'ы
        List<MobileElementModel> mobileElements = pageGenerator.collectMobileElements();
        if (mobileElements.isEmpty()) {
            throw new RuntimeException("Не нашли классов аннотированных MobileElement");
        }

        //собрали доступные PageObject'ы
        List<Page> pages = pageGenerator.collectPages(mobileElements);
        if (pages.isEmpty()) {
            throw new RuntimeException("Не нашли классов аннотированных PageObject");
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
    - Нужен механизм для возможности итераций по раундам (kek done try to rework)
    - Подумать над скринами, которые инитятся внутри других скринов
    - *Добавить логгирование аналогичное собранному значению в @Step'e (доработать StepListener в проекте)
    - Помолиться
     */