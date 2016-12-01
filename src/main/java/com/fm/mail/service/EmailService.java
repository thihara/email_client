package com.fm.mail.service;

import com.fm.mail.dto.EMail;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by thihara on 10/25/16.
 */
@Service
public class EmailService {

    private static final String HOST_NAME = "";
    public static final int PORT_NUMBER = 587;
    public static final String USER_NAME = "";
    public static final String PASSWORD = "";

    @Autowired
    S3Service s3Service;

    public void sendEmail(String from, String to, String subject, String message) throws EmailException {

        Email email = new SimpleEmail();
        email.setHostName(HOST_NAME);

        email.setSmtpPort(PORT_NUMBER);
        email.setAuthentication(USER_NAME, PASSWORD);
        email.setStartTLSEnabled(true);
        email.setStartTLSRequired(true);
        email.setFrom(from);
        email.setSubject(subject);
        email.setMsg(message);
        email.addTo(to);
        email.send();
    }

    public List<EMail> retrieveEmails(String emailAddress){
        List<String> emailFileKeys = s3Service.listAllFiles(emailAddress).stream()
                .map(summary -> summary.getKey()).collect(Collectors.toList());
        return s3Service.emailContents(emailFileKeys);
    }
}
