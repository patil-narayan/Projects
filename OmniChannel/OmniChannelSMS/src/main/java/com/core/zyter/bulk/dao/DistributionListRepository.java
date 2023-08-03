/*
 * @DistributionListRepository.java@
 * Created on 27Jan2023
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.core.zyter.bulk.entites.DistributionList;


public interface DistributionListRepository extends JpaRepository<DistributionList, Long> {
	
	@Query(value ="SELECT * FROM distribution_list WHERE RECORD_STATUS = 1 AND name =:name AND type =:type", nativeQuery = true)
	 DistributionList findByNameAndType(@Param("name") String name,@Param("type") String type);

	@Query(value ="SELECT * FROM distribution_list WHERE RECORD_STATUS = 1", nativeQuery = true)
    	 Page<DistributionList>  getTemplate (Pageable pageable);
	
	@Query(value ="SELECT * FROM distribution_list WHERE RECORD_STATUS = 1 AND TYPE =:type", nativeQuery = true)
    	 Page<DistributionList>  getTemplateAndType (Pageable pageable,@Param("type") String type);
    
    @Query(value ="SELECT * FROM distribution_list WHERE RECORD_STATUS = 1", nativeQuery = true)
    	List<DistributionList> getAllTemplate ();
}
