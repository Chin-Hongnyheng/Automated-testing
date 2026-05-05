package com.lab03.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.WebDriver;

public class LoginPage extends HomePage {

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(id = "username")
    WebElement username;

    @FindBy(id = "password")
    WebElement password;

    @FindBy(id = "loginBtn")
    WebElement loginBtn;

    @FindBy(id = "msg")
    WebElement message;

    public void login(String user, String pass) {
        username.clear();
        password.clear();

        username.sendKeys(user);
        password.sendKeys(pass);

        loginBtn.click();
    }

    public String getMessage() {
        return message.getText();
    }
}