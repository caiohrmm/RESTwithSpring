CREATE TABLE IF NOT EXISTS `person`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `first_name` varchar(100)  NOT NULL,
    `last_name`  varchar(100)  NOT NULL,
    `gender`     varchar(25)   NOT NULL,
    `address`    varchar(120) NOT NULL,

    PRIMARY KEY (`id`)
)


