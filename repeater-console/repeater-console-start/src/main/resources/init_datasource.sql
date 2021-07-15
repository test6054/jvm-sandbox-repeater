ALTER TABLE `replay` ADD COLUMN `batch_repeat_id` VARCHAR(32) DELETE NULL COMMIT '批量回放ID' AFTER `record_id`;


DROP TABLE IF EXISTS t_replay_batch;
CREATE TABLE t_replay_batch (
	id BIGINT ( 20 ) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
	ip VARCHAR ( 36 ) NOT NULL COMMENT '机器IP',
	app_name VARCHAR ( 255 ) NOT NULL COMMENT '应用名',
	environment VARCHAR ( 255 ) NULL COMMENT '环境信息',
	batch_repeat_id VARCHAR ( 50 ) NOT NULL COMMENT '批量回放ID',
	total INTEGER(11) NOT NULL COMMENT '回放总任务数',
	STATUS TINYINT NOT NULL COMMENT '回放状态',
	cost BIGINT ( 20 ) COMMENT '回放耗时',
	gmt_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	gmt_modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间'
) ENGINE = INNODB COMMENT = '批量回放信息' DEFAULT CHARSET = utf8 AUTO_INCREMENT = 1;

DROP TABLE IF EXISTS t_replay_batch_rel;
CREATE TABLE t_replay_batch_rel (
	id BIGINT ( 20 ) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
	batch_id BIGINT ( 20 ) NOT NULL COMMENT '批量回放主键id',
	repeat_id BIGINT ( 20 ) NOT NULL COMMENT '回放主键id',
	message VARCHAR ( 36 ) NOT NULL COMMENT '回放请求执行结果',
	data LONGTEXT COMMENT '回放请求执行返回结果'
) ENGINE = INNODB COMMENT = '批量回放关联表' DEFAULT CHARSET = utf8 AUTO_INCREMENT = 1;