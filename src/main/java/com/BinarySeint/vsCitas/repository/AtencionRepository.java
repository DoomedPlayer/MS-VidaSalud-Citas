package com.BinarySeint.vsCitas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.BinarySeint.vsCitas.classes.Atencion;
import com.BinarySeint.vsCitas.classes.EstadoAtencion;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AtencionRepository extends JpaRepository<Atencion, Long> {
    // Se cambió FechaHora por FechaCreacion
    List<Atencion> findByEstadoAndFechaCreacionBetween(EstadoAtencion estado, LocalDateTime from, LocalDateTime to);

    // Se cambió FechaHora por FechaCreacion
    List<Atencion> findByFechaCreacionBetween(LocalDateTime from, LocalDateTime to);

    List<Atencion> findByEstado(EstadoAtencion estado);
}