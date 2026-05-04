package com.rednorte.sigle.listas_service.service;

import com.rednorte.sigle.listas_service.model.Paciente;
import com.rednorte.sigle.listas_service.repository.PacienteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository repository;

    public List<Paciente> getAll() {
        return repository.findAll();
    }

    public Paciente getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
    }

    public Paciente getByRut(String rut) {
        return repository.findByRut(rut).orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
    }

    public Paciente create(Paciente p) {
        return repository.save(p);
    }

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

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
