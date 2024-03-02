package test.page;

import static com.codeborne.selenide.appium.ScreenObject.screen;
import annotation.PageElementGen;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import annotation.PageObject;
import io.appium.java_client.pagefactory.AndroidFindBy;

@PageObject
public class LoginScreen extends BaseScreen {

    private SelenideAppiumElement loginButton;

    @PageElementGen(value = "лайк")
    @AndroidFindBy(xpath = "xpath")
    private SelenideAppiumElement likeButton;

    @AndroidFindBy(xpath = "xpath")
    @PageElementGen(value = "тайтл")
    private SelenideAppiumElement titleLabel;

    public DeviceScreen deviceScreen() {
        return screen(DeviceScreen.class);
    }

}
