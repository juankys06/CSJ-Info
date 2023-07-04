package com.solicitud.solicitud.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.solicitud.solicitud.SolicitudApplication;
import com.solicitud.solicitud.exception.ExpiredAuthorizationCodeException;
import com.solicitud.solicitud.exception.ExpiredTokenException;
import com.solicitud.solicitud.exception.MSQueryException;
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
import com.solicitud.solicitud.repository.FormularioRepository;
import com.solicitud.solicitud.repository.SolicitudesRepository;
import com.solicitud.solicitud.repository.StatusRepository;
import com.solicitud.solicitud.service.SolicitudesService;

@RestController
public class MSGraphController {
    public static final String headerPlainTextKey = "Prefer";
    public static final String headerPlainTextValue = "outlook.body-content-type='html'";

    public static final String clientID = "05ffc59f-06b9-4d55-9521-dc43d97b5365";
    public static final String clientSecret = "39.PCPL8r3D-Cf_jhcUhJm7ks98rqF.1_M";
    public static final String grantType = "password";
    // -- public static final String scope = "offline_access user.read mail.read";
    public static final String scope = "https://graph.microsoft.com/.default";
    public static final String tenant = "622cba98-80f8-41f3-8df5-8eb99901598b";

    @Autowired
    private DeniedEmailRepository deniedEmailRepository;
    @Autowired
    private SolicitudesRepository solicitudesRepository;
    @Autowired
    private FormularioRepository formularioRepository;
    @Autowired
    private SolicitudesService solicitudesService;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ArchivosRepository archivosRepository;

