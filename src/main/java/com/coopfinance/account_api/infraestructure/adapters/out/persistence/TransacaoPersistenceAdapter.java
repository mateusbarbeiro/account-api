package com.coopfinance.account_api.infraestructure.adapters.out.persistence;

import com.coopfinance.account_api.application.ports.in.results.MovimentacaoResult;
import com.coopfinance.account_api.application.ports.out.repository.TransacaoRepository;
import com.coopfinance.account_api.domain.model.transacao.Deposito;
import com.coopfinance.account_api.domain.model.transacao.Saque;
import com.coopfinance.account_api.domain.model.transacao.transferencia.TransferenciaEnviada;
import com.coopfinance.account_api.domain.model.transacao.transferencia.TransferenciaRecebida;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.entity.transacao.TransacaoEntity;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.mapper.TransacaoPersistenceMapper;
import com.coopfinance.account_api.infraestructure.adapters.out.persistence.repository.TransacaoJpaRepository;
import jakarta.persistence.DiscriminatorValue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public class TransacaoPersistenceAdapter implements TransacaoRepository {

    private final TransacaoPersistenceMapper mapper;
    private final TransacaoJpaRepository repository;

    public TransacaoPersistenceAdapter(TransacaoPersistenceMapper mapper, TransacaoJpaRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public void salvar(Deposito deposito) {
        TransacaoEntity entity = mapper.toEntity(deposito);
        repository.save(entity);
    }

    @Override
    public void salvar(Saque saque) {
        TransacaoEntity entity = mapper.toEntity(saque);
        repository.save(entity);
    }

    @Override
    public void salvar(TransferenciaRecebida transferencia) {
        TransacaoEntity entity = mapper.toEntity(transferencia);
        repository.save(entity);
    }

    @Override
    public void salvar(TransferenciaEnviada transferencia) {
        TransacaoEntity entity = mapper.toEntity(transferencia);
        repository.save(entity);
    }

    @Override
    public List<MovimentacaoResult> listarTransacoesPorConta(UUID idContaCorrente, LocalDate inicioPeriodo, LocalDate fimPeriodo) {
        List<TransacaoEntity> movimentacoes = repository.findByContaCorrenteIdAndDataHoraTransacaoBetween(idContaCorrente, inicioPeriodo.atStartOfDay(), fimPeriodo.atStartOfDay());
        return movimentacoes.stream().map(m -> new MovimentacaoResult(
                m.getDataHoraTransacao().atZone(ZoneId.systemDefault()).toOffsetDateTime(), m.getClass().getAnnotation(DiscriminatorValue.class).value(), m.getValorMovimentado())).toList();
    }
}
