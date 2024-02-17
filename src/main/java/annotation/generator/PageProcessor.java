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

    @Override
    @SneakyThrows
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Logger log = new Logger(processingEnv.getMessager());
        Creator creator = new Creator(log);

        List<WidgetModel> widgets = creator.collectWidgets(roundEnv);
        if (widgets.isEmpty()) {
            log.warn("No widgets found");
            return true;
        }

        List<Page> pages = creator.collectPages(roundEnv, widgets);
        if (pages.isEmpty()) {
            log.warn("No pages found");
            return true;
        }

        Optional<Element> manager = creator.checkManager(roundEnv);
        if (manager.isEmpty()){
            log.warn("No ScreenRouter found");
            return true;
        }

        List<TypeSpec> specs = creator.generateClasses(pages, manager.get());

        for (TypeSpec spec : specs) {
            JavaFile.builder(PACKAGE_NAME, spec).build().writeTo(processingEnv.getFiler());
        }
        return true;
    }
}
