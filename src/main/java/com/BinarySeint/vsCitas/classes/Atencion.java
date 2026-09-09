package com.BinarySeint.vsCitas.classes;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAtencion estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public Atencion() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoAtencion.SOLICITADA; 
    }
    public void Atencion(Long id,String pacienteId,Long prestacionId,Long cupoId,EstadoAtencion estado, LocalDateTime fecha){
        this.id = id;
        this.pacienteId=pacienteId;
        this.prestacionId = prestacionId;
        this.cupoId = cupoId;
        this.estado = estado;
        this.fechaCreacion = fecha;
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

}