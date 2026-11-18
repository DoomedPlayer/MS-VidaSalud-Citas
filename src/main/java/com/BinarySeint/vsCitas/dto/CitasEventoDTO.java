package com.BinarySeint.vsCitas.dto;

import java.time.LocalDateTime;

public class CitasEventoDTO {

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private Long appointmentId;
    private String pacienteId;
    private String rut;
    private String nombrePaciente;
    private Long prestacionId;
    private Long cupoId;
    private String estadoAnterior;
    private String estadoActual;
    private LocalDateTime fechaCreacion;

    public CitasEventoDTO() {
    }

    public CitasEventoDTO(String eventId, String eventType, LocalDateTime timestamp,
                               Long appointmentId, String pacienteId, String rut,
                               String nombrePaciente, Long prestacionId, Long cupoId,
                               String estadoAnterior, String estadoActual, LocalDateTime fechaCreacion) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.appointmentId = appointmentId;
        this.pacienteId = pacienteId;
        this.rut = rut;
        this.nombrePaciente = nombrePaciente;
        this.prestacionId = prestacionId;
        this.cupoId = cupoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoActual = estadoActual;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getNombrePaciente() { return nombrePaciente; }
    public void setNombrePaciente(String nombrePaciente) { this.nombrePaciente = nombrePaciente; }

    public Long getPrestacionId() { return prestacionId; }
    public void setPrestacionId(Long prestacionId) { this.prestacionId = prestacionId; }

    public Long getCupoId() { return cupoId; }
    public void setCupoId(Long cupoId) { this.cupoId = cupoId; }

    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public String getEstadoActual() { return estadoActual; }
    public void setEstadoActual(String estadoActual) { this.estadoActual = estadoActual; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}