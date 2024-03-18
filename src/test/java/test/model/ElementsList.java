package test.model;

import annotation.Action;
import annotation.MobileElement;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import lombok.RequiredArgsConstructor;

@MobileElement("лист")
@RequiredArgsConstructor
public class ElementsList {

    private final ElementsCollection collection;

    @Action("Нажимаем в <elementName> на элемент с индексом: {index}")
    public ElementsList click(int index) {
        collection.shouldBe(CollectionCondition.sizeGreaterThanOrEqual(index))
            .get(index)
            .click();
        return this;
    }
}
