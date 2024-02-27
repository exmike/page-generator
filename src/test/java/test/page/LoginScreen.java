package test.page;

import annotation.PageElementGen;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import annotation.PageObject;
import io.appium.java_client.pagefactory.AndroidFindBy;

@PageObject
public class LoginScreen extends BaseScreen{

    @PageElementGen(value = "лайк")
    @AndroidFindBy(xpath = "xpath")
    private SelenideAppiumElement likeButton;

    @AndroidFindBy(xpath = "xpath")
    @PageElementGen(value = "тайтл")
    private SelenideAppiumElement titleLabel;

}
