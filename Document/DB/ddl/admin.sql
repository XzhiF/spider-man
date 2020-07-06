use admin;


CREATE TABLE IF NOT EXISTS `admin`.`admin_user` (
   `username` VARCHAR(32) NOT NULL,
   `password` VARCHAR(45) NULL,
   `active_flag` INT NULL DEFAULT 1,
   PRIMARY KEY (`username`))
ENGINE = InnoDB;