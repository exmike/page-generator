package test.model;

import annotation.Widget;
import com.codeborne.selenide.appium.SelenideAppiumElement;

@Widget
public class Button extends BaseElement {

    public Button(SelenideAppiumElement element) {
        super(element);
    }

    public void click() {
        element.click();
    }

    public void doubleClick() {
        element.doubleClick();
    }

    public void tripleClick() {
        element.doubleClick().click();
    }


}
