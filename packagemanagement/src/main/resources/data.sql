-- Carga inicial. Todos os usuários usam a senha: Senha@123

INSERT INTO moradores (id, nome, nome_normalizado, email, senha_hash, telefone, apartamento) VALUES
	(1, 'Ana Souza',       'ana souza',       'ana.souza@email.com',       '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', '11988880001', '101'),
	(2, 'Bruno Lima',      'bruno lima',      'bruno.lima@email.com',      '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', '11988880002', '102'),
	(3, 'Carla Mendes',    'carla mendes',    'carla.mendes@email.com',    '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', '11988880003', '201'),
	(4, 'Diego Rocha',     'diego rocha',     'diego.rocha@email.com',     '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', '11988880004', '202'),
	(5, 'Elisa Ferreira',  'elisa ferreira',  'elisa.ferreira@email.com',  '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', '11988880005', '301'),
	(6, 'Fábio Gonçalves', 'fabio goncalves', 'fabio.goncalves@email.com', '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', '11988880006', '302');

INSERT INTO funcionarios (id, nome, email, senha_hash, perfil) VALUES
	(1, 'Carlos Pereira', 'carlos.pereira@portaria.com', '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', 'PORTEIRO'),
	(2, 'Joana Alves',    'joana.alves@portaria.com',    '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', 'PORTEIRO'),
	(3, 'Marcos Dias',    'marcos.dias@portaria.com',    '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', 'PORTEIRO'),
	(4, 'Patrícia Nunes', 'patricia.nunes@portaria.com', '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', 'PORTEIRO'),
	(5, 'Roberto Silva',  'roberto.silva@portaria.com',  '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', 'PORTEIRO'),
	(6, 'Sandra Costa',   'sandra.costa@portaria.com',   '$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW', 'PORTEIRO');

INSERT INTO encomendas (id, morador_id, nome_destinatario, apartamento, descricao, status, data_recebimento,
		porteiro_recebimento_id, data_notificacao, data_confirmacao, data_retirada, porteiro_retirada_id) VALUES
	(1, 1, 'Ana Souza',       '101', 'Caixa pequena - Mercado Livre', 'RECEBIDA',   TIMESTAMP '2026-09-28 09:15:00', 1, NULL,                            NULL,                            NULL,                            NULL),
	(2, 2, 'Bruno Lima',      '102', 'Envelope - Correios',           'NOTIFICADA', TIMESTAMP '2026-09-28 10:30:00', 2, TIMESTAMP '2026-09-28 10:31:00', NULL,                            NULL,                            NULL),
	(3, 3, 'Carla Mendes',    '201', 'Caixa média - Amazon',          'NOTIFICADA', TIMESTAMP '2026-09-29 14:00:00', 3, TIMESTAMP '2026-09-29 14:01:00', NULL,                            NULL,                            NULL),
	(4, 4, 'Diego Rocha',     '202', 'Pacote - Shopee',               'CONFIRMADA', TIMESTAMP '2026-09-29 16:20:00', 4, TIMESTAMP '2026-09-29 16:21:00', TIMESTAMP '2026-09-29 18:00:00', NULL,                            NULL),
	(5, 5, 'Elisa Ferreira',  '301', 'Caixa grande - Magazine Luiza', 'RETIRADA',   TIMESTAMP '2026-09-30 08:45:00', 5, TIMESTAMP '2026-09-30 08:46:00', TIMESTAMP '2026-09-30 09:30:00', TIMESTAMP '2026-09-30 19:10:00', 6),
	(6, 6, 'Fábio Gonçalves', '302', 'Envelope - documentos',         'RETIRADA',   TIMESTAMP '2026-10-01 11:00:00', 6, TIMESTAMP '2026-10-01 11:01:00', TIMESTAMP '2026-10-01 12:15:00', TIMESTAMP '2026-10-01 18:40:00', 1);

INSERT INTO notificacoes (id, encomenda_id, morador_id, destinatario, assunto, mensagem, status, data_criacao,
		data_envio, data_confirmacao) VALUES
	(1, 1, 1, 'ana.souza@email.com', 'Chegou uma encomenda para você na portaria',
		'Olá, Ana Souza! Chegou uma encomenda para você na portaria. Descrição: Caixa pequena - Mercado Livre. Recebida em: 28/09/2026 09:15.',
		'FALHA', TIMESTAMP '2026-09-28 09:15:00', NULL, NULL),
	(2, 2, 2, 'bruno.lima@email.com', 'Chegou uma encomenda para você na portaria',
		'Olá, Bruno Lima! Chegou uma encomenda para você na portaria. Descrição: Envelope - Correios. Recebida em: 28/09/2026 10:30.',
		'ENVIADA', TIMESTAMP '2026-09-28 10:30:00', TIMESTAMP '2026-09-28 10:31:00', NULL),
	(3, 3, 3, 'carla.mendes@email.com', 'Chegou uma encomenda para você na portaria',
		'Olá, Carla Mendes! Chegou uma encomenda para você na portaria. Descrição: Caixa média - Amazon. Recebida em: 29/09/2026 14:00.',
		'ENVIADA', TIMESTAMP '2026-09-29 14:00:00', TIMESTAMP '2026-09-29 14:01:00', NULL),
	(4, 4, 4, 'diego.rocha@email.com', 'Chegou uma encomenda para você na portaria',
		'Olá, Diego Rocha! Chegou uma encomenda para você na portaria. Descrição: Pacote - Shopee. Recebida em: 29/09/2026 16:20.',
		'CONFIRMADA', TIMESTAMP '2026-09-29 16:20:00', TIMESTAMP '2026-09-29 16:21:00', TIMESTAMP '2026-09-29 18:00:00'),
	(5, 5, 5, 'elisa.ferreira@email.com', 'Chegou uma encomenda para você na portaria',
		'Olá, Elisa Ferreira! Chegou uma encomenda para você na portaria. Descrição: Caixa grande - Magazine Luiza. Recebida em: 30/09/2026 08:45.',
		'CONFIRMADA', TIMESTAMP '2026-09-30 08:45:00', TIMESTAMP '2026-09-30 08:46:00', TIMESTAMP '2026-09-30 09:30:00'),
	(6, 6, 6, 'fabio.goncalves@email.com', 'Chegou uma encomenda para você na portaria',
		'Olá, Fábio Gonçalves! Chegou uma encomenda para você na portaria. Descrição: Envelope - documentos. Recebida em: 01/10/2026 11:00.',
		'CONFIRMADA', TIMESTAMP '2026-10-01 11:00:00', TIMESTAMP '2026-10-01 11:01:00', TIMESTAMP '2026-10-01 12:15:00');

-- Os IDs acima foram informados manualmente; os próximos devem continuar a partir de 7.
ALTER TABLE moradores ALTER COLUMN id RESTART WITH 7;
ALTER TABLE funcionarios ALTER COLUMN id RESTART WITH 7;
ALTER TABLE encomendas ALTER COLUMN id RESTART WITH 7;
ALTER TABLE notificacoes ALTER COLUMN id RESTART WITH 7;
