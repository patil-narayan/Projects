package com.core.zyter.bulk.service;

import java.util.List;
import java.util.Optional;

import com.core.zyter.bulk.entites.Attribute;
import com.core.zyter.exceptions.OmnichannelException;
import com.fasterxml.jackson.databind.annotation.JsonAppend.Attr;

public interface OmniChannelAttributeService {

	Attribute createAttribute(Attribute attribute) throws OmnichannelException;
	List<Attribute> getAttributes() throws OmnichannelException;
	Attribute updateAttribute(Attribute attribute) throws OmnichannelException;
	Attribute deleteAttribute(Long Id) throws OmnichannelException;
}
