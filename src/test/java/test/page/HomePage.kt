package test.page

import annotation.PageElement
import annotation.PageObject
import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page

@PageObject
open class HomePage(page: Page): BaseScreen() {

    @PageElement("Пополнить")
    protected val rechargeButton: Locator = page.locator("text=Пополнить")

}