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
import util.GeneratorConfig;
import util.Logger;

@SupportedAnnotationTypes("annotation.*")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@AutoService(Processor.class)
public class PageProcessor extends AbstractProcessor {

    private static final int MAX_ROUNDS = 2;
    private int roundCount = 0;

    @Override
    public Set<String> getSupportedOptions() {
        return GeneratorConfig.SUPPORTED_OPTIONS;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        roundCount++;
        if (roundCount > MAX_ROUNDS) {
            return true;
        }
        Logger log = new Logger(processingEnv.getMessager());
        GeneratorConfig config = GeneratorConfig.from(processingEnv);
        PageGenerator pageGenerator =
            new PageGenerator(log, roundEnv, new SpecsCreator(config), processingEnv, config);

        switch (roundCount) {
            /*
            В первом раунде происходит генерация новых классов
             */
            case 1 -> pageGenerator.generatePages();
            /*
            Во втором раунде генерируется ScreenManager на основе ранее сгенерированных классов
             */
            case 2 -> pageGenerator.generateScreenManager();
        }

        return true;
    }
}

    /*
    todo list
    - Написать юнит-тесты(вероятно мокито)
    - Помолиться
     */