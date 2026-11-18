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
import com.BinarySeint.vsCitas.dto.AuditTimelineEventDTO;
import com.BinarySeint.vsCitas.dto.CitasEventoDTO;
import com.BinarySeint.vsCitas.repository.AtencionRepository;

@Service
public class CitasService {

    private final AtencionRepository atencionRepository;
    private final CatalogFeignClient catalogClient;
    private final NotificationProducerService notificationProducer;
    private final CitasKafkaProducerService kafkaProducer;

    public CitasService(AtencionRepository atencionRepository,
                        CatalogFeignClient catalogClient,
                        NotificationProducerService notificationProducer,
                        CitasKafkaProducerService kafkaProducer) {
        this.atencionRepository = atencionRepository;
        this.catalogClient = catalogClient;
        this.notificationProducer = notificationProducer;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public Atencion crearAtencion(Atencion atencion) {
       atencion.setEstado(EstadoAtencion.SOLICITADA);
        Atencion guardada = atencionRepository.save(atencion);

        CitasEventoDTO appointmentEvent = new CitasEventoDTO(
                UUID.randomUUID().toString(),
                "CITA_CREADA",
                LocalDateTime.now(),
                guardada.getId(),
                guardada.getPacienteId(),
                guardada.getRut(),
                guardada.getNombrePaciente(),
                guardada.getPrestacionId(),
                guardada.getCupoId(),
                null,
                guardada.getEstado().name(),
                guardada.getFechaCreacion()
        );
        kafkaProducer.publicarEventoCita(appointmentEvent);

        AuditTimelineEventDTO auditEvent = new AuditTimelineEventDTO(
                UUID.randomUUID().toString(),
                String.valueOf(guardada.getId()),
                guardada.getPacienteId(),
                "CREACION_CITA",
                LocalDateTime.now(),
                "127.0.0.1",
                "ms-vidasalud-appointments",
                "Cita solicitada exitosamente para prestación ID " + guardada.getPrestacionId()
        );
        kafkaProducer.publicarEventoAuditoria(auditEvent);

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "APPOINTMENT_CREATED");
        envelope.put("eventId", UUID.randomUUID().toString());
        envelope.put("traceId", UUID.randomUUID().toString());
        envelope.put("timestamp", LocalDateTime.now().toString());
        envelope.put("data", guardada);
        notificationProducer.enviarEmail(envelope);

        return guardada;
    }

    public Atencion obtenerAtencion(Long id) {
        return atencionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atención no encontrada"));
    }

    @Transactional
    public Atencion cambiarEstado(Long id, String nuevoEstadoStr) {
        Atencion atencion = obtenerAtencion(id);
        EstadoAtencion estadoAnterior = atencion.getEstado();
        EstadoAtencion nuevoEstado = EstadoAtencion.valueOf(nuevoEstadoStr.toUpperCase());

        if (nuevoEstado == EstadoAtencion.EN_ATENCION
                && estadoAnterior != EstadoAtencion.CONFIRMADA
                && estadoAnterior != EstadoAtencion.EN_ESPERA) {
            throw new IllegalStateException("El paciente debe estar confirmado o en espera antes de pasar a EN_ATENCIÓN.");
        }

        atencion.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoAtencion.CONFIRMADA) {
            catalogClient.consumirCupo(atencion.getCupoId());
        }

        Atencion actualizada = atencionRepository.save(atencion);

        Map<String, Object> envelope = new HashMap<>();
        envelope.put("type", "APPOINTMENT_UPDATED");
        envelope.put("eventId", UUID.randomUUID().toString());
        envelope.put("traceId", UUID.randomUUID().toString());
        envelope.put("timestamp", LocalDateTime.now().toString());
        envelope.put("data", actualizada);

        if (nuevoEstado == EstadoAtencion.CONFIRMADA) {
            notificationProducer.enviarEmail(envelope);
        } else if (nuevoEstado == EstadoAtencion.EN_ESPERA) {
            notificationProducer.enviarEmail(envelope);
            notificationProducer.emitirTicket(envelope);
        } else if (nuevoEstado == EstadoAtencion.EN_ATENCION) {
            notificationProducer.emitirTicket(envelope);
        } else if (nuevoEstado == EstadoAtencion.CERRADA) {
            notificationProducer.enviarEmail(envelope);
            notificationProducer.generarPdf(envelope);
        } else if (nuevoEstado == EstadoAtencion.CANCELADA) {
            notificationProducer.enviarEmail(envelope);
        }

        CitasEventoDTO appointmentEvent = new CitasEventoDTO(
                UUID.randomUUID().toString(),
                "APPOINTMENT_STATUS_CHANGED",
                LocalDateTime.now(),
                actualizada.getId(),
                actualizada.getPacienteId(),
                actualizada.getRut(),
                actualizada.getNombrePaciente(),
                actualizada.getPrestacionId(),
                actualizada.getCupoId(),
                estadoAnterior.name(),
                actualizada.getEstado().name(),
                actualizada.getFechaCreacion()
        );
        kafkaProducer.publicarEventoCita(appointmentEvent);

        AuditTimelineEventDTO auditEvent = new AuditTimelineEventDTO(
                UUID.randomUUID().toString(),
                String.valueOf(actualizada.getId()),
                "Recepcion",
                "CAMBIO_ESTADO_" + actualizada.getEstado().name(),
                LocalDateTime.now(),
                "127.0.0.1",
                "ms-vidasalud-appointments",
                "La cita cambió de " + estadoAnterior.name() + " a " + actualizada.getEstado().name()
        );
        kafkaProducer.publicarEventoAuditoria(auditEvent);

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