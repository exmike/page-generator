package test.model;

import annotation.Action;
import annotation.MobileElement;
import com.codeborne.selenide.appium.SelenideAppiumElement;

@MobileElement("кнопка")
public class Button extends BaseElement {

    public Button(SelenideAppiumElement element) {
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
