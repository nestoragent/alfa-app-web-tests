package com.template.lib.util;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import ru.yandex.qatools.allure.cucumberjvm.AllureRunListener;

import java.awt.*;
import java.io.IOException;

/**
 * Created by SBT-Velichko-AA on 09.03.2016.
 */
public class AllureAtpListener extends AllureRunListener {

    public static final String suiteLabelsKey = "SuiteLabels";

    public void testFailure(Failure failure) {
//        if (Init.getOs().equals("WebDriver")) {
//            try {
//                Init.takeFullScreenshot();
//            } catch (AWTException | IOException e) {
//                System.err.println("Failed to get full screenshot on test finished. Error message = " + e.getMessage());
//            }
//        }
//        super.testFailure(failure);
    }

    @Override
    public void testFinished(Description description) throws IllegalAccessException {
//        if (Init.getOs().equals("WebDriver")) {
//            try {
//                Init.takeFullScreenshot();
//            } catch (AWTException | IOException e) {
//                System.err.println("Failed to get full screenshot on test finished. Error message = " + e.getMessage());
//            }
//        }
//        super.testFinished(description);
    }
}
