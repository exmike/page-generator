package test.page;

import annotation.PageElement;
import annotation.PageObject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

@PageObject
public class DeviceWidget {

    private Page page;

    public DeviceWidget(Page page) {
        this.page = page;
    }

    @PageElement("девайсы")
    protected Locator deviceButton = page.locator("kek");

}
