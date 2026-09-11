package com.BinarySeint.vsCitas.service;

import java.util.HashMap;
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
    private final CitasEventProducer eventProducer;
    private final CatalogFeignClient catalogClient;
    private final RabbitTemplate rabbitTemplate;

    public CitasService(AtencionRepository atencionRepository, CitasEventProducer eventProducer,CatalogFeignClient catalogClient,
                              RabbitTemplate rabbitTemplate) {
        this.atencionRepository = atencionRepository;
        this.eventProducer = eventProducer;
        this.catalogClient = catalogClient;
        this.rabbitTemplate = rabbitTemplate;
    }
    

    @Transactional
    public Atencion crearAtencion(Atencion atencion) {
        atencion.setEstado(EstadoAtencion.SOLICITADA);
        Atencion guardada = atencionRepository.save(atencion);
    
        eventProducer.publicarEvento(guardada);
        
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
            rabbitTemplate.convertAndSend("cmd.direct", "email.send", envelope);
        } else if (nuevoEstado == EstadoAtencion.EN_ESPERA) {
            rabbitTemplate.convertAndSend("cmd.direct", "admission.ticket", envelope);
        } else if (nuevoEstado == EstadoAtencion.CERRADA) {
            rabbitTemplate.convertAndSend("cmd.direct", "record.gen", envelope);
        }

        eventProducer.publicarEvento(actualizada);

        return actualizada;
    }
}