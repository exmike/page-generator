package annotation.generator;

import com.google.auto.service.AutoService;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import util.Logger;

@SupportedAnnotationTypes("annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class PageProcessor extends AbstractProcessor {

    private static final int MAX_ROUNDS = 2;
    private int roundCount = 0;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundCount++;
        if (roundCount > MAX_ROUNDS) {
            return true;
        }
        Logger log = new Logger(processingEnv.getMessager());
        PageGenerator pageGenerator = new PageGenerator(log, roundEnv, new SpecsCreator(roundEnv), processingEnv);

        if (roundCount == 2) {
            pageGenerator.generateScreenManager();
            return true;
        }

        pageGenerator.generatePages();
        return true;
    }
}

    /*
    todo list
    - Написать юнит-тесты(вероятно мокито)
    - Нужен механизм для генерации методов в определенные пейджи на основе полей из BaseScreen
    - Нужен механизм для возможности итераций по раундам (kek done try to rework)
    - Подумать над скринами, которые инитятся внутри других скринов
    - *Добавить логгирование аналогичное собранному значению в @Step'e (доработать StepListener в проекте)
    - Помолиться
     */