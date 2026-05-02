create table transacao
(
    id                     uuid           not null,
    data_hora_transacao    timestamp(6)   not null,
    saldo_anterior         numeric(38, 2) not null,
    saldo_apos             numeric(38, 2) not null,
    valor_movimentado      numeric(38, 2) not null,
    conta_corrente_id      uuid           not null,
    ordem_transferencia_id uuid,
    tipo_movimentacao      varchar(31)    not null,
    primary key (id)
);

alter table if exists transacao
    add constraint FKmkp1fkyvmxuk7f8g00hi02lvs
    foreign key (conta_corrente_id)
    references conta_corrente;

alter table if exists transacao
    add constraint FKte6v6iks49ooj5mic7ucsi33d
    foreign key (ordem_transferencia_id)
    references ordem_transferencia;

create index idx_transacao_conta_data
    on transacao (conta_corrente_id, data_hora_transacao);