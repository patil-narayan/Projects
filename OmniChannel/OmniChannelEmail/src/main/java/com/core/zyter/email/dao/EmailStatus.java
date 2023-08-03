package com.core.zyter.email.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.zyter.email.entities.EmailDeliveryStatus;

public interface EmailStatus  extends JpaRepository<EmailDeliveryStatus, Long>{

}
