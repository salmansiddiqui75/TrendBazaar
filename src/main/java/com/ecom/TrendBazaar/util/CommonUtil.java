package com.ecom.TrendBazaar.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    public Boolean sendMail(String url, String reciepentMail) throws MessagingException, UnsupportedEncodingException {
        // Create a MimeMessage
        MimeMessage message = javaMailSender.createMimeMessage();

        // Use MimeMessageHelper to configure the message
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        mimeMessageHelper.setFrom("trendingbazaar429@gmail.com", "Trending Bazaar");
        mimeMessageHelper.setTo(reciepentMail);
        mimeMessageHelper.setSubject("Password Reset Request");

        // Create the HTML content
        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + url + "\">Change my password</a></p>"
                + "<p>If you did not request a password reset, please ignore this email. Your account will remain secure.</p>"
                + "<p>For your security, this link will expire in 24 hours. If you need further assistance, please contact our support team.</p>"
                + "<p>Thank you,<br/>Trending Bazaar Support Team</p>";

        // Set the email content as HTML
        mimeMessageHelper.setText(content, true);

        // Send the email
        javaMailSender.send(message);

        return true;
    }

    public static String generateUrl(HttpServletRequest request) {
        String siteUrl = request.getRequestURL().toString();
        return siteUrl.replace(request.getServletPath(), "");
    }
}
