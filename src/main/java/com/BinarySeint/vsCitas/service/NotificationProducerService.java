package com.BinarySeint.vsCitas.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service 
public class NotificationProducerService {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducerService.class);
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchanges.direct}")
    private String directExchange;

    @Value("${rabbitmq.routing.email-send}")
    private String emailRoutingKey;

    @Value("${rabbitmq.routing.admission-ticket}")
    private String admissionRoutingKey;

    @Value("${rabbitmq.routing.record-gen}")
    private String recordRoutingKey;

    public NotificationProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarEmail(Map<String, Object> envelope) {
        log.info("📤 Publicando evento de EMAIL en RabbitMQ (Exchange: {}, Routing: {})", directExchange, emailRoutingKey);
        rabbitTemplate.convertAndSend(directExchange, emailRoutingKey, envelope);
    }

    public void emitirTicket(Map<String, Object> envelope) {
        log.info("📤 Publicando evento de TICKET DE ADMISIÓN en RabbitMQ (Exchange: {}, Routing: {})", directExchange, admissionRoutingKey);
        rabbitTemplate.convertAndSend(directExchange, admissionRoutingKey, envelope);
    }

    public void generarPdf(Map<String, Object> envelope) {
        log.info("📤 Publicando evento de PDF/RECORD en RabbitMQ (Exchange: {}, Routing: {})", directExchange, recordRoutingKey);
        rabbitTemplate.convertAndSend(directExchange, recordRoutingKey, envelope);
    }
}