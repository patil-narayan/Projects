package com.core.zyter.email.bulk.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.core.zyter.email.bulk.entities.DistributionList;

public interface BulkEmailDistributionListRepository extends JpaRepository<DistributionList, Long> {
	
	@Query(value ="SELECT * FROM distribution_list WHERE RECORD_STATUS = 1 AND name =:name AND type =:type", nativeQuery = true)
	DistributionList findByNameAndType(@Param("name") String name,@Param("type") String type);
	
	@Query(value = "select * from distribution_list where RECORD_STATUS = 1", nativeQuery = true)
	Page<DistributionList> getTemplate(Pageable pageable);

	@Query(value = "select * from distribution_list where RECORD_STATUS = 1", nativeQuery = true)
	List<DistributionList> getAllTemplate();
	
	@Query(value = "select count(*) from distribution_list where RECORD_STATUS = 1", nativeQuery = true)
	long getTotalElements();

}
