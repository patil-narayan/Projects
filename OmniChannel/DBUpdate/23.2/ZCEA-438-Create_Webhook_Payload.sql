CREATE TABLE `webhook_payload` (
   `id` bigint(20) AUTO_INCREMENT,
   `account_sid` varchar(255) DEFAULT NULL,
   `sid` varchar(255) DEFAULT NULL,
   `parent_account_sid` varchar(255) DEFAULT NULL,
   `timestamp` datetime DEFAULT NULL,
   `level` varchar(255) DEFAULT NULL,
   `payload_type` varchar(255) DEFAULT NULL,
   `payload` text,
   PRIMARY KEY (`id`)
);