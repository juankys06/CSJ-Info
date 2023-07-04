package com.solicitud.solicitud.mail;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailConfiguration {
	//public static JavaMailSenderImpl getJavaMailSender(String host, int port, String username, String password) {
	public static JavaMailSenderImpl getJavaMailSender(String username, String password) {
		JavaMailSenderImpl emailSender = new JavaMailSenderImpl();
		
		//-- emailSender.setHost(host);
		//-- emailSender.setPort(port);
		//emailSender.setHost("smtp.mailtrap.io");
		//emailSender.setPort(2525);
        emailSender.setHost("smtp.office365.com");
        emailSender.setPort(587);
		
		//emailSender.setUsername("b1ea5a4deb652d");
		//emailSender.setPassword("1fa091633036fb");
        emailSender.setUsername(username);
        emailSender.setPassword(password);
		
		Properties props = emailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		// props.put("mail.debug", "true");
		//props.put("mail.properties.mail.smtp.connectiontimeout", 10000);
		//props.put("mail.properties.mail.smtp.timeout", 10000);
		//props.put("mail.properties.mail.smtp.writetimeout", 10000);
	

		return emailSender;
	}
}
