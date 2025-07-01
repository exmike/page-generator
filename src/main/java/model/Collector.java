package model;

import static util.Utils.WHITESPACE;
import static util.Utils.getElementTypeName;
import java.util.List;
import javax.lang.model.element.VariableElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Collector {

    private static final Collector INSTANCE = new Collector();

    private List<Element> element;
    private List<Page> pages;
    private List<VariableElement> baseScreenFields;

    private Collector() {

    }

    public String getStringElements() {
        StringBuilder sb = new StringBuilder();
        for (Element element : this.element) {
            sb.append(getElementTypeName(element)).append(WHITESPACE);
        }
        return sb.toString();
    }

    public static Collector getInstance() {
        return INSTANCE;
    }
}
