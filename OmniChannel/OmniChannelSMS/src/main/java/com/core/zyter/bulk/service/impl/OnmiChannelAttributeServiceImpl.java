package com.core.zyter.bulk.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.core.zyter.bulk.dao.AttributeRepository;
import com.core.zyter.bulk.entites.Attribute;
import com.core.zyter.bulk.service.OmniChannelAttributeService;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.util.Constants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OnmiChannelAttributeServiceImpl implements OmniChannelAttributeService{
	
@Autowired
	AttributeRepository attributeRepository;
	@Override
	public Attribute createAttribute(Attribute attribute) throws OmnichannelException {
		
		if (!attributeRepository.findByName(attribute.getName()).isEmpty()) {
            throw new OmnichannelException("Attribute name already exist, please enter different name",
                    Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }
		
		Attribute createAttribute;
		try {
			createAttribute = attributeRepository.save(attribute);
		} catch (Exception e) {
			log.error("", e);
            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return createAttribute;
	}
	
	@Override
	public List<Attribute> getAttributes() throws OmnichannelException {
		
		return attributeRepository.getAttribute();
	}

	@Override
	public Attribute updateAttribute(Attribute attribute) throws OmnichannelException {
		if (!attributeRepository.findByName(attribute.getName()).isEmpty()) {
            throw new OmnichannelException("Attribute name already exist, please enter different name",
                    Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }
		
		Attribute updated;
		try {
			Attribute update= attributeRepository.findById(attribute.getId()).get();
			update.setName(attribute.getName());
			update.setDescription(attribute.getDescription());
			update.setCreatedOn(attribute.getCreatedOn());
			updated = attributeRepository.save(update);
		} catch (Exception e) {
			log.error("", e);
            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return updated;
	}
	
	@Override
    public Attribute deleteAttribute(Long Id) throws OmnichannelException {
		Optional<Attribute> attribute = attributeRepository.findById(Id);
        if (attribute.isEmpty()) {
            throw new OmnichannelException("Attribute Id: " + Id + " doest exist", Constants.FAILURE, HttpStatus.NOT_FOUND);
        }
		try {
			attribute.get().setRecordstatus(false);
			attributeRepository.save(attribute.get());
			} catch (Exception e) {
			log.error("", e);
            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return attribute.get();
    }
}
