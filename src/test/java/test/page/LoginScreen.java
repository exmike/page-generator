package test.page;

import static com.codeborne.selenide.appium.ScreenObject.screen;
import annotation.PageElementGen;
import annotation.PageObject;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.appium.SelenideAppiumElement;
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

    @AndroidFindBy(xpath = "xpath")
    @PageElementGen(value = "лист")
    public ElementsCollection nextButtonElementsList;

    public DeviceWidget deviceWidget() {
        return screen(DeviceWidget.class);
    }

}
