package com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "conta_corrente")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaCorrenteEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "numero_conta", nullable = false, length = 20)
    private String numeroConta;

    @Column(name = "digito_verificador", nullable = false, length = 2)
    private String digitoVerificador;

    @Column(name = "documento", nullable = false, length = 14)
    private String documento;

    @Column(name = "saldo", nullable = false)
    private BigDecimal saldo;

    @Version
    @Column(name = "versao")
    private Long versao;
}
