package ru.netology.domain.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.val;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashboardPage {

    private SelenideElement heading = $("[data-test-id='dashboard']");
    private ElementsCollection cardLine = $$("body .list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";
    private final String searchAttribute = "data-test-id";
    private final String searchButton = "[data-test-id='action-deposit']";

    public DashboardPage() {
        heading.shouldBe(Condition.visible);
    }

    private int extractBalance(String text) {
        val start = text.indexOf(balanceStart);
        val finish = text.indexOf(balanceFinish);
        val value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    /*public int getIdAccountBalance(DataHelper.Card selectCard) {
        String text = cardLine.find(Condition.attribute(searchAttribute, selectCard.getId())).getText();
        return extractBalance(text);
    }

    public TransferPage replenishCard(DataHelper.Card selectTo) {
        cardLine.findBy(Condition.attribute(searchAttribute, selectTo.getId())).find(searchButton).click();
        return new TransferPage();
    }*/
}
