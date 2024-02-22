package test.model;

import static com.codeborne.selenide.Condition.text;
import annotation.BaseWidget;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import java.util.List;

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

    public BaseElement checkExist(List<Object> objects) {
        element.shouldBe(Condition.exist);
        return this;
    }

    public BaseElement checkText(String text, String text1, String text2) {
        element.shouldHave(text(text));
        System.out.println(text1);
        return this;
    }

    public <T, P> BaseElement checkText(T text, P kek) {
        element.shouldHave(text(text.toString()));
        return this;
    }

}
