package com.template.stepDefinitions;

import com.template.lib.PageFactory;
import com.template.lib.exceptions.AutotestError;
import com.template.lib.exceptions.PageException;
import com.template.lib.exceptions.PageInitializationException;
import com.template.lib.support.Environment;
import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import org.junit.Assert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.htmlelements.element.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nestor on 11.07.2017.
 */
public class GenericStepDefs {

    /**
     * Execute action with no parameters inside block element User|he keywords
     * are optional
     *
     * @param block path or name of the block
     * @param action title of the action to execute
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist in
     * specified block
     * @throws NoSuchElementException if block with given name couldn't be found
     */
    @And("com.template.pagefactory.userActionInBlockNoParams")
    public void userActionInBlockNoParams(String block, String action) throws PageInitializationException,
            NoSuchMethodException, NoSuchElementException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitleInBlock(block, action);
    }

    /**
     * Execute action with parameters taken from specified {@link DataTable}
     * inside block element User|he keywords are optional
     *
     * @param block path or name of the block
     * @param action title of the action to execute
     * @param dataTable table of parameters
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist in
     * specified block
     * @throws NoSuchElementException if block with given name couldn't be found
     */
    @And("com.template.pagefactory.userActionInBlockTableParam")
    public void userActionInBlockTableParam(String block, String action, DataTable dataTable) throws PageInitializationException, NoSuchMethodException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitleInBlock(block, action, dataTable);
    }

    /**
     * Execute action with one parameter inside block element User|he keywords
     * are optional
     *
     * @param block path or name of the block
     * @param action title of the action to execute
     * @param param parameter
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist in
     * specified block
     * @throws NoSuchElementException if block with given name couldn't be found
     */
    @And("com.template.pagefactory.userActionInBlockOneParam")
    public void userActionInBlockOneParam(String block, String action, String param) throws PageInitializationException, NoSuchMethodException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitleInBlock(block, action, param);
    }

    /**
     * Execute action with two parameters inside block element User|he keywords
     * are optional
     *
     * @param block path or name of the block
     * @param action title of the action to execute
     * @param param1 first parameter
     * @param param2 second parameter
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist in
     * specified block
     * @throws NoSuchElementException if block with given name couldn't be found
     */
    @And("com.template.pagefactory.userActionInBlockTwoParams")
    public void userActionInBlockTwoParams(String block, String action, String param1, String param2) throws PageInitializationException, NoSuchMethodException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitleInBlock(block, action, param1, param2);
    }

    /**
     * Find element inside given block. Element name itself is a parameter, and
     * defines type of the element to search for User|he keywords are optional
     *
     * @param block path or name of the block
     * @param elementType type of the searched element. Could be one of Yandex
     * element types types
     * @param elementTitle title of the element to search
     * @throws PageException if current page is not initialized, or element
     * wasn't found
     */
    @And("com.template.pagefactory.findElementInBlock")
    public void findElementInBlock(String block, String elementType, String elementTitle) throws PageException {
        Class<? extends WebElement> clazz;
        switch (elementType) {
            case "element":
            case "элемент":
                clazz = WebElement.class;
                break;
            case "textinput":
            case "текстовое поле":
                clazz = TextInput.class;
                break;
            case "checkbox":
            case "чекбокс":
                clazz = CheckBox.class;
                break;
            case "radiobutton":
            case "радиобатон":
                clazz = Radio.class;
                break;
            case "table":
            case "таблицу":
                clazz = Table.class;
                break;
            case "header":
            case "заголовок":
                clazz = TextBlock.class;
                break;
            case "button":
            case "кнопку":
                clazz = Button.class;
                break;
            case "link":
            case "ссылку":
                clazz = Link.class;
                break;
            case "image":
            case "изображение":
                clazz = Image.class;
                break;
            default:
                clazz = WebElement.class;
        }
        PageFactory.getInstance().getCurrentPage().findElementInBlockByTitle(block, elementTitle, clazz);
    }

    /**
     * Find element with given value in specified list User|he keywords are
     * optional
     *
     * @param listTitle title of the list to search for
     * @param value required value of the element. for text elements value is
     * being checked via getText() method
     * @throws PageException if page wasn't initialized of required list wasn't
     * found
     */
    @And("com.template.pagefactory.findElementInList")
    public void findElementInList(String listTitle, String value) throws PageException {
        boolean found = false;
        for (WebElement webElement : PageFactory.getInstance().getCurrentPage().findListOfElements(listTitle)) {
            if (webElement.getText().equals(value)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new AutotestError(String.format("com.template.pagefactory.Element with text '%s' is absent in list '%s'", value, listTitle));
        }
    }

    /**
     * Initialize a page with corresponding title (defined via
     * User|he keywords are optional
     *
     * @param title of the page to initialize
     * @throws PageInitializationException if page initialization failed
     */
    @And("com.template.pagefactory.openPage")
    public void openPage(String title) throws PageInitializationException {
        if (PageFactory.getEnvironment() != Environment.MOBILE
                && !PageFactory.getWebDriver().getWindowHandles().isEmpty()) {
            for (String windowHandle : PageFactory.getWebDriver().getWindowHandles()) {
                PageFactory.getWebDriver().switchTo().window(windowHandle);
            }
        }
        PageFactory.getInstance().getPage(title);
    }

    /**
     * Execute action with no parameters User|he keywords are optional
     *
     * @param action title of the action to execute
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist
     */
    @And("com.template.pagefactory.userActionNoParams")
    public void userActionNoParams(String action) throws PageInitializationException, NoSuchMethodException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitle(action);
    }

    /**
     * Execute action with one parameter User|he keywords are optional
     *
     * @param action title of the action to execute
     * @param param parameter
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist
     */
    @And("com.template.pagefactory.userActionOneParam")
    public void userActionOneParam(String action, String param) throws PageInitializationException, NoSuchMethodException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitle(action, param);
    }

    /**
     * Execute action with two parameters User|he keywords are optional
     *
     * @param action title of the action to execute
     * @param param1 first parameter
     * @param param2 second parameter
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist
     */
    @And("com.template.pagefactory.userActionTwoParams")
    public void userActionTwoParams(String action, String param1, String param2) throws PageInitializationException, NoSuchMethodException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitle(action, param1, param2);
    }

    /**
     * Execute action with three parameters User|he keywords are optional
     *
     * @param action title of the action to execute
     * @param param1 first parameter
     * @param param2 second patrameter
     * @param param3 third parameter
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist
     */
    @And("com.template.pagefactory.userActionThreeParams")
    public void userActionThreeParams(String action, String param1, String param2, String param3) throws PageInitializationException, NoSuchMethodException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitle(action, param1, param2, param3);
    }

    /**
     * Execute action with parameters from given {@link cucumber.api.DataTable}
     * User|he keywords are optional
     *
     * @param action title of the action to execute
     * @param dataTable table of parameters
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist
     */
    @And("com.template.pagefactory.userActionTableParam")
    public void userActionTableParam(String action, DataTable dataTable) throws PageInitializationException, NoSuchMethodException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitle(action, dataTable);
    }

    /**
     * Execute action with string parameter and {@link cucumber.api.DataTable}
     * User|he keywords are optional
     *
     * @param action title of the action to execute
     * @param param parameter
     * @param dataTable table of parameters
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist
     */
    @And("com.template.pagefactory.userDoActionWithObject")
    public void userDoActionWithObject(String action, String param, DataTable dataTable) throws PageInitializationException, NoSuchMethodException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitle(action, param, dataTable);
    }

    /**
     * Execute action with parameters taken from list User|he keywords are
     * optional
     *
     * @param action title of the action to execute
     * @param list parameters list
     * @throws PageInitializationException if current page is not initialized
     * @throws NoSuchMethodException if corresponding method doesn't exist
     */
    @And("com.template.pagefactory.userActionListParam")
    public void userActionListParam(String action, List<String> list) throws PageInitializationException, NoSuchMethodException {
        PageFactory.getInstance().getCurrentPage().executeMethodByTitle(action, list);
    }

    /**
     * Open a copy for current page in a new browser tab User|he keywords are
     * optional
     */
    @And("com.template.pagefactory.openCopyPage")
    public void openCopyPage() {
        String pageUrl = PageFactory.getWebDriver().getCurrentUrl();
        ((JavascriptExecutor) PageFactory.getWebDriver()).executeScript("com.template.pagefactory.window.open('" + pageUrl + "', '_blank')");
        List<String> tabs = new ArrayList<>(PageFactory.getWebDriver().getWindowHandles());
        PageFactory.getWebDriver().switchTo().window(tabs.get(tabs.size() - 1));
        Assert.assertEquals("com.template.pagefactory.Fails to open a new page. "
                + "URL is different from the expected: ", pageUrl, PageFactory.getWebDriver().getCurrentUrl());
    }

    /**
     * Switch to a neighbour browser tab
     */
    @And("com.template.pagefactory.switchesToNextTab")
    public void switchesToNextTab() {
        List<String> tabs = new ArrayList<>(PageFactory.getWebDriver().getWindowHandles());
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).equals(PageFactory.getWebDriver().getWindowHandle())) {
                PageFactory.getWebDriver().switchTo().window(tabs.get(i + 1));
            }
        }
    }

    /**
     * Check that current URL matches the inputted one
     *
     * @param url url for comparison
     */
    @And("com.template.pagefactory.urlMatches")
    public void urlMatches(String url) {
        Assert.assertEquals("com.template.pagefactory.URL is different from the expected: ", url, PageFactory.getWebDriver().getCurrentUrl());
    }

    /**
     * Close current browser tab and open a tab with given name
     *
     * @param title title of the page to open
     */
    @And("com.template.pagefactory.closingCurrentWin")
    public void closingCurrentWin(String title) {
        PageFactory.getWebDriver().close();
        for (String windowHandle : PageFactory.getWebDriver().getWindowHandles()) {
            PageFactory.getWebDriver().switchTo().window(windowHandle);
            if (PageFactory.getWebDriver().getTitle().equals(title)) {
                return;
            }
        }
        throw new AutotestError("Unable to return to the previously opened page: " + title);
    }

    /**
     * Return to previous location (via browser "back" button)
     */
    @And("com.template.pagefactory.backPage")
    public void backPage() {
        PageFactory.getWebDriver().navigate().back();
    }

    /**
     * Go to specified url
     *
     * @param url url to go to
     */
    @And("com.template.pagefactory.goToUrl")
    public void goToUrl(String url) {
        PageFactory.getWebDriver().get(url);
    }

    /**
     * Initialize a page with corresponding URL
     *
     * @param url value of the
     * @throws PageInitializationException if page with corresponding URL is
     * absent or couldn't be initialized
     */
    @And("com.template.pagefactory.goToPageByUrl")
    public void goToPageByUrl(String url) throws PageInitializationException {
        PageFactory.getInstance().changeUrlByTitle(url);
    }

    /**
     * Refresh browser page
     */
    @And("com.template.pagefactory.reInitPage")
    public void reInitPage() {
        PageFactory.getWebDriver().navigate().refresh();
    }
}
