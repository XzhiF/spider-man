use worker;

CREATE TABLE IF NOT EXISTS `worker`.`spider_cnf` (
  `spider_cnf_id` VARCHAR(30) NOT NULL,
  `spider_group_id` VARCHAR(30) NOT NULL,
  `spider_server_id` VARCHAR(30) NOT NULL,
  `spider_store_id` VARCHAR(30) NOT NULL,
  `spider_name` VARCHAR(45) NOT NULL,
  `spider_type` INT NOT NULL DEFAULT 1 COMMENT '1 web; 2 db; 3 file;',
  `spider_params` VARCHAR(2000) NULL COMMENT 'Json',
  `spider_desc` VARCHAR(500) NULL,
  `status` INT NOT NULL DEFAULT 0,
  `active_flag` INT NOT NULL DEFAULT 1,
  `create_time` DATETIME NULL,
  `processor` VARCHAR(255) NULL,
  `can_close_count` INT NULL,
  `worker_threads` INT NULL,
  `poll_timeout_seconds` INT NULL,
  `max_poll_timeout_count` INT NULL,
  PRIMARY KEY (`spider_cnf_id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `worker`.`spider_group` (
  `spider_group_id` VARCHAR(30) NOT NULL,
  `spider_group_name` VARCHAR(50) NULL,
  `spider_group_desc` VARCHAR(45) NULL,
  `create_time` DATETIME NULL,
  PRIMARY KEY (`spider_group_id`))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `worker`.`spider_server` (
  `spider_server_id` VARCHAR(30) NOT NULL,
  `host` VARCHAR(255) NOT NULL,
  `port` INT NOT NULL,
  `zone` VARCHAR(200) NULL,
  `create_time` DATETIME NULL,
  PRIMARY KEY (`spider_server_id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `worker`.`spider_store` (
  `spider_store_id` VARCHAR(30) NOT NULL,
  `host` VARCHAR(255) NULL,
  `port` INT NULL,
  `table_name` VARCHAR(60) NOT NULL,
  `type` INT NOT NULL DEFAULT 1 COMMENT '1 mongo',
  `url` VARCHAR(255) NULL,
  `username` VARCHAR(45) NULL,
  `password` VARCHAR(45) NULL,
  `database` varchar(128)  NULL,
  PRIMARY KEY (`spider_store_id`))
ENGINE = InnoDB;