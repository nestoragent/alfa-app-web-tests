package com.template.lib.drivers;

import com.template.lib.PageFactory;
import com.template.lib.exceptions.FactoryRuntimeException;
import com.template.lib.exceptions.UnsupportedBrowserException;
import com.template.lib.support.DesiredCapabilitiesParser;
import com.template.lib.support.Environment;
import com.template.lib.support.Props;
import com.template.lib.util.OsCheck;
import lombok.extern.slf4j.Slf4j;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.template.lib.PageFactory.getTimeOutInSeconds;

/**
 * Created by nestor on 11.07.2017.
 */
@Slf4j
public class NewWebDriver {

    private static final int WEBDRIVER_CREATE_ATTEMPTS = Integer.parseInt(Props.get("webdriver.create.attempts", "3"));
    private static final String WEBDRIVER_PATH = Props.get("webdriver.drivers.path");
    private static final String WEBDRIVER_URL = Props.get("webdriver.url");
    private static final String WEBDRIVER_PROXY = Props.get("webdriver.proxy");
    private static final String WEBDRIVER_BROWSER_NAME = Props.get("webdriver.browser.name").toLowerCase().equals("ie")
            // Normalize it for ie shorten name (ie)
            ? BrowserType.IE : Props.get("webdriver.browser.name").toLowerCase();
    private static final boolean IS_IE = WEBDRIVER_BROWSER_NAME.equals(BrowserType.IE.toLowerCase())
            || WEBDRIVER_BROWSER_NAME.equals(BrowserType.IE_HTA.toLowerCase())
            || WEBDRIVER_BROWSER_NAME.equals(BrowserType.IEXPLORE.toLowerCase());
    private static org.openqa.selenium.WebDriver webDriver;
    private static BrowserMobProxy proxy;

    public static WebDriver getDriver() {
        if (Environment.WEB != PageFactory.getEnvironment()) {
            throw new FactoryRuntimeException("Failed to get web driver while environment is not web");
        }

        if (null == webDriver) {
            for (int i = 1; i <= WEBDRIVER_CREATE_ATTEMPTS; i++) {
                log.info("Attempt #" + i + " to start web driver");
                try {
                    createDriver();
                    break;
                } catch (UnreachableBrowserException e) {
                    log.warn("Failed to create web driver. Attempt number {}", i, e);
                    if (null != webDriver) {
                        // Don't dispose when driver is already null, cuz it causes new driver creation at Init.getWebDriver()
                        dispose();
                    }
                } catch (UnsupportedBrowserException | MalformedURLException e) {
                    log.error("Failed to create web driver", e);
                    break;
                }
            }
        }
        return webDriver;
    }

    private static void createDriver() throws UnsupportedBrowserException, MalformedURLException {
        DesiredCapabilities capabilities = new DesiredCapabilitiesParser().parse();
        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE);
        File downloadDir = new File("download");
        OsCheck checkOS = new OsCheck();
        StringBuilder pathToDriver = new StringBuilder("src/test/resources/webdrivers/");

        //Local proxy available on local webdriver instances only
        if (!WEBDRIVER_PROXY.isEmpty()) {
            setProxy(new BrowserMobProxyServer());
            proxy.start(0);
            Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
            capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
        }
        capabilities.setBrowserName(WEBDRIVER_BROWSER_NAME);

        if (WEBDRIVER_BROWSER_NAME.equals(BrowserType.FIREFOX.toLowerCase())) {
            capabilities.setBrowserName("firefox");
            FirefoxProfile fProfile = new FirefoxProfile();
            fProfile.setAcceptUntrustedCertificates(true);
            fProfile.setPreference("browser.download.dir", downloadDir.getAbsolutePath());
            fProfile.setPreference("browser.download.folderList", 2);
            fProfile.setPreference("browser.download.manager.showWhenStarting", false);
            fProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
            fProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                    "application/zip;application/octet-stream;application/x-zip;application/x-zip-compressed");
            fProfile.setPreference("plugin.disable_full_page_plugin_for_types", "application/zip");

