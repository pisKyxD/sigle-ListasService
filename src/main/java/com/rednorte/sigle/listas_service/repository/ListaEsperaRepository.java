package com.rednorte.sigle.listas_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rednorte.sigle.listas_service.model.EstadoLista;
import com.rednorte.sigle.listas_service.model.ListaEspera;

import java.util.List;

@Repository
public interface ListaEsperaRepository extends JpaRepository<ListaEspera, Long> {
    List<ListaEspera> findByPacienteId(Long pacienteId);
    List<ListaEspera> findByEstado(EstadoLista estado);
    List<ListaEspera> findByEspecialidadAndEstadoOrderByPrioridadAscFechaIngresoAsc(String especialidad, EstadoLista estado);
}
