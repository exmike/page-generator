package test.model;

import static com.codeborne.selenide.Condition.text;
import annotation.Action;
import annotation.BaseMobileElement;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import java.util.List;

@BaseMobileElement
public class BaseElement {

    protected SelenideAppiumElement element;

    protected BaseElement(SelenideAppiumElement element) {
        this.element = element;
    }

    @Action(action = "Проверяем not visible")
    public BaseElement checkNotVisible() {
        element.shouldNotBe(Condition.visible);
        return this;
    }

    @Action(action = "checkExist")
    public BaseElement checkExist(List<Object> objects) {
        element.shouldBe(Condition.exist);
        return this;
    }

    @Action(action = "checkTextMoreParams")
    public BaseElement checkText(String text, String text1, String text2) {
        element.shouldHave(text(text));
        return this;
    }

    @Action(action = "checkTextTypeParams")
    public <T, P> BaseElement checkText(T text, P kek) {
        element.shouldHave(text(text.toString()));
        return this;
    }

    @Action(action = "checkTextBogdan")
    public <Bogdan> BaseElement checkText(Bogdan text) {
        element.shouldHave(text(text.toString()));
        return this;
    }

}
