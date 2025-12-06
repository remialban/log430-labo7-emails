package com.log430.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class KafkaEmailListener {
    private MailSender mailSender;

    public KafkaEmailListener(MailSender mailSender) {
        this.mailSender = mailSender;
    }
    @KafkaListener(topics = "newUser", groupId = "mail-group")
    public void listen(Map<String, Object> message) {
        System.out.println("Message reçu : " + message.get("email"));


        try {
            String to = (String) message.get("email");
            String token = (String) message.get("token");
            System.out.println("Message reçu : " + to);

            String subject = "Vérification de votre compte";
            String text = String.format("""
                    Bonjour %s,
                    Merci de vous être inscrit sur notre plateforme. Veuillez cliquer sur le lien ci-dessous pour vérifier votre adresse e-mail et activer votre compte :
                    http://localhost:5050/users/validate?token=%s
                    """, message.get("name"), token);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(text);
            System.out.println(mailMessage);
            mailSender.send(mailMessage);
            System.out.println("Message envoyé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }

    }

    @KafkaListener(topics = "newOrder", groupId = "mail-group")
    public void onNewOrdre(Map<String, String> message) {
        try {
            // Map to json :

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(message);

            System.out.println("Creation d'un ordre : " + json);

            String subject = "Ordre créé";
            String text = String.format("""
                    Bonjour %s,
                    L'ordre que vous avez réalisé a bien été créé : %s
                    """, json);
            String email = message.get("email");

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setSubject(subject);
            mailMessage.setText(text);
            System.out.println(mailMessage);
            mailSender.send(mailMessage);
            System.out.println("Message envoyé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }

    }

    @KafkaListener(topics = "updateOrder", groupId = "mail-group")
    public void onUpdateOrdre(Map<String, String> message) {


        try {
            // Map to json :

            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(message);

            System.out.println("Mise a jour d'un ordre : " + json);

            String subject = "Mise a jour d'un ordre";
            String text = String.format("""
                    Bonjour %s,
                    Un ordre a été mis à jour : %s
                    """, json);
            String email = message.get("email");
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject(subject);
            mailMessage.setText(text);
            System.out.println(mailMessage);
            mailSender.send(mailMessage);
            System.out.println("Message envoyé avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }

    }
}
