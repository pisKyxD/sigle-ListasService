package com.rednorte.sigle.listas_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rednorte.sigle.listas_service.dto.ListaEsperaDTO;
import com.rednorte.sigle.listas_service.model.EstadoLista;
import com.rednorte.sigle.listas_service.model.ListaEspera;
import com.rednorte.sigle.listas_service.model.Paciente;
import com.rednorte.sigle.listas_service.model.Prioridad;
import com.rednorte.sigle.listas_service.service.ListaEsperaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListaEsperaController.class)
@DisplayName("ListaEsperaController")
class ListaEsperaControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean ListaEsperaService listaEsperaService;

    private ListaEspera buildLista() {
        Paciente p = new Paciente();
        p.setId(1L);
        p.setRut("12345678-9");
        p.setNombre("Juan");
        p.setApellido("Pérez");

        return ListaEspera.builder()
                .id(1L)
                .paciente(p)
                .especialidad("Cardiología")
                .diagnostico("Hipertensión")
                .prioridad(Prioridad.ALTA)
                .estado(EstadoLista.ESPERA)
                .fechaIngreso(LocalDateTime.now())
                .perteneceGes(false)
                .build();
    }

    @Test
    @DisplayName("GET /api/listas retorna 200 con lista")
    void getAll_returns200() throws Exception {
        when(listaEsperaService.getAll()).thenReturn(List.of(buildLista()));
        mockMvc.perform(get("/api/listas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].especialidad").value("Cardiología"));
    }

    @Test
    @DisplayName("GET /api/listas/{id} retorna 200 cuando existe")
    void getById_returns200() throws Exception {
        when(listaEsperaService.getById(1L)).thenReturn(buildLista());
        mockMvc.perform(get("/api/listas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/listas/paciente/{id} retorna listas del paciente")
    void getByPacienteId_returns200() throws Exception {
        when(listaEsperaService.getByPacienteId(1L)).thenReturn(List.of(buildLista()));
        mockMvc.perform(get("/api/listas/paciente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].especialidad").value("Cardiología"));
    }

    @Test
    @DisplayName("GET /api/listas/especialidad/{especialidad} retorna listas")
    void getByEspecialidad_returns200() throws Exception {
        when(listaEsperaService.obtenerListasPorEspecialidad("Cardiología"))
                .thenReturn(List.of(buildLista()));
        mockMvc.perform(get("/api/listas/especialidad/Cardiología"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("PUT /api/listas/{id}/estado retorna 200 con estado actualizado")
    void updateEstado_returns200() throws Exception {
        ListaEspera actualizada = buildLista();
        actualizada.setEstado(EstadoLista.ATENDIDO);
        when(listaEsperaService.updateEstado(1L, EstadoLista.ATENDIDO, null))
                .thenReturn(actualizada);
        mockMvc.perform(put("/api/listas/1/estado")
                .param("estado", "ATENDIDO"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/listas/{id} retorna 200")
    void delete_returns200() throws Exception {
        mockMvc.perform(delete("/api/listas/1"))
                .andExpect(status().isOk());
    }
}