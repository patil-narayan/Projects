/*
 * @TwilioReceiveSMSRepository.java@
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

import com.core.zyter.entites.ReceiveSMS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ReceiveSMSRepository extends JpaRepository<ReceiveSMS, Long> {

    @Query(value = "SELECT * FROM receive_message " +
            "WHERE CREATED_ON > ?1 AND FROM_NUMBER = ?2 AND TO_NUMBER = ?3", nativeQuery = true)
    Optional<List<ReceiveSMS>> getReceiveSMS(Date date, String fromNumber, String toNumber);
}
