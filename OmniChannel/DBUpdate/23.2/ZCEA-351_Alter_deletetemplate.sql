alter table attribute
add column `RECORD_STATUS` tinyint(1) DEFAULT '1';

alter table distribution_list
add column `RECORD_STATUS` tinyint(1) DEFAULT '1';

alter table bulk_template
add column `RECORD_STATUS` tinyint(1) DEFAULT '1';