-- Insere contas correntes para transferencia
INSERT INTO public.conta_corrente
(id, documento, numero_conta, digito_verificador, saldo, versao)
VALUES('019df186-853c-72ca-aba2-3c6badab591e'::uuid, '98765432111', 2, 6, 999.99, 4),
      ('019df186-5c03-7652-9ef6-b14da1d93e40'::uuid, '12345678977', 1, 8, 999.99, 4);

INSERT INTO public.ordem_transferencia (id, data_hora_solicitacao, status, valor, conta_destino_id, conta_origem_id)
VALUES ('019df188-2c46-76f1-8cbc-0d3a1db5c494'::uuid, '2026-05-04 02:48:46.534', 'CONCLUIDA', 111.11, '019df186-853c-72ca-aba2-3c6badab591e'::uuid, '019df186-5c03-7652-9ef6-b14da1d93e40'::uuid),
       ('019df188-8137-74be-9f56-58d7343ea2a0'::uuid, '2026-05-04 02:49:08.279', 'CONCLUIDA', 111.11, '019df186-5c03-7652-9ef6-b14da1d93e40'::uuid, '019df186-853c-72ca-aba2-3c6badab591e'::uuid);

INSERT INTO public.transacao
(id, data_hora_transacao, saldo_anterior, saldo_apos, valor_movimentado, conta_corrente_id, ordem_transferencia_id, tipo_movimentacao)
VALUES('019df186-bf43-7620-be94-cd9a5828ed7a'::uuid, '2026-04-29 02:47:13.091', 0.00, 1000.00, 1000.00, '019df186-5c03-7652-9ef6-b14da1d93e40'::uuid, NULL, 'DEPOSITO'),
      ('019df186-ffe0-762f-9726-05c3fbdc9860'::uuid, '2026-04-30 02:47:29.632', 0.00, 1000.00, 1000.00, '019df186-853c-72ca-aba2-3c6badab591e'::uuid, NULL, 'DEPOSITO'),
      ('019df187-5146-74b7-9ba7-429f98e1d673'::uuid, '2026-05-01 02:47:50.470', 1000.00, 999.99, 0.01, '019df186-5c03-7652-9ef6-b14da1d93e40'::uuid, NULL, 'SAQUE'),
      ('019df187-89a2-7164-87a7-b0cbb5e83cbe'::uuid, '2026-05-03 02:48:04.898', 1000.00, 999.99, 0.01, '019df186-853c-72ca-aba2-3c6badab591e'::uuid, NULL, 'SAQUE'),
      ('019df188-2c49-7524-add9-a8ecf062f667'::uuid, '2026-05-04 02:48:46.537', 999.99, 888.88, -111.11, '019df186-5c03-7652-9ef6-b14da1d93e40'::uuid, '019df188-2c46-76f1-8cbc-0d3a1db5c494'::uuid, 'TRANSFERENCIA_ENVIADA'),
      ('019df188-2c49-76c6-a564-c5733e976899'::uuid, '2026-05-04 02:48:46.538', 999.99, 1111.10, 111.11, '019df186-853c-72ca-aba2-3c6badab591e'::uuid, '019df188-2c46-76f1-8cbc-0d3a1db5c494'::uuid, 'TRANSFERENCIA_RECEBIDA'),
      ('019df188-8139-7668-b506-971013f82179'::uuid, '2026-05-04 02:49:08.281', 1111.10, 999.99, -111.11, '019df186-853c-72ca-aba2-3c6badab591e'::uuid, '019df188-8137-74be-9f56-58d7343ea2a0'::uuid, 'TRANSFERENCIA_ENVIADA'),
      ('019df188-8139-774e-b42c-cc4db972316e'::uuid, '2026-05-04 02:49:08.281', 888.88, 999.99, 111.11, '019df186-5c03-7652-9ef6-b14da1d93e40'::uuid, '019df188-8137-74be-9f56-58d7343ea2a0'::uuid, 'TRANSFERENCIA_RECEBIDA');