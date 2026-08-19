package annotation.generator;

import annotation.generator.interfaces.ClassGenerator;
import annotation.generator.interfaces.ElementCollector;
import annotation.generator.interfaces.MethodGenerator;
import annotation.generator.interfaces.PageCollector;
import annotation.generator.interfaces.ScreenManagerGenerator;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import lombok.SneakyThrows;
import model.Collector;
import util.GeneratorConfig;
import util.Logger;

public class PageGenerator {

    private final Logger log;
    private final ProcessingEnvironment processingEnvironment;
    private final GeneratorConfig config;
    private final Collector collector = Collector.getInstance();

    private final ElementCollector elementCollector;
    private final PageCollector pageCollector;
    private final MethodGenerator methodGenerator;
    private final ClassGenerator classGenerator;
    private final ScreenManagerGenerator screenManagerGenerator;

    public PageGenerator(Logger log, RoundEnvironment roundEnv, SpecsCreator specsCreator,
        ProcessingEnvironment processingEnvironment, GeneratorConfig config) {
        this.log = log;
        this.processingEnvironment = processingEnvironment;
        this.config = config;

        this.elementCollector = new ElementCollectorImpl(roundEnv, log, collector, processingEnvironment, config);
        this.pageCollector = new PageCollectorImpl(roundEnv, log, collector, processingEnvironment);
        this.methodGenerator = new MethodGeneratorImpl(specsCreator, log, collector);
        this.classGenerator = new ClassGeneratorImpl(specsCreator, log);
        this.screenManagerGenerator = new ScreenManagerGeneratorImpl(roundEnv, specsCreator, processingEnvironment,
            log, config);
    }

    /*
    Основной метод для поэтапной генерации классов
     */
    public void generatePages() {
        log.info("Starting generatePages");
        //собрали доступные Element'ы
        elementCollector.collectElements();
        //собрали доступные PageObject'ы
        pageCollector.collectPages();
        //сгенерировали для каждой Page методы
        methodGenerator.generateMethodsToPage(collector.getPages());
        //сгенерировали классы на основе ранее сгенерированных pages и записал их в Filer
        classGenerator.generateClasses(collector.getPages())
            .forEach(this::writeClass);
        log.info("Finished generatePages");
    }

    public void generateScreenManager() {
        screenManagerGenerator.generateScreenManager();
    }

    @SneakyThrows
    private void writeClass(TypeSpec typeSpec) {
        log.debug("Writing class: " + typeSpec.name);
        JavaFile.builder(config.packageName(), typeSpec)
            .build()
            .writeTo(this.processingEnvironment.getFiler());
    }
}
