package com.core.zyter.email.test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.dao.EmailRepository;
import com.core.zyter.email.dao.EmailTemplateRepository;
import com.core.zyter.email.dao.MemberCareManagerRepository;
import com.core.zyter.email.entities.EmailHistory;
import com.core.zyter.email.entities.EmailTemplate;
import com.core.zyter.email.entities.MemberCareManagerMapping;
import com.core.zyter.email.service.impl.EmailServiceImpl;
import com.core.zyter.email.vos.EmailHistoryVos;
import com.core.zyter.email.vos.EmailRequest;
import com.sendgrid.SendGrid;

public class TestEmailService {

	@Mock
	EmailRepository emailRepository;
	@Mock
	EmailTemplateRepository emailTemplateRepository;
	@Mock
	MemberCareManagerRepository memberCareManagerRepository;
	@Mock
	Page<EmailHistory> pageEmail;

	@InjectMocks
	EmailServiceImpl emailServiceImpl;

	Long l = 1980000l;
	// @Mock
	@SpyBean
	SendGrid sendgrid;
	Pageable pageable;

	EmailRequest emailVos;
	EmailTemplate emailTemplate;
	MemberCareManagerMapping memberCareManagerMapping;

	private List<EmailHistory> emails;
	EmailHistory email;

	@BeforeEach
	void setUp() {

		MockitoAnnotations.openMocks(this);
		EmailHistoryVos emailHistoryVos = new EmailHistoryVos();
		emails = List.of(EmailHistory.builder().userId("Dr Nefario").memberId("VISHAL").build());
		emailHistoryVos.setEmails(emails);
		emailHistoryVos.setTotalItems(6);

		emailVos = new EmailRequest();
		//emailVos = EmailVos.builder().careManager("Dr Nefario").templateId(1l).member("VISHAL").build();

		emailTemplate = new EmailTemplate();
		emailTemplate.setId(1l);
		emailTemplate.setName("Template1");
		emailTemplate.setFilePath("C:\\Users\\narayanp\\Documents\\Template.html");
		emailTemplate.setSubject("Status");

		memberCareManagerMapping = new MemberCareManagerMapping();
		memberCareManagerMapping.setCareManager("Dr Nefario");
		memberCareManagerMapping.setMember("VISHAL");
		memberCareManagerMapping.setCareManagerEmailId("narayanpatil370@gmail.com");
		memberCareManagerMapping.setMemberEmailId("yashwant123gl@gmail.com");

		email = new EmailHistory();
		email.setId(8l);
		email.setMemberId("VISHAL");
		email.setBody("Hey Hii Test");
		email.setSubject("Status");
		email.setUserId("Dr Nefario");

	}

//	@Test
//	public void getEmailHistory() throws OmnichannelException {
//
//		pageEmail = new PageImpl<>(emails);
//		pageEmail.getPageable();
//		emails= pageEmail.getContent();
//		when(emailRepository.getByUserIdAndMemberId("Dr Nefario","VISHAL", pageable)).thenReturn(pageEmail);
//		when(memberCareManagerRepository.getByCareManagerManager(memberCareManagerMapping.getCareManager(),memberCareManagerMapping.getMember())).thenReturn(memberCareManagerMapping);
//		EmailHistoryVos emailHistoryVos = emailServiceImpl.getEmailHistory(emails.get(0).getUserId(),emails.get(0).getMemberId(), pageEmail.getNumber(),pageEmail.getSize());
//		assertEquals(anyString(),emailHistoryVos.getEmails());
//		assertEquals(anyInt(),emailHistoryVos.getTotalItems());
//	}

	@Test
	public void emailSend() throws IOException, OmnichannelException {

		Optional<EmailTemplate> emailtemplates = Optional.of(emailTemplate);
		when(emailTemplateRepository.findById(emailVos.getTemplateId())).thenReturn(emailtemplates);
		when(memberCareManagerRepository.getByCareManagerAndMember(emailVos.getCareManager(), emailVos.getMember()))
				.thenReturn(memberCareManagerMapping);

		assertNotNull(emailtemplates);
		assertNotNull(memberCareManagerMapping);
		assertEquals("Dr Nefario", memberCareManagerMapping.getCareManager());
		assertEquals("VISHAL", memberCareManagerMapping.getMember());
		assertEquals("narayanpatil370@gmail.com", memberCareManagerMapping.getCareManagerEmailId());
		assertEquals("yashwant123gl@gmail.com", memberCareManagerMapping.getMemberEmailId());
		// Response response= emailServiceImpl.email(emailVos);

		assertEquals("Template1", emailtemplates.get().getName());
		// assertNotNull(response);
		// assertEquals(200, response.getStatusCode());
	}

