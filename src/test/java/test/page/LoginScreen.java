package test.page;

import annotation.PageElement;
import annotation.PageObject;
import com.microsoft.playwright.Locator;

@PageObject
public class LoginScreen extends BaseScreen {

    @PageElement(value = "лайк")
    protected Locator likeButton;

    @PageElement(value = "тайтл")
    protected Locator titleLabel;

//    public DeviceWidget deviceWidget() {
//        return screen(DeviceWidget.class);
//    }

}
