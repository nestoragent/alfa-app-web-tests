package com.template.lib.extensions;

import com.template.lib.PageFactory;
import com.template.lib.exceptions.WaitException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Created by nestor on 11.07.2017.
 */
@Slf4j
public class DriverExtension {
    /**
     * Wait until element present
     *
     * @param webElement Desired web element
     */
    public static void waitUntilElementPresent(WebElement webElement) {
        new WebDriverWait(PageFactory.getDriver(), PageFactory.getTimeOutInSeconds()).
                until(ExpectedConditions.visibilityOf(webElement));
    }

    /**
     * Wait until element present
     *
     * @param webElement Desired web element
     * @param timeout Timeout in seconds
     */
    public static void waitUntilElementPresent(WebElement webElement, int timeout) {
        new WebDriverWait(PageFactory.getDriver(), timeout).
                until(ExpectedConditions.visibilityOf(webElement));
    }

    /**
     * Wait until element present
     *
     * @param webElement Desired web element
     */
    public static void waitUntilElementToBeClickable(WebElement webElement) {
        new WebDriverWait(PageFactory.getDriver(), PageFactory.getTimeOutInSeconds()).
                until(ExpectedConditions.elementToBeClickable(webElement));
    }

    /**
     * Wait until element present
     *
     * @param webElement Desired web element
     * @param timeout Timeout in seconds
     */
    public static void waitUntilElementToBeClickable(WebElement webElement, int timeout) {
        new WebDriverWait(PageFactory.getDriver(), timeout).
                until(ExpectedConditions.elementToBeClickable(webElement));
    }

    /**
     * Wait until element is present and enable to check page prepare to work
     *
     * @param webElement Desired web element
     */
    public static void waitUntilPagePrepared(WebElement webElement) {
        try {
            new WebDriverWait(PageFactory.getDriver(), PageFactory.getTimeOutInSeconds() / 2).
                    until(ExpectedConditions.visibilityOf(webElement));
        } catch (Exception | AssertionError e) {
            log.debug("Element {} does not become visible after timeout", webElement, e);
            PageFactory.getDriver().navigate().refresh();
            log.debug("Page refreshed");
            new WebDriverWait(PageFactory.getDriver(), PageFactory.getTimeOutInSeconds()).
                    until(ExpectedConditions.visibilityOf(webElement));
        }
    }

    public static void waitForPageToLoad(boolean... stopRecursion) {
        long timeOutTime = System.currentTimeMillis() + PageFactory.getTimeOut();
        while (timeOutTime > System.currentTimeMillis()) {
            try {
                if ((((JavascriptExecutor) PageFactory.getDriver()).executeScript("return document.readyState").toString())
                        .equals("complete"))
                    return;
            } catch (Exception | AssertionError e) {
                log.error("Page doesn't become to ready state. Error message = " + e.getMessage());
//                Init.getDriver().navigate().refresh();
//                if ((stopRecursion.length == 0) || (stopRecursion.length > 0 && !stopRecursion[0]))
//                    waitForPageToLoad(true);
            }
        }
    }

    /**
     * Wait until element present
     *
     * @param by a {@link org.openqa.selenium.By} object.
     * @return return appeared WebElement
     */
    public static WebElement waitUntilElementAppearsInDom(By by) {
        new WebDriverWait(PageFactory.getDriver(), PageFactory.getTimeOutInSeconds())
                .until(ExpectedConditions.presenceOfElementLocated(by));

        return PageFactory.getDriver().findElement(by);
    }

    /**
     * Wait until element present
     *
     * @param by a {@link org.openqa.selenium.By} object.
     * @param timeout timeout in seconds
     * @return return appeared WebElement
     */
    public static WebElement waitUntilElementAppearsInDom(By by, long timeout) {
        new WebDriverWait(PageFactory.getDriver(), timeout)
                .until(ExpectedConditions.presenceOfElementLocated(by));

        return PageFactory.getDriver().findElement(by);
    }

