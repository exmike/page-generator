package test.page;

import annotation.PageElement;
import annotation.PageObject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

@PageObject
public class ErrorScreen {

    private Page page;

    public ErrorScreen(Page page) {
        this.page = page;
    }

//    @PageElement("Удалить")
//    protected Locator deleteButton = page.locator("");

    @PageElement("инфо")
    protected Locator infoLabel;

    @PageElement("Экран")
    protected Locator screenView;

    @PageElement("Лейбл")
    protected Locator kekLabel;
}
