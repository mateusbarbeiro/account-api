package com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.transacao;

import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.OrdemTransferenciaEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public abstract class TransferenciaEntity extends TransacaoEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_transferencia_id")
    private OrdemTransferenciaEntity ordemTransferencia;
}
