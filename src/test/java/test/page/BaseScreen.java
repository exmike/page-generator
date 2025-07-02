package test.page;

import annotation.BasePageObject;
import annotation.PageElement;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.HowToUseLocators;
import io.appium.java_client.pagefactory.LocatorGroupStrategy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;
import org.openqa.selenium.support.FindBy;

@BasePageObject
public abstract class BaseScreen {

    @PageElement("Назад")
    @HowToUseLocators(androidAutomation = LocatorGroupStrategy.ALL_POSSIBLE)
    @iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeNavigationBar/XCUIElementTypeButton")
    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Back']")
    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Navigate up']")
    private SelenideAppiumElement backButton;

    @PageElement("Логин")
    @HowToUseLocators(androidAutomation = LocatorGroupStrategy.ALL_POSSIBLE)
    @iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeNavigationBar/XCUIElementTypeButton")
    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Back']")
    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Navigate up']")
    private SelenideAppiumElement loginButton;

    @PageElement("вью")
    @FindBy(id = "view")
    private SelenideElement infoView;

}
