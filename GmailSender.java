package com.dencofamily.paycom.brain;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GmailSender {
    private static final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    // Use your API key here
    private static final String API_KEY = "AIzaSyCxR8Xc4Q2K8H1ThQPQaM4izErE_FoHBX0";
    
    private static MimeMessage createEmail1(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }
    
    public static boolean sendEmail(String to, String subject, String body) throws IOException, GeneralSecurityException, MessagingException {
        Gmail service = createGmailService();
        Message message = createMessageWithEmail(createEmail1(to, "me", subject, body));
        service.users().messages().send("me", message).execute();
        return true;
    }

    private static Gmail createGmailService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        HttpRequestInitializer httpRequestInitializer = request -> {
            // Set API key for authentication
            request.getHeaders().set("X-Goog-Api-Key", API_KEY);
        };
        return new Gmail.Builder(httpTransport, JSON_FACTORY, httpRequestInitializer)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private static Message createMessageWithEmail(MimeMessage email) throws IOException, MessagingException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }



    public static void main(String[] args) throws MessagingException {
        String to = "sdubey@dencofamily.com";
        String subject = "Test Email";
        String body = "This is a test email sent using the Gmail API.";
        try {
            sendEmail(to, subject, body);
            System.out.println("Email sent successfully!");
        } catch (IOException | GeneralSecurityException e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
    }
}
