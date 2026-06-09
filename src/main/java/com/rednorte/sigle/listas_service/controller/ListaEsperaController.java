package com.rednorte.sigle.listas_service.controller;

import com.rednorte.sigle.listas_service.dto.ListaEsperaDTO;
import com.rednorte.sigle.listas_service.model.EstadoLista;
import com.rednorte.sigle.listas_service.model.ListaEspera;
import com.rednorte.sigle.listas_service.model.Paciente;
import com.rednorte.sigle.listas_service.service.ListaEsperaService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listas")
@RequiredArgsConstructor
public class ListaEsperaController {

    private final ListaEsperaService listaService;

    @GetMapping
    public ResponseEntity<List<ListaEsperaDTO>> getAll() {
        return ResponseEntity.ok(listaService.getAll().stream()
                .map(ListaEsperaDTO::from)
                .toList());
    }

    @GetMapping("/paginado")
    public ResponseEntity<Map<String, Object>> getAllPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ListaEspera> resultado = listaService.getAllPaginado(PageRequest.of(page, size));
        Map<String, Object> response = new HashMap<>();
        response.put("content", resultado.getContent().stream().map(ListaEsperaDTO::from).toList());
        response.put("totalPages", resultado.getTotalPages());
        response.put("totalElements", resultado.getTotalElements());
        response.put("currentPage", resultado.getNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListaEsperaDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ListaEsperaDTO.from(listaService.getById(id)));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<ListaEsperaDTO>> getByPacienteId(@PathVariable Long pacienteId) {
        return ResponseEntity.ok(listaService.getByPacienteId(pacienteId).stream()
                .map(ListaEsperaDTO::from)
                .toList());
    }

    @GetMapping("/paciente/{pacienteId}/paginado")
    public ResponseEntity<Map<String, Object>> getByPacienteIdPaginado(
            @PathVariable Long pacienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<ListaEspera> resultado = listaService.getByPacienteIdPaginado(pacienteId, PageRequest.of(page, size));
        Map<String, Object> response = new HashMap<>();
        response.put("content", resultado.getContent().stream().map(ListaEsperaDTO::from).toList());
        response.put("totalPages", resultado.getTotalPages());
        response.put("totalElements", resultado.getTotalElements());
        response.put("currentPage", resultado.getNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paciente/email/{email}")
    public ResponseEntity<List<ListaEsperaDTO>> getByPacienteEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(listaService.getByPacienteEmail(email).stream()
                    .map(ListaEsperaDTO::from)
                    .toList());
        } catch (RuntimeException e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<ListaEspera> registrarPaciente(@Valid @RequestBody RegistroListaRequest request) {
        ListaEspera nueva = listaService.registrarPacienteEnLista(
                request.getPaciente(),
                request.getEspecialidad(),
                request.getDiagnostico(),
                request.getPerteneceGes());
        return ResponseEntity.ok(nueva);
    }

    @GetMapping("/especialidad/{especialidad}")
    public ResponseEntity<List<ListaEsperaDTO>> obtenerPorEspecialidad(@PathVariable String especialidad) {
        return ResponseEntity.ok(listaService.obtenerListasPorEspecialidad(especialidad).stream()
                .map(ListaEsperaDTO::from)
                .toList());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<ListaEsperaDTO> updateEstado(
            @PathVariable Long id,
            @RequestParam EstadoLista estado,
            @RequestParam(required = false) String diagnostico) {
        return ResponseEntity.ok(ListaEsperaDTO.from(listaService.updateEstado(id, estado, diagnostico)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        listaService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class RegistroListaRequest {
        @NotNull(message = "El paciente es obligatorio")
        @Valid
        private Paciente paciente;

        @NotBlank(message = "La especialidad es obligatoria")
        private String especialidad;

        @NotBlank(message = "El diagnóstico es obligatorio")
        private String diagnostico;

        private Boolean perteneceGes;
    }
}