package com.template.lib.support;

import com.template.lib.drivers.MobileDriver;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by nestor on 11.07.2017.
 */
@Slf4j
public class AdbConsole {

    private AdbConsole() {
        throw new IllegalAccessError("Utility class");
    }

    public static boolean execute(String command) {
        return execute(MobileDriver.getDeviceUDID(), command);
    }

    public static boolean execute(String deviceUDID, String command) {
        ProcessBuilder processBuilder = new ProcessBuilder(new String[]{"adb", "-s", deviceUDID, "shell", command});
        log.info("Command '{}' is processing...", command);
        try {
            Process process = processBuilder.start();

            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            log.debug(builder.toString());

            return process.waitFor() == 0;
        } catch (IOException | InterruptedException ex) {
            log.error("Failed to process command '{}'", command, ex);
        }

        return false;
    }
}
