package com.BinarySeint.vsCitas.classes;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "atenciones")
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "paciente_id", nullable = false)
    private String pacienteId; 

    @Column(name = "prestacion_id", nullable = false)
    private Long prestacionId; 

    @Column(name = "cupo_id", nullable = false)
    private Long cupoId; 

    @Column(name = "rut")
    private String rut;

    @Column(name = "nombre_paciente")
    private String nombrePaciente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAtencion estado;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Atencion() {
    }
    public Atencion(Long id,String pacienteId,Long prestacionId,Long cupoId,EstadoAtencion estado, LocalDateTime fecha, String rut, String nombre){
        this.id = id;
        this.pacienteId=pacienteId;
        this.prestacionId = prestacionId;
        this.cupoId = cupoId;
        this.estado = estado;
        this.fechaCreacion = fecha;
        this.rut=rut;
        this.nombrePaciente=nombre;
    }
    public Long getId(){
        return id;
    }

    public String getPacienteId(){
        return pacienteId;
    }
    public void setPacienteId(String pacienteId){
        this.pacienteId=pacienteId;
    }
    public Long getPrestacionId(){
        return prestacionId;
    }
    public void setPrestacionId(Long prestacionId){
        this.prestacionId=prestacionId;
    }
    public Long getCupoId(){
        return cupoId;
    }
    public void setCupoId(Long cupoId){
        this.cupoId=cupoId;
    }
    public EstadoAtencion getEstado(){
        return estado;
    }
    public void setEstado(EstadoAtencion estado){
        this.estado=estado;
    }
    public LocalDateTime getFechaCreacion(){
        return fechaCreacion;
    }
    public void setFechaCreacion(LocalDateTime fecha){
        this.fechaCreacion=fecha;
    }

    public String getRut() {
        return rut;
    }
    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }
    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    
    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.estado == null) {
            this.estado = EstadoAtencion.SOLICITADA;
        }
    }
}