package com.core.zyter.bulk.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.core.zyter.bulk.entites.Attribute;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long>{

	 Optional<Attribute> findByName(String name); 
	 
	 @Query(value ="SELECT * FROM attribute \r\n"
	    		+ "WHERE RECORD_STATUS = 1"
	    		, nativeQuery = true)
	 List<Attribute> getAttribute ();
}
