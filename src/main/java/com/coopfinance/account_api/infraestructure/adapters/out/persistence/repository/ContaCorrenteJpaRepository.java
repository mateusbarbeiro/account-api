package com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository;

import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.ContaCorrenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContaCorrenteJpaRepository extends JpaRepository<ContaCorrenteEntity, UUID> {

    @Query("SELECT NEXTVAL('seq_numero_conta')")
    Long findNextNumeroConta();

    Optional<ContaCorrenteEntity> findByNumeroContaAndDigitoVerificador(Long numeroConta, int digitoVerificador);
}
