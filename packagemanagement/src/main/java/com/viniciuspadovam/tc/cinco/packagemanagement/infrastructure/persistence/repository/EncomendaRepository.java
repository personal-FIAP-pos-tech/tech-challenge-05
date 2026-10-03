package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.repository;

import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity.EncomendaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncomendaRepository extends JpaRepository<EncomendaEntity, Long> {

	List<EncomendaEntity> findByMoradorIdOrderByDataRecebimentoDesc(Long moradorId);
}
