package ru.netology.domain.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.domain.data.DataHelper;
import ru.netology.mode.User;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private SelenideElement loginField = $("[data-test-id='login'] input");
    private SelenideElement passwordField = $("[data-test-id='password'] input");
    private SelenideElement loginButton = $("button.button");
    private SelenideElement errorMessage = $("[data-test-id='error-notification']");

    public LoginPage() {
        loginField.shouldBe(Condition.visible);
        passwordField.shouldBe(Condition.visible);
        loginButton.shouldBe(Condition.visible);
    }

    public VerificationPage validLogin(User info) {
        loginField.setValue(info.getLogin());
        //passwordField.setValue(info.getPassword());
        passwordField.setValue(DataHelper.getValidPass());
        loginButton.click();
        return new VerificationPage();
    }

    public VerificationPage validLogin(String login, String password) {
        loginField.setValue(login);
        passwordField.setValue(password);
        loginButton.click();
        return new VerificationPage();
    }

    public LoginPage invalidLogin(String login, String password) {
        loginField.sendKeys(Keys.LEFT_CONTROL + "A");
        loginField.sendKeys(Keys.BACK_SPACE);
        loginField.setValue(login);
        passwordField.sendKeys(Keys.LEFT_CONTROL + "A");
        passwordField.sendKeys(Keys.BACK_SPACE);
        passwordField.setValue(password);
        loginButton.click();

        errorMessage.shouldBe(Condition.visible);
        errorMessage.find("button").click();
        return new LoginPage();
    }
}
