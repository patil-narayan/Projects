package com.core.zyter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.dao.EmailTemplateRepository;
import com.core.zyter.email.entities.EmailTemplate;
import com.core.zyter.email.service.impl.EmailTemplateServiceImpl;
import com.core.zyter.email.vos.PlaceHolder;
import com.core.zyter.email.vos.TemplateResponse;

public class OmniChannelEmailTemplateTest {

	@Mock
	EmailTemplateRepository emailTemplateRepository;

	@InjectMocks
	EmailTemplateServiceImpl emailTemplateServiceImpl;

	List<EmailTemplate> emailTemplate;
	EmailTemplate emailTemplates;

	@BeforeEach
	void setUpforget() {
		MockitoAnnotations.openMocks(this);
		  emailTemplate =
		  List.of(EmailTemplate.builder().id(1L).name("template1").subject("gretting").
		  mode("sms") .active(true).createdBy("yashwanth").build());
	}

	@Test
	public void gettemplateTest() throws OmnichannelException {
		Mockito.when(emailTemplateRepository.getTemplates()).thenReturn(emailTemplate);
		List<EmailTemplate> email = emailTemplateServiceImpl.getTemplates();
		assertNotNull(emailTemplate);
		assertEquals("sms", email.get(0).getMode());
		assertEquals(true, email.get(0).isActive());
		assertEquals("gretting", email.get(0).getSubject());
	}

	@BeforeEach
	void setUpforgetbyid() {
		MockitoAnnotations.openMocks(this);
		emailTemplates = new EmailTemplate();
		emailTemplates.setId(2L);
		emailTemplates.setName("template1");
		emailTemplates.setSubject("greeting");
		emailTemplates.setMode("SMS");
		emailTemplates.setFilePath("../OmniChannelEmail/src/test/resources/SampleTemplates/Template01");
		emailTemplates.setActive(true);
		emailTemplates.setCreatedBy("yashwanth");
	}

	@Test
	public void testTemplategetbyid() throws OmnichannelException, IOException {
		PlaceHolder placeHolder =new PlaceHolder();
		Optional<EmailTemplate> emailsTemplate = Optional.of(emailTemplates);
		Mockito.when(emailTemplateRepository.findById(emailTemplates.getId())).thenReturn(emailsTemplate);
		TemplateResponse templateResponse = emailTemplateServiceImpl.getTemplates(2L,placeHolder);
		assertNotNull(templateResponse.getEmailTemplate().get().getId());
		assertEquals(2, templateResponse.getEmailTemplate().get().getId());
		assertEquals("greeting", templateResponse.getEmailTemplate().get().getSubject());
		assertEquals("hi", templateResponse.getContent());
	}
}