            capabilities.setJavascriptEnabled(true);
            capabilities.setCapability(FirefoxDriver.PROFILE, fProfile);
            setWebDriver(new FirefoxDriver(capabilities));
        } else if (WEBDRIVER_BROWSER_NAME.equals(BrowserType.SAFARI.toLowerCase())) {
            System.setProperty("webdriver.safari.noinstall", "true");
            capabilities.setBrowserName("safari");
            setWebDriver(new SafariDriver(capabilities));
        } else if (WEBDRIVER_BROWSER_NAME.equals(BrowserType.CHROME.toLowerCase())) {
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory", downloadDir.getAbsolutePath());
            ChromeOptions options = new ChromeOptions();
            options.setExperimentalOption("prefs", chromePrefs);
            capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);
            capabilities.setBrowserName("chrome");
            if (!WEBDRIVER_PATH.isEmpty()) {
                System.setProperty("webdriver.chrome.driver", new File(WEBDRIVER_PATH).getAbsolutePath());
            } else {
                log.warn("The value of property 'webdriver.drivers.path is not specified."
                        + " Trying to get {} driver from system PATH'", WEBDRIVER_BROWSER_NAME);
            }
            if (WEBDRIVER_URL.isEmpty()) {
                setWebDriver(new ChromeDriver(capabilities));
            }
        } else if (WEBDRIVER_BROWSER_NAME.equals(BrowserType.IE.toLowerCase())
                || WEBDRIVER_BROWSER_NAME.equals(BrowserType.IE_HTA.toLowerCase())
                || WEBDRIVER_BROWSER_NAME.equals(BrowserType.IEXPLORE.toLowerCase())) {
            if (!WEBDRIVER_PATH.isEmpty()) {
                System.setProperty("webdriver.ie.driver", new File(WEBDRIVER_PATH).getAbsolutePath());
            } else {
                log.warn("The value of property 'webdriver.drivers.path is not specified."
                        + " Trying to get {} driver from system PATH'", WEBDRIVER_BROWSER_NAME);
            }
            if (WEBDRIVER_URL.isEmpty()) {
                setWebDriver(new InternetExplorerDriver(capabilities));
            }
        } else {
            throw new UnsupportedBrowserException("'" + WEBDRIVER_BROWSER_NAME + "' is not supported yet");
        }
        if (!WEBDRIVER_URL.isEmpty()) {
            URL remoteUrl = new URL(WEBDRIVER_URL);
            setWebDriver(new RemoteWebDriver(remoteUrl, capabilities));
        }
        webDriver.manage().timeouts().pageLoadTimeout(getTimeOutInSeconds(), TimeUnit.SECONDS);
        webDriver.manage().window().maximize();
    }

    public static void dispose() {
        if (webDriver == null) {
            return;
        }

        try {
            log.info("Checking any alert opened");
            WebDriverWait alertAwaiter = new WebDriverWait(webDriver, 2);
            alertAwaiter.until(ExpectedConditions.alertIsPresent());
            Alert alert = webDriver.switchTo().alert();
            log.info("Got an alert: " + alert.getText() + "\n Closing it.");
            alert.dismiss();
        } catch (WebDriverException e) {
            log.debug("No alert opened. Closing webdriver.", e);
        }

        Set<String> windowHandlesSet = webDriver.getWindowHandles();
        try {
            if (windowHandlesSet.size() > 1) {
                for (String winHandle : windowHandlesSet) {
                    webDriver.switchTo().window(winHandle);
                    ((JavascriptExecutor) webDriver).executeScript(
                            "var objWin = window.self;"
                                    + "objWin.open('','_self','');"
                                    + "objWin.close();");
                }
            }
        } catch (Exception e) {
            log.warn("Failed to kill all of the iexplore windows", e);
        }

        try {
            webDriver.quit();
        } finally {
            setWebDriver(null);
        }
    }

    private static void killIE() {
        try {
            log.info("Trying to terminate iexplorer process");
            Runtime.getRuntime().exec("taskkill /f /im iexplore.exe").waitFor();
            log.info("All iexplorer processes were terminated");
        } catch (IOException | InterruptedException e) {
            log.warn("Failed to wait for browser processes finish", e);
        }
    }

    /**
     * @param aWebDriver the webDriver to set
     */
    public static void setWebDriver(org.openqa.selenium.WebDriver aWebDriver) {
        webDriver = aWebDriver;
    }

    /**
     * @param aProxy the proxy to set
     */
    public static void setProxy(BrowserMobProxy aProxy) {
        proxy = aProxy;
    }

    /**
     * @return the WEBDRIVER_BROWSER_NAME
     */
    public static String getBrowserName() {
        return WEBDRIVER_BROWSER_NAME;
    }
}
