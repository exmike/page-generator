package test.model;

import annotation.Action;
import annotation.MobileElement;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import java.time.Duration;

@MobileElement("лейбл")
public class Label extends BaseElement {

    public Label(SelenideAppiumElement element) {
        super(element);
    }


    @Action("Проверяем, что <elementName> не отображается")
    public BaseElement checkNotVisible(Duration duration) {
        element.shouldNotBe(Condition.visible);
        return this;
    }

}
