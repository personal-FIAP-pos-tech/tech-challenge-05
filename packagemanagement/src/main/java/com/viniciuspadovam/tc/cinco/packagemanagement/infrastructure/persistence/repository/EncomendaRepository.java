package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.repository;

import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity.EncomendaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncomendaRepository extends JpaRepository<EncomendaEntity, Long> {
}
