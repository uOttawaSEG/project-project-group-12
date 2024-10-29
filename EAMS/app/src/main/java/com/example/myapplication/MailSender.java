package com.example.myapplication;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailSender {

    private static MailSender instance;

    private MailSender() {}

    public static synchronized MailSender getInstance() {
        if (instance == null) {
            instance = new MailSender();
        }
        return instance;
    }
    private final String username = "eamsbot@outlook.com";
    private final String password = "EAMS12345";
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void sendEmail(String toEmail, String subject, String messageBody){
        Properties properties = new Properties();

        //Outlook SMTP configuration
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.office365.com");
        properties.put("mail.smtp.port", "587");

        //Create a session with the required authentification
        Session session = Session.getInstance(properties, new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(username, password);
            }
        });


        // Send email asynchronously using ExecutorService
        executorService.submit(() -> {
            try {
                // Create a new email message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username)); // From email
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); // To email
                message.setSubject(subject); // Subject
                message.setText(messageBody); // Message body

                // Send the email
                Transport.send(message);
                System.out.println("Email sent successfully");

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
        // Shut down the executor service when it's no longer needed
        executorService.shutdown();
    }


}


        //send email asynchronously
/*        new SendMailTask(session, toEmail, subject, messageBody).execute();
    }
    private static class SendMailTask extends AsyncTask<Void, Void, Void> {
        private Session session;
        private String toEmail;
        private String subject;
        private String messageBody;

        public SendMailTask(Session session, String toEmail, String subject, String messageBody) {
            this.session = session;
            this.toEmail = toEmail;
            this.subject = subject;
            this.messageBody = messageBody;
        }
        protected Void doInBackground(Void...params){
            try{
                //create a new email message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("eamsbot@outlook.com")); // From email
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail)); // To email
                message.setSubject(subject); // Subject
                message.setText(messageBody); // Message body

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }*/

