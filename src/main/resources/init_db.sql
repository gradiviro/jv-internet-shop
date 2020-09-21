CREATE SCHEMA `online_store` DEFAULT CHARACTER SET utf8 ;

CREATE TABLE `internet-shop`.`products` (
  `product_id` BIGINT(11) NOT NULL AUTO_INCREMENT,
  `productName` VARCHAR(225) NOT NULL,
  `price` DOUBLE NOT NULL,
  `deleted` TINYINT NULL,
  PRIMARY KEY (`product_id`));