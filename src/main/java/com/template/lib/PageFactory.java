package com.template.lib;

import com.template.lib.drivers.MobileDriver;
import com.template.lib.drivers.NewWebDriver;
import com.template.lib.exceptions.FactoryRuntimeException;
import com.template.lib.support.Environment;
import com.template.lib.support.Props;
import io.appium.java_client.AppiumDriver;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nestor on 11.07.2017.
 */
@Slf4j
public class PageFactory {
    private static final Map<Class<? extends Page>, Map<Field, String>> PAGES_REPOSITORY = new HashMap<>();
    private static final String ENVIRONMENT = Props.get("driver.environment");
    private static final String PAGES_PACKAGE = Props.get("page.package");
    private static final String TIMEOUT = Props.get("page.load.timeout");
    private static final String ENVIRONMENT_WEB = "web";
    private static final String ENVIRONMENT_MOBILE = "mobile";
    private static Actions actions;
    private static PageWrapper PageWrapper;
    private static boolean aspectsDisabled = false;
    private static boolean isSharingProcessing = false;

    public static WebDriver getWebDriver() {
        return getDriver();
    }

    public static AppiumDriver getMobileDriver() {
        return (AppiumDriver) getDriver();
    }

    public static WebDriver getDriver() {
        switch (getEnvironment()) {
            case WEB:
                return NewWebDriver.getDriver();
            case MOBILE:
                return MobileDriver.getDriver();
            default:
                throw new FactoryRuntimeException("Failed to get driver");
        }
    }

    public static void dispose() {
        PageWrapper = null;
        switch (getEnvironment()) {
            case WEB:
                NewWebDriver.dispose();
                break;
            case MOBILE:
                MobileDriver.dispose();
                break;
            default:
                throw new FactoryRuntimeException("Failed to dispose");
        }
    }

    public static void initElements(WebDriver driver, Object page) {
        org.openqa.selenium.support.PageFactory.initElements(driver, page);
    }

    public static void initElements(FieldDecorator decorator, Object page) {
        org.openqa.selenium.support.PageFactory.initElements(decorator, page);
    }

    /**
     * Get PageFactory instance
     *
     * @return PageFactory
     */
    public static PageWrapper getInstance() {
        if (null == PageWrapper) {
            PageWrapper = new PageWrapper(PAGES_PACKAGE);
        }
        return PageWrapper;
    }

    /**
     * Get driver actions
     *
     * @return Actions
     */
    public static Actions getActions() {
        if (null == actions) {
            actions = new Actions(getWebDriver());
        }
        return actions;
    }

    /**
     * @return the pagesPackage
     */
    public static String getPagesPackage() {
        return PAGES_PACKAGE;
    }

    /**
     * @return the timeOut
     */
    public static int getTimeOut() {
        return Integer.parseInt(TIMEOUT);
    }

    /**
     * @return the timeOut
     */
    public static int getTimeOutInSeconds() {
        return Integer.parseInt(TIMEOUT) / 1000;
    }

    /**
     * @return the pageRepository
     */
    public static Map<Class<? extends Page>, Map<Field, String>> getPageRepository() {
        return PAGES_REPOSITORY;
    }

    /**
     * Affects click and sendKeys aspects only
     *
     * @return the aspectsDisabled default false
     */
    public static boolean isAspectsDisabled() {
        return aspectsDisabled;
    }

    /**
     * Affects click and sendKeys aspects only
     *
     * @param aAspectsDisabled default false
     */
    public static void setAspectsDisabled(boolean aAspectsDisabled) {
        aspectsDisabled = aAspectsDisabled;
    }


    public static Environment getEnvironment() {
        switch (ENVIRONMENT) {
            case ENVIRONMENT_WEB:
                return Environment.WEB;
            case ENVIRONMENT_MOBILE:
                return Environment.MOBILE;
            default:
                if (ENVIRONMENT.equals("")) {
                    throw new FactoryRuntimeException("Please add 'driver.environment = web' or 'driver.environment = mobile' to application.properties");
                } else {
                    throw new FactoryRuntimeException("Environment '" + ENVIRONMENT + "' is not supported");
                }
        }
    }

    /**
     * @return the isSharingProcessing
     */
    public static boolean isSharingProcessing() {
        return isSharingProcessing;
    }

    /**
     * @param aIsSharingProcessing the isSharingProcessing to set
     */
    public static void setSharingProcessing(boolean aIsSharingProcessing) {
        isSharingProcessing = aIsSharingProcessing;
    }
}

