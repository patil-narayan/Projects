package com.core.zyter.bulk.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.core.zyter.bulk.entites.BulkDeliveryStatus;
import com.core.zyter.entites.DeliveryStatus;

public interface BulkDeliveryStatusRepository extends JpaRepository<BulkDeliveryStatus, Long> {

	 @Query(value = "select * from delivery_status where MESSAGE_SID IN (:smsId)  AND ID IN (SELECT MAX(S.ID) " +
	            "FROM DELIVERY_STATUS S WHERE S.MESSAGE_SID=MESSAGE_SID GROUP BY S.MESSAGE_SID)", nativeQuery = true)
	    List<DeliveryStatus> getLatestStatus(@Param("smsId") List<String> smsId);
}
