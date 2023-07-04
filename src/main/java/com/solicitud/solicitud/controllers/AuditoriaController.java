package com.solicitud.solicitud.controllers;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.solicitud.solicitud.models.Auditoria;
import com.solicitud.solicitud.repository.AuditoriaRepository;
import com.solicitud.solicitud.repository.SolicitudesRepository;

@RestController
public class AuditoriaController {
    @Autowired
    AuditoriaRepository auditoriaRepository;

    @Autowired
    SolicitudesRepository solicitudesRepository;

    @PostMapping("/auditoria/excel")
    public ResponseEntity<byte[]> generarExcel(
            @RequestParam(required = false) String usuarioModified,
            @RequestParam(required = false) Long idSolicitud,
            @RequestParam(required = false) String updateDateTimeStart,
            @RequestParam(required = false) String updateDateTimeEnd) {

        Specification<Auditoria> spec = Specification.where(null);

        if (usuarioModified != null && !usuarioModified.isEmpty()) {
            spec = spec.and(
                    (root, query, builder) -> builder.like(root.get("usuarioModified"), "%" + usuarioModified + "%"));
        }

        if (idSolicitud != null) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get("id_solicitud"), idSolicitud));
        }

        if (updateDateTimeStart != null && !updateDateTimeStart.isEmpty()) {
            LocalDate startDate = LocalDate.parse(updateDateTimeStart);
            LocalDateTime startDateTime = startDate.atStartOfDay();
            spec = spec.and(
                    (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("updateDateTime"), startDateTime));
        }

        if (updateDateTimeEnd != null && !updateDateTimeEnd.isEmpty()) {
            LocalDate endDate = LocalDate.parse(updateDateTimeEnd);
            final LocalDateTime endDateTime = endDate.atStartOfDay().with(LocalTime.MAX);
            spec = spec.and(
                    (root, query, builder) -> builder.lessThanOrEqualTo(root.get("updateDateTime"), endDateTime));
        }

        List<Auditoria> auditoriaList = auditoriaRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));

        List<Map<String, Object>> excelData = new ArrayList<Map<String, Object>>();
        String fechas = "";
        Gson gson = new Gson();

        for (Auditoria auditoria : auditoriaList) {
            try {
                LocalDateTime localDateTime = auditoria.getUpdateDateTime();
                if (localDateTime != null) {
                    ZoneId zoneId = ZoneId.systemDefault(); // o la zona horaria que corresponda
                    Instant instant = localDateTime.atZone(zoneId).toInstant();
                    Date fecha = Date.from(instant);
                    fechas = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(fecha);
                }
            } catch (IllegalArgumentException ex) {
            }

            Map<String, Object> row = new LinkedHashMap<String, Object>();
            row.put("Numero de auditoria", auditoria.getId());
            row.put("Fecha", fechas);
            row.put("Usuario", auditoria.getUsuarioModified());

            if (auditoria.getCodigo() != null) {
                row.put("Modulo", "Entidad");
            } else if (auditoria.getId_formulario() != null) {
                row.put("Modulo", "Formulario");
            } else if (auditoria.getId_solicitud() != null) {
                row.put("Modulo", "Solicitud");
                String numeroSolicitud = "";
                try {
                    LocalDateTime localDateTimeCreate = auditoria.getUpdateDateTime();
                    if (localDateTimeCreate != null) {
                        ZoneId zoneId = ZoneId.systemDefault(); // o la zona horaria que corresponda
                        Instant instant = localDateTimeCreate.atZone(zoneId).toInstant();
                        Date fechaCreate = Date.from(instant);
                        numeroSolicitud = new SimpleDateFormat("yyyy").format(
                                fechaCreate) + "-"
                                + auditoria.getId_solicitud();
                        row.put("Numero de Solicitud", numeroSolicitud);
                    }
                } catch (IllegalArgumentException ex) {
                    row.put("Numero de Solicitud", numeroSolicitud);
                }

            } else if (auditoria.getId_encuesta() != null) {
                row.put("Modulo", "Encuesta");
            } else if (auditoria.getId_roles() != null) {
                row.put("Modulo", "Roles");
            } else if (auditoria.getId_permiso() != null) {
                row.put("Modulo", "Permisos");
            } else if (auditoria.getId_user() != null) {
                row.put("Modulo", "Usuarios");
            }

            if (auditoria.getRevtype() == 0) {
                row.put("Evento", "Agregar");
            } else if (auditoria.getRevtype() == 1) {
                row.put("Evento", "Actualizar");
            } else if (auditoria.getRevtype() == 2) {
                row.put("Evento", "Eliminar");
            }

            // Agregar columnas adicionales desde el objeto JSON de auditoria
            String json = auditoria.getContenido_solicitud();
            if (json != null && !json.isEmpty()) {
                JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
                Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    String key = entry.getKey();
                    String value = entry.getValue().toString();
                    if (value.length() > 20000) {
                        value = value.substring(0, 20000);
                    }
                    row.put(key, value);
                }
            }

            excelData.add(row);
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Auditoria");

        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);

        // Agregar encabezados para las columnas adicionales
        Set<String> keySet = new LinkedHashSet<String>();
        for (Map<String, Object> data : excelData) {
            keySet.addAll(data.keySet());
        }

        int colNum = 0;
        for (String key : keySet) {
            headerRow.createCell(colNum++).setCellValue(key);
        }

        for (Map<String, Object> data : excelData) {
            Row row = sheet.createRow(rowNum++);
            colNum = 0;
            for (String key : keySet) {
                if (data.containsKey(key)) {
                    Object value = data.get(key);
                    if (value instanceof Integer) {
                        row.createCell(colNum++).setCellValue((Integer) value);
                    } else if (value instanceof Double) {
                        row.createCell(colNum++).setCellValue((Double) value);
                    } else if (value instanceof Date) {
                        row.createCell(colNum++).setCellValue((Date) value);
                    } else if (value instanceof String) {
                        row.createCell(colNum++).setCellValue((String) value);
                    } else {
                        if (value != null) {
                            row.createCell(colNum++).setCellValue(value.toString());
                        } else {
                            row.createCell(colNum++).setCellValue("");
                        }
                    }
                } else {
                    row.createCell(colNum++).setCellValue("");
                }
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] bytes = outputStream.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
                MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add("Content-Disposition", "attachment; filename=Auditoria.xlsx");

        return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
    }

    @GetMapping("/auditoria")
    public Page<Auditoria> getAuditorias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String usuarioModified,
            @RequestParam(required = false) Long idSolicitud,
            @RequestParam(required = false) String filterField,
            @RequestParam(required = false) String filterValue,
            @RequestParam(required = false) String updateDateTimeStart,
            @RequestParam(required = false) String updateDateTimeEnd) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Sort sortObject = Sort.by(sortDirection, sort);
        Pageable pageable = PageRequest.of(page, size, sortObject);

        Specification<Auditoria> spec = Specification.where(null);

        if (usuarioModified != null && !usuarioModified.isEmpty()) {
            spec = spec.and(
                    (root, query, builder) -> builder.like(root.get("usuarioModified"), "%" + usuarioModified + "%"));
        }

        if (filterField != null && !filterField.isEmpty() && filterValue != null && !filterValue.isEmpty()) {
            try {
                Long valueAsLong = Long.parseLong(filterValue);
                spec = spec.and((root, query, builder) -> builder.equal(root.get(filterField), valueAsLong));
            } catch (NumberFormatException e) {
                spec = spec.and((root, query, builder) -> builder.like(root.get(filterField), "%" + filterValue + "%"));
            }
        }

        if (idSolicitud != null) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get("id_solicitud"), idSolicitud));
        }

        if (updateDateTimeStart != null && !updateDateTimeStart.isEmpty()) {
            LocalDate startDate = LocalDate.parse(updateDateTimeStart);
            LocalDateTime startDateTime = startDate.atStartOfDay();
            spec = spec.and(
                    (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("updateDateTime"), startDateTime));
        }

        if (updateDateTimeEnd != null && !updateDateTimeEnd.isEmpty()) {
            LocalDate endDate = LocalDate.parse(updateDateTimeEnd);
            final LocalDateTime endDateTime = endDate.atStartOfDay().with(LocalTime.MAX);
            spec = spec.and(
                    (root, query, builder) -> builder.lessThanOrEqualTo(root.get("updateDateTime"), endDateTime));
        }

        return auditoriaRepository.findAll(spec, pageable);
    }

    @GetMapping("/auditoria/distinct")
    public List<Auditoria> getAuditoriasDistinct() {
        return auditoriaRepository.findByAll();
    }

    @GetMapping("/auditoria/correo/{correo}")
    public List<Auditoria> getAuditoriasCorreo(@PathVariable(value = "correo") String Correo) {
        return auditoriaRepository.findByCorreo_Solicitante(Correo);
    }

    @GetMapping("/auditoria/usuario/{correo}")
    public List<Auditoria> getAuditoriasUsuario(@PathVariable(value = "correo") String Correo) {
        return auditoriaRepository.findByUsuario(Correo);
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/auditoria/modulo/{id}")
    public List<Auditoria> getAuditoriaById(@PathVariable(value = "id") Long Id,
            @RequestParam(required = true) String modulo) {
        if (modulo.compareTo("Entidad") == 0)
            return auditoriaRepository.findByCodigo(Id);
        else if (modulo.compareTo("Roles") == 0)
            return auditoriaRepository.findById_Roles(Id);
        else if (modulo.compareTo("Formulario") == 0)
            return auditoriaRepository.findById_Formulario(Id);
        else if (modulo.compareTo("Encuestas") == 0)
            return auditoriaRepository.findById_Encuesta(Id);
        else if (modulo.compareTo("Permisos") == 0)
            return auditoriaRepository.findById_Permiso(Id);
        else if (modulo.compareTo("Solicitud") == 0)
            return auditoriaRepository.findById_Solicitud(Id);
        else if (modulo.compareTo("User") == 0)
            return auditoriaRepository.findById_User(Id);
        else
            return (List<Auditoria>) ResponseEntity.ok("Modulo no Existe");
    }

    @GetMapping("/auditoria/{id}")
    public Optional<Auditoria> getAuditoriaById(@PathVariable(value = "id") Integer id) {
        return auditoriaRepository.findById(id);

    }

}
