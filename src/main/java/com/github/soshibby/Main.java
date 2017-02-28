package com.github.soshibby;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.soshibby.swedbank.Swedbank;
import com.github.soshibby.swedbank.app.SwedbankApp;
import com.github.soshibby.swedbank.authentication.MobileBankID;
import com.github.soshibby.swedbank.exceptions.SwedbankAuthenticationException;
import com.github.soshibby.swedbank.exceptions.SwedbankServerException;
import com.github.soshibby.swedbank.exceptions.SwedbankUnauthorizedException;
import com.github.soshibby.swedbank.exceptions.SwedbankUserException;
import com.github.soshibby.swedbank.types.AccountList;
import com.github.soshibby.types.Config;
import com.github.soshibby.types.ImportHistory;
import com.github.soshibby.utils.ConsoleHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by Henrik on 01/02/2017.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static Swedbank swedbank = new Swedbank();
    private static ConsoleHelper console = new ConsoleHelper();
    private static ObjectMapper mapper = new ObjectMapper();
    private static Config config;

    public static void main(String[] args) throws IOException, SwedbankAuthenticationException, InterruptedException, SwedbankUnauthorizedException, SwedbankServerException, SwedbankUserException {
        String importHistoryPath = getApplicationPath() + "/importHistory.json";
        String configPath = getApplicationPath() + "/config.json";
        config = readConfig(configPath);

        String personalNumber = getPersonalNumber(config.getPersonalNumber());
        log.info("Using personal number {}.", personalNumber);

        String spreadsheetId = getSpreadsheetId(config.getSpreadsheetId());
        log.info("Using spreadsheet id {}.", spreadsheetId);

        writeConfig(configPath, config);
        log.info("Saving config changes.");

        ImportHistory importHistory = readImportHistory(importHistoryPath);

        authenticate(personalNumber);

        log.info("Fetching account list.");
        AccountList accountList = swedbank.accountList();

        log.info("Starting import.");
        Importer importer = new Importer(swedbank, spreadsheetId);
        importHistory = importer.importAllAccounts(accountList, importHistory);
        writeImportHistory(importHistoryPath, importHistory);

        log.info("Import finsished successfully!");
    }

    private static String getPersonalNumber(String previousPersonalNumber) throws IOException {
        if (previousPersonalNumber == null) {
            log.info("Enter your personal number: ");
        } else {
            log.info("Enter your personal number[{}]: ", previousPersonalNumber);
        }

        String personalNumber = console.readString(true, previousPersonalNumber);
        config.setPersonalNumber(personalNumber);

        return personalNumber;
    }

    private static String getSpreadsheetId(String previousSpreadsheetId) throws IOException {
        if (previousSpreadsheetId == null) {
            log.info("Enter the spreadsheet id that you want to import to: ");
        } else {
            log.info("Enter the spreadsheet id that you want to import to[{}]: ", previousSpreadsheetId);
        }

        String spreadsheetId = console.readString(true, previousSpreadsheetId);
        config.setSpreadsheetId(spreadsheetId);

        return spreadsheetId;
    }

    private static Config readConfig(String configPath) {
        File configFile = new File(configPath);

        try {
            if (configFile.exists()) {
                log.debug("Reading config file {}.", configPath);
                return mapper.readValue(configFile, Config.class);
            } else {
                log.debug("Couldn't find config file. Creating a new config.");
                return new Config();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read config file.", e);
        }
    }

    private static void writeConfig(String configPath, Config config) {
        try {
            log.debug("Saving config to file {}.", configPath);
            mapper.writeValue(new File(configPath), config);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write config to file.", e);
        }
    }

    private static String getApplicationPath() {
        try {
            return new File(".").getCanonicalPath();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get the path to the application.");
        }
    }

    public static ImportHistory readImportHistory(String filePath) throws IOException {
        ImportHistory importHistory;

        File file = new File(filePath);
        if (file.exists()) {
            log.debug("Reading import history from file {}.", filePath);
            try {
                importHistory = mapper.readValue(file, ImportHistory.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read import history file.", e);
            }
        } else {
            log.debug("No import history file found.");
            return new ImportHistory();
        }

        log.info("You have imported before, do you want to continue where you left off (Y/N) [Y]?");
        Boolean useImportHistory = console.readBoolean("Y");

        if (useImportHistory) {
            log.debug("Using existing import history.");
            return importHistory;
        } else {
            log.debug("Ignoring import history.");
            return new ImportHistory();
        }
    }

    public static void writeImportHistory(String importHistoryPath, ImportHistory importHistory) {
        try {
            log.debug("Writing import history to file {}.", importHistoryPath);
            mapper.writeValue(new File(importHistoryPath), importHistory);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write import history to file.");
        }
    }

    private static void authenticate(String personalNumber) throws SwedbankAuthenticationException, InterruptedException {
        MobileBankID mobileBankID = new MobileBankID(new SwedbankApp(), personalNumber);
        swedbank.login(mobileBankID);

        int retries = 0;
        int maxRetries = 20;
        int retryTimeout = 5000;

        while (!swedbank.isLoggedIn()) {
            log.info("Waiting for user to login.");
            Thread.sleep(retryTimeout);

            if (retries > maxRetries) {
                throw new RuntimeException("Login aborted, you didn't login for " + (maxRetries * retryTimeout / 1000) + " seconds.");
            }

            retries++;
        }

        log.info("Successfully logged in.");
    }

}
