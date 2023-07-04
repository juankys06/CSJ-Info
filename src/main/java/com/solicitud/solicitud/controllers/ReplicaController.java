package com.solicitud.solicitud.controllers;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.lang.String;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import com.solicitud.solicitud.models.Archivos;
import com.solicitud.solicitud.models.Replica;
import com.solicitud.solicitud.models.Solicitudes;
import com.solicitud.solicitud.repository.ArchivosRepository;
import com.solicitud.solicitud.repository.ReplicaRepository;
import com.solicitud.solicitud.repository.SolicitudesRepository;
import com.solicitud.solicitud.repository.StatusRepository;
import com.solicitud.solicitud.service.EncuestaService;

import net.bytebuddy.agent.builder.AgentBuilder.InitializationStrategy.SelfInjection.Split;

import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.mail.EmailConfiguration;
import com.solicitud.solicitud.mail.EmailService;
import com.solicitud.solicitud.mail.Mail;

@RestController
public class ReplicaController {
    @Autowired
    private TemplateEngine htmlTemplateEngine;

    @Autowired
    private EmailService emailService;

    @Autowired
    ReplicaRepository replicaRepository;

    @Autowired
    SolicitudesRepository solicitudesRepository;

    @Autowired
    private ArchivosRepository archivosRepository;

    private Archivos archivos;

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    EncuestaService encuestaService;

    @GetMapping("/replica")
    public List<Replica> getReplica() {
        return replicaRepository.findAll();
    }

    @GetMapping("/replica/{id}")
    public Replica getReplicaById(@PathVariable(value = "id") Long RespuestaId) {
        return replicaRepository.findById(RespuestaId)
                .orElseThrow(() -> new ResourceNotFoundException("Replica", "id", RespuestaId));
    }

