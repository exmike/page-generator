package annotation.generator.interfaces;

import java.util.List;
import javax.lang.model.element.VariableElement;

public interface BaseScreenFieldCollector {

    List<VariableElement> collectBaseScreenFields();

}
