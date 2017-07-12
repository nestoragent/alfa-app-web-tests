package com.template.lib.support;

import com.template.lib.PageFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by nestor on 11.07.2017.
 */
@Slf4j
public class ScreenShooter {
    /**
     * Takes screenshot with driver
     *
     * @return screenshot in byte array
     */
    public static byte[] takeWithDriver() {
        return ((TakesScreenshot) PageFactory.getDriver()).getScreenshotAs(OutputType.BYTES);
    }

    /**
     * Takes whole screen screenshot
     *
     * @return screenshot in byte array
     */
    public static byte[] takeRaw() {
        try {
            Rectangle screenBounds = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage picture = new Robot().createScreenCapture(screenBounds);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ImageIO.write(picture, "png", bytes);
            return bytes.toByteArray();
        } catch (AWTException | IOException ex) {
            log.error("Failed to get full screenshot on test failure because of IOException", ex);
            return "".getBytes();
        }
    }

    /**
     * Takes screenshot as indicated in application.properties
     *
     * @return screenshot in byte array
     */
    public static byte[] take() {
        String screenshotStrategy = Props.get("screenshot.strategy", "raw");

        switch (screenshotStrategy) {
            case "driver":
                return ScreenShooter.takeWithDriver();
            case "raw":
            default:
                return ScreenShooter.takeRaw();
        }
    }
}
