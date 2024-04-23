package aor.paj.bean;

import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

@Singleton
public class EmailService {

    // Configurações para acesso ao servidor de e-mail
    private final String username = "vsgm13@outlook.pt";
    private final String password = "vaNiaSo1986";

    @Resource(name = "mail/session")
    private Session session;

    public EmailService() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.office365.com"); // Servidor SMTP do Outlook
        props.put("mail.smtp.port", "587"); // Porta SMTP padrão para o Outlook

        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("E-mail enviado com sucesso para: " + to);
        } catch (MessagingException e) {
            System.out.println("Erro ao enviar e-mail para: " + to);
            e.printStackTrace();
        }
    }
}

