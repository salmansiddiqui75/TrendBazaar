package com.ecom.TrendBazaar.util;

import com.ecom.TrendBazaar.model.ProductOrder;
import com.ecom.TrendBazaar.model.User;
import com.ecom.TrendBazaar.service.UserService.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private UserService userService;

    @Value("${aws.s3.bucket.category}")
    private String categoryBucket;
    @Value("${aws.s3.bucket.product}")
    private String productBucket;
    @Value("${aws.s3.bucket.profile}")
    private String profileBucket;
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

    String msg=null;




    public Boolean sendMailForProductOrder(ProductOrder productOrder,String status) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        msg = "<p>Hello [[name]],</p>"
                + "<p>Thank you for placing your order with Trending Bazaar!</p>"
                + "<p>Your order status is: <b>[[orderStatus]]</b>.</p>"
                + "<p><b>Product Details:</b></p>"
                + "<p>Name: [[productName]]</p>"
                + "<p>Category: [[category]]</p>"
                // +  "<p>Quantity: [[quantity]]</p>"
                + "<p>Price: [[price]]</p>"
                + "<p>Payment Type: [[paymentType]]</p>"
                + "<p>If you have any questions, feel free to contact our support team.</p>"
                + "<p>Thanks again for choosing us!<br/>Trending Bazaar Support Team</p>";

        // Use MimeMessageHelper to configure the message
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        mimeMessageHelper.setFrom("trendingbazaar429@gmail.com", "Trending Bazaar");
        mimeMessageHelper.setTo(productOrder.getBillingAddress().getEmail());
        mimeMessageHelper.setSubject("Order status");

        // Create the HTML content
        msg=msg.replace("[[name]]",productOrder.getBillingAddress().getFirstName());
        msg=msg.replace("[[orderStatus]]",status);
        msg=msg.replace("[[productName]]", productOrder.getProduct().getTitle());
        msg=msg.replace("[[category]]", productOrder.getProduct().getCategory());
       // msg=msg.replace("[[quantity]]", productOrder.getQuantity());
        msg=msg.replace("[[price]]", productOrder.getPrice().toString());
        msg=msg.replace("[[paymentType]]", productOrder.getPaymentType());
        // Set the email content as HTML
        mimeMessageHelper.setText(msg, true);

        // Send the email
        javaMailSender.send(message);

        return true;
    }
    public User getLoggedInUserDetails(Principal principal)
    {
        String name = principal.getName();
        User user = userService.findByEmail(name);
        return user;
    }

    public String getImageUrl(MultipartFile file,int bucketType)
    {
        String bucketName=null;
        if(bucketType==1)
        {
            bucketName=categoryBucket;
        }
        else if(bucketType==2)
        {
            bucketName=productBucket;
        }else{
            bucketName=profileBucket;
        }
        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";

        //https://trendbazzar-category.s3.eu-north-1.amazonaws.com/fashion.png
        //https://trendbazaar-category.s3.eu-north-1.amazonaws.com/iphone.png
        String url="https://"+bucketName+".s3.eu-north-1.amazonaws.com/"+imageName;
        return url;
    }
}
