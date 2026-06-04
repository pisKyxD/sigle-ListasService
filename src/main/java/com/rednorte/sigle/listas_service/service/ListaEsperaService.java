package com.rednorte.sigle.listas_service.service;

import com.rednorte.sigle.listas_service.model.EstadoLista;
import com.rednorte.sigle.listas_service.model.ListaEspera;
import com.rednorte.sigle.listas_service.model.Paciente;
import com.rednorte.sigle.listas_service.model.Prioridad;
import com.rednorte.sigle.listas_service.repository.ListaEsperaRepository;
import com.rednorte.sigle.listas_service.repository.PacienteRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListaEsperaService {

    private final ListaEsperaRepository listaRepository;
    private final PacienteRepository pacienteRepository;

    public List<ListaEspera> getAll() {
        return listaRepository.findAll();
    }

    public ListaEspera getById(Long id) {
        return listaRepository.findById(id).orElseThrow(() -> new RuntimeException("Registro en lista no encontrado"));
    }

    public List<ListaEspera> getByPacienteId(Long pacienteId) {
        return listaRepository.findByPacienteId(pacienteId);
    }

    public List<ListaEspera> getByPacienteEmail(String email) {
        Paciente paciente = pacienteRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        return listaRepository.findByPacienteId(paciente.getId());
    }

    public ListaEspera registrarPacienteEnLista(Paciente paciente, String especialidad, String diagnostico, Boolean perteneceGes) {
        Paciente savedPaciente = pacienteRepository.findByRut(paciente.getRut())
                .orElseGet(() -> pacienteRepository.save(paciente));

        Prioridad prioridadCalculada = calcularPrioridad(diagnostico, perteneceGes);

        ListaEspera nuevaLista = ListaEspera.builder()
                .paciente(savedPaciente)
                .especialidad(especialidad)
                .diagnostico(diagnostico)
                .prioridad(prioridadCalculada)
                .estado(EstadoLista.ESPERA)
                .fechaIngreso(LocalDateTime.now())
                .perteneceGes(perteneceGes)
                .build();

        return listaRepository.save(nuevaLista);
    }

    private Prioridad calcularPrioridad(String diagnostico, Boolean isGes) {
        if (isGes != null && isGes) {
            return Prioridad.ALTA;
        }
        if (diagnostico.toLowerCase().contains("urgente") || diagnostico.toLowerCase().contains("grave")) {
            return Prioridad.ALTA;
        } else if (diagnostico.toLowerCase().contains("crónico") || diagnostico.toLowerCase().contains("cronico")) {
            return Prioridad.MEDIA;
        }
        return Prioridad.BAJA;
    }

    public List<ListaEspera> obtenerListasPorEspecialidad(String especialidad) {
        return listaRepository.findByEspecialidad(especialidad);
    }

    public ListaEspera updateEstado(Long id, EstadoLista estado) {
        ListaEspera existing = getById(id);
        existing.setEstado(estado);
        return listaRepository.save(existing);
    }

    public void delete(Long id) {
        listaRepository.deleteById(id);
    }
}