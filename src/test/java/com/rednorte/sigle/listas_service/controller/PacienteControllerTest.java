package com.rednorte.sigle.listas_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rednorte.sigle.listas_service.model.Paciente;
import com.rednorte.sigle.listas_service.service.PacienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PacienteController.class)
@DisplayName("PacienteController")
class PacienteControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean PacienteService pacienteService;

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
    @DisplayName("GET /api/listas/pacientes retorna 200 con lista")
    void getAll_returns200() throws Exception {
        when(pacienteService.getAll()).thenReturn(List.of(pacienteEjemplo));
        mockMvc.perform(get("/api/listas/pacientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }

    @Test
    @DisplayName("GET /api/listas/pacientes/{id} retorna 200 cuando existe")
    void getById_returns200() throws Exception {
        when(pacienteService.getById(1L)).thenReturn(pacienteEjemplo);
        mockMvc.perform(get("/api/listas/pacientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    @DisplayName("GET /api/listas/pacientes/rut/{rut} retorna 200")
    void getByRut_returns200() throws Exception {
        when(pacienteService.getByRut("12345678-9")).thenReturn(pacienteEjemplo);
        mockMvc.perform(get("/api/listas/pacientes/rut/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    @DisplayName("GET /api/listas/pacientes/email/{email} retorna 200")
    void getByEmail_returns200() throws Exception {
        when(pacienteService.getByEmail("juan@email.com")).thenReturn(pacienteEjemplo);
        mockMvc.perform(get("/api/listas/pacientes/email/juan@email.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@email.com"));
    }

    @Test
    @DisplayName("GET /api/listas/pacientes/email/{email} retorna 404 cuando no existe")
    void getByEmail_returns404() throws Exception {
        when(pacienteService.getByEmail("noexiste@email.com"))
                .thenThrow(new RuntimeException("Paciente no encontrado"));
        mockMvc.perform(get("/api/listas/pacientes/email/noexiste@email.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/listas/pacientes retorna 200 con paciente creado")
    void create_returns200() throws Exception {
        when(pacienteService.create(any(Paciente.class))).thenReturn(pacienteEjemplo);
        mockMvc.perform(post("/api/listas/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    @DisplayName("PUT /api/listas/pacientes/{id} retorna 200 con paciente actualizado")
    void update_returns200() throws Exception {
        when(pacienteService.update(anyLong(), any(Paciente.class))).thenReturn(pacienteEjemplo);
        mockMvc.perform(put("/api/listas/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pacienteEjemplo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/listas/pacientes/{id} retorna 200")
    void delete_returns200() throws Exception {
        mockMvc.perform(delete("/api/listas/pacientes/1"))
                .andExpect(status().isOk());
    }
}