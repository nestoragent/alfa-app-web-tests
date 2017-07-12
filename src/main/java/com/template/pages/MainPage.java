package com.template.pages;

import com.template.lib.Page;
import com.template.lib.PageFactory;
import com.template.lib.annotations.ElementTitle;
import com.template.lib.annotations.PageEntry;
import com.template.lib.extensions.DriverExtension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

/**
 * Created by nestor on 12.07.2017.
 */
@PageEntry(title = "Main")
public class MainPage extends Page {

    @FindBy(css = "span.uiSearchInput > input")
    @ElementTitle("search")
    public WebElement inputSearch;

    @FindBy(css = "form#searchBarClickRef button[type='submit']")
    @ElementTitle("submit search")
    public WebElement submitSearch;

    public MainPage() {
        PageFactory.initElements(
                new HtmlElementDecorator(new HtmlElementLocatorFactory(PageFactory.getDriver())), this);
    }

    public void search_item(String item) {
        fillField(inputSearch, item);
        clickWebElement(submitSearch);
        DriverExtension.waitForPageToLoad();
    }

}
