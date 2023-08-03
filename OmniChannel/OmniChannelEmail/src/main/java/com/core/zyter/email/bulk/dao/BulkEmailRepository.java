package com.core.zyter.email.bulk.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.zyter.email.bulk.entities.BulkEmail;

public interface BulkEmailRepository extends JpaRepository<BulkEmail, Long>{

}
