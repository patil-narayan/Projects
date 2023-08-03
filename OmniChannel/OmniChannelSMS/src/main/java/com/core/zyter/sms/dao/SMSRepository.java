package com.core.zyter.sms.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.zyter.sms.entities.SMSHistory;

public interface SMSRepository extends JpaRepository<SMSHistory, Long> {

}
