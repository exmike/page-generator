package test.model;

import annotation.Element;
import com.codeborne.selenide.SelenideElement;

@Element("экран")
public class View extends BaseElement {

    public View(SelenideElement element) {
        super(element);
    }
}
