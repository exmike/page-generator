package test.model;

import com.codeborne.selenide.appium.SelenideAppiumElement;

public class BaseElement {

    protected SelenideAppiumElement element;

    protected BaseElement(SelenideAppiumElement element) {
        this.element = element;
    }

}
