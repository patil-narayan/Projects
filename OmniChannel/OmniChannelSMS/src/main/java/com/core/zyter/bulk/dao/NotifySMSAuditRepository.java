package com.core.zyter.bulk.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.core.zyter.bulk.entites.Audit;

public interface NotifySMSAuditRepository extends JpaRepository<Audit, Long> {

    Page<Audit> findByCareManager(String caremanager, Pageable pageable);
    Page<Audit> findByCareManagerAndMode(String caremanager, String mode, Pageable pageable);
    
//    @Query(value = "Select * from audit where MODE IN (:mode)", nativeQuery = true)
//	Audit getByMode(@Param("mode") String mode);
}
