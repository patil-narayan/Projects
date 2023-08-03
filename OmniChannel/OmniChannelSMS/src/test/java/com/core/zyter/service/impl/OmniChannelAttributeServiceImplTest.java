package com.core.zyter.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import com.core.zyter.bulk.dao.AttributeRepository;
import com.core.zyter.bulk.entites.Attribute;
import com.core.zyter.bulk.service.impl.OnmiChannelAttributeServiceImpl;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.util.Constants;

public class OmniChannelAttributeServiceImplTest {

	@Mock
	AttributeRepository attributeRepository;
	@InjectMocks
	OnmiChannelAttributeServiceImpl omniChannelAttributeServiceImpl;

	Attribute attributes;
	List<Attribute> attribute;

	@BeforeEach
	void setup() {
		attributes = new Attribute();
		attributes.setId(1L);
		attributes.setName("lokesh");
		attributes.setDescription("hello,how are you?");
		attributes.setRecordstatus(true);
	}

	@Test
	public void testCreateAttribute() throws OmnichannelException {

		when(attributeRepository.save(attributes)).thenReturn(attributes);

		Attribute createdAttribute = omniChannelAttributeServiceImpl.createAttribute(attributes);

		assertNotNull(createdAttribute);
		assertEquals(1, createdAttribute.getId());
		assertEquals("lokesh", createdAttribute.getName());
		assertEquals("hello,how are you?", createdAttribute.getDescription());
		assertTrue(createdAttribute.isRecordstatus());

		verify(attributeRepository, times(1)).save(attributes);

	}

	
	@Test
	void testCreateUserWithExistingUserId() {
		
		when(attributeRepository.findByName(attributes.getName())).thenReturn(Optional.of(attributes));

		OmnichannelException exception = assertThrows(OmnichannelException.class, () -> {
			omniChannelAttributeServiceImpl.createAttribute(attributes);
		});

		assertEquals("Attribute name already exist, please enter different name", exception.getMessage());
		assertEquals(Constants.FAILURE, exception.getStatus());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

		verify(attributeRepository, times(1)).findByName(attributes.getName());
		verify(attributeRepository, never()).save(attributes);
	}
	
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		attribute = List.of(
				Attribute.builder().id(12L).name("lokeshs").description("hello,how are you?").recordstatus(true)
						.build(),
				Attribute.builder().id(13L).name("lokesh").description("hello,how are you?").recordstatus(true)
						.build());

	}

	@Test
	public void testGetAttribute() throws OmnichannelException {

		Mockito.when(attributeRepository.getAttribute()).thenReturn(attribute);
		List<Attribute> attributes = omniChannelAttributeServiceImpl.getAttributes();
		assertEquals(12, attributes.get(0).getId());
		assertEquals("lokeshs", attributes.get(0).getName());
		assertEquals("hello,how are you?", attributes.get(0).getDescription());
		assertTrue(attributes.get(0).isRecordstatus());
		
	}


	@BeforeEach
	void set_up() {
		MockitoAnnotations.openMocks(this);
		
	}

	@Test
	public void testUpdateAttribute() throws OmnichannelException {
		
		when(attributeRepository.findById(attributes.getId())).thenReturn(Optional.of(attributes));

		when(attributeRepository.save(attributes)).thenReturn(attributes);
		Attribute updateAttribute = omniChannelAttributeServiceImpl.updateAttribute(attributes);
		assertEquals("hello,how are you?", updateAttribute.getDescription());
		verify(attributeRepository, times(1)).save(attributes);
	}


	@Test
	void testUpdateAttributeWithExistingName() {
		
		when(attributeRepository.findByName(attributes.getName())).thenReturn(Optional.of(attributes));

		OmnichannelException exception = assertThrows(OmnichannelException.class, () -> {
			omniChannelAttributeServiceImpl.updateAttribute(attributes);
		});

		assertEquals("Attribute name already exist, please enter different name", exception.getMessage());
		assertEquals(Constants.FAILURE, exception.getStatus());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

		verify(attributeRepository, times(1)).findByName(attributes.getName());
		verify(attributeRepository, never()).save(attributes);
	}
	
	@Test
	public void testUpdateAttributeIdNotFound() {
	
		OmnichannelException exception = assertThrows(OmnichannelException.class, () -> {
			omniChannelAttributeServiceImpl.updateAttribute(attributes);
		}, "exception occured due to attribute ID is required");

		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
		assertEquals(Constants.FAILURE, exception.getStatus());
		
		verify(attributeRepository, times(1)).findById(attributes.getId());
		verify(attributeRepository, never()).save(attributes);
	}

	
	@BeforeEach
	void setupp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testDeleteAttribute() throws OmnichannelException {
		Attribute attribute = new Attribute();
		when(attributeRepository.findById(attribute.getId())).thenReturn(Optional.of(attribute));
		attribute.setRecordstatus(false);
		when(attributeRepository.save(attribute)).thenReturn(attribute);
		Attribute deleteAttribute = omniChannelAttributeServiceImpl.deleteAttribute(attribute.getId());
		assertFalse(deleteAttribute.isRecordstatus());
		verify(attributeRepository, times(1)).save(attribute);

	}

	
	 @Test 

	 void testDeleteAttributeMissingId() throws OmnichannelException{ 
		 Attribute attribute = new Attribute();
	 when(attributeRepository.findById(attribute.getId())).thenReturn(Optional.empty()); 

	 OmnichannelException exception = assertThrows(OmnichannelException.class, () -> {
			omniChannelAttributeServiceImpl.deleteAttribute(attribute.getId());
		});
	 assertEquals(String.format("Attribute Id: %s doest exist", attribute.getId()), exception.getMessage()); 
	 assertEquals(Constants.FAILURE, exception.getStatus()); 
	 assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
	 
	 } 
}
