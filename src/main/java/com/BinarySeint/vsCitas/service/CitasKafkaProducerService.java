package com.BinarySeint.vsCitas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.BinarySeint.vsCitas.dto.CitasEventoDTO;
import com.BinarySeint.vsCitas.dto.AuditTimelineEventDTO;

@Service 
public class CitasKafkaProducerService {
private static final Logger log = LoggerFactory.getLogger(CitasKafkaProducerService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.appointments-events}")
    private String appointmentsEventsTopic;

    @Value("${kafka.topics.audit-timeline}")
    private String auditTimelineTopic;

    public CitasKafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEventoCita(CitasEventoDTO event) {
        if (event == null || event.getAppointmentId() == null) {
            log.warn("⚠️ Payload inválido para publicar en {}", appointmentsEventsTopic);
            return;
        }

        String key = String.valueOf(event.getAppointmentId());

        kafkaTemplate.send(appointmentsEventsTopic, key, event)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("📨 Evento [{}] publicado en tópico '{}' | Key: {} | Partición: {} | Offset: {}",
                            event.getEventType(),
                            appointmentsEventsTopic,
                            key,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("❌ Error al publicar evento [{}] en tópico '{}' | Key: {} | Causa: {}",
                            event.getEventType(),
                            appointmentsEventsTopic,
                            key,
                            ex.getMessage(), ex);
                }
            });
    }

    public void publicarEventoAuditoria(AuditTimelineEventDTO auditEvent) {
        if (auditEvent == null || auditEvent.getAppointmentId() == null) {
            log.warn("⚠️ Payload inválido para publicar en {}", auditTimelineTopic);
            return;
        }

        String key = auditEvent.getAppointmentId();

        kafkaTemplate.send(auditTimelineTopic, key, auditEvent)
            .whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("🛡️ Auditoría [{}] publicada en tópico '{}' | Key: {} | Partición: {} | Offset: {}",
                            auditEvent.getAccion(),
                            auditTimelineTopic,
                            key,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                } else {
                    log.error("❌ Error al publicar auditoría [{}] en tópico '{}' | Key: {} | Causa: {}",
                            auditEvent.getAccion(),
                            auditTimelineTopic,
                            key,
                            ex.getMessage(), ex);
                }
            });
    }
}
