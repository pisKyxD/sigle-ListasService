package com.rednorte.sigle.listas_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rednorte.sigle.listas_service.model.EstadoLista;
import com.rednorte.sigle.listas_service.model.ListaEspera;

import java.util.List;

@Repository
public interface ListaEsperaRepository extends JpaRepository<ListaEspera, Long> {
    List<ListaEspera> findByPacienteId(Long pacienteId);
    List<ListaEspera> findByEstado(EstadoLista estado);
    List<ListaEspera> findByEspecialidad(String especialidad);

    @Query("SELECT l FROM ListaEspera l WHERE l.especialidad = :especialidad AND l.estado = :estado ORDER BY l.fechaIngreso ASC")
    List<ListaEspera> findByEspecialidadAndEstado(@Param("especialidad") String especialidad, @Param("estado") EstadoLista estado);
}