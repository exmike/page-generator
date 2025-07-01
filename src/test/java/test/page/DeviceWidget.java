package test.page;

import annotation.PageElement;
import annotation.PageObject;
import com.codeborne.selenide.appium.SelenideAppiumElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

@PageObject
public class DeviceWidget {

    @PageElement(value = "девайсы", timeout = 10)
    @iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeNavigationBar/XCUIElementTypeButton")
    @AndroidFindBy(xpath = "//android.widget.ImageButton[@content-desc='Navigate up']")
    private SelenideAppiumElement deviceButton;

}
