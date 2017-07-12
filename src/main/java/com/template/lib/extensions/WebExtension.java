package com.template.lib.extensions;

import com.template.lib.PageFactory;
import com.template.lib.exceptions.WaitException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.Set;

import static com.template.lib.extensions.DriverExtension.waitUntilElementAppearsInDom;

/**
 * Created by nestor on 11.07.2017.
 */
@Slf4j
public class WebExtension {
    /**
     * Get outer element text. Used for get text from checkboxes and radio
     * buttons
     *
     * @param webElement TODO
     * @return text of element
     */
    public static String getElementValue(WebElement webElement) throws IllegalArgumentException {
        String elementValue = "Cannot parse element";
        String elementId = webElement.getAttribute("id");

        if (elementId == null) {
            throw new IllegalArgumentException("Getting value is not support in element without id");
        }

        WebElement possibleTextMatcher = PageFactory.getWebDriver().findElement(By.xpath("//*[@id='" + elementId + "']/.."));
        if (possibleTextMatcher.getText().isEmpty()) {
            possibleTextMatcher = PageFactory.getWebDriver().findElement(By.xpath("//*[@id='" + elementId + "']/../.."));
            if ("tr".equals(possibleTextMatcher.getTagName())) {
                elementValue = possibleTextMatcher.getText();
            }
        } else {
            elementValue = possibleTextMatcher.getText();
        }
        return elementValue;
    }

    /**
     * Wait for page prepared with javascript
     *
     * @param stopRecursion TODO
     * @throws WaitException
     */
    public static void waitForPageToLoad(boolean... stopRecursion) throws WaitException {
        long timeoutTime = System.currentTimeMillis() + PageFactory.getTimeOut();
        while (timeoutTime > System.currentTimeMillis()) {
            try {
                if ("complete".equals((String) ((JavascriptExecutor) PageFactory.getWebDriver()).executeScript("return document.readyState"))) {
                    return;
                }
                sleep(1);
            } catch (Exception | AssertionError e) {
                log.debug("Page does not become to ready state", e);
                PageFactory.getWebDriver().navigate().refresh();
                log.debug("Page refreshed");
                if ((stopRecursion.length == 0) || (stopRecursion.length > 0 && !stopRecursion[0])) {
                    waitForPageToLoad(true);
                }
            }
        }

        throw new WaitException("Timed out after " + PageFactory.getTimeOutInSeconds() + " seconds waiting for preparedness of page");
    }

    /**
     * @param webElement a {@link org.openqa.selenium.WebElement} object.
     * @param timeout    in milliseconds
     * @throws WaitException
     */
    public static void waitForTextInInputExists(WebElement webElement, long timeout) throws WaitException {
        long timeoutTime = System.currentTimeMillis() + timeout;
        while (timeoutTime > System.currentTimeMillis()) {
            sleep(1);
            if (!webElement.getAttribute("value").isEmpty()) {
                return;
            }
        }
        throw new WaitException("Timed out after '" + timeout + "' milliseconds waiting for existence of '" + webElement + "'");
    }

    /**
     * Wait until specified text either appears, or disappears from page source
     *
     * @param text                text to search in page source
     * @param shouldTextBePresent boolean, self explanatory
     * @throws WaitException TODO
     */
    public static void waitForTextPresenceInPageSource(String text, boolean shouldTextBePresent) throws WaitException {
        long timeoutTime = System.currentTimeMillis() + PageFactory.getTimeOut();
        WebElement body = waitUntilElementAppearsInDom(By.tagName("body"));
        while (timeoutTime > System.currentTimeMillis()) {
            sleep(1);
            if (body.getText().replaceAll("\\s+", "").contains(text.replaceAll("\\s+", "")) == shouldTextBePresent) {
                return;
            }
        }
        throw new WaitException("Timed out after '" + PageFactory.getTimeOutInSeconds() + "' seconds waiting for presence of '" + text + "' in page source");
    }

    /**
     * @param existingHandles TODO
     * @param timeout         TODO
     * @return TODO
     * @throws WaitException TODO
     */
    public static String findNewWindowHandle(Set<String> existingHandles, int timeout) throws WaitException {
        long timeoutTime = System.currentTimeMillis() + timeout;

        while (timeoutTime > System.currentTimeMillis()) {
            Set<String> currentHandles = PageFactory.getWebDriver().getWindowHandles();

            if (currentHandles.size() != existingHandles.size()
                    || (currentHandles.size() == existingHandles.size() && !currentHandles.equals(existingHandles))) {
                for (String currentHandle : currentHandles) {
                    if (!existingHandles.contains(currentHandle)) {
                        return currentHandle;
                    }
                }
            }
            sleep(1);
        }

        throw new WaitException("Timed out after '" + timeout + "' milliseconds waiting for new modal window");
    }

    /**
     * @param existingHandles TODO
     * @return TODO
     * @throws WaitException TODO
     */
    public static String findNewWindowHandle(Set<String> existingHandles) throws WaitException {
        return findNewWindowHandle(existingHandles, PageFactory.getTimeOut());
    }

    /**
     * Turn on element highlight
     *
     * @param webElement TODO
     * @return initial element style
     */
    public static String highlightElementOn(WebElement webElement) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) PageFactory.getWebDriver();
            String style = (String) js.executeScript("return arguments[0].style.border", webElement);
            js.executeScript("arguments[0].style.border='3px solid red'", webElement);
            return style;
        } catch (Exception e) {
            log.warn("Something went wrong with element highlight", e);
            return null;
        }
    }

    /**
     * Turn off element highlight
     *
     * @param webElement TODO
     * @param style      element style to set
     */
    public static void highlightElementOff(WebElement webElement, String style) {
        if (style == null) {
            return;
        }
        try {
            JavascriptExecutor js = (JavascriptExecutor) PageFactory.getWebDriver();
            js.executeScript("arguments[0].style.border='" + style + "'", webElement);
        } catch (Exception e) {
            log.debug("Something went wrong with element highlight", e);
        }
    }

    /**
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
