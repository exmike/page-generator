package test.page;

import annotation.PageObject;
import com.microsoft.playwright.Page;

@PageObject
public class LoginScreen {

    private Page page;

    public LoginScreen(Page page){
        this.page = page;
    }

//    @PageElement(value = "тайтл")
//    protected Locator titleLabel;
//
//    public DeviceWidget deviceWidget() {
//        return new DeviceWidget(page);
//    }

}
