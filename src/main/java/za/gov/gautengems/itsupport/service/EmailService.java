package za.gov.gautengems.itsupport.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String recipientEmail,
            String resetLink) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            // =====================================================
            // EMAIL DETAILS
            // =====================================================

            helper.setTo(recipientEmail);

            helper.setSubject(
                    "Gauteng EMS IT Support - Password Reset"
            );


            // =====================================================
            // HTML EMAIL
            // =====================================================

            String htmlContent =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<meta charset='UTF-8'>" +
                            "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                            "<title>Password Reset</title>" +
                            "</head>" +

                            "<body style='margin:0; padding:0; background-color:#f4f6f8; font-family:Arial, Helvetica, sans-serif;'>" +

                            "<div style='max-width:600px; margin:40px auto; background-color:#ffffff; border-radius:12px; overflow:hidden; box-shadow:0 4px 15px rgba(0,0,0,0.10);'>" +

                            // =====================================================
                            // HEADER
                            // =====================================================

                            "<div style='background-color:#17365d; padding:30px; text-align:center;'>" +

                            "<h1 style='color:#ffffff; margin:0; font-size:26px;'>" +
                            "GAUTENG EMS" +
                            "</h1>" +

                            "<p style='color:#dce6f2; margin:8px 0 0 0; font-size:15px;'>" +
                            "IT Support Management System" +
                            "</p>" +

                            "</div>" +


                            // =====================================================
                            // MAIN CONTENT
                            // =====================================================

                            "<div style='padding:40px 35px; color:#333333;'>" +

                            "<h2 style='color:#17365d; margin-top:0; font-size:24px;'>" +
                            "Password Reset Request" +
                            "</h2>" +

                            "<p style='font-size:16px; line-height:1.6;'>" +
                            "A request was made to reset the password for your " +
                            "Gauteng EMS IT Support account." +
                            "</p>" +

                            "<p style='font-size:16px; line-height:1.6;'>" +
                            "Click the button below to create a new password:" +
                            "</p>" +


                            // =====================================================
                            // RESET BUTTON
                            // =====================================================

                            "<div style='text-align:center; margin:35px 0;'>" +

                            "<a href='" + resetLink + "'" +

                            " style='display:inline-block;" +
                            " padding:15px 30px;" +
                            " background-color:#198754;" +
                            " color:#ffffff;" +
                            " text-decoration:none;" +
                            " font-size:16px;" +
                            " font-weight:bold;" +
                            " border-radius:6px;'>" +

                            "RESET PASSWORD" +

                            "</a>" +

                            "</div>" +


                            // =====================================================
                            // EXPIRY NOTICE
                            // =====================================================

                            "<div style='background-color:#f8f9fa;" +
                            " border-left:4px solid #198754;" +
                            " padding:15px;" +
                            " margin-top:25px;'>" +

                            "<p style='margin:0; font-size:14px; color:#555555;'>" +

                            "This password reset link will expire after " +

                            "<strong>30 minutes</strong>." +

                            "</p>" +

                            "</div>" +


                            // =====================================================
                            // SECURITY MESSAGE
                            // =====================================================

                            "<p style='font-size:14px; line-height:1.6; color:#666666; margin-top:30px;'>" +

                            "If you did not request a password reset, " +
                            "you can safely ignore this email." +

                            "</p>" +


                            // =====================================================
                            // SIGNATURE
                            // =====================================================

                            "<p style='font-size:15px; line-height:1.6; margin-top:30px;'>" +

                            "Regards,<br>" +

                            "<strong>Gauteng EMS IT Support</strong>" +

                            "</p>" +

                            "</div>" +


                            // =====================================================
                            // FOOTER
                            // =====================================================

                            "<div style='background-color:#f1f3f5; padding:20px; text-align:center;'>" +

                            "<p style='margin:0; color:#777777; font-size:12px;'>" +

                            "Gauteng EMS IT Support Management System" +

                            "</p>" +

                            "<p style='margin:6px 0 0 0; color:#999999; font-size:11px;'>" +

                            "This is an automated email. Please do not reply." +

                            "</p>" +

                            "</div>" +

                            "</div>" +

                            "</body>" +
                            "</html>";


            // =====================================================
            // TELL SPRING THIS IS HTML
            // =====================================================

            helper.setText(
                    htmlContent,
                    true
            );


            // =====================================================
            // SEND EMAIL
            // =====================================================

            mailSender.send(message);

        } catch (MessagingException e) {

            throw new RuntimeException(
                    "Failed to send password reset email.",
                    e
            );
        }
    }
}