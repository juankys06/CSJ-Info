package com.solicitud.solicitud.controllers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Flags.Flag;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.OrTerm;
import javax.validation.Valid;
import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solicitud.solicitud.SolicitudApplication;
import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.mail.DeniedEmail;
import com.solicitud.solicitud.mail.EmailConfiguration;
import com.solicitud.solicitud.mail.EmailService;
import com.solicitud.solicitud.mail.Mail;
import com.solicitud.solicitud.models.Archivos;
import com.solicitud.solicitud.models.Formulario;
import com.solicitud.solicitud.models.Solicitudes;
import com.solicitud.solicitud.models.Status;
import com.solicitud.solicitud.repository.ArchivosRepository;
import com.solicitud.solicitud.repository.DeniedEmailRepository;
import com.solicitud.solicitud.repository.StatusRepository;
import com.solicitud.solicitud.service.SolicitudesService;

@RestController
@PreAuthorize("hasAuthority('Administrador')")
public class EmailController {
	@Autowired
	private DeniedEmailRepository deniedEmailRepository;
	@Autowired
	private SolicitudesService solicitudesService;
	@Autowired
	private ArchivosRepository archivosRepository;
	@Autowired
	private StatusRepository statusRepository;
	@Autowired
	private EmailService emailService;
	
	@GetMapping("/mail/denyList")
	public List<DeniedEmail> getDeniedEmails(){
		return deniedEmailRepository.findAll();
	}
	
	@PostMapping("/mail/denyMail")
	public DeniedEmail denyEmail(@RequestParam @Email String email) {
		return deniedEmailRepository.save(new DeniedEmail(email));
	}
	
	@PutMapping("/mail/denyMail/{id}")
	public DeniedEmail editDeniedEmail(@PathVariable(value = "id") Long deniedEmailId, 
									   @RequestParam @Email String email)
	{
		DeniedEmail deniedEmail = deniedEmailRepository.findById(deniedEmailId).orElseThrow(() -> new ResourceNotFoundException("", "id", deniedEmailId));
		
		deniedEmail.setEmail(email);
		
		return deniedEmailRepository.save(deniedEmail);
	}
	
	@DeleteMapping("/mail/denyMail/{id}")
	public ResponseEntity<DeniedEmail> deleteDenyEmail(@PathVariable(name = "id") Long deniedEmailId){
		try {
			deniedEmailRepository.deleteById(deniedEmailId);
		} catch (EmptyResultDataAccessException exception) {
			throw new ResourceNotFoundException("Email", "id", deniedEmailId);
		}
		
		return ResponseEntity.noContent().build();
	}
	/*
	@GetMapping("/download_emails")
	public ResponseEntity<?> downloadEmail(@RequestParam(value = "formulario") Long formularioId) throws Exception {
		// POP3
		// String protocol = "pop3";
		// String host = "pop.gmail.com";
		// String port = "995";
		Formulario formulario = formularioRepository.findById(formularioId).orElseThrow(() -> new ResourceNotFoundException("Formulario", "id", formularioId));
		// IMAP
		String protocol = "imap";
		 
		//EmailReceiver receiver = new EmailReceiver();
		downloadEmails(protocol, formulario);
		
		return ResponseEntity.ok("Bandeja de correos actualizada");
	}
	*/
	 
	private Properties getServerProperties(String protocol, String host,String port) {
		Properties properties = new Properties();
		
		// server setting
		properties.put(String.format("mail.%s.host", protocol), host);
		properties.put(String.format("mail.%s.port", protocol), port);
		 
		// SSL setting
		properties.setProperty(
		String.format("mail.%s.socketFactory.class", protocol),
		"javax.net.ssl.SSLSocketFactory");
		properties.setProperty(
		String.format("mail.%s.socketFactory.fallback", protocol),
		"false");
		properties.setProperty(
		String.format("mail.%s.socketFactory.port", protocol),
		String.valueOf(port));
		 
		return properties;
	}
	 
