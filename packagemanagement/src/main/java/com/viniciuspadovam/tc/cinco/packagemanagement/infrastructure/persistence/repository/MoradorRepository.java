package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.repository;

import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity.MoradorEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoradorRepository extends JpaRepository<MoradorEntity, Long> {

	Optional<MoradorEntity> findByEmail(String email);

	boolean existsByEmail(String email);

	Optional<MoradorEntity> findByApartamentoAndNomeNormalizado(String apartamento, String nomeNormalizado);
}
