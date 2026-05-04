create table conta_corrente
(
    id                 uuid           not null,
    documento          varchar(14)    not null,
    numero_conta       bigint         not null,
    digito_verificador int            not null,
    saldo              numeric(38, 2) not null,
    versao             bigint,
    primary key (id)
);