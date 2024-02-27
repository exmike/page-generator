package model;

import static util.Utils.getWidgetTypeName;
import com.squareup.javapoet.MethodSpec;
import java.util.ArrayList;
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
        this.methodSpecs = new ArrayList<>();
    }

    public String getStringWidgets() {
        StringBuilder sb = new StringBuilder();
        for (WidgetModel widget : this.widgets) {
            sb.append(getWidgetTypeName(widget)).append(" ");
        }
        return sb.toString();
    }

    public void addSpec(MethodSpec methodSpec) {
        this.methodSpecs.add(methodSpec);
    }

}
