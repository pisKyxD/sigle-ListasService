package com.rednorte.sigle.listas_service.dto;

import com.rednorte.sigle.listas_service.model.EstadoLista;
import com.rednorte.sigle.listas_service.model.ListaEspera;
import com.rednorte.sigle.listas_service.model.Prioridad;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ListaEsperaDTO {
    private Long id;
    private Long pacienteId;
    private String especialidad;
    private String diagnostico;
    private Prioridad prioridad;
    private EstadoLista estado;
    private LocalDateTime fechaIngreso;
    private Boolean perteneceGes;

    public static ListaEsperaDTO from(ListaEspera lista) {
        ListaEsperaDTO dto = new ListaEsperaDTO();
        dto.setId(lista.getId());
        dto.setPacienteId(lista.getPaciente() != null ? lista.getPaciente().getId() : null);
        dto.setEspecialidad(lista.getEspecialidad());
        dto.setDiagnostico(lista.getDiagnostico());
        dto.setPrioridad(lista.getPrioridad());
        dto.setEstado(lista.getEstado());
        dto.setFechaIngreso(lista.getFechaIngreso());
        dto.setPerteneceGes(lista.getPerteneceGes());
        return dto;
    }
}