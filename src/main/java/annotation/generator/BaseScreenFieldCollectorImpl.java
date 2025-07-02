package annotation.generator;

import annotation.BasePageObject;
import annotation.generator.interfaces.BaseScreenFieldCollector;
import java.util.List;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import lombok.RequiredArgsConstructor;
import model.Collector;
import util.Logger;

@RequiredArgsConstructor
public class BaseScreenFieldCollectorImpl implements BaseScreenFieldCollector {

    private final RoundEnvironment roundEnv;
    private final Logger log;
    private final Collector collector;

    /*
    Собираем поля из BaseScreen
     */
    @Override
    public List<VariableElement> collectBaseScreenFields() {
        log.debug("Starting collectBaseScreenFields");

        List<VariableElement> fields = roundEnv.getElementsAnnotatedWith(BasePageObject.class)
            .stream()
            .flatMap(baseScreenFields -> ElementFilter.fieldsIn(baseScreenFields.getEnclosedElements()).stream())
            .toList();

        log.debug("Finished collectBaseScreenFields");
        collector.setBaseScreenFields(fields);
        return fields;
    }

}