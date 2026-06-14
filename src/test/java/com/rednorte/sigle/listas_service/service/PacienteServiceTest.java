package com.rednorte.sigle.listas_service.service;

import com.rednorte.sigle.listas_service.model.Paciente;
import com.rednorte.sigle.listas_service.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PacienteService")
class PacienteServiceTest {

    @Mock private PacienteRepository repository;
    @InjectMocks private PacienteService pacienteService;

    private Paciente pacienteEjemplo;

    @BeforeEach
    void setUp() {
        pacienteEjemplo = new Paciente();
        pacienteEjemplo.setId(1L);
        pacienteEjemplo.setRut("12345678-9");
        pacienteEjemplo.setNombre("Juan");
        pacienteEjemplo.setApellido("Pérez");
        pacienteEjemplo.setEmail("juan@email.com");
        pacienteEjemplo.setTelefono("+56912345678");
    }

    @Test
    @DisplayName("getAll retorna lista de pacientes")
    void getAll_retornaLista() {
        when(repository.findAll()).thenReturn(List.of(pacienteEjemplo));
        List<Paciente> resultado = pacienteService.getAll();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("getById retorna paciente cuando existe")
    void getById_retornaPaciente() {
        when(repository.findById(1L)).thenReturn(Optional.of(pacienteEjemplo));
        Paciente resultado = pacienteService.getById(1L);
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getRut()).isEqualTo("12345678-9");
    }

    @Test
    @DisplayName("getById lanza excepción cuando no existe")
    void getById_lanzaExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> pacienteService.getById(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Paciente no encontrado");
    }

    @Test
    @DisplayName("getByRut retorna paciente cuando existe")
    void getByRut_retornaPaciente() {
        when(repository.findByRut("12345678-9")).thenReturn(Optional.of(pacienteEjemplo));
        Paciente resultado = pacienteService.getByRut("12345678-9");
        assertThat(resultado.getRut()).isEqualTo("12345678-9");
    }

    @Test
    @DisplayName("getByEmail retorna paciente cuando existe")
    void getByEmail_retornaPaciente() {
        when(repository.findByEmail("juan@email.com")).thenReturn(Optional.of(pacienteEjemplo));
        Paciente resultado = pacienteService.getByEmail("juan@email.com");
        assertThat(resultado.getEmail()).isEqualTo("juan@email.com");
    }

    @Test
    @DisplayName("create guarda paciente correctamente")
    void create_guardaPaciente() {
        when(repository.save(any(Paciente.class))).thenReturn(pacienteEjemplo);
        Paciente resultado = pacienteService.create(pacienteEjemplo);
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("update actualiza datos del paciente")
    void update_actualizaDatos() {
        Paciente datosNuevos = new Paciente();
        datosNuevos.setNombre("Pedro");
        datosNuevos.setApellido("González");
        datosNuevos.setEmail("pedro@email.com");
        datosNuevos.setTelefono("+56987654321");

        when(repository.findById(1L)).thenReturn(Optional.of(pacienteEjemplo));
        when(repository.save(any(Paciente.class))).thenAnswer(i -> i.getArgument(0));

        Paciente resultado = pacienteService.update(1L, datosNuevos);
        assertThat(resultado.getNombre()).isEqualTo("Pedro");
        assertThat(resultado.getEmail()).isEqualTo("pedro@email.com");
    }

    @Test
    @DisplayName("delete elimina paciente por id")
    void delete_eliminaPaciente() {
        doNothing().when(repository).deleteById(1L);
        pacienteService.delete(1L);
        verify(repository, times(1)).deleteById(1L);
    }
}