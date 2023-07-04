package com.solicitud.solicitud.controllers;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.validator.constraints.Length;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import com.solicitud.solicitud.mail.EmailConfiguration;
import com.solicitud.solicitud.mail.EmailService;
import com.solicitud.solicitud.mail.Mail;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.util.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.Email;

import com.solicitud.solicitud.models.Archivos;
import com.solicitud.solicitud.models.Auditoria;
import com.solicitud.solicitud.models.Departamento;
import com.solicitud.solicitud.models.Encuesta;
import com.solicitud.solicitud.models.Entidad;
import com.solicitud.solicitud.models.Municipio;
import com.solicitud.solicitud.security.SystemLoggedInUserAuditorAware;
import com.solicitud.solicitud.service.EncuestaService;
import com.solicitud.solicitud.service.SolicitudesService;
import com.solicitud.solicitud.models.Solicitudes;
import com.solicitud.solicitud.models.intermediate.EntidadDepartamentoMunicipio;
import com.solicitud.solicitud.repository.EntidadRepository;
import com.solicitud.solicitud.repository.FormularioRepository;
import com.solicitud.solicitud.repository.MunicipioRepository;
import com.solicitud.solicitud.repository.SolicitudesRepository;
import com.solicitud.solicitud.repository.StatusRepository;
import com.solicitud.solicitud.repository.AuditoriaRepository;
import com.solicitud.solicitud.repository.DepartamentoReposytory;
import com.solicitud.solicitud.repository.EntidadDepartamentoMunicipioRepository;
import com.solicitud.solicitud.repository.ArchivosRepository;
import com.liferay.portal.kernel.json.JSONException;
import com.solicitud.solicitud.SolicitudApplication;
import com.solicitud.solicitud.exception.ResourceNotFoundException;
import com.solicitud.solicitud.payload.request.SolicitudesRequest;

@RestController
public class SolicitudesController {
    private static final short EN_ESPERA = 1;
    private static final short ASIGNADO = 2;
    // private static final short GESTION_TRAMITE = 3;
    private static final short RESOLUCION = 3;

    @Autowired
    SystemLoggedInUserAuditorAware systemLoggedInUserAuditorAware;

    @Autowired
    private EmailService emailService;

    private Archivos archivos;

    @Autowired
    private EntidadRepository entidadRepository;

    @Autowired
    private DepartamentoReposytory departamentoReposytory;

    @Autowired
    private EntidadDepartamentoMunicipioRepository entidadDepartamentoMunicipioRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private ArchivosRepository archivosRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private SolicitudesService solicitudesService;

    @Autowired
    private AuditoriaRepository auditoriaRepository;

    @Autowired
    private FormularioRepository formularioRepository;

