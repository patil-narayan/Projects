ALTER TABLE notify_sms_audit MODIFY NOTE varchar(100);
ALTER TABLE notify_sms_audit DROP COLUMN message;