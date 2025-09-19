package test.page;

import annotation.PageElement;
import annotation.PageObject;
import com.microsoft.playwright.Locator;

@PageObject
public class DeviceWidget  {

    @PageElement("девайсы")
    protected Locator deviceButton;

}