    @Transactional
    @PostMapping("/download_emails")
    public ResponseEntity<?> downloadEmails(@RequestParam(value = "formulario") Long formularioId)
            throws MSQueryException, MessagingException, IOException, URISyntaxException, ParseException {
        Formulario formulario = formularioRepository.findById(formularioId)
                .orElseThrow(() -> new ResourceNotFoundException("Formulario", "Id", formularioId));

        if (formulario.getToken() != null) {
            System.out.println("Existe token de sesion");
            try {
                storeUnreadMessages(formulario);
            } catch (ExpiredTokenException ex) {
                System.out.println("Existe token de sesion pero expiro");
                storeAuthToken(formulario);
                storeUnreadMessages(formulario);
            }
        } else {
            System.out.println("No existe token de sesion");
            storeAuthToken(formulario);
            storeUnreadMessages(formulario);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        return new ResponseEntity<String>("{'message':'¡La bandeja de entrada se ha actualizado!'}".replace('\'', '"'),
                responseHeaders, HttpStatus.OK);
    }

    public void downloadEmailsT(Long formularioId)
            throws MSQueryException, MessagingException, IOException, URISyntaxException, ParseException {
        Formulario formulario = formularioRepository.findById(formularioId)
                .orElseThrow(() -> new ResourceNotFoundException("Formulario", "Id", formularioId));

        if (formulario.getToken() != null) {
            System.out.println("Existe token de sesion");
            try {
                storeUnreadMessages(formulario);
            } catch (ExpiredTokenException ex) {
                System.out.println("Existe token de sesion pero expiro");
                storeAuthToken(formulario);
                System.out.println("Almacenando mensajes sin leer");
                storeUnreadMessages(formulario);
                System.out.println("Mensajes procesados");
            }
        } else {
            System.out.println("No existe token de sesion");
            storeAuthToken(formulario);
            storeUnreadMessages(formulario);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
    }

    /**
     * 
     * @param code
     * @param formulario
     * @throws ClientProtocolException
     * @throws IOException
     * @throws URISyntaxException
     * @throws ExpiredAuthorizationCodeException
     * @throws MSQueryException
     */
    private void storeAuthToken(Formulario formulario)
            throws ClientProtocolException, IOException, URISyntaxException, MSQueryException {
        HttpPost apiRequest = new HttpPost("https://login.microsoftonline.com/" + tenant + "/oauth2/v2.0/token");
        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

        urlParameters.add(new BasicNameValuePair("client_id", clientID));
        urlParameters.add(new BasicNameValuePair("scope", scope));
        urlParameters.add(new BasicNameValuePair("grant_type", grantType));
        urlParameters.add(new BasicNameValuePair("client_secret", clientSecret));
        urlParameters.add(new BasicNameValuePair("userName", formulario.getEmail()));
        urlParameters.add(new BasicNameValuePair("password", formulario.getEmailPassword()));

        apiRequest.setEntity(new UrlEncodedFormEntity(urlParameters));

        Any jsonResponse;

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(apiRequest)) {
            if (response.getStatusLine().getStatusCode() != 200)
                throw new MSQueryException();

            jsonResponse = JsonIterator.deserialize(EntityUtils.toString(response.getEntity()));

            response.close();
        }

        formulario.setToken(jsonResponse.get("access_token").toString());

        formularioRepository.save(formulario);
    }

    /**
     * 
     * @param token
     * @param formulario
     * @throws MSQueryException
     * @throws MessagingException
     * @throws JsonProcessingException
     * @throws UnsupportedEncodingException
     * @throws ParseException
     */
    private void storeUnreadMessages(Formulario formulario)
            throws MSQueryException, MessagingException, JsonProcessingException, UnsupportedEncodingException,
            ParseException {
        HttpGet apiRequest = new HttpGet(
                "https://graph.microsoft.com/v1.0/me/mailFolders/Inbox/messages?$filter=isRead%20eq%20false&$select=body,from,subject,hasAttachments,toRecipients,ccRecipients,receivedDateTime&top=999&$count=true&$expand=attachments($select=id,name,microsoft.graph.fileAttachment/contentId)&$orderby=receivedDateTime");
        Any jsonResponse;

        apiRequest.addHeader("Authorization", "Bearer " + formulario.getToken());
        apiRequest.addHeader(headerPlainTextKey, headerPlainTextValue);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(apiRequest)) {
            if (response.getStatusLine().getStatusCode() == 401)
                throw new ExpiredTokenException();

            if (response.getStatusLine().getStatusCode() != 200)
                throw new MSQueryException();

            jsonResponse = JsonIterator.deserialize(EntityUtils.toString(response.getEntity()));

            response.close();
        } catch (IOException e) {
            throw new MSQueryException();
        }

        // List<Message> messages = new ArrayList<Message>();
        Status state_in_process = statusRepository.findById((long) 1).get();

        List<DeniedEmail> deniedEmails = deniedEmailRepository.findAll();

        if (jsonResponse.get("@odata.nextLink") != null)
            ; // -- Si contiene más registros...
        if (jsonResponse.get("value").size() > 0)
            for (Any message : jsonResponse.get("value")) {
                Boolean saltar_correo = false;
                if (message.get("attachments").size() > 0) {
                    for (Any attachment : message.get("attachments")) {
                        if (attachment.get("contentId").toBoolean() != true
                                && attachment.get("@odata.mediaContentType").toBoolean() != true && !attachment
                                        .get("@odata.type").toString().equals("#microsoft.graph.itemAttachment")) {
                            saltar_correo = true;
                            break;
                        }
                    }
                    if (saltar_correo == true) {
                        continue;
                    }
                }
                
                String sender = message.get("from").get("emailAddress").get("address").toString();

                // -- TODO: Check if this method gives any performance gain, that the other
                if (deniedEmails.stream().anyMatch(deniedEmail -> deniedEmail.getEmail().equals(sender)))
                    continue;

                LocalDateTime fecha = ZonedDateTime.parse(message.get("receivedDateTime").toString())
                        .withZoneSameInstant(ZoneId.of("America/Bogota")).toLocalDateTime();
                
                SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");
                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                String formattedfecha = fecha.format(formatter);

                String header = formatContentHeader(message.get("from"), message.get("toRecipients"), formattedfecha);
                String toCC = getEmailsRecipients(message.get("ccRecipients"), "CC: ");
                String recipients = header + toCC;

                // if(deniedEmailRepository.existsByEmail(sender))
                // continue;


                // Message msgToStore = new Message();
                String asunto = message.get("subject").toString();

                HashMap<String, String> jsonContenido = new HashMap<>();
                jsonContenido.put("Nombre", sender);
                jsonContenido.put("Correo Electronico", sender);
                jsonContenido.put("Asunto", asunto);

                String contenido_body = message.get("body").get("content").toString();
                contenido_body = recipients + contenido_body;
                Matcher safelink_matcher = Pattern.compile(
                        "(?=\\<https:).[^>]*>",
                        Pattern.CASE_INSENSITIVE).matcher(contenido_body);

                while (safelink_matcher.find()) {
                    String safelink = safelink_matcher.group();
                    MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(
                            safelink).build()
                            .getQueryParams();
                    List<String> url = queryParams.get("url");
                    if (url != null && url.size() > 0) {
                        String final_url = "<br><br><strong>Enlace adjunto:</strong> <a href=\""
                                + URLDecoder.decode(url.get(0), StandardCharsets.UTF_8.toString()) +
                                "\" target=\"_blank\">"
                                + URLDecoder.decode(url.get(0), StandardCharsets.UTF_8.toString()) + "</a><br>";
                        contenido_body = contenido_body.replaceFirst("(?=\\<https:).[^>]*>", final_url);
                        contenido_body = contenido_body.replaceFirst("(?=\\[https:).[^\\]]*\\]", "<br>");
                    }
                }

                jsonContenido.put("Contenido", contenido_body);
                String json = new ObjectMapper().writeValueAsString(jsonContenido);

                Solicitudes solicitud;

                // if (sender.compareToIgnoreCase("postmaster@outlook.com") != 0) {
                Matcher idSolicitud = Pattern.compile("[0-9\\-]{4}\\-[0-9]+", Pattern.CASE_INSENSITIVE)
                        .matcher(asunto);

                Long solicitudId = (long) 0;

                if (idSolicitud.find()) {
                    solicitudId = Long.parseLong(idSolicitud.group().split("-")[1]);
                }

                if (solicitudesRepository.existsById(solicitudId)) { // -- Ya existe la solicitud
                    try {
                        final Long solicitudId_error = solicitudId;

                        solicitud = solicitudesService.findById(solicitudId).orElseThrow(
                                () -> new ResourceNotFoundException("Solicitudes", "Id",
                                        solicitudId_error.toString()));

                        if (solicitud.getStatus().getId() == (long) 7) {
                            solicitud.setContenidoAppend(contenido_body, true);
                        } else {
                            solicitud.setContenidoAppend(contenido_body);
                        }

                        solicitud.setRe(solicitud.getRe() + 1);

                        if (solicitud.getStatus().getId() != (long) 10 ||
                                solicitud.getStatus().getId() != (long) 5 ||
                                solicitud.getStatus().getId() != (long) 9) {
                            solicitud.setStatus(statusRepository.findById((long) 1).get());
                        }

                        solicitud = solicitudesService.save(solicitud);

                        getAttachmentsSimple(message.get("id").toString(),
                                formulario.getToken(), message.get("attachments"), solicitud);

                    } catch (IllegalStateException e) { // -- Exception by Matcher.find()
                        System.out.println("Advertencia: se encontró un RE: sin algún código de solicitud.");
                        continue;
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                } else {
                    solicitud = new Solicitudes();

                    solicitud.setStatus(state_in_process);
                    solicitud.setAsunto(asunto);
                    solicitud.setFormulario(formulario);
                    solicitud.setCorreo_solicitante(sender);
                    solicitud.setContenido(json);
                    solicitud.setFecha(fecha);

                    solicitud = solicitudesService.save(solicitud);

                    String formattedDateTime = solicitud.getCreateDateTime().format(formatter);

                    String currentYear;
                    try {
                        Date date = inputFormat.parse(formattedDateTime);
                        currentYear = getYearFormat.format(date);

                    } catch (ParseException e) {
                        currentYear = new SimpleDateFormat("yyyy").format(new Date());
                    }

                    Mail mail = new Mail();
                    mail.setFrom(formulario.getEmail());
                    mail.setTo(solicitud.getCorreo_solicitante());
                    mail.setSubject(
                            "Caso: " + currentYear + "-" + solicitud.getId() + " "
                                    + solicitud.getFormulario().getDescripcion());

                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("numero", solicitud.getId());
                    model.put("asunto", solicitud.getAsunto());
                    model.put("status", "En proceso");
                    model.put("contenido", jsonContenido);
                    model.put("fecha", currentYear);

                    mail.setModel(model);

                    sendEmail(formulario, mail);

                    getAttachmentsSimple(message.get("id").toString(),
                            formulario.getToken(), message.get("attachments"), solicitud);
                }
                
                markAsRead(message.get("id").toString(), formulario.getToken());
            }
    }

    private String getEmailsRecipients(Any recipients, String tipo) {
        String formated_recipients = "";
        if (recipients.size() > 0) {
            formated_recipients = formated_recipients + " ----------------- <br>" + tipo;
            for (Any recipient : recipients) {
                String nombre = recipient.get("emailAddress").get("name").toString();
                String address = recipient.get("emailAddress").get("address").toString();
                formated_recipients = formated_recipients + nombre + " [" + address + "], ";
            }

            formated_recipients = formated_recipients + "<br> ----------------- <br><br>";
        }

        return formated_recipients;
    }

    private String formatContentHeader(Any sender, Any recipients, String fecha) {
        String formated_recipients = "";

        String nombre = sender.get("emailAddress").get("name").toString();
        String address = sender.get("emailAddress").get("address").toString();

        formated_recipients = formated_recipients + " ----------------- <br>";
        formated_recipients = formated_recipients + "Fecha: " + fecha + "<br>";
        formated_recipients = formated_recipients + "De: " + nombre + " [" + address + "]<br>";

        if (recipients.size() > 0) {
            formated_recipients = formated_recipients + "Para: ";
            for (Any recipient : recipients) {
                String rnombre = recipient.get("emailAddress").get("name").toString();
                String raddress = recipient.get("emailAddress").get("address").toString();
                formated_recipients = formated_recipients +  rnombre + " [" + raddress + "], ";
            }

            formated_recipients = formated_recipients + "<br> ----------------- <br><br>";
        }

        return formated_recipients;
    }

    /**
     * Marca el mensaje como leído
     * 
     * @param messageId
     * @param authToken
     * @throws UnsupportedEncodingException
     */
    @Async
    public void markAsRead(String messageId, String authToken) throws UnsupportedEncodingException {
        HttpPatch apiRequest = new HttpPatch("https://graph.microsoft.com/v1.0/me/messages/" + messageId);

        apiRequest.addHeader("Authorization", "Bearer " + authToken);
        apiRequest.addHeader("Content-Type", "application/json");

        String requestBody = "{'isRead':true}".replace('\'', '"');
        apiRequest.setEntity(new StringEntity(requestBody));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(apiRequest)) {
            if (response.getStatusLine().getStatusCode() != 200)
                throw new IOException("Hubo un problema al actualizar el mensaje como leído.");

            response.close();
        } catch (IOException e) {
            System.err.println("Fallo al marcar el correo como no leído");
            e.printStackTrace();
        }
    }

