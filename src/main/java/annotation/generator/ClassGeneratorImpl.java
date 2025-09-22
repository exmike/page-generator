package annotation.generator;

import annotation.generator.interfaces.ClassGenerator;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import model.Page;
import util.Logger;

public record ClassGeneratorImpl(SpecsCreator specsCreator, Logger log) implements ClassGenerator {

    /*
    Метод для генерации всех классов на основе собранных объектов Page
     */
    @Override
    public List<TypeSpec> generateClasses(List<Page> pages) {
        log.debug("Starting generateClasses");
        List<TypeSpec> typeSpecs = pages.stream()
            .map(specsCreator::getTypeSpecFromPage)
            .toList();
        log.debug("Finished generateClasses");
        return typeSpecs;
    }
}
