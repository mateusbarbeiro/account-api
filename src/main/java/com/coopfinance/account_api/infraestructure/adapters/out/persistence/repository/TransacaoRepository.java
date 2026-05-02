package com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository;

import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.transacao.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoEntity, UUID> {
    
}
