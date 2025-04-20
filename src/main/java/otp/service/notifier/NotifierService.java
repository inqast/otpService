package otp.service.notifier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotifierService {

    private final TelegramService telegramService;
    private final EmailService emailService;
    private final SmsService smsService;

    public NotifierService(
                          TelegramService telegramService,
                          EmailService emailService,
                          SmsService smsService) {
        this.telegramService = telegramService;
        this.emailService = emailService;
        this.smsService = smsService;
    }

    public void sendToFile(String username, String code) {
        try {
            Files.writeString(Path.of(username + "_otp.txt"), "Your OTP code is: " + code);
        } catch (IOException e) {
            throw new RuntimeException("file error");
        }
    }

    public void notify(String code, String channel, String destination) {
        switch (channel.toLowerCase()) {
            case "file" -> sendToFile(destination, code);
            case "email" -> sendToEmail(destination, code);
            case "sms" -> sendToSms(destination, code);
            case "telegram" -> sendToTelegram(destination, code);
            default -> throw new RuntimeException("Incorrect channel");
        }
    }

    public void sendToEmail(String email, String code) {
        emailService.sendOtp(email, code);
    }

    public void sendToSms(String phone, String code) {
        smsService.sendOtp(phone, code);
    }

    public void sendToTelegram(String chatId, String code) {
        telegramService.sendOtp(chatId, code);
    }

}
