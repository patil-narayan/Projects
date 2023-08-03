package com.core.zyter.email.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.core.zyter.email.entities.EmailTemplate;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long>{

	
	@Query(value ="select * from email_template WHERE ACTIVE =1" , nativeQuery = true)
	List<EmailTemplate> getTemplates();
	 Optional<EmailTemplate> findById(String templateId);
}
