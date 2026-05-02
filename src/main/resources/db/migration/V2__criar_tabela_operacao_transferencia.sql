create table ordem_transferencia
(
    id                    uuid           not null,
    data_hora_solicitacao timestamp(6)   not null,
    status                varchar(255)   not null check (status in ('PENDENTE', 'CONCLUIDA', 'FALHOU')),
    valor                 numeric(38, 2) not null,
    conta_destino_id      uuid           not null,
    conta_origem_id       uuid           not null,
    primary key (id)
);

alter table if exists ordem_transferencia
    add constraint FKs67mh9vkhyv4jcmn3a7hkt85i
    foreign key (conta_destino_id)
    references conta_corrente;

alter table if exists ordem_transferencia
    add constraint FK3ef2yadp7ucg6yay57x7yhmhw
    foreign key (conta_origem_id)
    references conta_corrente;