package com.khwish.backend.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.khwish.backend.constants.Constants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class GoogleSheetsService {

    private static final String classTag = GoogleSheetsService.class.getSimpleName();
    private static final Logger LOGGER = LogManager.getLogger(GoogleSheetsService.class);

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/home/ssj/khwish/khwish-backend-service-account.json";

    @EventListener(ApplicationReadyEvent.class)
    private static Credential getCredentials() throws IOException {
        FileInputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        return GoogleCredential.fromStream(in).createScoped(SCOPES);
    }

    private static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = GoogleSheetsService.getCredentials();
        return new Sheets.Builder(HTTP_TRANSPORT, JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(Constants.APPLICATION_NAME)
                .build();
    }

    public static void makeWithdrawalRequestEntry(String userName, Double amount, String transferMethod, String upiId,
                                                  String accountNumber, String ifscCode,
                                                  String accountHoldersName) throws IOException, GeneralSecurityException {
        ValueRange appendBody = new ValueRange()
                .setValues(Collections.singletonList(
                        Arrays.asList(userName, amount, transferMethod, upiId, accountNumber, ifscCode, accountHoldersName)));

        AppendValuesResponse appendResult = getSheetsService().spreadsheets().values()
                                                              .append(Constants.getProfileConfig()
                                                                               .getWithdrawalRequestsSpreadsheetId()
                                                                      , "A2", appendBody)
                                                              .setValueInputOption("USER_ENTERED")
                                                              .setInsertDataOption("INSERT_ROWS")
                                                              .setIncludeValuesInResponse(true)
                                                              .execute();
        LOGGER.log(Level.INFO,
                "[" + classTag + "][makeWithdrawalRequestEntry] Row appended to google sheet. {" + userName + ", " + amount + ", "
                        + transferMethod + ", " + upiId + ", " + accountNumber + ", " + ifscCode + ", " + accountHoldersName
                        + "} Result = " + appendResult.toPrettyString());
    }
}
