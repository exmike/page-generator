package test.model;

import annotation.Widget;
import com.codeborne.selenide.appium.SelenideAppiumElement;

@Widget
public class View extends BaseElement {

    public View(SelenideAppiumElement element) {
        super(element);
    }
}
