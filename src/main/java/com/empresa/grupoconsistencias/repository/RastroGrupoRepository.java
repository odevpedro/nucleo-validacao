package com.empresa.grupoconsistencias.repository;

import com.empresa.grupoconsistencias.model.entity.RastroGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RastroGrupoRepository extends JpaRepository<RastroGrupo, Long> {

    List<RastroGrupo> findByDataInicioAfter(LocalDateTime data);
}