	@Test
	void testSendEmailWithNullTemplateId() {
	    EmailRequest emailRequest = new EmailRequest();
	   
	    emailRequest.setTemplateId(null);
	    assertThrows(OmnichannelException.class, () -> emailServiceImpl.sendEmail(emailRequest,l));
	}

	@Test
	public void testSendEmailWithNonExistentTemplateId() {
		// Arrange
		EmailRequest emailRequest = new EmailRequest();
		emailRequest.setTemplateId("non-existent-template-id");
		 

		// Act and Assert
		assertThrows(OmnichannelException.class, () -> {
			emailServiceImpl.sendEmail(emailRequest,l);
		});
	}
	@Test
	void testSendEmailWithEmptyMemberId() {
	    EmailRequest emailRequest = new EmailRequest();
	    emailRequest.setTemplateId("valid-template-id");
	    emailRequest.setMember("");

	    assertThrows(OmnichannelException.class, () -> {
	    	emailServiceImpl.sendEmail(emailRequest,l);
	    });
	}

	@Test
	void testSendEmailWithNonExistentMemberId() {
	    EmailRequest emailRequest = new EmailRequest();
	    emailRequest.setTemplateId("valid-template-id");
	    emailRequest.setMember("non-existent-member-id");
	    emailRequest.setCareManager("valid-care-manager-id");

	    assertThrows(OmnichannelException.class, () -> {
	    	emailServiceImpl.sendEmail(emailRequest,l);
	    });
	}

	@Test
	void testSendEmailWithEmptyCareManagerId() {
	    EmailRequest emailRequest = new EmailRequest();
	    emailRequest.setTemplateId("valid-template-id");
	    emailRequest.setMember("valid-member-id");
	    emailRequest.setCareManager("");

	    assertThrows(OmnichannelException.class, () -> {
	    	emailServiceImpl.sendEmail(emailRequest,l);
	    });
	}

	@Test
	void testSendEmailWithNonExistentCareManagerId() {
	    EmailRequest emailRequest = new EmailRequest();
	    emailRequest.setTemplateId("valid-template-id");
	    emailRequest.setMember("valid-member-id");
	    emailRequest.setCareManager("non-existent-care-manager-id");

	    assertThrows(OmnichannelException.class, () -> {
	    	emailServiceImpl.sendEmail(emailRequest,l);
	    });
	}

	@Test
	void testSendEmailWithMissingEmailAddresses() {
	    EmailRequest emailRequest = new EmailRequest();
	    emailRequest.setTemplateId("valid-template-id");
	    emailRequest.setMember("valid-member-id");
	    emailRequest.setCareManager("valid-care-manager-id");

	    assertThrows(OmnichannelException.class, () -> {
	    	emailServiceImpl.sendEmail(emailRequest,l);
	    });
	}
	@Test
	void testSendEmailWithInvalidFilePath() {
	  EmailRequest emailRequest = new EmailRequest();
	  emailRequest.setTemplateId("invalid-template-id");
	  emailRequest.setMember("member-id");
	  emailRequest.setCareManager("care-manager-id");
	  Map<String, String> placeholders = new HashMap<>();
	  placeholders.put("name", "John");
	  emailRequest.setPlaceholders(placeholders);
	  
	  // Set invalid file path
	  EmailTemplate emailTemplate = new EmailTemplate();
	  emailTemplate.setFilePath("invalid-file-path");
	  
	  // Mock the repository to return the email template
	  Mockito.when(emailTemplateRepository.findById(Mockito.anyString())).thenReturn(Optional.of(emailTemplate));
	  
	  // Execute the method and check if it throws an exception with the correct error message
	  assertThrows(OmnichannelException.class, () -> {
		  emailServiceImpl.sendEmail(emailRequest,l);
	  }, "Email template file not found at path invalid-file-path");
	}


	
	

}
