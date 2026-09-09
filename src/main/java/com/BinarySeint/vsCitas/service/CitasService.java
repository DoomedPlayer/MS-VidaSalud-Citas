package com.BinarySeint.vsCitas.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.BinarySeint.vsCitas.classes.Atencion;
import com.BinarySeint.vsCitas.classes.EstadoAtencion;
import com.BinarySeint.vsCitas.repository.AtencionRepository;

@Service
public class CitasService {

    private final AtencionRepository atencionRepository;
    private final CitasEventProducer eventProducer;

    public CitasService(AtencionRepository atencionRepository, CitasEventProducer eventProducer) {
        this.atencionRepository = atencionRepository;
        this.eventProducer=eventProducer;
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
        
        // Si el nuevoEstado es CONFIRMADA, aquí deberás enviar un request a ms-vidasalud-catalog
        // porque el cupo del box disminuye al confirmar la atención[cite: 1]

        // Aquí también enviarías un mensaje asíncrono (cola) a RabbitMQ para la notificación al paciente y ticket al box[cite: 1]
        Atencion actualizada = atencionRepository.save(atencion);

        // Publicamos el evento del cambio de estado al tópico appointments.events[cite: 1]
        eventProducer.publicarEvento(actualizada);

        return actualizada;
    }
}