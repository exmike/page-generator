package test.model;

import annotation.BaseWidget;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.appium.SelenideAppiumElement;

@BaseWidget
public class BaseElement {

    protected SelenideAppiumElement element;

    protected BaseElement(SelenideAppiumElement element) {
        this.element = element;
    }

    public BaseElement checkNotVisible() {
        element.shouldNotBe(Condition.visible);
        return this;
    }

    public BaseElement checkExist() {
        element.shouldBe(Condition.exist);
        return this;
    }

}
