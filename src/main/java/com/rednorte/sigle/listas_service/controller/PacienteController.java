package com.rednorte.sigle.listas_service.controller;

import com.rednorte.sigle.listas_service.model.Paciente;
import com.rednorte.sigle.listas_service.service.PacienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listas/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService service;

    @GetMapping
    public ResponseEntity<List<Paciente>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<Paciente> getByRut(@PathVariable String rut) {
        return ResponseEntity.ok(service.getByRut(rut));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Paciente> getByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(service.getByEmail(email));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Paciente> create(@Valid @RequestBody Paciente p) {
        return ResponseEntity.ok(service.create(p));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> update(@PathVariable Long id, @Valid @RequestBody Paciente p) {
        return ResponseEntity.ok(service.update(id, p));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}