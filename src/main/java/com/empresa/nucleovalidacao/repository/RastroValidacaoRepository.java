package com.empresa.nucleovalidacao.repository;

import com.empresa.nucleovalidacao.model.entity.RastroValidacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RastroValidacaoRepository extends JpaRepository<RastroValidacao, Long> {

    List<RastroValidacao> findByDataInicioAfter(LocalDateTime data);
}
