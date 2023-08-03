package com.core.zyter.bulk.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.zyter.bulk.entites.AuditDetails;

public interface NotifySMSAuditDetailsRepository extends JpaRepository<AuditDetails, Long> {

}
