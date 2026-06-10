package com.rednorte.sigle.listas_service.service;

import com.rednorte.sigle.listas_service.model.EstadoLista;
import com.rednorte.sigle.listas_service.model.ListaEspera;
import com.rednorte.sigle.listas_service.model.Paciente;
import com.rednorte.sigle.listas_service.model.Prioridad;
import com.rednorte.sigle.listas_service.repository.ListaEsperaRepository;
import com.rednorte.sigle.listas_service.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListaEsperaService")
class ListaEsperaServiceTest {

    @Mock private ListaEsperaRepository listaRepository;
    @Mock private PacienteRepository pacienteRepository;
    @InjectMocks private ListaEsperaService listaEsperaService;

    private ListaEspera listaEjemplo;
    private Paciente pacienteEjemplo;

    @BeforeEach
    void setUp() {
        pacienteEjemplo = new Paciente();
        pacienteEjemplo.setId(1L);
        pacienteEjemplo.setRut("12345678-9");
        pacienteEjemplo.setNombre("Juan");
        pacienteEjemplo.setApellido("Pérez");
        pacienteEjemplo.setEmail("juan@email.com");

        listaEjemplo = ListaEspera.builder()
                .id(1L)
                .paciente(pacienteEjemplo)
                .especialidad("Cardiología")
                .diagnostico("Hipertensión arterial")
                .prioridad(Prioridad.ALTA)
                .estado(EstadoLista.ESPERA)
                .fechaIngreso(LocalDateTime.now())
                .perteneceGes(false)
                .build();
    }

    @Test
    @DisplayName("getAll retorna lista de espera")
    void getAll_retornaLista() {
        when(listaRepository.findAll()).thenReturn(List.of(listaEjemplo));
        List<ListaEspera> resultado = listaEsperaService.getAll();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getEspecialidad()).isEqualTo("Cardiología");
    }

    @Test
    @DisplayName("getById retorna lista cuando existe")
    void getById_retornaLista_cuandoExiste() {
        when(listaRepository.findById(1L)).thenReturn(Optional.of(listaEjemplo));
        ListaEspera resultado = listaEsperaService.getById(1L);
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getDiagnostico()).isEqualTo("Hipertensión arterial");
    }

    @Test
    @DisplayName("getById lanza excepción cuando no existe")
    void getById_lanzaExcepcion_cuandoNoExiste() {
        when(listaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> listaEsperaService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Registro en lista no encontrado");
    }

    @Test
    @DisplayName("getByPacienteId retorna listas del paciente")
    void getByPacienteId_retornaListas() {
        when(listaRepository.findByPacienteId(1L)).thenReturn(List.of(listaEjemplo));
        List<ListaEspera> resultado = listaEsperaService.getByPacienteId(1L);
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPaciente().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("calcularPrioridad retorna ALTA para GES")
    void registrar_prioridadAlta_paraPacienteGes() {
        when(pacienteRepository.findByRut("12345678-9")).thenReturn(Optional.of(pacienteEjemplo));
        when(listaRepository.save(any(ListaEspera.class))).thenAnswer(i -> i.getArgument(0));

        ListaEspera resultado = listaEsperaService.registrarPacienteEnLista(
                pacienteEjemplo, "Cardiología", "Control rutinario", true);

        assertThat(resultado.getPrioridad()).isEqualTo(Prioridad.ALTA);
        assertThat(resultado.getEstado()).isEqualTo(EstadoLista.ESPERA);
    }

    @Test
    @DisplayName("calcularPrioridad retorna ALTA para diagnostico urgente")
    void registrar_prioridadAlta_paraDiagnosticoUrgente() {
        when(pacienteRepository.findByRut("12345678-9")).thenReturn(Optional.of(pacienteEjemplo));
        when(listaRepository.save(any(ListaEspera.class))).thenAnswer(i -> i.getArgument(0));

        ListaEspera resultado = listaEsperaService.registrarPacienteEnLista(
                pacienteEjemplo, "Cardiología", "Caso urgente de corazón", false);

        assertThat(resultado.getPrioridad()).isEqualTo(Prioridad.ALTA);
    }

    @Test
    @DisplayName("calcularPrioridad retorna BAJA para diagnostico normal")
    void registrar_prioridadBaja_paraDiagnosticoNormal() {
        when(pacienteRepository.findByRut("12345678-9")).thenReturn(Optional.of(pacienteEjemplo));
        when(listaRepository.save(any(ListaEspera.class))).thenAnswer(i -> i.getArgument(0));

        ListaEspera resultado = listaEsperaService.registrarPacienteEnLista(
                pacienteEjemplo, "Cardiología", "Control de rutina", false);

        assertThat(resultado.getPrioridad()).isEqualTo(Prioridad.BAJA);
    }

    @Test
    @DisplayName("updateEstado actualiza estado y diagnostico")
    void updateEstado_actualizaEstadoYDiagnostico() {
        when(listaRepository.findById(1L)).thenReturn(Optional.of(listaEjemplo));
        when(listaRepository.save(any(ListaEspera.class))).thenAnswer(i -> i.getArgument(0));

        ListaEspera resultado = listaEsperaService.updateEstado(1L, EstadoLista.ATENDIDO, "Nuevo diagnóstico");

        assertThat(resultado.getEstado()).isEqualTo(EstadoLista.ATENDIDO);
        assertThat(resultado.getDiagnostico()).isEqualTo("Nuevo diagnóstico");
    }

    @Test
    @DisplayName("delete elimina lista por id")
    void delete_eliminaLista() {
        doNothing().when(listaRepository).deleteById(1L);
        listaEsperaService.delete(1L);
        verify(listaRepository, times(1)).deleteById(1L);
    }
}