    /**
     * Downloads new messages and fetches details for each message.
     * @param protocol
     * @param host
     * @param port
     * @param userName
     * @param password
     * @throws Exception 
     */
	public void downloadEmails(String protocol, Formulario formulario) throws Exception
	{
		//Properties properties = getServerProperties(protocol, formulario.imapHost, formulario.imapPort.toString());
		System.out.println(">>>>>"+protocol);
		Properties properties = getServerProperties(protocol, "","");
		Session session = Session.getDefaultInstance(properties);
 
		try {
			// connects to the message store
			Store store = session.getStore(protocol);
			store.connect(formulario.getEmail(), formulario.getEmailPassword());
			// opens the inbox folder
			Folder folderInbox = store.getFolder("INBOX");
			folderInbox.open(Folder.READ_WRITE);
			// folderInbox.getMessage(myMsgID).getContent();
			
			//-- Build DeniedEmail filter
			List<DeniedEmail> deniedEmails = deniedEmailRepository.findAll();
			OrTerm denyListFilter = null;
			
			if(!deniedEmails.isEmpty()) {
				FromTerm[] denyList = new FromTerm[deniedEmails.size()];
				for(int i = 0; i < deniedEmails.size() ; i++)
					denyList[i] = new FromTerm(new InternetAddress(deniedEmails.get(i).getEmail()));
				
				denyListFilter = new OrTerm(denyList);
			}
			
			// fetches new messages from server
			FlagTerm unseen = new FlagTerm(new Flags(Flag.SEEN), false); //-- Filtro para los no-leidos
			Message[] messages;
			
			if(denyListFilter != null)
				messages = folderInbox.search(new AndTerm(unseen, new NotTerm(denyListFilter)));
			else
				messages = folderInbox.search(unseen);
			
			Status state_in_process = statusRepository.findById((long) 1).get();
			
			for (int i=0; i < messages.length; i++) {
				Message msg = messages[i];
				Address[] fromAddress = msg.getFrom();
				String from = fromAddress[0].toString();
				String subject = msg.getSubject();
				String toList = parseAddresses(msg
						.getRecipients(RecipientType.TO));
				String ccList = parseAddresses(msg
						.getRecipients(RecipientType.CC));
				String sentDate = msg.getSentDate().toString();
 
				String messageContent = "";
				String attachFiles = "";
				Object content = msg.getContent();
				
				messageContent = getTextFromMessage(msg);
                  
                if (content instanceof MimeMultipart) {
                	//MimeMultipart multipart = (MimeMultipart) content;
                    Multipart multipart = (Multipart) msg.getContent();
                    
                    if (multipart.getCount() > 0) {
                    	BodyPart part = multipart.getBodyPart(0);
                    	content = part.getContent();
                    
                        for (int j=0;j<multipart.getCount();j++) {
                        	//Part unaParte = multipart.getBodyPart(j);
                        	MimeBodyPart part2 = (MimeBodyPart) multipart.getBodyPart(j);
                        	
                        	if (Part.ATTACHMENT.equalsIgnoreCase(part2.getDisposition())) {
								// this part is attachment
								String fileName = part2.getFileName();
								attachFiles += fileName.replace(" ","-") + ",";
								part2.saveFile(SolicitudApplication.FILES_PATH + File.separator + fileName);
                           }
                        }
                        
                        if (attachFiles.length() > 1)
                        	attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                    }
                }

                String[] fromArray= from.split("<");
                String[] asunto= subject.split(":");
                
                HashMap<String, String> jsonContenido = new HashMap<>();
	       		jsonContenido.put("Nombre", fromArray[0]);
	       		jsonContenido.put("Correo Electronico", fromArray[1].replace(">","").trim());
	       		jsonContenido.put("Asunto", subject);
	       		
                if(fromArray[1].replace(">","").trim().compareToIgnoreCase("postmaster@microsoft.com")!=0) {
                if(asunto[0].compareToIgnoreCase("re")==0) {
					asunto = asunto[2].split(" ");
					long idSolicitud = Long.parseLong(asunto[1]);
					
					String contenido = messageContent.toString().replace(">","");
					jsonContenido.put("Contenido", contenido);
					String json = new ObjectMapper().writeValueAsString(jsonContenido);
					
					if(!solicitudesService.existsById(idSolicitud))
					    throw new ResourceNotFoundException("Solicitudes ","id",idSolicitud);
					
					solicitudesService.findById(idSolicitud).map(solicitud -> {
						solicitud.setContenido(json);
						@Valid
						Solicitudes myobj = solicitudesService.saveAndFlush(solicitud);
					    int solicitudesRe= myobj.getRe(); 
						
					    solicitud.setRe(solicitudesRe+1);
						 
					    return solicitudesService.save(solicitud);
					}).orElseThrow(() -> new ResourceNotFoundException("Solicitudes ", "id", idSolicitud));
                }else {
	                // print out details of each message
	                // System.out.println("Message #" + (i + 1) + ":");
	                // System.out.println("\t nombre: " + fromArray[0]);
	                // System.out.println("\t correo del solicitante: " + fromArray[1].replace(">","").trim());
	                // System.out.println("\t To: " + toList);
	                // System.out.println("\t CC: " + ccList);
	                // System.out.println("\t Subject: " + subject);
	                // System.out.println("\t Sent Date: " + sentDate);
	                // System.out.println("\t Message: " + messageContent);
	                //msg.writeTo(System.out);
	                
	                Solicitudes solicitud = new Solicitudes();
	                
	                solicitud.setStatus(state_in_process);
	                solicitud.setAsunto(subject);
	       			solicitud.setFormulario(formulario);
	       			solicitud.setCorreo_solicitante(fromArray[1].replace(">","").trim());
	       			solicitud.setContenido(messageContent);
	       			
	       			jsonContenido.put("Contenido", messageContent);
					
					Mail mail = new Mail();
					mail.setFrom(formulario.getEmail());
					mail.setTo(solicitud.getCorreo_solicitante());
					mail.setSubject("Solicitud de Informacion");
					
					Map < String , Object> model = new HashMap<String, Object>();
					model.put("numero", solicitud.getId());
					model.put("asunto", solicitud.getAsunto());
					model.put("status", "En Proceso");
					model.put("contenido", jsonContenido);
					
					if(attachFiles != "") {
						String[] strFile = attachFiles.split(",");
						String[] fileDownloadUri= new String[strFile.length];
       	            	
						for(int cont=0; cont<strFile.length; cont++ ) {
							fileDownloadUri[cont] = ServletUriComponentsBuilder.fromCurrentContextPath()
									.path("/api/download/")
									.path(strFile[cont])
									.toUriString();
						}
       	            	
						List<String> strFiles = Arrays.asList(fileDownloadUri);
						model.put("archivos", strFiles);
       	            	
						strFiles.forEach(url -> {
							Archivos archivos;
                            try {
                                archivos = new Archivos(URLDecoder.decode(URLDecoder.decode(url,
                                        StandardCharsets.UTF_8.toString()), "ISO-8859-1"), null, null);
                                archivos.setSolicitudes(solicitud);
                                archivosRepository.save(archivos);
                            } catch (UnsupportedEncodingException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
						});  

					} else
						model.put("archivos", false);
					    
					mail.setModel(model);
					emailService.sendSimpleMessage(EmailConfiguration.getJavaMailSender(formulario.getEmail(), formulario.getEmailPassword()), mail);
					solicitudesService.save(solicitud);
                } 
                }else {
                	String[] correoError= messageContent.split(" ");
                	System.out.println(correoError);
                	//Solicitudes solicitud = (Solicitudes) solicitudesService.findByCorreo_Asignacion("");
                }
                messages[i].setFlag(Flag.SEEN, true);
			}
			
			// disconnect
			folderInbox.close(false);
			store.close();
		} catch (NoSuchProviderException ex) {
			System.out.println("No provider for protocol: " + protocol);
			ex.printStackTrace();
		} catch (MessagingException ex) {
			System.out.println("Could not connect to the message store");
			ex.printStackTrace();
		}
	}

	private String getTextFromMessage(Message message) throws MessagingException, IOException {
	    String result = "";
	    if (message.isMimeType("text/plain")) {
	        result = message.getContent().toString();
	    } else if (message.isMimeType("multipart/*")) {
	        MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
	        result = getTextFromMimeMultipart(mimeMultipart);
	    }
	    return result;
	}

	private String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
	    String result = "";
	    int count = mimeMultipart.getCount();
	    for (int i = 0; i < count; i++) {
	        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	        if (bodyPart.isMimeType("text/plain")) {
	            result = result + "\n" + bodyPart.getContent();
	            break; // without break same text appears twice in my tests
//	        } else if (bodyPart.isMimeType("text/html")) {
//	            String html = (String) bodyPart.getContent();
//	            result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
	        } else if (bodyPart.getContent() instanceof MimeMultipart){
	            result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	        }
	    }
	    return result;
	}
    /**
     * Returns a list of addresses in String format separated by comma
     *
     * @param address an array of Address objects
     * @return a string represents a list of addresses
     */
	private String parseAddresses(Address[] address) {
		String listAddress = "";
		 
		if (address != null)
			for (int i = 0; i < address.length; i++)
				listAddress += address[i].toString() + ", ";
		
		if (listAddress.length() > 1)
			listAddress = listAddress.substring(0, listAddress.length() - 2);
		 
		return listAddress;
	}
}
