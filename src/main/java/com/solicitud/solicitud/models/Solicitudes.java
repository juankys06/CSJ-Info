package com.solicitud.solicitud.models;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.json.JSONObject;
import org.springframework.data.annotation.LastModifiedBy;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.solicitud.solicitud.payload.request.SolicitudesRequest;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "csjinfo_Solicitudes")
@DynamicUpdate
@AuditTable(value = "csjinfo_auditoria")
@Audited
public class Solicitudes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long id;

    @Column(length = 75)
    @Email
    private String correo_solicitante;

    @Column
    @Lob
    private String correo_asignacion;

    @Column
    @Lob
    private String telefono_asignacion;

    @Column
    private String asunto;

    @Lob
    @Column(name = "contenido_solicitud")
    private String contenido;

    @Column(nullable = false, columnDefinition = "int default (0)")
    private int re;

    @LastModifiedBy
    @Column(name = "usuarioModified")
    private String usuario;

    @Column
    private LocalDateTime fecha;

    @CreationTimestamp
    @Column(name = "create_date_time", nullable = false)
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    @Column(name = "updateDateTime")
    private LocalDateTime updateDateTime;

    @OneToMany(mappedBy = "solicitudes", orphanRemoval = true)
    @NotAudited
    private List<Replica> replicas = new ArrayList<>();

    @OneToMany(mappedBy = "solicitudes", orphanRemoval = true, cascade = CascadeType.ALL)
    @NotAudited
    private List<Archivos> archivos = new ArrayList<>();

    @Column(name = "edm")
    @Lob
    private String entidadDepartamentoMunicipio;

    @ManyToOne
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinColumn(name = "status_id", nullable = false, columnDefinition = "bigint default(1)", foreignKey = @ForeignKey(name = "FK_STATUS"))
    private Status status;

    @ManyToOne
    @JoinColumn(name = "formulario_id", nullable = false, foreignKey = @ForeignKey(name = "FK_FORMULARIO"))
    @NotAudited
    private Formulario formulario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Solicitudes parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @AuditJoinTable(name = "csjinfo_auditoria")
    private Set<Solicitudes> childrens;

    public Solicitudes(Long id, @Email String correo_solicitante, @Email String correo_Asignacion,
            String telefono_Asignacion,
            String contenido, int re, String usuario,
            LocalDateTime fecha, LocalDateTime createDateTime,
            LocalDateTime updateDateTime, List<Replica> replicas,
            List<Archivos> archivos, Status status, String entidadDepartamentoMunicipio) {
        this.id = id;
        this.correo_solicitante = correo_solicitante;
        this.correo_asignacion = correo_Asignacion;
        this.telefono_asignacion = telefono_Asignacion;
        this.contenido = contenido;
        this.re = re;
        this.usuario = usuario;
        this.fecha = fecha;
        this.createDateTime = createDateTime;
        this.updateDateTime = updateDateTime;
        this.replicas = replicas;
        this.archivos = archivos;
        this.status = status;
        this.entidadDepartamentoMunicipio = entidadDepartamentoMunicipio;
    }

    public Solicitudes(SolicitudesRequest request) {
        this.correo_solicitante = request.getCorreo_solicitante();
        this.contenido = request.getContenido();
    }

    public Solicitudes() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorreo_solicitante() {
        return correo_solicitante;
    }

    public void setCorreo_solicitante(String correo_solicitante) {
        this.correo_solicitante = correo_solicitante;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getContenido() {
        return contenido;
    }

    public static String getValueOfContenido(String key, String contenido) {
        JSONObject json_contenido = new JSONObject(contenido);
        return json_contenido.getString(key);
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public List<Replica> getReplicas() {
        return replicas;
    }

    public void setReplicas(List<Replica> replicas) {
        this.replicas = replicas;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getCorreo_asignacion() {
        return correo_asignacion;
    }

    public void setCorreo_asignacion(String correo_Asignacion) {
        this.correo_asignacion = correo_Asignacion;
    }

    public String getTelefono_asignacion() {
        return telefono_asignacion;
    }

    public void setTelefono_asignacion(String telefono_Asignacion) {
        this.telefono_asignacion = telefono_Asignacion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public LocalDateTime getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(LocalDateTime updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<Archivos> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivos> archivos) {
        this.archivos = archivos;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public Set<Solicitudes> getSolicitudes() {
        return childrens;
    }

    public void setSolicitudes(Set<Solicitudes> solicitudes) {
        this.childrens = solicitudes;
    }

    public void addSolicitudes(Solicitudes solicitud) {
        // this.childrens.add(solicitud);
        solicitud.setParent(this);
    }

    public Solicitudes getParent() {
        return parent;
    }

    public void setParent(Solicitudes solicitud) {
        this.parent = solicitud;
    }

    public int getRe() {
        return re;
    }

    public void setRe(int re) {
        this.re = re;
    }

    public String getEntidadDepartamentoMunicipio() {
        return entidadDepartamentoMunicipio;
    }

    public void setEntidadDepartamentoMunicipio(String entidadDepartamentoMunicipio) {
        this.entidadDepartamentoMunicipio = entidadDepartamentoMunicipio;
    }

    public void setContenidoAppend(String contenido, Boolean EliminarPrevio) {
        JSONObject json_contenido = new JSONObject(this.contenido);
        if (!json_contenido.has("Contenido")) {
            json_contenido.put("Contenido", "(Sin contenido previo)");
        }
        String parsed_content = json_contenido.getString("Contenido");
        List<String> split_content = new ArrayList<String>(Arrays.asList(parsed_content.split("(\\|\\|,\\|\\|)")));
        split_content.remove(0);
        String contenido_sin_previo = String.join("||,||", split_content);
        String new_contenido = contenido + "||,||" + contenido_sin_previo;
        this.contenido = json_contenido.put("Contenido", new_contenido).toString();
    }

    public void setContenidoAppend(String contenido) {
        JSONObject json_contenido = new JSONObject(this.contenido);
        if (!json_contenido.has("Contenido")) {
            json_contenido.put("Contenido", "(Sin contenido previo)");
        }
        String parsed_content = json_contenido.getString("Contenido");
        String new_contenido = contenido + "||,||" + parsed_content;
        this.contenido = json_contenido.put("Contenido", new_contenido).toString();
    }

    public String ContenidoAppend(String contenido, Boolean EliminarPrevio) {
        JSONObject json_contenido = new JSONObject(this.contenido);
        if (!json_contenido.has("Contenido")) {
            json_contenido.put("Contenido", "(Sin contenido previo)");
        }
        String parsed_content = json_contenido.getString("Contenido");
        List<String> split_content = new ArrayList<String>(Arrays.asList(parsed_content.split("(\\|\\|,\\|\\|)")));
        split_content.remove(0);
        String contenido_sin_previo = String.join("||,||", split_content);
        return contenido + "||,||" + contenido_sin_previo;
    }

    public String ContenidoAppend(String contenido) {
        JSONObject json_contenido = new JSONObject(this.contenido);
        if (!json_contenido.has("Contenido")) {
            json_contenido.put("Contenido", "(Sin contenido previo)");
        }
        String parsed_content = json_contenido.getString("Contenido");
        return contenido + "||,||" + parsed_content;
    }

}