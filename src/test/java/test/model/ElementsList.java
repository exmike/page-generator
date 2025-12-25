package test.model;

import annotation.Action;
import annotation.Element;
import com.microsoft.playwright.Locator;
import lombok.RequiredArgsConstructor;

@Element("лист")
@RequiredArgsConstructor
public class ElementsList {

    private final Locator collection;

    @Action("Нажимаем в <elementName> на элемент с индексом: {index}")
    public ElementsList click(int index) {
        collection.nth(index);
        return this;
    }
//
//    @Action("Проверяем, что в <elementName> есть элементы")
//    public ElementsList waitElement(Duration duration) {
//        collection.shouldHave(CollectionCondition.sizeGreaterThanOrEqual(1), duration);
//        return this;
//    }

}
