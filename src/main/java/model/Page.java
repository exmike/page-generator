package model;

import com.squareup.javapoet.MethodSpec;
import java.util.List;
import javax.lang.model.element.VariableElement;
import lombok.Data;

@Data
public class Page {

    private String pageName;
    private List<WidgetModel> widgets;
    private List<VariableElement> fields;
    private List<MethodSpec> methodSpecs;

    public Page(String pageName, List<VariableElement> fields, List<WidgetModel> widgets) {
        this.pageName = pageName;
        this.fields = fields;
        this.widgets = widgets;
    }

}
