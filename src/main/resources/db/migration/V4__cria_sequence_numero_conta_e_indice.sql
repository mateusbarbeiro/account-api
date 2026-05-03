create index if not exists idx_conta_corrente_numero_conta
    on conta_corrente (numero_conta);

CREATE SEQUENCE seq_numero_conta
    START WITH 1
    INCREMENT BY 50   -- igual ao allocationSize da aplicação
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
