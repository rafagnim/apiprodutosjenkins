CREATE TABLE `tb_produto` (
	`produto_id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`descricao` VARCHAR(255) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',
	`is_active` BIT(1) NULL DEFAULT NULL,
	`nome` VARCHAR(255) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',
	`quantidade` DECIMAL(19,2) NULL DEFAULT NULL,
	`quantidade_reservada_carrinho` DECIMAL(19,2) NULL DEFAULT NULL,
	`vlr_unitario` DECIMAL(19,2) NULL DEFAULT NULL,
	PRIMARY KEY (`produto_id`) USING BTREE
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;