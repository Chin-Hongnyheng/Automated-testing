package lab04.pages;

import com.microsoft.playwright.Page;

public class LoginPage extends HomePage {

    public LoginPage(Page page) {
        super(page);
    }

    public void login(String user, String pass) {
        page.fill("#username", user);
        page.fill("#password", pass);
        page.click("#loginBtn");
    }

    public String getMessage() {
        return page.textContent("#msg");
    }
}