    @Autowired
    private EncuestaService encuestaService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/solicitudes")
    public Page<Solicitudes> getSolicitudes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String correo_asignacion,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) List<Long> status,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(required = false) String buscar,
            @RequestParam(name = "formulario", required = false) Long formularioId) {
        
        Pageable pageable = PageRequest.of(page, size);

        if (sort != null && !sort.equals("") && direction != null && !direction.equals("")) {
            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            Sort sortObject = Sort.by(sortDirection, sort);
            pageable = PageRequest.of(page, size, sortObject);
            
        }

        return solicitudesService.getSolicitudes(correo_asignacion, entidad, status, formularioId, desde, hasta, buscar, pageable);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/solicitudes/combinar")
    public Solicitudes combinarSolicitudes2(@RequestBody Map<String, Object> payload) {

        Long id_solicitud_base = ((Number) payload.get("solicitud_base_id")).longValue();
        List<Map> solicitudes = (List<Map>) payload.get("solicitudes");

        Solicitudes solicitud_base = solicitudesService.findById(
                id_solicitud_base)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitudes",
                        "id",
                        id_solicitud_base));

        for (Map<String, Object> solicitud : solicitudes) {

            Solicitudes children = solicitudesService.findById(((Number) solicitud.get("id")).longValue())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Solicitudes",
                            "id",
                            id_solicitud_base));
            solicitud_base.addSolicitudes(children);
            children.setStatus(statusRepository.findById((long) 9).get());
            solicitudesService.save(children);

        }

        return solicitudesService.save(solicitud_base);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/solicitudes/descombinar/{id}")
    public Solicitudes descombinarSolicitudes(@PathVariable(value = "id") Long solicitudId) {

        Solicitudes solicitud = solicitudesService.findById(
                solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Solicitudes",
                        "id",
                        solicitudId));
        solicitud.setStatus(statusRepository.findById((long) 1).get());
        solicitud.setParent(null);
        return solicitudesService.save(solicitud);

    }

    @GetMapping("/status/solicitudes")
    public List<Solicitudes> geStatustSolicitudes(@RequestParam(value = "formulario") Long formulario) {
        return solicitudesService.findAllByOrderByStatusIdAscFechaAsc(formulario);
    }

    @GetMapping("/id/solicitudes")
    public List<Solicitudes> geIdSolicitudes(@RequestParam(value = "formulario") Long formulario) {
        return solicitudesService.findAllByOrderByIdSolicitudDesc(formulario);
    }

    @GetMapping("/entidad/solicitudes")
    public List<Solicitudes> geEntidadSolicitudes(@RequestParam(value = "formulario") Long formulario,
            @RequestParam(value = "entidad") Long entidad) {
        return solicitudesService.findAllByOrderByEntidadCodigoAscIdAsc(formulario, entidad);
    }

    @GetMapping("/reporte/solicitudes")
    public List<Solicitudes> geEntidadSolicitudes(
            @RequestParam(required = false) Long entidad,
            @RequestParam(required = false) List<Long> status,
            @RequestParam(required = false) String desde,
            @RequestParam(required = false) String hasta,
            @RequestParam(value = "formulario") Long formulario) {
        return solicitudesService.findAllByOrderByEntidadCodigoAscIdAsc(formulario, entidad, status, desde, hasta);
    }
    /*
     * @GetMapping("/entidad/{entidadCodigo}/solicitudes")
     * public Page<Solicitudes> getSolicitudesByEntidadId(@PathVariable (value =
     * "entidadCodigo") Long entidadCodigo, Pageable pageable) {
     * return solicitudesService.findByEntidadCodigo(entidadCodigo, pageable);
     * }
     */

    @GetMapping("/solicitudes/{id}")
    public Solicitudes getSolicitudesById(@PathVariable(value = "id") Long solicitudesId) {
        return solicitudesService.findById(solicitudesId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitudes", "id", solicitudesId));
    }

    @GetMapping("/status/{statusId}/solicitudes")
    public Page<Solicitudes> getSolicitudesByStatusId(@PathVariable(value = "statusId") Long statusId,
            Pageable pageable) {
        return solicitudesService.findByStatusId(statusId, pageable);
    }

    @PostMapping("/solicitudes/fecha")
    public List<Solicitudes> getSolicitudesByFecha(@RequestParam(value = "desde") String desde,
            @RequestParam(value = "hasta") String hasta,
            @RequestParam(value = "formulario") Long formulario) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        LocalDateTime dateTimeDesde = LocalDateTime.parse(desde, formatter);
        LocalDateTime dateTimeHasta = LocalDateTime.parse(hasta, formatter);

        if (formulario != null) {
            return solicitudesService.findByAll(dateTimeDesde, dateTimeHasta, formulario);

        } else {
            return solicitudesService.findByAll(dateTimeDesde, dateTimeHasta);
        }

    }

    @Transactional
    @PostMapping("/solicitudes")
    public Solicitudes createSolicitud(@Valid @RequestBody SolicitudesRequest request) throws MessagingException,
            IOException, ParseException, org.apache.tomcat.util.json.ParseException, JSONException {
        final Solicitudes solicitudes = new Solicitudes(request);
        Map<String, Object> body_map = new HashMap<String, Object>();

        solicitudes.setFormulario(formularioRepository.findById(request.getFormulario())
                .orElseThrow(() -> new ResourceNotFoundException("Formulario", "id", request.getFormulario())));
        solicitudes.setStatus(statusRepository.findById((long) EN_ESPERA).get());
        solicitudes.setAsunto(request.getAsunto());

        Mail mail = new Mail();
        mail.setFrom(solicitudes.getFormulario().getEmail());
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("departamento", false);
        model.put("municipio", false);

        if (request.getEdm() != null) {
            JSONArray body = new JSONArray(request.getEdm());

            if (body.length() > 0) {
                body_map = new ObjectMapper().readValue(body.get(0).toString(), HashMap.class);
            }

            if (body_map.get("correo_asignacion") != null &&
                    !((String) body_map.get("correo_asignacion")).equals("")) {
                solicitudes.setStatus(statusRepository.findById((long) 6).get());
                solicitudes.setEntidadDepartamentoMunicipio(request.getEdm());
                solicitudes.setCorreo_asignacion((String) body_map.get("correo_asignacion"));
                solicitudes.setTelefono_asignacion((String) body_map.get("telefono_asignacion"));

            }
        }

        LocalDateTime fecha = LocalDateTime.now(ZoneId.of("America/Bogota"));

        solicitudes.setFecha(fecha);
        solicitudesService.save(solicitudes);

        Map<String, Object> contenidoSolicitud = new HashMap<String, Object>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode contenidoS = mapper.readTree(solicitudes.getContenido());
        Iterator<Map.Entry<String, JsonNode>> it = contenidoS.getFields();

        while (it.hasNext()) {
            Map.Entry<String, JsonNode> field = it.next();
            if (field.getKey().equals("Contenido")) {
                contenidoSolicitud.put(field.getKey(), field.getValue().asText().split("(\\|\\|,\\|\\|)"));
            } else {
                String key = field.getKey();
                if (key.contains("_")) {
                    key = key.replace("_", " ");
                }
                contenidoSolicitud.put(key, field.getValue().asText());
            }
        }

        SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String formattedDateTime = solicitudes.getCreateDateTime().format(formatter);

        Date date = inputFormat.parse(formattedDateTime);
        String currentYear = getYearFormat.format(date);

        if (request.getEdm() != null && body_map.get("correo_asignacion") != null &&
                !((String) body_map.get("correo_asignacion")).equals("")) {
            model.put("dependencia", ((HashMap) body_map.get("entidad")).get("nombre").toString());
            model.put("dependenciaTipo", ((HashMap) body_map.get("entidad")).get("tipo").toString());

            Mail mailAutomatico = new Mail();
            mailAutomatico.setFrom(solicitudes.getFormulario().getEmail());

            mailAutomatico.setSubject("Caso: " + currentYear + "-" + solicitudes.getId() + " "
                    + solicitudes.getFormulario().getDescripcion());

            Map<String, Object> modelAutomatico = new HashMap<String, Object>();
            modelAutomatico.put("numero", solicitudes.getId());

            if (solicitudes.getAsunto() != null) {
                modelAutomatico.put("asunto", solicitudes.getAsunto());
            } else {
                modelAutomatico.put("asunto", false);
            }

            modelAutomatico.put("contenido", contenidoSolicitud);
            modelAutomatico.put("fecha", currentYear);
            modelAutomatico.put("correo_solicitante", solicitudes.getCorreo_solicitante());

            modelAutomatico.put("departamento", false);
            modelAutomatico.put("municipio", false);

            modelAutomatico.put("dependencia", ((HashMap) body_map.get("entidad")).get("nombre").toString());
            modelAutomatico.put("dependenciaTipo", ((HashMap) body_map.get("entidad")).get("tipo").toString());
            mailAutomatico.setTo(body_map.get("correo_asignacion").toString());

            mail.setTo(solicitudes.getCorreo_solicitante());

            List<String> strFile = new ArrayList<>();
            Set<String> strFiles = request.getUrl();

            if (strFiles != null) {
                strFiles.forEach(url -> {

                    try {
                        strFile.add(URLDecoder.decode(URLDecoder.decode(url, "UTF-8"), "ISO-8859-1"));
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                });
            }

            mailAutomatico.setModel(modelAutomatico);
            if (!((HashMap) body_map.get("entidad")).get("nombre").toString().equals("OTRAS SOLICITUDES"))
                emailService
                        .sendMessageAsignar(EmailConfiguration.getJavaMailSender(solicitudes.getFormulario().getEmail(),
                                solicitudes.getFormulario().getEmailPassword()), mailAutomatico, strFile);

        } else {
            mail.setTo(solicitudes.getCorreo_solicitante());
            model.put("dependencia", false);
        }

        if (solicitudes.getFormulario().getEmail().compareToIgnoreCase("info@cendoj.ramajudicial.gov.co") == 0) {

            mail.setSubject("info: " + currentYear + "-" + solicitudes.getId() + " "
                    + solicitudes.getFormulario().getDescripcion());
        } else if (solicitudes.getFormulario().getEmail()
                .compareToIgnoreCase("aroncanr@cendoj.ramajudicial.gov.co") == 0) {
            mail.setSubject("aroncanr: " + currentYear + "-" + solicitudes.getId() + " "
                    + solicitudes.getFormulario().getDescripcion());
        } else {
            mail.setSubject("Caso: " + currentYear + "-" + solicitudes.getId() + " "
                    + solicitudes.getFormulario().getDescripcion());
        }

        model.put("numero", solicitudes.getId());
        model.put("status", "En proceso");

        if (solicitudes.getAsunto() != null) {
            model.put("asunto", solicitudes.getAsunto());
        } else {
            model.put("asunto", false);
        }

        model.put("fecha", currentYear);
        model.put("contenido", contenidoSolicitud);

        Set<String> strFiles = request.getUrl();

        if (request.getUrl() != null) {

            // model.put("archivos", strFiles);

            strFiles.forEach(url -> {
                Archivos archivos;
                try {
                    archivos = new Archivos(URLDecoder.decode(URLDecoder.decode(url,
                            StandardCharsets.UTF_8.toString()), "ISO-8859-1"), null, null);
                    this.archivos = archivos;

                    solicitudesService.findById(solicitudes.getId()).map(solicitud -> {
                        this.archivos.setSolicitudes(solicitud);
                        return this.archivos;
                    }).orElseThrow(() -> new ResourceNotFoundException("Solicitudes ", "id", solicitudes.getId()));

                    archivosRepository.save(archivos);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }

        mail.setModel(model);
        emailService.sendSimpleMessage1(EmailConfiguration.getJavaMailSender(solicitudes.getFormulario().getEmail(),
                solicitudes.getFormulario().getEmailPassword()), mail, null);

        return solicitudes;
    }

    @PreAuthorize("hasAuthority('Administrador')")
    @PutMapping("/solicitudes2/{id}")
    public Solicitudes updateSolicitudes2(
            @PathVariable(value = "id") Long solicitudId,
            @RequestBody String data_string) throws Exception {

        // Cuerpo del request
        JSONObject body = new JSONObject(data_string);
        Map<String, Object> body_map = new ObjectMapper().readValue(data_string, HashMap.class);

        // Extraccion de data
        String correo_solicitante = body.getString("correo_solicitante");
        String[] correos_asignados = body.getString("correo_asignacion").replaceAll("\\s+", "").split(",");
        String[] correos_cc = body.getString("CC").replaceAll("\\s+", "").split(",");
        String contenido = body.getString("contenido");
        ArrayList entidades = ((ArrayList) body_map.get("entidades"));

        // Se obtiene la solicitud a actualizar
        Solicitudes solicitud = solicitudesService.findById(solicitudId)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitudes", "id", solicitudId));

        // Inicializacion de variables para archivos
        List<String> strFile = new ArrayList<>();
        List<String> strFileNotificar = new ArrayList<>();

        // Fecha, el año actual para el asunto
        SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String formattedDateTime = solicitud.getCreateDateTime().format(formatter);
        Date date = inputFormat.parse(formattedDateTime);
        String currentYear = getYearFormat.format(date);

        if (contenido == null || contenido.equals("")) {
            throw new Exception("El mensaje es obligatorio");
        }

        // Reasignacion de correos - se le notifica a los anteriores asignados de la
        // reasignacion
        if (solicitud.getCorreo_asignacion() != null) {
            String[] correos_anteriores = solicitud.getCorreo_asignacion().split(",");
            for (int i = 0; i < correos_anteriores.length; i++) {
                String correo = correos_anteriores[i];
                if (!body.getString("correo_asignacion").contains(correo)) {
                    Mail mail = new Mail();

                    mail.setFrom(solicitud.getFormulario().getEmail());
                    mail.setTo(correo);
                    mail.setSubject("Reasignacion Caso: " + currentYear + "-" + solicitud.getId() + " "
                            + solicitud.getFormulario().getDescripcion());

                    Map<String, Object> model = new HashMap<String, Object>();
                    model.put("numero", solicitudId);
                    if (solicitud.getAsunto() != null) {
                        model.put("asunto", solicitud.getAsunto());
                    } else {
                        model.put("asunto", false);
                    }

                    model.put("old_email", solicitud.getCorreo_asignacion());
                    model.put("new_email", body.getString("correo_asignacion"));
                    model.put("fecha", currentYear);

                    mail.setModel(model);
                    emailService.sendMessageReasignar(EmailConfiguration.getJavaMailSender(
                            solicitud.getFormulario().getEmail(), solicitud.getFormulario().getEmailPassword()), mail);
                }
            }
        }

        if (!contenido.equals("") && contenido != null) {

            // Notificar al usuario
            Mail notificacion_solicitante = new Mail();
            notificacion_solicitante.setFrom(solicitud.getFormulario().getEmail());
            notificacion_solicitante.setTo(correo_solicitante);

            notificacion_solicitante.setSubject("Progreso Caso: " + currentYear + "-" + solicitud.getId() +
                    " " + solicitud.getFormulario().getDescripcion());

            Map<String, Object> model_notificacion = new HashMap<String, Object>();
            model_notificacion.put("numero", solicitudId);

            if (solicitud.getAsunto() != null) {
                model_notificacion.put("asunto", solicitud.getAsunto());
            } else {
                model_notificacion.put("asunto", false);
            }

            model_notificacion.put("dependencias", entidades);
            model_notificacion.put("correo", body.getString("correo_asignacion"));
            model_notificacion.put("telefono", body.getString("telefono_asignacion"));
            model_notificacion.put("fecha", currentYear);

            archivosRepository.findBySolicitudesId(solicitudId).forEach(url -> {
                String url1 = url.getUrl();
                strFileNotificar.add(url1);
            });

            notificacion_solicitante.setModel(model_notificacion);
            emailService
                    .sendMessageAsignacionNotificar(
                            EmailConfiguration.getJavaMailSender(solicitud.getFormulario().getEmail(),
                                    solicitud.getFormulario().getEmailPassword()),
                            notificacion_solicitante, new ArrayList<>());

            // Envio de asignacion a las entidades

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

            Mail mail = new Mail();
            mail.setFrom(solicitud.getFormulario().getEmail());
            mail.setTo(correos_asignados);

            if (correos_cc.length > 0 && !correos_cc[0].equals("")) {
                mail.setCc(correos_cc);
            }

            mail.setSubject("Caso: " + currentYear + "-" + solicitud.getId() + " " +
                    solicitud.getFormulario().getDescripcion());

            Map<String, Object> model_asignacion = new HashMap<String, Object>();
            model_asignacion.put("correo_solicitante", correo_solicitante);
            model_asignacion.put("contenidoA", contenido);
            model_asignacion.put("numero", solicitud.getId());

            if (solicitud.getAsunto() != null) {
                model_asignacion.put("asunto", solicitud.getAsunto());
            } else {
                model_asignacion.put("asunto", false);
            }

            model_asignacion.put("contenido", contenidoSolicitud);
            model_asignacion.put("fecha", currentYear);

            archivosRepository.findBySolicitudesId(solicitudId).forEach(url -> {
                String url1 = url.getUrl();
                strFile.add(url1);
            });

            mail.setModel(model_asignacion);
            emailService.sendMessageAsignar(EmailConfiguration.getJavaMailSender(solicitud.getFormulario().getEmail(),
                    solicitud.getFormulario().getEmailPassword()), mail, strFile);
        }

        solicitud.setCorreo_asignacion(body.getString("correo_asignacion"));
        solicitud.setTelefono_asignacion(body.getString("telefono_asignacion"));
        solicitud.setEntidadDepartamentoMunicipio(body.get("entidades").toString());
        statusRepository.findById((long) ASIGNADO).ifPresent(status -> {
            solicitud.setStatus(status);
        });

        return solicitudesService.save(solicitud);
    }

    @PutMapping("/status/{status}/solicitudes/{id}")
    public Solicitudes updateStatus(@PathVariable(value = "status") Long statusId,
            @PathVariable(value = "id") Long id) throws MessagingException, IOException, ParseException {
        if (!statusRepository.existsById(statusId))
            throw new ResourceNotFoundException("Status ", "id", statusId);

        Solicitudes solicitud = solicitudesService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitudes ", "id", id));

        statusRepository.findById(statusId).ifPresent(status -> {
            solicitud.setStatus(status);
        });

        if (statusId == RESOLUCION &&
                encuestaService.getEncuestas(true, solicitud.getFormulario().getId()).size() > 0) {
            Encuesta encuesta = encuestaService.getEncuestas(true, solicitud.getFormulario().getId()).get(0);

            SimpleDateFormat getYearFormat = new SimpleDateFormat("yyyy");
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

            String formattedDateTime = solicitud.getCreateDateTime().format(formatter);

            Date date = inputFormat.parse(formattedDateTime);
            String currentYear = getYearFormat.format(date);

            Mail mail = new Mail();
            mail.setFrom(solicitud.getFormulario().getEmail());
            mail.setTo(solicitud.getCorreo_solicitante());
            mail.setSubject(solicitud.getAsunto());

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("numero", solicitud.getId());
            if (solicitud.getAsunto() != null) {
                model.put("asunto", solicitud.getAsunto());
            } else {
                model.put("asunto", false);
            }
            model.put("url", encuesta.getUrl());
            model.put("fecha", currentYear);
            model.put("email", solicitud.getCorreo_solicitante());
            mail.setModel(model);

            emailService.sendPollLink(EmailConfiguration.getJavaMailSender(solicitud.getFormulario().getEmail(),
                    solicitud.getFormulario().getEmailPassword()), mail);
        }

        return solicitudesService.save(solicitud);
    }

    @DeleteMapping("/solicitudes/{id}")
    public ResponseEntity<?> deleteSolicitudes(@PathVariable(value = "id") Long solicitudesId) {
        try {
            solicitudesService.deleteById(solicitudesId);
            Date in = new Date();
            LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());
            Optional<Auditoria> auditoria = auditoriaRepository.findById(auditoriaRepository.findByMaxRev());
            Auditoria aud = auditoria.get();
            aud.setUpdateDateTime(ldt);
            aud.setUsuarioModified(systemLoggedInUserAuditorAware.getCurrentAuditor().get());
            auditoriaRepository.save(aud);
        } catch (EmptyResultDataAccessException exception) {
            throw new ResourceNotFoundException("Solicitud", "id", solicitudesId);
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadToLocalFileSystem(@RequestParam("file") MultipartFile file)
            throws MalformedURLException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        Path path = Paths.get(SolicitudApplication.FILES_PATH + File.separator + fileName);

        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/download/")
                .path(fileName)
                .toUriString();

        return ResponseEntity.ok(new URL(fileDownloadUri).getPath());
    }

    @PostMapping("/multi-upload")
    public ResponseEntity<?> multiUpload(@RequestParam("files") MultipartFile[] files) {
        List<Object> fileDownloadUrls = new ArrayList<>();

        Arrays.asList(files).stream().forEach(file -> {
            try {
                fileDownloadUrls.add(uploadToLocalFileSystem(file).getBody());
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        return ResponseEntity.ok(fileDownloadUrls);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<?> downloadFileFromLocal(@PathVariable String fileName) throws IOException {
        Path path = Paths.get(SolicitudApplication.FILES_PATH + File.separator + fileName);

        String content_type = Files.probeContentType(path);

        if (content_type == null) {
            content_type = "application/octet-stream";
        }

        Resource resource = null;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content_type))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
