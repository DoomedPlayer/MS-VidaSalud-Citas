package com.BinarySeint.vsCitas.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.BinarySeint.vsCitas.classes.Atencion;

@Service 
public class CitasEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "appointments.events"; 

    public CitasEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publicarEvento(Atencion atencion) {
        kafkaTemplate.send(TOPIC, atencion.getId().toString(), atencion);
        System.out.println("Evento publicado en Kafka para la atención ID: " + atencion.getId());
    }
}
