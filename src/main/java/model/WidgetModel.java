package model;

import enums.WidgetAction;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class WidgetModel {

    private TypeMirror type;
    private List<ExecutableElement> methods;
    private WidgetAction widgetAction;

}