    @Async
    public void getAttachmentsSimple(String messageId, String authToken, Any attachments, Solicitudes solicitud) {
        if (attachments.size() > 0) {
            for (Any attachment : attachments)
                if (!attachment.get("@odata.type").toString().equals("#microsoft.graph.itemAttachment")) {
                    storeAttachment(messageId, authToken, attachment.get("id").toString(),
                            attachment.get("name").toString(),
                            solicitud);

                }
        }
    }

    @Async
    public void getAttachments(String messageId, String authToken, Solicitudes solicitud) {
        HttpGet apiRequest = new HttpGet("https://graph.microsoft.com/v1.0/me/mailFolders/Inbox/messages/" + messageId
                + "/attachments?$select=name");
        Any jsonResponse = null;

        apiRequest.addHeader("Authorization", "Bearer " + authToken);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(apiRequest)) {
            if (response.getStatusLine().getStatusCode() != 200)
                throw new IOException("Hubo un problema al descargar los archivos adjuntos.");

            jsonResponse = JsonIterator.deserialize(EntityUtils.toString(response.getEntity()));
            response.close();
        } catch (IOException e) {
            System.err.println("Fallo en getAttachment");
            e.printStackTrace();
        }

        Any attachments = jsonResponse.get("value");

        if (attachments.size() > 0) {
            for (Any attachment : jsonResponse.get("value"))
                storeAttachment(messageId, authToken, attachment.get("id").toString(),
                        attachment.get("name").toString(),
                        solicitud);
        }

    }

    public String remove(String texto) {
        String normalizer = Normalizer.normalize(texto, Normalizer.Form.NFD);
        return normalizer.replaceAll("[^\\p{ASCII}]", "");
    }

    public void storeAttachment(String messageId, String authToken, String attachmentId, String attachmentName,
            Solicitudes solicitud) {
        HttpGet apiRequest = new HttpGet("https://graph.microsoft.com/v1.0/me/mailFolders/Inbox/messages/" + messageId
                + "/attachments/" + attachmentId + "/$value");
        apiRequest.addHeader("Authorization", "Bearer " + authToken);

        String sanitized_name = this.remove(attachmentName);

        String filePath = SolicitudApplication.FILES_PATH + File.separator + sanitized_name;
        String fileURI = "/api/download/" + sanitized_name;

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(apiRequest)) {
            if (response.getStatusLine().getStatusCode() != 200)
                throw new IOException("Hubo un problema al descargar el archivo " + sanitized_name);

            response.getEntity().writeTo(new FileOutputStream(filePath));
            response.close();
        } catch (IOException e) {
            System.err.println("Fallo en storeAttachment");
            e.printStackTrace();
        }

        Archivos archivo = new Archivos(fileURI, solicitud);
        archivosRepository.save(archivo);
    }

    @Async
    public void sendEmail(Formulario formulario, Mail mail) {
        try {
            emailService.sendSimpleMessage(
                    EmailConfiguration.getJavaMailSender(formulario.getEmail(), formulario.getEmailPassword()), mail);
        } catch (IOException e) {
            System.err.println("No se pudo enviar el correo a " + mail.getTo());
            e.printStackTrace();
        } catch (MessagingException e) {
            System.err.println("No se pudo enviar el correo a " + mail.getTo());
            System.err.println("Algo malo en el formato?");
            e.printStackTrace();
        }
    }

    public class Message {
        public String id;

        public boolean hasAttachments;
        public String internetMessageId;

        public String subject;
        public String parentFolderId;
        public String coversationId;
        public String coversationIndex;
        public boolean isRead;
        public boolean isDraft;

        public String contentType;
        public String bodyContent;

        public String from;
        public String sender;
        public List<String> toRecipients;

        public Message() {
        }

        /**
         * Indica si el mensaje es la respuesta a una solicitud.
         * 
         * @return <b>true</b> si contiene RE en el asunto
         */
        public boolean isAnswer() {
            Matcher match = Pattern.compile("^re", Pattern.CASE_INSENSITIVE).matcher(subject);

            return match.find();
        }

        /**
         * Marca el mensaje como leído
         * 
         * @param messageId
         * @param authToken
         * @throws IOException Algún error de entrada
         */
        public void markAsRead(String messageId, String authToken) throws IOException {
            HttpPatch apiRequest = new HttpPatch("https://graph.microsoft.com/v1.0/me/messages/" + messageId);

            apiRequest.addHeader("Authorization", "Bearer " + authToken);
            apiRequest.addHeader("Content-Type", "application/json");

            String requestBody = "{'isRead':false}".replace('\'', '"');
            apiRequest.setEntity(new StringEntity(requestBody));

            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                    CloseableHttpResponse response = httpClient.execute(apiRequest)) {
                if (response.getStatusLine().getStatusCode() != 200)
                    throw new IOException("Hubo un problema al actualizar el mensaje como leído.");

                response.close();
            }
        }
    }
}