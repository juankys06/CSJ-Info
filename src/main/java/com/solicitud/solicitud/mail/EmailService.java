package com.solicitud.solicitud.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Service
public class EmailService {
    @Autowired
    private TemplateEngine htmlTemplateEngine;
    @Autowired
    private TemplateEngine textTemplateEngine;

    @Autowired
    private Environment environment;

    @Async
    public void sendSimpleMessage(JavaMailSenderImpl emailSender, Mail mail)
            throws MessagingException, IOException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        final String html = this.htmlTemplateEngine.process("email-template", context);
        final String text = this.textTemplateEngine.process("email-template", context);

        helper.setTo(mail.getTo());
        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        // FileSystemResource file = new FileSystemResource(new File("c:/Sample.jpg"));
        //helper.addAttachment("CoolImage.jpg", file);

        emailSender.send(message);
    }
    
    @Async
    public void sendSimpleMessage1(JavaMailSenderImpl emailSender, Mail mail, Set<String> archivos)
            throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        final String html = this.htmlTemplateEngine.process("email-template", context);
        final String text = this.textTemplateEngine.process("email-template", context);

        helper.setTo(mail.getTo());
        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        if (archivos != null) {
            for (String url : archivos) {
                try {
                    String[] urlV = url.split("/");
                    urlV[3] = URLDecoder.decode(URLDecoder.decode(urlV[3],
                            StandardCharsets.UTF_8.toString()), "ISO-8859-1");
                    File file = new File(
                            environment.getProperty("files_path")
                                    + urlV[3]);
                    helper.addAttachment(urlV[3], file);

                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        emailSender.send(message);
    }
    
    @Async
    public void sendPollLink(JavaMailSenderImpl emailSender, Mail mail) throws MessagingException, IOException
    {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        final String html = this.htmlTemplateEngine.process("email-encuesta", context);
        final String text = this.textTemplateEngine.process("email-encuesta", context);

        helper.setTo(mail.getTo());
        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        emailSender.send(message);
    }
    
    @Async
    public void sendMessageCreateEmail(JavaMailSenderImpl emailSender, Mail mail)
            throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        final String html = this.htmlTemplateEngine.process("email-creacion", context);
        final String text = this.textTemplateEngine.process("email-creacion", context);

        helper.setTo(mail.getTo());
        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        emailSender.send(message);
    }
    
    @Async
    public void sendMessageCreateEntidad(JavaMailSenderImpl emailSender, Mail mail)
            throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        final String html = this.htmlTemplateEngine.process("email-creacion-entidad", context);
        final String text = this.textTemplateEngine.process("email-creacion-entidad", context);

        helper.setTo(mail.getTo());
        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        emailSender.send(message);
    }
    
    @Async
    public void sendMessageAsignar(JavaMailSenderImpl emailSender, Mail mail, List<String> archivos)
            throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());

        final String html = this.htmlTemplateEngine.process("email-asignacion", context);
        final String text = this.textTemplateEngine.process("email-asignacion", context);

        helper.setTo(mail.getTo());
        if (mail.getCc() != null && mail.getCc().length > 0)
            helper.setCc(mail.getCc());
        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        if (archivos != null) {
            for (String url : archivos) {
                try {
                    String[] urlV = url.split("/");
                    urlV[3] = URLDecoder.decode(URLDecoder.decode(urlV[3],
                            StandardCharsets.UTF_8.toString()), "ISO-8859-1");
                    File file = new File(environment.getProperty("files_path") + urlV[3]);
                    helper.addAttachment(urlV[3], file);
                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {

                }
            }
            ;
        }

        emailSender.send(message);
    }
    
    @Async
    public void sendMessageAsignar1(JavaMailSenderImpl emailSender, Mail mail, List<String> archivos)
            throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());

        final String html = this.htmlTemplateEngine.process("email-asignacion-automatico", context);
        final String text = this.textTemplateEngine.process("email-asignacion-automatico", context);

        helper.setTo(mail.getTo());
        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        if (archivos != null) {
            for (String url : archivos) {
                try {
                    String[] urlV = url.split("/");
                    urlV[3] = URLDecoder.decode(URLDecoder.decode(urlV[3],
                            StandardCharsets.UTF_8.toString()), "ISO-8859-1");
                    File file = new File(
                            environment.getProperty("files_path")
                                    + urlV[3]);
                    helper.addAttachment(urlV[3], file);

                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        emailSender.send(message);
    }
    
    @Async
    public void sendMessageRespuesta(JavaMailSenderImpl emailSender, Mail mail, Set<String> archivos)
            throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        final String html = this.htmlTemplateEngine.process("email-respuesta", context);
        final String text = this.textTemplateEngine.process("email-respuesta", context);

        helper.setTo(mail.getTo());

        if (mail.getCc() != null && mail.getCc().length > 0)
            helper.setCc(mail.getCc());
        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        if (archivos != null) {
            for (String url : archivos) {
                try {
                    String[] urlV = url.split("/");
                    urlV[3] = URLDecoder.decode(URLDecoder.decode(urlV[3],
                            StandardCharsets.UTF_8.toString()), "ISO-8859-1");
                    File file = new File(
                            environment.getProperty("files_path")
                                    + urlV[3]);
                    helper.addAttachment(urlV[3], file);

                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        emailSender.send(message);
    }

    @Async
    public void sendReenviar(JavaMailSenderImpl emailSender, Mail mail, List<String> archivos)
            throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        final String html = this.htmlTemplateEngine.process("email-reenviar", context);
        final String text = this.textTemplateEngine.process("email-reenviar", context);

        helper.setTo(mail.getTo());

        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        if (archivos != null) {
            for (String url : archivos) {
                try {
                    String[] urlV = url.split("/");
                    urlV[3] = URLDecoder.decode(URLDecoder.decode(urlV[3],
                            StandardCharsets.UTF_8.toString()), "ISO-8859-1");
                    File file = new File(environment.getProperty("files_path") + urlV[3]);
                    helper.addAttachment(urlV[3], file);
                } catch (MessagingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {

                }
            }
            ;
        }

        emailSender.send(message);
    }
    
    @Async
    public void sendMessageAsignacionNotificar(JavaMailSenderImpl emailSender, Mail mail, List<String> archivos) throws MessagingException, IOException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        final String html = this.htmlTemplateEngine.process("email-asignacion-notificar", context);
        final String text = this.textTemplateEngine.process("email-asignacion-notificar", context);

        helper.setTo(mail.getTo());
        if(mail.getCc()!=null && mail.getCc().length>0)
        	helper.setCc(mail.getCc());
        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());
         
        if (archivos != null) {
	        for (String url : archivos) {
				try {
					String[] urlV = url.split("/");
					urlV[3] = URLDecoder.decode(URLDecoder.decode(urlV[3], 
                            StandardCharsets.UTF_8.toString()), "ISO-8859-1");
					File file = new File(environment.getProperty("files_path")+urlV[3]);
					helper.addAttachment(urlV[3], file);
					
				}  catch (MessagingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
        }

        emailSender.send(message);
    }
    
    @Async
    public void sendMessageReasignar(JavaMailSenderImpl emailSender, Mail mail) throws MessagingException, IOException{
    	MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        final String html = this.htmlTemplateEngine.process("email-reasignado", context);
        final String text = this.textTemplateEngine.process("email-reasignado", context);

        helper.setTo(mail.getTo());
        if(mail.getCc()!=null && mail.getCc().length>0) {
        	helper.setCc(mail.getCc());
        }
        helper.setText(text, html);
        helper.setSubject(mail.getSubject());
        helper.setFrom(mail.getFrom());

        emailSender.send(message);
    }
}
