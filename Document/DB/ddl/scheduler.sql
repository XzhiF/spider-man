use scheduler;

CREATE TABLE IF NOT EXISTS `scheduler`.`task` (
      `task_id` VARCHAR(30) NOT NULL,
      `task_group_id` VARCHAR(30) NOT NULL,
      `task_name` VARCHAR(45) NOT NULL,
      `task_description` VARCHAR(256) NULL,
      `status` INT NOT NULL DEFAULT 0,
      `active_flag` INT NULL DEFAULT 1,
      `last_running_time` DATETIME NULL,
      `last_running_result` INT NULL,
      `create_time` DATETIME NULL,
      `spider_group_id` VARCHAR(30) NULL,
      `job_name` VARCHAR(256) NULL,
      `schedule_class` VARCHAR(256) NULL,
      `schedule_props` VARCHAR(2000) NULL,
      PRIMARY KEY (`task_id`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `scheduler`.`task_group` (
            `task_group_id` VARCHAR(30) NOT NULL,
            `task_group_name` VARCHAR(45) NULL,
            `task_group_desc` VARCHAR(256) NULL,
            PRIMARY KEY (`task_group_id`))
    ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `scheduler`.`task_arg` (
  `task_id` VARCHAR(30) NOT NULL,
  `arg_key` VARCHAR(45) NOT NULL,
  `arg_value` VARCHAR(500) NOT NULL,
  PRIMARY KEY (`task_id`, `arg_key`))
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `scheduler`.`task_log` (
          `task_log_id` BIGINT(20) NOT NULL,
          `task_id` VARCHAR(30) NOT NULL,
          `uuid` VARCHAR(45) NOT NULL,
          `event` VARCHAR(45) NOT NULL,
          `create_time` DATETIME NOT NULL,
          `has_error` INT NOT NULL,
          `content` VARCHAR(500) NULL,
          PRIMARY KEY (`task_log_id`),
          INDEX `task_id_idx` (`task_id` ASC) )
ENGINE = InnoDB;