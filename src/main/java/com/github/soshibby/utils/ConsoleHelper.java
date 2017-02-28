package com.github.soshibby.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Henrik on 04/02/2017.
 */
public class ConsoleHelper {

    private static final Logger log = LoggerFactory.getLogger(ConsoleHelper.class);
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public String readString() throws IOException {
        return readString(false, null);
    }

    public String readString(boolean required, String defaultValue) throws IOException {
        String value = br.readLine();

        if (required && value.isEmpty()) {
            if (defaultValue == null) {
                log.info("You must enter a value.");
                return readString(required, defaultValue);
            } else {
                return defaultValue;
            }
        }

        return value;
    }

    public Boolean readBoolean() throws IOException {
        return readBoolean(null);
    }

    public Boolean readBoolean(String defaultValue) throws IOException {
        String val = readString(true, defaultValue);

        switch (val) {
            case "Y":
            case "y":
                return true;
            case "N":
            case "n":
                return false;
            default:
                log.info("Expected Y or N.");
                return readBoolean(defaultValue);
        }
    }
}
