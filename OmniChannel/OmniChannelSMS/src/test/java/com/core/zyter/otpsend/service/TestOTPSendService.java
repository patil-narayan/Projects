package com.core.zyter.otpsend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.core.zyter.dao.DeliveryStatusRepository;
import com.core.zyter.dao.MemberCareManagerRepository;
import com.core.zyter.dao.ReceiveSMSRepository;
import com.core.zyter.dao.SMSConversationRepository;
import com.core.zyter.dao.UserMasterRepository;
import com.core.zyter.entites.OmnichannelSMS;
import com.core.zyter.entites.UserMaster;
import com.core.zyter.service.TwilioSMSOperationService;
import com.core.zyter.service.impl.OmniChannelSMSServiceImpl;
import com.twilio.rest.api.v2010.account.Message;

public class TestOTPSendService {
	
	@Mock
    DeliveryStatusRepository twilioSMSRepository;
    @Mock
    ReceiveSMSRepository twilioReceiveSMSRepository;
    @Mock
    SMSConversationRepository smsConversationRepository;
    @Mock
    TwilioSMSOperationService twilioSMSOperationService;
    @Mock
    UserMasterRepository userMasterRepository;
    @Mock
    MemberCareManagerRepository memberCareManagerRepository;
    @InjectMocks
    OmniChannelSMSServiceImpl omniChannelSMSServiceImpl;
    List<UserMaster> userMasters;
    OmnichannelSMS omnichannelSMS;
    List<String> phoneNumbers;
    Message message;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        phoneNumbers = List.of("+15292920900", "+15292920901", "+15292920902");

        userMasters = List.of(
              //  UserMaster.builder().userType(CARE_MANAGER).userId("Dr Nefario").build(),
                UserMaster.builder().userType("MEMBER").userId("Jhon").build()
        );

        omnichannelSMS = OmnichannelSMS.builder().to(userMasters.get(1)).
                from(userMasters.get(0)).message("Hi, \nHow you doing?").build();

    }

//    @Test
//    public void getAllUsers() {
//        Mockito.when(userMasterRepository.findAll()).thenReturn(userMasters);
//        List<UserMaster> masters = omniChannelSMSServiceImpl.getAllUsers();
//        assertEquals(CARE_MANAGER, masters.get(0).getUserType());
//        assertEquals("Dr Nefario", masters.get(0).getUserId());
//    }


//    @Test
//    public void sendMessage() throws OmnichannelException {
//
//        Mockito.when(userMasterRepository.getUserMasterByUserIdAndUserType(Mockito.anyString(), Mockito.anyString())).
//                thenReturn(userMasters.get(0));
//        Mockito.when(twilioSMSOperationService.fetchTwilioPhonesNumbers()).thenReturn(phoneNumbers);
//        Mockito.when(twilioSMSOperationService.sendMessage(
//                Mockito.any(), Mockito.any())).thenReturn(Mockito.any(Message.class));
//
//        omniChannelSMSServiceImpl.sendMessage(omnichannelSMS);
//
//    }
    
    @Test
    public void sendMessage() {

        Mockito.when(userMasterRepository.getUserMasterByUserIdAndUserType(Mockito.anyString(), Mockito.anyString())).
                thenReturn(userMasters.get(0));
        Mockito.when(twilioSMSOperationService.fetchTwilioPhonesNumbers(null)).thenReturn(phoneNumbers);
//        Mockito.when(twilioSMSOperationService.sendMessage(
//                        Mockito.any(), Mockito.any())).thenReturn(Mockito.any());

        //omniChannelSMSServiceImpl.sendMessage(omnichannelSMS);
        List<String> phonenum = twilioSMSOperationService.fetchTwilioPhonesNumbers(null);
        assertNotNull(phonenum);
        assertEquals("+15627847204",phonenum);
        assertEquals("Hi, Bhushan\\n how you doing ?", omnichannelSMS.getMessage());

    }

}
