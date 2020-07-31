use admin;

CREATE TABLE IF NOT EXISTS `admin`.`admin_user` (
    `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(50) NULL,
    `enabled` TINYINT(1) NULL,
    PRIMARY KEY (`username`))
ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS `admin`.`admin_user_authority` (
      `id` INT(11) NOT NULL,
      `username` VARCHAR(50) NOT NULL,
      `authority` VARCHAR(50) NOT NULL,
      PRIMARY KEY (`id`),
      UNIQUE INDEX `ix_auth_username` (`username` ASC, `authority` ASC) )
ENGINE = InnoDB;