package test.model;

import static com.codeborne.selenide.Condition.text;
import annotation.Action;
import annotation.BaseMobileElement;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import java.util.List;

@BaseMobileElement
public abstract class BaseElement {

    protected SelenideAppiumElement element;
    protected ElementsCollection collection;

    protected BaseElement(SelenideAppiumElement element) {
        this.element = element;
    }

    protected BaseElement(ElementsCollection collection) {
        this.collection = collection;
    }

    @Action("Проверяем, что <elementName> не отображается")
    public BaseElement checkNotVisible() {
        element.shouldNotBe(Condition.visible);
        return this;
    }

    @Action("Проверяем, что <elementName> отображается")
    public BaseElement checkExist(List<Object> objects) {
        element.shouldBe(Condition.exist);
        return this;
    }

    @Action("Проверяем, что <elementName> содержит текст")
    public BaseElement checkText(String text, String text1, String text2) {
        element.shouldHave(text(text));
        return this;
    }

    @Action("Проверяем, что <elementName> содержит тайп параметр")
    public <T, P> BaseElement checkText(T text, P kek) {
        element.shouldHave(text(text.toString()));
        return this;
    }

    @Action("Проверяем, что <elementName> содержит Богдана?")
    public <Bogdan> BaseElement checkText(Bogdan text) {
        element.shouldHave(text(text.toString()));
        return this;
    }

}
