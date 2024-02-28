package model;

import static util.Utils.WHITESPACE;
import static util.Utils.getMobileElementTypeName;
import com.squareup.javapoet.MethodSpec;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.VariableElement;
import lombok.Data;

@Data
public class Page {

    private String pageName;
    private List<MobileElementModel> mobileElements;
    private List<VariableElement> fields;
    private List<MethodSpec> methodSpecs;

    public Page(String pageName, List<VariableElement> fields, List<MobileElementModel> mobileElements) {
        this.pageName = pageName;
        this.fields = fields;
        this.mobileElements = mobileElements;
        this.methodSpecs = new ArrayList<>();
    }

    public String getStringMobileElements() {
        StringBuilder sb = new StringBuilder();
        for (MobileElementModel element : this.mobileElements) {
            sb.append(getMobileElementTypeName(element)).append(WHITESPACE);
        }
        return sb.toString();
    }

    public void addSpec(MethodSpec methodSpec) {
        this.methodSpecs.add(methodSpec);
    }
}
