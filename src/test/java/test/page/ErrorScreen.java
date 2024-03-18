package test.page;

import annotation.PageElement;
import annotation.PageObject;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.HowToUseLocators;
import io.appium.java_client.pagefactory.LocatorGroupStrategy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

@PageObject
public class ErrorScreen extends BaseScreen {

    @PageElement("Удалить")
    @HowToUseLocators(androidAutomation = LocatorGroupStrategy.ALL_POSSIBLE)
    @iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeNavigationBar/XCUIElementTypeButton")
    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Back']")
    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Navigate up']")
    private SelenideAppiumElement deleteButton;

    @PageElement("инфо")
    @AndroidFindBy(xpath = "xpath")
    private SelenideAppiumElement infoLabel;

    @PageElement("Экран")
    @iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeNavigationBar/XCUIElementTypeButton")
    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Navigate up']")
    private SelenideAppiumElement screenView;
}
