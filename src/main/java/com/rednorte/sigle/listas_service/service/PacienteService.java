package com.rednorte.sigle.listas_service.service;

import com.rednorte.sigle.listas_service.model.Paciente;
import com.rednorte.sigle.listas_service.repository.PacienteRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository repository;

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackGetAll")
    public List<Paciente> getAll() {
        return repository.findAll();
    }

    public List<Paciente> fallbackGetAll(Exception e) {
        return List.of();
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackGetById")
    public Paciente getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
    }

    public Paciente fallbackGetById(Long id, Exception e) {
        throw new RuntimeException("Servicio no disponible temporalmente");
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackGetByRut")
    public Paciente getByRut(String rut) {
        return repository.findByRut(rut).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
    }

    public Paciente fallbackGetByRut(String rut, Exception e) {
        throw new RuntimeException("Servicio no disponible temporalmente");
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackGetByEmail")
    public Paciente getByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
    }

    public Paciente fallbackGetByEmail(String email, Exception e) {
        throw new RuntimeException("Servicio no disponible temporalmente");
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackCreate")
    public Paciente create(Paciente p) {
        return repository.save(p);
    }

    public Paciente fallbackCreate(Paciente p, Exception e) {
        throw new RuntimeException("No se puede crear el paciente. Servicio no disponible temporalmente.");
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackUpdate")
    public Paciente update(Long id, Paciente data) {
        Paciente existing = getById(id);
        existing.setNombre(data.getNombre());
        existing.setApellido(data.getApellido());
        existing.setFechaNacimiento(data.getFechaNacimiento());
        existing.setEmail(data.getEmail());
        existing.setTelefono(data.getTelefono());
        existing.setEstablecimientoId(data.getEstablecimientoId());
        return repository.save(existing);
    }

    public Paciente fallbackUpdate(Long id, Paciente data, Exception e) {
        throw new RuntimeException("No se puede actualizar el paciente. Servicio no disponible temporalmente.");
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}