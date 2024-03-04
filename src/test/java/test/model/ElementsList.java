package test.model;

import annotation.MobileElement;
import com.codeborne.selenide.ElementsCollection;

@MobileElement("лист")
public class ElementsList extends BaseElement{

    public ElementsList(ElementsCollection element) {
        super(element);
    }
}
