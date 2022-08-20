package com.khwish.backend.google;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FirebaseService {

    private static Logger LOGGER = LogManager.getLogger(FirebaseService.class);
    private static String classTag = FirebaseService.class.getSimpleName();

    @EventListener(ApplicationReadyEvent.class)
    private static void initFirebase() throws IOException {
        String googleApplicationCredentials = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        LOGGER.log(Level.INFO, "[" + classTag + "][initFirebase] GOOGLE_APPLICATION_CREDENTIALS = " + googleApplicationCredentials);
        if (googleApplicationCredentials != null) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.getApplicationDefault())
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

    public static String verifyTokenAndGetUid(String idToken) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            return decodedToken.getUid();
        } catch (FirebaseAuthException | IllegalArgumentException e) {
            LOGGER.log(Level.ERROR, "[" + classTag + "][verifyTokenAndGetUid] Error occurred. " + e.toString());
            return null;
        }
    }

    public static void sendNotificationToUser(String userNotificationToken, String notificationTitle, String notificationText) {
        Notification notification = Notification.builder()
                .setTitle(notificationTitle).setBody(notificationText)
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(userNotificationToken)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            LOGGER.log(Level.INFO, "[" + classTag + "][sendNotificationToUser] Successfully sent message: " + response);
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, e.toString(), e);
        }
    }
}
