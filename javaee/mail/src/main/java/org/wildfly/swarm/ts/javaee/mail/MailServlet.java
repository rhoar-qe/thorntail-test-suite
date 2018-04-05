package org.wildfly.swarm.ts.javaee.mail;

import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/mail")
public class MailServlet extends HttpServlet {
    @Resource(lookup = "java:jboss/mail/default")
    private Session mailSession;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Message message = new MimeMessage(mailSession);
            message.setRecipient(Message.RecipientType.TO, new InternetAddress("swarm-test-suite@example.com"));

            Multipart multipart = new MimeMultipart();
            MimeBodyPart text = new MimeBodyPart();
            text.setText("FOO");
            multipart.addBodyPart(text);

            message.setContent(multipart);
            Transport.send(message);
            resp.getWriter().print("OK");
        } catch (MessagingException e) {
            throw new ServletException(e);
        }
    }
}
