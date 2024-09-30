package com.xworkz.metro.service;

import com.xworkz.metro.util.EncryptionDecryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailSent {

    @Autowired
    private   JavaMailSender emailSender;

    @Autowired
    MetroService metroService;

    @Autowired
    private EncryptionDecryption encryptionDecryption;

    private String otpGeneretor(){
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }

    public boolean emailSend(String email){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("amith.s.xworkz@gmail.com");
        message.setTo(email);
        message.setSubject("Reset Password Otp");
        String generatedOtp=otpGeneretor();
        message.setText(generatedOtp);
        emailSender.send(message);
        return true;
    }



}
