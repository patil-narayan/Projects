package com.core.zyter.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.zyter.entites.TemplateType;


public interface TemplateTypeRepository extends JpaRepository<TemplateType, Long>{

	
	Optional<TemplateType> getByNameAndTypeAndMode(String name, String type,String mode );
}
