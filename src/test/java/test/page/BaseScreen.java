package test.page;

import annotation.BasePageObject;
import annotation.PageElement;
import com.microsoft.playwright.Locator;

@BasePageObject
public abstract class BaseScreen {

    @PageElement("Назад")
    protected Locator backButton;

    @PageElement("Логин")
    protected Locator loginButton;

    @PageElement("вью")
    protected Locator infoView;

}
