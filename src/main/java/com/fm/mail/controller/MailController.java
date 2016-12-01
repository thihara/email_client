package com.fm.mail.controller;

import com.fm.mail.dto.EMail;
import com.fm.mail.service.EmailService;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class MailController {

    @Autowired
    EmailService emailService;

    @RequestMapping("/")
    public String index(){
        return "App Running. Hello from SpringBoot!";
    }

    @RequestMapping(value = "/email", method = RequestMethod.POST)
    public String sendEmail(@RequestParam String from, @RequestParam String to, @RequestParam String subject,
                            @RequestParam String bodyContent){
        String message;
        try {
            emailService.sendEmail(from,to,subject,bodyContent);
            message = "E-Mail Sent Successfully.";
        } catch (EmailException e) {
            e.printStackTrace();
            message = "E-Mail Sending Failed. Please retry later.";
        }

        return message;
    }

    @RequestMapping(value = "/email", method = RequestMethod.GET)
    public Collection<EMail> retrieveEmails(@RequestParam String emailAddress){

        if(emailAddress == null || emailAddress.isEmpty()){
            throw new IllegalArgumentException("Invalid emailAddress");
        }

        Collection<EMail> eMails = emailService.retrieveEmails(emailAddress);

        return eMails;
    }
}