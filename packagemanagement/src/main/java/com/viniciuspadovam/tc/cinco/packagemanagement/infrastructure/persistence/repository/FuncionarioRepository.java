package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.repository;

import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity.FuncionarioEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<FuncionarioEntity, Long> {

	Optional<FuncionarioEntity> findByEmail(String email);

	boolean existsByEmail(String email);
}