    @Transactional
    @PostMapping("solicitudes/{solicitudesId}/replica")
    public Replica createReplica(@PathVariable(value = "solicitudesId") Long solicitudesId,
            @RequestParam String respuesta,
            @RequestParam String correo_solicitante,
            @RequestParam String asunto,
            @RequestParam(required = false) String cc,
            @RequestParam(required = false) Set<String> url,
            @RequestParam(required = false) Long statusId) throws Exception {

        if (cc.equals("undefined"))
            cc = "";

        if (statusId.equals(null))
            statusId = (long) 3;

        Replica replica = new Replica(respuesta, asunto, cc);

        Mail mail = new Mail();

        Solicitudes solicitud = solicitudesRepository.findById(solicitudesId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitudes", "id", solicitudesId));
        replica.setSolicitudes(solicitud);
        mail.setTo(correo_solicitante);
        if (!replica.getCc().isEmpty()) {
            String[] vcc = replica.getCc().split(",");
            mail.setCc(vcc);
        }

        SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String formattedDateTime = solicitud.getCreateDateTime().format(formatter);

        Date date = inputFormat.parse(formattedDateTime);
        String currentYear = getYearFormat.format(date);

        statusRepository.findById(statusId).ifPresent(status -> {
            solicitud.setStatus(status);
        });

        solicitudesRepository.saveAndFlush(solicitud);
        replicaRepository.save(replica);

        mail.setFrom(solicitud.getFormulario().getEmail());
        mail.setSubject("Caso: " + currentYear + "-" + solicitudesId + " SSI -- " + replica.getAsunto());

        Map<String, Object> model = new HashMap<String, Object>();

        if (replica.getAsunto() != null) {
            model.put("asunto", replica.getAsunto());
        } else {
            model.put("asunto", false);
        }
        model.put("numero", solicitudesId);
        model.put("respuesta", replica.getRespuesta());
        model.put("fecha", currentYear);
        model.put("email", correo_solicitante);

        if (!encuestaService.getEncuestas(null, solicitud.getFormulario().getId()).isEmpty())
            model.put("url", encuestaService.getEncuestas(null, solicitud.getFormulario().getId()).get(0).getUrl());
        else
            model.put("url", false);

        Set<String> strFiles = url;
        if (url != null) {

            strFiles.forEach(url1 -> {
                Archivos archivos;
                try {
                    archivos = new Archivos(URLDecoder.decode(URLDecoder.decode(url1,
                            StandardCharsets.UTF_8.toString()), "ISO-8859-1"), null, null);
                    this.archivos = archivos;

                    replicaRepository.findById(replica.getId()).map(replicas -> {
                        this.archivos.setReplicas(replicas);
                        ;
                        return this.archivos;
                    }).orElseThrow(() -> new ResourceNotFoundException("Replica", "id", replica.getId()));

                    archivosRepository.save(archivos);
                    // mail.setFile(new File(new URL(url1).getPath()));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }

        solicitud.setContenidoAppend(replica.getRespuesta());
        solicitudesRepository.saveAndFlush(solicitud);

        mail.setModel(model);

        emailService.sendMessageRespuesta(EmailConfiguration.getJavaMailSender(solicitud.getFormulario().getEmail(),
                solicitud.getFormulario().getEmailPassword()), mail, strFiles);

        return replica;
    }

    @PutMapping("solicitudes/{solicitudesId}/reenviar")
    public Solicitudes createReenviar(@PathVariable(value = "solicitudesId") Long solicitudesId,
            @RequestParam String correo_solicitante) throws Exception {

        Mail mail = new Mail();

        Solicitudes solicitud = solicitudesRepository.findById(solicitudesId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitudes", "id", solicitudesId));
        mail.setTo(correo_solicitante);

        SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String formattedDateTime = solicitud.getCreateDateTime().format(formatter);

        Date date = inputFormat.parse(formattedDateTime);
        String currentYear = getYearFormat.format(date);

        statusRepository.findById((long) 12).ifPresent(status -> {
            solicitud.setStatus(status);
        });

        mail.setFrom(solicitud.getFormulario().getEmail());
        mail.setSubject("RV: Caso: " + currentYear + "-" + solicitudesId + " SSI -- " + solicitud.getAsunto());

        Map<String, Object> contenidoSolicitud = new HashMap<String, Object>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode contenidoS = mapper.readTree(solicitud.getContenido());
        Iterator<Map.Entry<String, JsonNode>> it = contenidoS.getFields();

        while (it.hasNext()) {
            Map.Entry<String, JsonNode> field = it.next();
            if (field.getKey() == "Contenido") {
                contenidoSolicitud.put(field.getKey(), field.getValue().asText().split("(\\|\\|,\\|\\|)"));
            } else {
                contenidoSolicitud.put(field.getKey(), field.getValue().asText());
            }
        }

        Map<String, Object> model = new HashMap<String, Object>();

        if (solicitud.getAsunto() != null) {
            model.put("asunto", solicitud.getAsunto());
        } else {
            model.put("asunto", false);
        }
        model.put("numero", solicitudesId);
        model.put("contenido", contenidoSolicitud);
        model.put("fecha", currentYear);
        model.put("email", correo_solicitante);

        if (!encuestaService.getEncuestas(null, solicitud.getFormulario().getId()).isEmpty())
            model.put("url", encuestaService.getEncuestas(null, solicitud.getFormulario().getId()).get(0).getUrl());
        else
            model.put("url", false);

        mail.setModel(model);

        List<String> strFile = new ArrayList<>();

        archivosRepository.findBySolicitudesId(solicitudesId).forEach(url -> {
            String url1 = url.getUrl();
            strFile.add(url1);
        });

        emailService.sendReenviar(EmailConfiguration.getJavaMailSender(solicitud.getFormulario().getEmail(),
                solicitud.getFormulario().getEmailPassword()), mail, strFile);

        solicitudesRepository.saveAndFlush(solicitud);
        return solicitud;
    }
}
