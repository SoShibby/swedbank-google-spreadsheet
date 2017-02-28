package com.github.soshibby.integration;

import com.github.soshibby.types.Table;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Henrik on 01/02/2017.
 */
public class GoogleSpreadsheet {

    private static final Logger log = LoggerFactory.getLogger(GoogleSpreadsheet.class);

    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Swedbank - Google Spreadsheet";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/gswedbank");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/sheets.googleapis.com-java-quickstart
     */
    private static final List<String> SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            log.error("Failed to initialize Google Spreadsheet.", t);
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        File clientSecretFile = new File(DATA_STORE_DIR.getAbsolutePath() + "/client_secret.json");
        InputStream in = new FileInputStream(clientSecretFile);

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();

        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");

        log.info("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Sheets API client service.
     *
     * @return an authorized Sheets API client service
     * @throws IOException
     */
    public static Sheets getSheetsService() throws IOException {
        Credential credential = authorize();
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static List<String> getSheetNames(String spreadsheetId) throws IOException {
        Sheets service = GoogleSpreadsheet.getSheetsService();
        Spreadsheet response = service.spreadsheets().get(spreadsheetId).setIncludeGridData(false).execute();
        List<Sheet> sheets = response.getSheets();

        List<String> names = new ArrayList();
        sheets.forEach(sheet -> names.add(sheet.getProperties().getTitle()));
        return names;
    }

    public static boolean existsSheetName(String spreadsheetId, String name) throws IOException {
        return getSheetNames(spreadsheetId).contains(name);
    }

    public static void createSheet(String spreadsheetId, String name) throws IOException {
        Sheets service = GoogleSpreadsheet.getSheetsService();

        List<Request> requests = new ArrayList();
        requests.add(new Request().setAddSheet(new AddSheetRequest()
                .setProperties(new SheetProperties().setTitle(name))));

        BatchUpdateSpreadsheetRequest body = new BatchUpdateSpreadsheetRequest().setRequests(requests);
        service.spreadsheets().batchUpdate(spreadsheetId, body).execute();
    }

    public static void append(String spreadsheetId, String sheetName, Table table) throws IOException {
        Sheets service = GoogleSpreadsheet.getSheetsService();
        String range = sheetName + "!" + table.getRange();
        service.spreadsheets().values().append(spreadsheetId, range, table.toValueRange()).setValueInputOption("RAW").execute();
    }

    public static void update(String spreadsheetId, String sheetName, Table table) throws IOException {
        Sheets service = GoogleSpreadsheet.getSheetsService();
        String range = sheetName + "!" + table.getRange();
        service.spreadsheets().values().update(spreadsheetId, range, table.toValueRange()).setValueInputOption("RAW").execute();
    }

}

