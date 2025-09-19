package test.model;

import annotation.Action;
import annotation.Element;
import com.microsoft.playwright.Locator;

@Element("кнопка")
public class Button extends BaseElement {

    public Button(Locator element) {
        super(element);
    }

    @Action("Нажимаем на <elementName>")
    public void click() {
        element.click();
    }

    @Action("Дважды Нажимаем на <elementName>")
    public void doubleClick() {
        element.dblclick();
    }
}
