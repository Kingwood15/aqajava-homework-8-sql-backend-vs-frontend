package ru.netology.domain.test;

import org.junit.jupiter.api.*;
import ru.netology.domain.data.DataHelper;
import ru.netology.domain.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

public class AuthDbInteractinDbUtilsTest {

    @BeforeAll
    public static void resetSUT() {
        DataHelper.clearSUTData();
        DataHelper.resetSUTData();
    }

    @BeforeEach
    void shouldStart() {
        open("http://localhost:9999/");
    }

    @AfterAll
    public static void cleanDatabase() {
        DataHelper.clearSUTData();
    }

    @Test
    void shouldValidAuthForVasyaAccountTest() {
        String vasyaLogin = "vasya";
        String vasyaPass = "qwerty123";

        var loginPage = new LoginPage();
        var authInfoForVasya = DataHelper.getAuthInfo(vasyaLogin);
        var verificationPage = loginPage.validLogin(vasyaLogin, vasyaPass);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfoForVasya);
        verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldValidAuthForAddNewFakerAccountTest() {

        var loginPage = new LoginPage();
        var authIntoFaker = DataHelper.CreateUser();
        var verificationPage = loginPage.validLogin(authIntoFaker);
        var verificationCode = DataHelper.getVerificationCodeFor(authIntoFaker);
        verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldCheckThreeTimesInvalidPassInputForVasyaTest() {
        String vasyaLogin = "vasya";
        String vasyaPass = "qwerty123";
        String invalidPass1 = DataHelper.getRandPass();
        String invalidPass2 = DataHelper.getRandPass();
        String invalidPass3 = DataHelper.getRandPass();

        var loginPage = new LoginPage();

        loginPage.invalidLogin(vasyaLogin, invalidPass1);
        loginPage.invalidLogin(vasyaLogin, invalidPass2);
        loginPage.invalidLogin(vasyaLogin, invalidPass3);
        loginPage.invalidLogin(vasyaLogin, vasyaPass);
    }
}
