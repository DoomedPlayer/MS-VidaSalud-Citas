package com.BinarySeint.vsCitas.dto;

import java.time.LocalDateTime;

public class AuditoriaEvent {

    private String usuarioId;
    private String accion;
    private LocalDateTime fechaHora;
    private String origenIp;
    private String entidadId;
    private String detalles;

    public AuditoriaEvent() {
    }

    public AuditoriaEvent(String usuarioId, String accion, LocalDateTime fechaHora, String origenIp, String entidadId, String detalles) {
        this.usuarioId = usuarioId;
        this.accion = accion;
        this.fechaHora = fechaHora;
        this.origenIp = origenIp;
        this.entidadId = entidadId;
        this.detalles = detalles;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getOrigenIp() {
        return origenIp;
    }

    public void setOrigenIp(String origenIp) {
        this.origenIp = origenIp;
    }

    public String getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(String entidadId) {
        this.entidadId = entidadId;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }
}
