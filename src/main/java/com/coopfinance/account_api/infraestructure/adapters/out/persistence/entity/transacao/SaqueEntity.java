package com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.transacao;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SAQUE")
public class SaqueEntity extends TransacaoEntity {
}
