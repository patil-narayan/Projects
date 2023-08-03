package com.core.zyter.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.core.zyter.entites.WebhookPayload;

@Repository
public interface WebhookRepository extends JpaRepository<WebhookPayload, Long> {

}
