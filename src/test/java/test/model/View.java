package test.model;

import annotation.MobileElement;
import com.codeborne.selenide.appium.SelenideAppiumElement;

@MobileElement("экран")
public class View extends BaseElement {

    public View(SelenideAppiumElement element) {
        super(element);
    }
}
