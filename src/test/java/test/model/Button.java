package test.model;

import annotation.Action;
import annotation.Element;
import com.codeborne.selenide.SelenideElement;

@Element("кнопка")
public class Button extends BaseElement {

    public Button(SelenideElement element) {
        super(element);
    }

    @Action("Нажимаем на <elementName>")
    public void click() {
        element.click();
    }

    @Action("Дважды Нажимаем на <elementName>")
    public void doubleClick() {
        element.doubleClick();
    }
}
