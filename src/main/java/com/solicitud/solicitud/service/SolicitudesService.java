package com.solicitud.solicitud.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.solicitud.solicitud.models.Solicitudes;
import com.solicitud.solicitud.repository.SolicitudesRepository;

@Service
public class SolicitudesService {
    @Autowired
    SolicitudesRepository solicitudesRepository;

    public Optional<Solicitudes> findById(Long id) {
        return solicitudesRepository.findById(id);
    }

    public List<Solicitudes> findByAll(LocalDateTime dateTimeDesde, LocalDateTime dateTimeHasta) {
        return solicitudesRepository.findByAll(dateTimeDesde, dateTimeHasta);
    }

    public List<Solicitudes> findByAll(LocalDateTime dateTimeDesde, LocalDateTime dateTimeHasta, Long formulario) {
        return solicitudesRepository.findByAll(dateTimeDesde, dateTimeHasta, formulario);
    }

    @SuppressWarnings("serial")
    public List<Solicitudes> getSolicitudes(String correo_asignacion, String entidad, List<Long> status,
            Long formularioId) {
        return solicitudesRepository.findAll(new Specification<Solicitudes>() {
            @Override
            public Predicate toPredicate(Root<Solicitudes> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if (formularioId != null)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("formulario"), formularioId)));

                if (correo_asignacion != null)
                    predicates.add(criteriaBuilder
                            .and(criteriaBuilder.equal(root.get("correo_asignacion"), correo_asignacion)));

                if (entidad != null)
                    try {
                        long nEntidad = Long.parseLong(entidad);
                        predicates.add(criteriaBuilder
                                .and(criteriaBuilder.equal(root.get("entidadDepartamentoMunicipio"), nEntidad)));
                    } catch (NumberFormatException exception) {
                        predicates.add(
                                criteriaBuilder.and(criteriaBuilder.isNull(root.get("entidadDepartamentoMunicipio"))));
                    }

                if (status != null)
                    predicates.add(criteriaBuilder.and(root.get("status").get("id").in(status)));

                List<Order> orderList = new ArrayList();

                orderList.add(criteriaBuilder.asc(root.get("status").get("id")));
                orderList.add(criteriaBuilder.asc(root.get("fecha")));
                // orderList.add(criteriaBuilder.desc(root.get("id")));

                query.orderBy(orderList);

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    @SuppressWarnings("serial")
    public Page<Solicitudes> getSolicitudes(String correo_asignacion, String entidad, List<Long> status,
            Long formularioId, String desde, String hasta, String buscar, Pageable pageable) {
        return solicitudesRepository.findAll(new Specification<Solicitudes>() {
            @Override
            public Predicate toPredicate(Root<Solicitudes> root, CriteriaQuery<?> query,
                    CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                if (formularioId != null)
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("formulario"), formularioId)));

                if (correo_asignacion != null)
                    predicates.add(criteriaBuilder
                            .and(criteriaBuilder.equal(root.get("correo_asignacion"), correo_asignacion)));

                if (entidad != null)
                    try {
                        long nEntidad = Long.parseLong(entidad);
                        predicates.add(criteriaBuilder
                                .and(criteriaBuilder.equal(root.get("entidadDepartamentoMunicipio"), nEntidad)));
                    } catch (NumberFormatException exception) {
                        predicates.add(
                                criteriaBuilder.and(criteriaBuilder.isNull(root.get("entidadDepartamentoMunicipio"))));
                    }

                if (status != null)
                    predicates.add(criteriaBuilder.and(root.get("status").get("id").in(status)));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                if (desde != null && desde != "") {
                    LocalDateTime dateTimeDesde = LocalDateTime.parse(desde, formatter);
                    predicates.add(criteriaBuilder
                            .and(criteriaBuilder.greaterThan(root.get("fecha"), dateTimeDesde)));
                }

                if (hasta != null && hasta != "") {
                    LocalDateTime dateTimeHasta = LocalDateTime.parse(hasta, formatter);
                    predicates.add(criteriaBuilder
                            .and(criteriaBuilder.lessThan(root.get("fecha"), dateTimeHasta)));
                }

                if (buscar != null && buscar != "") {
                    Long buscarLong = null;
                    try {
                        buscarLong = Long.parseLong(buscar);
                    } catch (NumberFormatException ex) {
                        // Si no se puede convertir a Long, tratar como una cadena
                    }

                    Predicate predicateIdOrAsuntoOrCorreoSolicitante = null;
                    if (buscarLong != null) {
                        predicateIdOrAsuntoOrCorreoSolicitante = criteriaBuilder.or(
                                criteriaBuilder.equal(root.get("id"), buscarLong),
                                criteriaBuilder.like(root.get("asunto"), "%" + buscar + "%"),
                                criteriaBuilder.like(root.get("correo_solicitante"), "%" + buscar + "%"));
                    } else {
                        predicateIdOrAsuntoOrCorreoSolicitante = criteriaBuilder.or(
                                criteriaBuilder.like(root.get("asunto"), "%" + buscar + "%"),
                                criteriaBuilder.like(root.get("correo_solicitante"), "%" + buscar + "%"));
                    }

                    predicates.add(criteriaBuilder.and(predicateIdOrAsuntoOrCorreoSolicitante));
                }

                List<Order> orderList = new ArrayList();

                orderList.add(criteriaBuilder.asc(root.get("status").get("id")));
                orderList.add(criteriaBuilder.asc(root.get("fecha")));
                // orderList.add(criteriaBuilder.desc(root.get("id")));

                query.orderBy(orderList);

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    public List<Solicitudes> findByCorreo_Asignacion(String correo_asignacion) {
        if (correo_asignacion != null)
            return solicitudesRepository.findByCorreo_Asignacion(correo_asignacion);
        else
            return solicitudesRepository.findByCorreo_Asignacion();
    }

    public List<Solicitudes> findAllByOrderByStatusIdAscFechaAsc(Long formulario) {
        return solicitudesRepository.findAllByOrderByStatusIdAscFechaAsc(formulario);
    }

    public List<Solicitudes> findAllByOrderByIdSolicitudDesc(Long formulario) {
        return solicitudesRepository.findAllByOrderByIdSolicitudDesc(formulario);
    }

    public List<Solicitudes> findAllByOrderByEntidadCodigoAscIdAsc(Long formulario, Long entidad) {
        return solicitudesRepository.findAllByOrderByEntidadCodigoAscIdAsc(formulario, entidad);
    }

    public List<Solicitudes> findAllByOrderByEntidadCodigoAscIdAsc(Long formulario, Long entidad, List<Long> status,
            String desde, String hasta) {
        return solicitudesRepository.findAllByOrderByEntidadCodigoAscIdAsc(formulario, entidad, status, desde, hasta);
    }
    /*
     * public Page<Solicitudes> findByEntidadCodigo(Long entidadCodigo, Pageable
     * pageable) {
     * return solicitudesRepository.findByEntidadCodigo(entidadCodigo, pageable);
     * }
     */

    public Page<Solicitudes> findByStatusId(Long statusId, Pageable pageable) {
        return solicitudesRepository.findByStatusId(statusId, pageable);
    }

    public @Valid Solicitudes saveAndFlush(Solicitudes solicitudes) {
        return solicitudesRepository.saveAndFlush(solicitudes);
    }

    public Solicitudes save(Solicitudes solicitudes) {
        return solicitudesRepository.save(solicitudes);
    }

    public List<Solicitudes> saveAll(List<Solicitudes> solicitudes) {
        return solicitudesRepository.saveAll(solicitudes);
    }

    public boolean existsById(long id) {
        return solicitudesRepository.existsById(id);
    }

    public void delete(Solicitudes solicitudes) {
        solicitudesRepository.delete(solicitudes);
    }

    public void deleteById(Long id) {
        solicitudesRepository.deleteById(id);
    }
}
