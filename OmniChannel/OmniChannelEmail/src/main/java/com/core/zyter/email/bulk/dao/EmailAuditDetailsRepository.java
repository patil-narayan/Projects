package com.core.zyter.email.bulk.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.zyter.email.bulk.entities.AuditDetails;

public interface EmailAuditDetailsRepository extends JpaRepository<AuditDetails, Long> {

}
