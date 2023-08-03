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

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.core.zyter.bulk.entites.BulkTemplate;

@Repository
public interface BulkTemplateRepository extends JpaRepository<BulkTemplate, Long> {

	@Query(value ="SELECT * FROM bulk_template WHERE RECORD_STATUS = 1 AND name =:name AND type =:type", nativeQuery = true)
    Optional<BulkTemplate> getBulkTemplateByNameAndType(@Param("name") String name,@Param("type") String type);

    @Query(value ="SELECT * FROM bulk_template WHERE RECORD_STATUS = 1", nativeQuery = true)
    	 Page<BulkTemplate>  getTemplate (Pageable pageable);
    
    @Query(value ="SELECT * FROM bulk_template WHERE RECORD_STATUS = 1 AND TYPE =:type", nativeQuery = true)
    	 Page<BulkTemplate>  getTemplateAndType (Pageable pageable,@Param("type") String type);
    
    @Query(value ="SELECT * FROM bulk_template WHERE RECORD_STATUS = 1", nativeQuery = true)
    	List<BulkTemplate> getAllTemplate ();
}