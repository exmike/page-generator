package test.page;

import static com.codeborne.selenide.appium.ScreenObject.screen;
import annotation.ScreenRouter;

@ScreenRouter
public class ScreenManager {

    public LoginPage loginPage() {
        return screen(LoginPage.class);
    }

    public ErrorPage errorPage() {
        return screen(ErrorPage.class);
    }
}
