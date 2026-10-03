package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.repository;

import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity.NotificacaoEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<NotificacaoEntity, Long> {

	Optional<NotificacaoEntity> findByEncomendaId(Long encomendaId);
}
