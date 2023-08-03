Drop tables email_audit,email_audit_deatils,notify_sms_audit,bulk_email_template,bulk_template,distribution_list,notify_sms_audit_details,bulk_email_template,email_distribution_list;





CREATE TABLE `audit` (
`ID` int NOT NULL AUTO_INCREMENT,
`CARE_MANAGER` varchar(100) DEFAULT NULL,
`NOTE` varchar(100) DEFAULT NULL,
`CREATED_ON` timestamp NULL DEFAULT NULL,
`DISTRIBUTION_LIST` varchar(50) DEFAULT NULL,
`TEMPLATE` varchar(50) DEFAULT NULL,
`STATUS` varchar(255) DEFAULT NULL,
`REASON` varchar(255) DEFAULT NULL,
`MODE` varchar(50) DEFAULT NULL,
PRIMARY KEY (`ID`)
); 

CREATE TABLE `audit_details` (
`ID` int NOT NULL AUTO_INCREMENT,
`Audit_Id` varchar(100) DEFAULT NULL,
`SSID` varchar(50) DEFAULT NULL,
`CREATED_ON` timestamp NULL DEFAULT NULL,
PRIMARY KEY (`ID`)
);

CREATE TABLE `bulk_template` (
`ID` int NOT NULL AUTO_INCREMENT,
`NAME` varchar(100) DEFAULT NULL,
`SUBJECT` varchar(255) DEFAULT NULL,
`FILE_PATH` varchar(255) DEFAULT NULL,
`TYPE` varchar(50) DEFAULT NULL,
`RECORD_STATUS` tinyint DEFAULT '1',
`CREATED_ON` timestamp NULL DEFAULT NULL,
PRIMARY KEY (`ID`)
);

CREATE TABLE `distribution_list` (
`ID` int NOT NULL AUTO_INCREMENT,
`NAME` varchar(100) DEFAULT NULL,
`FILE_PATH` varchar(255) DEFAULT NULL,
`TYPE` varchar(50) DEFAULT NULL,
`RECORD_STATUS` tinyint DEFAULT '1',
`CREATED_ON` timestamp NULL DEFAULT NULL,
PRIMARY KEY (`ID`)
); 