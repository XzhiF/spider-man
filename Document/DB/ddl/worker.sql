use worker;


CREATE TABLE IF NOT EXISTS `worker`.`spider` (
  `spider_id` VARCHAR(30) NOT NULL,
  `spider_name` VARCHAR(45) NOT NULL,
  `spider_desc` VARCHAR(500) NULL,
  `spider_group_id` VARCHAR(30) NULL,
  `status` INT NOT NULL DEFAULT 0,
  `active_flag` INT NOT NULL DEFAULT 1,
  `create_time` DATETIME NULL,
  PRIMARY KEY (`spider_id`))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `worker`.`spider_group` (
  `spider_group_id` VARCHAR(30) NOT NULL,
  `spider_group_name` VARCHAR(50) NULL,
  `spider_group_desc` VARCHAR(45) NULL,
  PRIMARY KEY (`spider_group_id`))
ENGINE = InnoDB;