package com.core.zyter.sms.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.zyter.sms.entities.SMSDeliveryStatus;

public interface SMSDeliveryStatusRepository extends JpaRepository<SMSDeliveryStatus, Long> {

}
