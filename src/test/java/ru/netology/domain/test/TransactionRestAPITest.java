package ru.netology.domain.test;

import org.junit.jupiter.api.*;
import ru.netology.domain.data.Card;
import ru.netology.domain.data.DataHelper;
import ru.netology.domain.data.RestAPIHelper;

public class TransactionRestAPITest {

    @BeforeEach
    public void resetSUT() {
        DataHelper.clearSUTData();
        DataHelper.resetSUTData();
    }

    @AfterAll
    public static void cleanDatabase() {
        DataHelper.clearSUTData();
    }

    @Test
    void shouldTransferFromCard1ToCard2Test() {
        RestAPIHelper page = new RestAPIHelper();
        var testUser = DataHelper.getAuthInfo();
        int sum = 100;
        String card1 = "5559 0000 0000 0001", card2 = "5559 0000 0000 0002";

        page.openLoginPage(testUser);
        String verifyCode = DataHelper.getVerificationCodeFor(testUser);
        String token = page.verificationCodePage(testUser, verifyCode);

        Card[] cardsBefore = page.checkBalance(token);
        int beforeBalanceCard1 = Integer.parseInt(cardsBefore[0].getBalance());
        int beforeBalanceCard2 = Integer.parseInt(cardsBefore[1].getBalance());

        page.makeTransaction(token, card1, card2, sum);

        Card[] cardsAfter = page.checkBalance(token);
        int afterBalanceCard1 = Integer.parseInt(cardsAfter[0].getBalance());
        int afterBalanceCard2 = Integer.parseInt(cardsAfter[1].getBalance());

        int expectedCard1 = beforeBalanceCard1 - sum;
        int expectedCard2 = beforeBalanceCard2 + sum;
        Assertions.assertEquals(expectedCard1, afterBalanceCard1);
        Assertions.assertEquals(expectedCard2, afterBalanceCard2);
    }

    @Test
    void shouldTransferFromCard2ToCard1Test() {
        RestAPIHelper page = new RestAPIHelper();
        var testUser = DataHelper.getAuthInfo();
        int sum = 100;
        String card1 = "5559 0000 0000 0001", card2 = "5559 0000 0000 0002";

        page.openLoginPage(testUser);
        String verifyCode = DataHelper.getVerificationCodeFor(testUser);
        String token = page.verificationCodePage(testUser, verifyCode);

        Card[] cardsBefore = page.checkBalance(token);
        int beforeBalanceCard1 = Integer.parseInt(cardsBefore[0].getBalance());
        int beforeBalanceCard2 = Integer.parseInt(cardsBefore[1].getBalance());

        page.makeTransaction(token, card2, card1, sum);

        Card[] cardsAfter = page.checkBalance(token);
        int afterBalanceCard1 = Integer.parseInt(cardsAfter[0].getBalance());
        int afterBalanceCard2 = Integer.parseInt(cardsAfter[1].getBalance());

        int expectedCard1 = beforeBalanceCard1 + sum;
        int expectedCard2 = beforeBalanceCard2 - sum;
        Assertions.assertEquals(expectedCard1, afterBalanceCard1);
        Assertions.assertEquals(expectedCard2, afterBalanceCard2);
    }

    @Test
    void shouldTransferLargeSumFromCard1ToCard2Test() {
        RestAPIHelper page = new RestAPIHelper();
        var testUser = DataHelper.getAuthInfo();
        int sum = 15000;
        String card1 = "5559 0000 0000 0001", card2 = "5559 0000 0000 0002";

        page.openLoginPage(testUser);
        String verifyCode = DataHelper.getVerificationCodeFor(testUser);
        String token = page.verificationCodePage(testUser, verifyCode);

        Card[] cardsBefore = page.checkBalance(token);
        int beforeBalanceCard1 = Integer.parseInt(cardsBefore[0].getBalance());
        int beforeBalanceCard2 = Integer.parseInt(cardsBefore[1].getBalance());

        page.makeTransaction(token, card1, card2, sum);

        Card[] cardsAfter = page.checkBalance(token);
        int afterBalanceCard1 = Integer.parseInt(cardsAfter[0].getBalance());
        int afterBalanceCard2 = Integer.parseInt(cardsAfter[1].getBalance());

        int expectedCard1 = beforeBalanceCard1 + sum;
        int expectedCard2 = beforeBalanceCard2 - sum;
        Assertions.assertEquals(expectedCard1, afterBalanceCard1);
        Assertions.assertEquals(expectedCard2, afterBalanceCard2);
    }

    @Test
    void shouldTransferNegativeAmountSumFromCard2ToCard1Test() {
        RestAPIHelper page = new RestAPIHelper();
        var testUser = DataHelper.getAuthInfo();
        int sum = -100;
        String card1 = "5559 0000 0000 0001", card2 = "5559 0000 0000 0002";

        page.openLoginPage(testUser);
        String verifyCode = DataHelper.getVerificationCodeFor(testUser);
        String token = page.verificationCodePage(testUser, verifyCode);

        Card[] cardsBefore = page.checkBalance(token);
        int beforeBalanceCard1 = Integer.parseInt(cardsBefore[0].getBalance());
        int beforeBalanceCard2 = Integer.parseInt(cardsBefore[1].getBalance());

        page.makeTransaction(token, card2, card1, sum);

        Card[] cardsAfter = page.checkBalance(token);
        int afterBalanceCard1 = Integer.parseInt(cardsAfter[0].getBalance());
        int afterBalanceCard2 = Integer.parseInt(cardsAfter[1].getBalance());

        int expectedCard1 = beforeBalanceCard1 + sum;
        int expectedCard2 = beforeBalanceCard2 - sum;
        Assertions.assertEquals(expectedCard1, afterBalanceCard1);
        Assertions.assertEquals(expectedCard2, afterBalanceCard2);
    }
}
