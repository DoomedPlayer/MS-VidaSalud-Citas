package com.BinarySeint.vsCitas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.BinarySeint.vsCitas.classes.Atencion;
import com.BinarySeint.vsCitas.service.CitasService;

import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class CitasController {

    private final CitasService appointmentService;

    public CitasController(CitasService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<Atencion> createAppointment(@RequestBody Atencion atencion) {
        return ResponseEntity.ok(appointmentService.crearAtencion(atencion));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atencion> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.obtenerAtencion(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Atencion> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(appointmentService.cambiarEstado(id, status));
    }
}