    /**
     * Wait until element gone from dom
     *
     * @param timeout in milliseconds
     * @param webElement a {@link org.openqa.selenium.WebElement} object.
     */
    public static void waitUntilElementGoneFromDom(WebElement webElement, long timeout) {
        Long start = System.currentTimeMillis();
        while (System.currentTimeMillis() < start + timeout) {
            try {
                if (!webElement.isDisplayed()) {
                    return;
                }
            } catch (NoSuchElementException | StaleElementReferenceException e) {
                log.debug("There is no element {} in dom", webElement, e);
                return;
            }
            sleep(1);
        }
        throw new NoSuchElementException("Timed out after " + timeout + " milliseconds waiting for web element '" + webElement.toString() + "' gone from DOM");
    }

    /**
     *
     * @param element a {@link org.openqa.selenium.WebElement} object.
     */
    public static void waitUntilElementGetInvisible(WebElement element) {
        new WebDriverWait(PageFactory.getDriver(), PageFactory.getTimeOutInSeconds())
                .until(ExpectedConditions.not(ExpectedConditions.visibilityOf(element)));
    }

    /**
     * <p>
     * waitForElementGetEnabled.</p>
     *
     * @param webElement a {@link org.openqa.selenium.WebElement} object.
     * @param timeout a long.
     * @throws WaitException
     */
    public static void waitForElementGetEnabled(WebElement webElement, long timeout) throws WaitException {
        long timeoutTime = System.currentTimeMillis() + timeout;
        while (timeoutTime > System.currentTimeMillis()) {
            sleep(1);
            try {
                if (webElement.isEnabled()) {
                    return;
                }
            } catch (Exception e) {
                log.debug("Target element still not enable", e);
            }

        }
        throw new WaitException("Timed out after '" + timeout + "' milliseconds waiting for availability of '" + webElement + "'");
    }

    /**
     * Accept any alert regardless of its message
     *
     * @throws WaitException if alert didn't appear during timeout
     */
    public static void acceptAlert() throws WaitException {
        interactWithAlert("", true);
    }

    /**
     * Dismiss any alert regardless of its message
     *
     * @throws WaitException if alert didn't appear during timeout
     */
    public static void dismissAlert() throws WaitException {
        interactWithAlert("", false);
    }

    /**
     * Wait for an alert with corresponding text (if specified). Depending on the decision, either accept it or decline
     * If messageText is empty, text doesn't matter
     *
     * @param messageText text of an alert. If empty string is provided, it is being ignored
     * @param decision true - accept, false - dismiss
     * @throws WaitException in case if alert didn't appear during default wait timeout
     */
    public static void interactWithAlert(String messageText, boolean decision) throws WaitException {
        long timeoutTime = System.currentTimeMillis() + PageFactory.getTimeOut();

        while (timeoutTime > System.currentTimeMillis()) {
            try {
                Alert alert = PageFactory.getDriver().switchTo().alert();
                if (!messageText.isEmpty()) {
                    Assert.assertEquals(alert.getText(), messageText);
                }
                if (decision) {
                    alert.accept();
                } else {
                    alert.dismiss();
                }
                return;
            } catch (Exception e) {
                log.debug("Alert has not appeared yet", e);
            }
            sleep(1);
        }
        throw new WaitException("Timed out after '" + PageFactory.getTimeOutInSeconds() + "' seconds waiting for alert to accept");
    }



    /**
     *
     * @param text a {@link java.lang.String} object.
     * @param timeout a {int} object. wait text during sec period
     * @return true if exists
     */
    public static boolean checkElementWithTextIsPresent(String text, int timeout) {
        try {
            new WebDriverWait(PageFactory.getDriver(), timeout)
                    .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(., '" + text + "')]")));
            return true;
        } catch (TimeoutException e) {
            log.debug("Element with text {} is not located on page", text, e);
            return false;
        }
    }

    /**
     *
     * @param sec a int.
     */
    private static void sleep(int sec) {
        try {
            Thread.sleep(sec * 1000L);
        } catch (InterruptedException e) {
            log.warn("Error while thread is sleeping", e);
            Thread.currentThread().interrupt();
        }
    }
}