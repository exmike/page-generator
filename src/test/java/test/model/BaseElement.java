package test.model;

import static com.codeborne.selenide.Condition.text;
import annotation.Action;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import java.time.Duration;
import java.util.List;

@annotation.BaseElement
public abstract class BaseElement {
    //комментарий
    protected SelenideElement element;

    protected BaseElement(SelenideElement element) {
        this.element = element;
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

    @Action("Ожидаем загрузки <elementName>")
    public BaseElement waitElement(Duration duration) {
        element.shouldBe(Condition.exist, duration);
        return this;
    }

}
