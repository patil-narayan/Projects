package com.core.zyter.email.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.zyter.email.entities.EmailEventDetails;

public interface EmailEventDetailsRepository extends JpaRepository<EmailEventDetails, Long>{
}
