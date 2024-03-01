package test.model;

import annotation.MobileElement;
import com.codeborne.selenide.appium.SelenideAppiumElement;

@MobileElement("лейбл")
public class Label extends BaseElement {

    public Label(SelenideAppiumElement element) {
        super(element);
    }

}
