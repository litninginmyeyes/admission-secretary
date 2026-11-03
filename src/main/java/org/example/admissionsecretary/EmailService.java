package org.example.admissionsecretary;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {

    private static final String FROM_EMAIL = Config.get("mail.user");
    private static final String PASSWORD   = Config.get("mail.password");;

    private static Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });
    }

    public static void sendApplicationConfirmation(String toEmail, String fullName, String speciality) {
        String subject = "Заявление принято — Приёмная комиссия";
        String body =
                "Здравствуйте, " + fullName + "!\n\n" +
                        "Ваше заявление на специальность \"" + speciality + "\" успешно подано.\n" +
                        "Для проверки статуса откройте приложение и введите ваш email.\n\n" +
                        "С уважением,\nПриёмная комиссия";

        send(toEmail, subject, body);
    }

    public static void sendStatusUpdate(String toEmail, String fullName, String status) {
        String subject = "Обновление статуса — Приёмная комиссия";
        String body =
                "Здравствуйте, " + fullName + "!\n\n" +
                        "Ваш статус обновлён: " + status + "\n" +
                        "Для подробной информации откройте приложение и введите ваш email.\n\n" +
                        "С уважением,\nПриёмная комиссия";

        send(toEmail, subject, body);
    }

    private static void send(String toEmail, String subject, String body) {
        try {
            Session session = getSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("Письмо отправлено: " + toEmail);
        } catch (MessagingException e) {
            System.out.println("Ошибка отправки письма: " + e.getMessage());
        }
    }
}