package com.template.lib.drivers;

import com.template.lib.PageFactory;
import com.template.lib.exceptions.FactoryRuntimeException;
import com.template.lib.support.Props;
import com.template.lib.support.Environment;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.MalformedURLException;
import java.net.URL;

import static com.template.lib.PageFactory.setAspectsDisabled;

/**
 * Created by nestor on 11.07.2017.
 */
@Slf4j
public class MobileDriver {
    private static AppiumDriver<AndroidElement> mobileDriver;
    private static final String APPIUM_URL = Props.get("appium.url");
    private static final String APPIUM_DEVICE_NAME = Props.get("appium.device.name");
    private static final String APPIUM_DEVICE_PLATFORM = Props.get("appium.device.platform");
    private static final String APPIUM_APP_PACKAGE = Props.get("appium.app.package");
    private static final String APPIUM_APP_ACTIVITY = Props.get("appium.app.activity");
    private static final boolean APPIUM_FILL_ADB = "true".equalsIgnoreCase(Props.get("appium.fill.adb"));
    private static final boolean APPIUM_CLICK_ADB = "true".equalsIgnoreCase(Props.get("appium.click.adb"));
    private static String deviceUdId;

    public static AppiumDriver<AndroidElement> getDriver() {
        if (Environment.MOBILE != PageFactory.getEnvironment()) {
            throw new FactoryRuntimeException("Failed to get mobile driver while environment is not mobile");
        }

        if (null == mobileDriver) {
            createDriver();
        }
        return mobileDriver;
    }

    private static void createDriver() {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", APPIUM_DEVICE_NAME);
        capabilities.setCapability("platformVersion", APPIUM_DEVICE_PLATFORM);
        capabilities.setCapability("appPackage", APPIUM_APP_PACKAGE);
        capabilities.setCapability("appActivity", APPIUM_APP_ACTIVITY);
        capabilities.setCapability("autoGrantPermissions", "true");
        capabilities.setCapability("unicodeKeyboard", "true");
        capabilities.setCapability("resetKeyboard", "true");
        log.info("Capabilities are {}", capabilities);

        URL url;
        try {
            url = new URL(APPIUM_URL);
        } catch (MalformedURLException e) {
            throw new FactoryRuntimeException("Could not parse appium url. Check 'appium.url' property", e);
        }

        setAspectsDisabled(true);
        log.debug("Aspect disabled");
        mobileDriver = new AndroidDriver<>(url, capabilities);
        log.info("Mobile driver created {}", mobileDriver);
        deviceUdId = (String) mobileDriver.getSessionDetails().get("deviceUDID");
    }

    public static void dispose() {
        if (mobileDriver == null) {
            return;
        }

        try {
            mobileDriver.quit();
        } finally {
            setMobileDriver(null);
        }

    }

    /**
     * @return the APPIUM_FILL_ADB
     */
    public static boolean getAppiumFillAdb() {
        return APPIUM_FILL_ADB;
    }

    /**
     * @return the APPIUM_CLICK_ADB
     */
    public static boolean getAppiumClickAdb() {
        return APPIUM_CLICK_ADB;
    }

    /**
     * @return the deviceUdId
     */
    public static String getDeviceUDID() {
        return deviceUdId;
    }

    /**
     * @param aMobileDriver the mobileDriver to set
     */
    public static void setMobileDriver(AppiumDriver<AndroidElement> aMobileDriver) {
        mobileDriver = aMobileDriver;
    }
}
