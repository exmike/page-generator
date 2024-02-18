package test.page;

import annotation.PageElementGen;
import annotation.PageObject;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.HowToUseLocators;
import io.appium.java_client.pagefactory.LocatorGroupStrategy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

@PageObject
public class ErrorPage {

    @PageElementGen("Удалить")
    @HowToUseLocators(androidAutomation = LocatorGroupStrategy.ALL_POSSIBLE)
    @iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeNavigationBar/XCUIElementTypeButton")
    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Back']")
    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Navigate up']")
    private SelenideAppiumElement deleteButton;

    @AndroidFindBy(xpath = "xpath")
    @PageElementGen("кекЛейбл")
    private SelenideAppiumElement kekLabel;
}
