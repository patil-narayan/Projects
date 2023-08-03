alter table template_type
add column  `MODE` varchar(50) DEFAULT NULL;

alter table template_type
add column `CREATED_ON` timestamp NULL DEFAULT NULL;