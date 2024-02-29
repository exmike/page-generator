package test.model;

import static com.codeborne.selenide.Condition.text;
import annotation.Action;
import annotation.BaseMobileElement;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import java.util.List;

@BaseMobileElement
public abstract class BaseElement {

    protected SelenideAppiumElement element;

    protected BaseElement(SelenideAppiumElement element) {
        this.element = element;
    }

    @Action(value = "Проверяем not visible")
    public BaseElement checkNotVisible() {
        element.shouldNotBe(Condition.visible);
        return this;
    }

    @Action(value = "checkExist")
    public BaseElement checkExist(List<Object> objects) {
        element.shouldBe(Condition.exist);
        return this;
    }

    @Action(value = "checkTextMoreParams")
    public BaseElement checkText(String text, String text1, String text2) {
        element.shouldHave(text(text));
        return this;
    }

    @Action(value = "checkTextTypeParams")
    public <T, P> BaseElement checkText(T text, P kek) {
        element.shouldHave(text(text.toString()));
        return this;
    }

    @Action(value = "checkTextBogdan")
    public <Bogdan> BaseElement checkText(Bogdan text) {
        element.shouldHave(text(text.toString()));
        return this;
    }

}
