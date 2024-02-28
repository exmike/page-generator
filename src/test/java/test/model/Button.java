package test.model;

import annotation.Action;
import annotation.MobileElement;
import com.codeborne.selenide.appium.SelenideAppiumElement;

@MobileElement
public class Button extends BaseElement {

    public Button(SelenideAppiumElement element) {
        super(element);
    }

    @Action(action = "Нажимаем на кнопку")
    public void click() {
        element.click();
    }

    @Action(action = "Дважды нажимаем на кнопку")
    public void doubleClick() {
        element.doubleClick();
    }
}
