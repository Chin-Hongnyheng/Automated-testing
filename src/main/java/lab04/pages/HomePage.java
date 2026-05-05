package lab04.pages;

import com.microsoft.playwright.Page;

public class HomePage {

    protected Page page;

    public HomePage(Page page) {
        this.page = page;
    }
}