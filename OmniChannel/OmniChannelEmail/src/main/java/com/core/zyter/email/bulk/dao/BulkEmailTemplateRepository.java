package com.core.zyter.email.bulk.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.core.zyter.email.bulk.entities.BulkTemplate;

public interface BulkEmailTemplateRepository extends JpaRepository<BulkTemplate, Long> {
	
	@Query(value ="SELECT * FROM bulk_template WHERE RECORD_STATUS = 1 AND name =:name AND type =:type", nativeQuery = true)
	Optional<BulkTemplate> getBulkTemplateByNameAndType(@Param("name") String name,@Param("type") String type);

	@Query(value = "select * from bulk_template where RECORD_STATUS = 1", nativeQuery = true)
	Page<BulkTemplate> getTemplate(Pageable pageable);

	@Query(value = "select * from bulk_template WHERE RECORD_STATUS = 1", nativeQuery = true)
	List<BulkTemplate> getAllTemplate();
	
	@Query(value = "select count(*) from bulk_template  where RECORD_STATUS = 1", nativeQuery = true)
	long getTotalElements();

}
