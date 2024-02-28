package test.model;

import static com.codeborne.selenide.Condition.visible;
import annotation.Action;
import annotation.MobileElement;
import com.codeborne.selenide.appium.SelenideAppiumElement;

@MobileElement
public class Label extends BaseElement {

    public Label(SelenideAppiumElement element) {
        super(element);
    }

    @Action(action = "checkvisible")
    public void checkVisible() {
        element.should(visible);
    }

}
