package model;

import com.squareup.javapoet.MethodSpec;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import lombok.Data;

@Data
public class Page {

    private String pageName;
    private List<VariableElement> fields;
    private List<MethodSpec> generatedMethodSpecs;
    private List<ExecutableElement> methods;

    public Page(String pageName, List<VariableElement> fields, List<ExecutableElement> methods) {
        this.pageName = pageName;
        this.fields = fields;
        this.generatedMethodSpecs = new ArrayList<>();
        this.methods = methods;
    }

    public void addSpec(MethodSpec methodSpec) {
        this.generatedMethodSpecs.add(methodSpec);
    }
}
