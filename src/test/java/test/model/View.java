package test.model;

import annotation.Element;
import com.microsoft.playwright.Locator;

@Element("экран")
public class View extends BaseElement {

    public View(Locator element) {
        super(element);
    }
}
