package com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.transacao;

import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.ContaCorrenteEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacao")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_movimentacao", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
public abstract class TransacaoEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_corrente_id", nullable = false)
    private ContaCorrenteEntity contaCorrente;

    @Column(name = "valor_movimentado", nullable = false)
    private BigDecimal valorMovimentado;

    @Column(name = "data_hora_transacao", nullable = false)
    private LocalDateTime dataHoraTransacao;

    @Column(name = "saldo_anterior", nullable = false)
    private BigDecimal saldoAnterior;

    @Column(name = "saldo_apos", nullable = false)
    private BigDecimal saldoApos;
}
