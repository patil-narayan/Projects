package com.core.zyter.email.bulk.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.core.zyter.email.bulk.entities.Audit;

public interface EmailAuditRepository extends JpaRepository<Audit, Long>{
	
	Page<Audit> findByCareManager(String caremanager, Pageable pageable);
	
	Page<Audit> findByCareManagerAndMode(String caremanager,String mode, Pageable pageable);
	
	@Query(value = "SELECT count(1) AS count, MODE FROM audit where CARE_MANAGER IN (:careManager) GROUP BY MODE",nativeQuery = true)
	List<Object[]> getByMode(@Param(value = "careManager") String careManager);
	

}
