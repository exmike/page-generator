package annotation.generator;

import annotation.generator.interfaces.ClassGenerator;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import lombok.RequiredArgsConstructor;
import model.Page;
import util.Logger;

@RequiredArgsConstructor
public class ClassGeneratorImpl implements ClassGenerator {

    private final SpecsCreator specsCreator;
    private final Logger log;

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
