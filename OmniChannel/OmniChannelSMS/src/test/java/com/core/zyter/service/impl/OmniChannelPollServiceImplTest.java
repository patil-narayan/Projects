package com.core.zyter.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.core.zyter.bulk.vos.TemplateTypeResponse;
import com.core.zyter.dao.MemberCareManagerRepository;
import com.core.zyter.dao.SMSConversationRepository;
import com.core.zyter.dao.TemplateTypeRepository;
import com.core.zyter.entites.MemberCareManagerMapping;
import com.core.zyter.entites.SmsConversation;
import com.core.zyter.entites.TemplateType;
import com.core.zyter.exceptions.OmnichannelException;

public class OmniChannelPollServiceImplTest {

	@Mock
	TemplateTypeRepository templateTypeRepository;

    @Mock
    private MemberCareManagerRepository memberCareManagerRepository;
    
    @Mock
    private SMSConversationRepository smsConversationRepository;

	@InjectMocks 
	OmniChannelPollServiceImpl omniChannelPollServiceImpl;
	
	TemplateType templateType ;
	@BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        
 }
 
	@Test
	public void testTemplateFormat() throws OmnichannelException, IOException {
		TemplateType templateType = new TemplateType();
		templateType.setId(1L);
		templateType.setName("DISTRIBUTION_LIST");
		templateType.setType("PDF_FORMAT");
		templateType.setMode("SMS");
		templateType.setFilepath("../OmniChannelSMS/src/test/resources/DistributionList Template/user.csv");
		when(templateTypeRepository.getByNameAndTypeAndMode(templateType.getName(), templateType.getType(),
				templateType.getMode())).thenReturn(Optional.of(templateType));

		when(templateTypeRepository.save(templateType)).thenReturn(templateType);
		TemplateTypeResponse templateTypeResponse = omniChannelPollServiceImpl.templateFormat(templateType.getName(), templateType.getType(),
				templateType.getMode());

		assertEquals("DISTRIBUTION_LIST", templateTypeResponse.getTemplateType().get().getName());
		assertEquals("First Name,Last Name,DOB,Phonenumber\r\n"
				+ "sharadhi,lokesh,05/02/2000,9.17349E+11\r\n"
				+ "sharadhia,lokesha,05/02/2000,9.17349E+11", templateTypeResponse.getContent());
		
	}
	

	    @Test
	    void testGetReceiveMessages() throws Exception {
	        // setup test data
	        String dateString = "2022-01-01 00:00:00";
	        String careManager = "John";
	        String member = "Doe";
	        Long clientOffset = 0L;
	        MemberCareManagerMapping mapping = new MemberCareManagerMapping();
	        mapping.setCareManager(careManager);
	        mapping.setMember(member);
	        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
	        List<SmsConversation> smsList = new ArrayList<>();
	        smsList.add(new SmsConversation());
	        
	        // mock dependencies
	        when(memberCareManagerRepository.getByCareManagerAndMember(careManager, member)).thenReturn(mapping);
	        when(smsConversationRepository.getReceiveSMS(date, careManager, member)).thenReturn(Optional.of(smsList));
	        
	        // execute method under test
	        List<SmsConversation> result = omniChannelPollServiceImpl.getReceiveMessages(dateString, careManager, member, clientOffset);
	        
	        // verify results
	        assertNotNull(result);
	        assertEquals(smsList.size(), result.size());
	        assertEquals(2, smsList.get(0).getMessageStatus()); // the message status should have been updated to 2
	        assertNull(smsList.get(0).getDeliveryStatus()); // the delivery status should have been set to null
	    }
	    
	    @Test
	    void shouldThrowExceptionWhenDateStringIsIncorrect() {
	        String dateString = "2022-04-17";
	        String careManager = "careManager";
	        String member = "member";
	        Long clientOffset = 0L;

	        Assertions.assertThrows(OmnichannelException.class, () -> {
	        	omniChannelPollServiceImpl.getReceiveMessages(dateString, careManager, member, clientOffset);
	        });
	    }
	    @Test
	    void shouldThrowExceptionWhenMappingIsInvalid() {
	        String dateString = "2022-04-17 12:00:00";
	        String careManager = "careManager";
	        String member = "member";
	        Long clientOffset = 0L;

	        when(memberCareManagerRepository.getByCareManagerAndMember(careManager, member)).thenReturn(null);

	        Assertions.assertThrows(OmnichannelException.class, () -> {
	        	omniChannelPollServiceImpl.getReceiveMessages(dateString, careManager, member, clientOffset);
	        });
	    }
	    @Test
	    void shouldThrowExceptionWhenMemberIsBlank() {
	        String dateString = "2022-04-17 12:00:00";
	        String careManager = "careManager";
	        String member = "";
	        Long clientOffset = 0L;

	        Assertions.assertThrows(OmnichannelException.class, () -> {
	        	omniChannelPollServiceImpl.getReceiveMessages(dateString, careManager, member, clientOffset);
	        });
	    }
	    @Test
	    void shouldThrowExceptionWhenCareManagerIsBlank() {
	        String dateString = "2022-04-17 12:00:00";
	        String careManager = "";
	        String member = "member";
	        Long clientOffset = 0L;

	        Assertions.assertThrows(OmnichannelException.class, () -> {
	        	omniChannelPollServiceImpl.getReceiveMessages(dateString, careManager, member, clientOffset);
	        });
	    }	

}
