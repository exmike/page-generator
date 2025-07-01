package test.model;

import annotation.Action;
import annotation.Element;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import java.time.Duration;

@Element("лейбл")
public class Label extends BaseElement {

    public Label(SelenideElement element) {
        super(element);
    }

    @Action("Проверяем, что <elementName> не отображается")
    public BaseElement checkNotVisible(Duration duration) {
        element.shouldNotBe(Condition.visible);
        return this;
    }

}
