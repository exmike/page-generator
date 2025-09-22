package test.page;

import annotation.PageElement;
import annotation.PageObject;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

@PageObject
public class LoginScreen {

    private Page page;

    public LoginScreen(Page page){
        this.page = page;
    }

    @PageElement(value = "лайк")
    protected Locator likeButton;

    @PageElement(value = "тайтл")
    protected Locator titleLabel;

}
