package com.template.pages;

import com.template.lib.Page;
import com.template.lib.PageFactory;
import com.template.lib.annotations.ElementTitle;
import com.template.lib.annotations.PageEntry;
import com.template.lib.datajack.Stash;
import org.apache.commons.digester.StackAction;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

/**
 * Created by nestor on 12.07.2017.
 */
@PageEntry(title = "Login")
public class LoginPage extends Page {

    @FindBy(id = "email")
    @ElementTitle("email")
    public WebElement inputEmail;

    @FindBy(id = "pass")
    @ElementTitle("pass")
    public WebElement inputPass;

    @FindBy(id = "loginbutton")
    @ElementTitle("loginbutton")
    public WebElement buttonLogin;

    public LoginPage() {
        PageFactory.initElements(
                new HtmlElementDecorator(new HtmlElementLocatorFactory(PageFactory.getDriver())), this);
    }

    public void login_into_facebook() {
        fillField(inputEmail, Stash.getValue("login"));
        fillField(inputPass, Stash.getValue("password"));
        clickWebElement(buttonLogin);
    }

}
