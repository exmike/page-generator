package model;

import static util.Utils.WHITESPACE;
import static util.Utils.getMobileElementTypeName;
import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Collector {

    private static final Collector INSTANCE = new Collector();

    private List<MobileElementModel> mobileElements;
    private List<Page> pages;
    private List<VariableElement> baseScreenFields;

    private Collector() {

    }

    public String getStringMobileElements() {
        StringBuilder sb = new StringBuilder();
        for (MobileElementModel element : this.mobileElements) {
            sb.append(getMobileElementTypeName(element)).append(WHITESPACE);
        }
        return sb.toString();
    }

    public static Collector getInstance() {
        return INSTANCE;
    }
}
