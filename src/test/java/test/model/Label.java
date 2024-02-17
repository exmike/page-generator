package test.model;

import static com.codeborne.selenide.Condition.visible;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import annotation.Widget;

@Widget
public class Label extends BaseElement {

    public Label(SelenideAppiumElement element) {
        super(element);
    }

    public void checkVisible() {
        element.should(visible);
    }

}
