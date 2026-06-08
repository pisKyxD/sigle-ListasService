package com.rednorte.sigle.listas_service.service;

import com.rednorte.sigle.listas_service.model.EstadoLista;
import com.rednorte.sigle.listas_service.model.ListaEspera;
import com.rednorte.sigle.listas_service.model.Paciente;
import com.rednorte.sigle.listas_service.model.Prioridad;
import com.rednorte.sigle.listas_service.repository.ListaEsperaRepository;
import com.rednorte.sigle.listas_service.repository.PacienteRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListaEsperaService {

    private final ListaEsperaRepository listaRepository;
    private final PacienteRepository pacienteRepository;

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackGetAll")
    public List<ListaEspera> getAll() {
        return listaRepository.findAll();
    }

    public List<ListaEspera> fallbackGetAll(Exception e) {
        return List.of();
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackGetAllPaginado")
    public Page<ListaEspera> getAllPaginado(Pageable pageable) {
        return listaRepository.findAll(pageable);
    }

    public Page<ListaEspera> fallbackGetAllPaginado(Pageable pageable, Exception e) {
        return Page.empty();
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackGetById")
    public ListaEspera getById(Long id) {
        return listaRepository.findById(id).orElseThrow(() -> new RuntimeException("Registro en lista no encontrado"));
    }

    public ListaEspera fallbackGetById(Long id, Exception e) {
        throw new RuntimeException("Servicio de listas no disponible temporalmente");
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackGetByPacienteId")
    public List<ListaEspera> getByPacienteId(Long pacienteId) {
        return listaRepository.findByPacienteId(pacienteId);
    }

    public List<ListaEspera> fallbackGetByPacienteId(Long pacienteId, Exception e) {
        return List.of();
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackGetByPacienteIdPaginado")
    public Page<ListaEspera> getByPacienteIdPaginado(Long pacienteId, Pageable pageable) {
        return listaRepository.findByPacienteId(pacienteId, pageable);
    }

    public Page<ListaEspera> fallbackGetByPacienteIdPaginado(Long pacienteId, Pageable pageable, Exception e) {
        return Page.empty();
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackGetByPacienteEmail")
    public List<ListaEspera> getByPacienteEmail(String email) {
        Paciente paciente = pacienteRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        return listaRepository.findByPacienteId(paciente.getId());
    }

    public List<ListaEspera> fallbackGetByPacienteEmail(String email, Exception e) {
        return List.of();
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackRegistrar")
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

    public ListaEspera fallbackRegistrar(Paciente paciente, String especialidad, String diagnostico, Boolean perteneceGes, Exception e) {
        throw new RuntimeException("No se puede registrar el paciente. Servicio no disponible temporalmente.");
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

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackObtenerPorEspecialidad")
    public List<ListaEspera> obtenerListasPorEspecialidad(String especialidad) {
        return listaRepository.findByEspecialidad(especialidad);
    }

    public List<ListaEspera> fallbackObtenerPorEspecialidad(String especialidad, Exception e) {
        return List.of();
    }

    @CircuitBreaker(name = "listasService", fallbackMethod = "fallbackUpdateEstado")
    public ListaEspera updateEstado(Long id, EstadoLista estado) {
        ListaEspera existing = getById(id);
        existing.setEstado(estado);
        return listaRepository.save(existing);
    }

    public ListaEspera fallbackUpdateEstado(Long id, EstadoLista estado, Exception e) {
        throw new RuntimeException("No se puede actualizar el estado. Servicio no disponible temporalmente.");
    }

    public void delete(Long id) {
        listaRepository.deleteById(id);
    }
}