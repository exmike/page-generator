package test.page;

import static com.codeborne.selenide.appium.ScreenObject.screen;
import annotation.PageElement;
import annotation.PageObject;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import io.appium.java_client.pagefactory.AndroidFindBy;

@PageObject
public class LoginScreen extends BaseScreen {

    private SelenideAppiumElement loginButton;

    @PageElement(value = "лайк")
    @AndroidFindBy(xpath = "xpath")
    private SelenideAppiumElement likeButton;

    @AndroidFindBy(xpath = "xpath")
    @PageElement(value = "тайтл")
    private SelenideAppiumElement titleLabel;

    @AndroidFindBy(xpath = "xpath")
    @PageElement(value = "лист")
    public ElementsCollection nextButtonElementsList;

    public DeviceWidget deviceWidget() {
        return screen(DeviceWidget.class);
    }

}
