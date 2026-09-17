package com.BinarySeint.vsCitas.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.BinarySeint.vsCitas.classes.Atencion;
import com.BinarySeint.vsCitas.classes.EstadoAtencion;
import com.BinarySeint.vsCitas.repository.AtencionRepository;

@Service
public class CitasService {

    private final AtencionRepository atencionRepository;
    private final CatalogFeignClient catalogClient;
    private final NotifyClient notifyClient;
    private final AuditClient auditClient;
    private final ReportClient reportClient;

    public CitasService(AtencionRepository atencionRepository, 
                        CatalogFeignClient catalogClient,
                        NotifyClient notifyClient, 
                        AuditClient auditClient, 
                        ReportClient reportClient) {
        this.atencionRepository = atencionRepository;
        this.catalogClient = catalogClient;
        this.notifyClient = notifyClient;
        this.auditClient = auditClient;
        this.reportClient = reportClient;
    }

    @Transactional
    public Atencion crearAtencion(Atencion atencion) {
        atencion.setEstado(EstadoAtencion.SOLICITADA);
        Atencion guardada = atencionRepository.save(atencion);
    
        Map<String, String> eventoReporte = new HashMap<>();
        eventoReporte.put("estado", guardada.getEstado().name());
        eventoReporte.put("prestacionId", String.valueOf(guardada.getPrestacionId()));
        reportClient.registrarEventoReporte(eventoReporte);
        Map<String, Object> eventoCreacion = new HashMap<>();
        eventoCreacion.put("accion", "CREACION_CITA");
        eventoCreacion.put("usuarioId", guardada.getPacienteId());
        eventoCreacion.put("entidadId", "Citas_Microservicio");
        eventoCreacion.put("origenIp", "127.0.0.1"); // O la IP real si la capturas
        eventoCreacion.put("detalles", "Cita solicitada exitosamente.");

        auditClient.registrarEventoAuditoria(eventoCreacion);
                
        return guardada;
    }

    public Atencion obtenerAtencion(Long id) {
        return atencionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atención no encontrada"));
    }

    @Transactional
    public Atencion cambiarEstado(Long id, String nuevoEstadoStr) {
        Atencion atencion = obtenerAtencion(id);
        EstadoAtencion nuevoEstado = EstadoAtencion.valueOf(nuevoEstadoStr.toUpperCase());

        if (nuevoEstado == EstadoAtencion.EN_ATENCION && atencion.getEstado() != EstadoAtencion.CONFIRMADA && atencion.getEstado() != EstadoAtencion.EN_ESPERA) {
            throw new IllegalStateException("El paciente debe estar confirmado o en espera antes de pasar a EN_ATENCIÓN.");
        }

        atencion.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoAtencion.CONFIRMADA) {
            catalogClient.consumirCupo(atencion.getCupoId());
        }

        Atencion actualizada = atencionRepository.save(atencion);

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("traceId", UUID.randomUUID().toString());
        envelope.put("data", actualizada);

        if (nuevoEstado == EstadoAtencion.CONFIRMADA) {
            notifyClient.enviarEmail(envelope);
        } else if (nuevoEstado == EstadoAtencion.EN_ESPERA) {
            notifyClient.emitirTicket(envelope);
        } else if (nuevoEstado == EstadoAtencion.CERRADA) {
            notifyClient.generarPdf(envelope);
        }

        Map<String, Object> eventoCambio = new HashMap<>();
        eventoCambio.put("accion", "CAMBIO_ESTADO_" + actualizada.getEstado().name());
        eventoCambio.put("usuarioId", "Recepcion");
        eventoCambio.put("entidadId", "Citas_Microservicio");
        eventoCambio.put("origenIp", "127.0.0.1");
        eventoCambio.put("detalles", "La cita cambió de estado a " + actualizada.getEstado().name());

        auditClient.registrarEventoAuditoria(eventoCambio);

        Map<String, String> eventoReporte = new HashMap<>();
        eventoReporte.put("estado", actualizada.getEstado().name());
        reportClient.registrarEventoReporte(eventoReporte);

        return actualizada;
    }
    public List<Atencion> listarAtenciones(String statusStr, LocalDateTime from, LocalDateTime to) {
        EstadoAtencion estado = null;
        if (statusStr != null && !statusStr.isEmpty()) {
            estado = EstadoAtencion.valueOf(statusStr.toUpperCase());
        }

        if (estado != null && from != null && to != null) {
            return atencionRepository.findByEstadoAndFechaCreacionBetween(estado, from, to);
        } else if (from != null && to != null) {
            return atencionRepository.findByFechaCreacionBetween(from, to);
        } else if (estado != null) {
            return atencionRepository.findByEstado(estado);
        } else {
            return atencionRepository.findAll();
        }
    }
}