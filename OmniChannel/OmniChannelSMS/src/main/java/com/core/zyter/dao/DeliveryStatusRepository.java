/*
 * @TwilioSMSRepository.java@
 * Created on 08Dec2022
 *
 * Copyright (c) 2022 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.dao;

import com.core.zyter.entites.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryStatusRepository extends JpaRepository<DeliveryStatus, Long> {

    @Query(value = "select * from delivery_status where MESSAGE_SID IN (:smsId)  AND ID IN (SELECT MAX(S.ID) " +
            "FROM delivery_status S WHERE S.MESSAGE_SID=MESSAGE_SID GROUP BY S.MESSAGE_SID)", nativeQuery = true)
    List<DeliveryStatus> getLatestStatus(@Param("smsId") List<String> smsId);
}
