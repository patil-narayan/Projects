/*
 * @TemplateFileOperationRepository.java@
 * Created on 31Jan2023
 *
 * Copyright (c) 2023 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.bulk.dao;

import com.core.zyter.bulk.entites.BulkTemplateAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkTemplateAuditRepository extends JpaRepository<BulkTemplateAudit, String> {
}