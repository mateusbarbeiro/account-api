create table conta_corrente
(
    id                 uuid           not null,
    documento          varchar(14)    not null,
    numero_conta       varchar(20)    not null,
    digito_verificador varchar(2)     not null,
    saldo              numeric(38, 2) not null,
    versao             bigint,
    primary key (id)
);