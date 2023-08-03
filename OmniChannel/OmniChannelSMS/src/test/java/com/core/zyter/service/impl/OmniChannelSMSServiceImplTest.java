/*
 * @OmniChannelSMSServiceImplTest.java@
 * Created on 27Dec2022
 *
 * Copyright (c) 2022 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.core.zyter.dao.DeliveryStatusRepository;
import com.core.zyter.dao.MemberCareManagerRepository;
import com.core.zyter.dao.MemberChannelOptinRepository;
import com.core.zyter.dao.ReceiveSMSRepository;
import com.core.zyter.dao.SMSConversationRepository;
import com.core.zyter.dao.UserMasterRepository;
import com.core.zyter.entites.MemberCareManagerMapping;
import com.core.zyter.entites.MemberChannelOptinStatus;
import com.core.zyter.entites.OmnichannelSMS;
import com.core.zyter.entites.UserMaster;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.service.TwilioSMSOperationService;
import com.core.zyter.sms.dao.SMSRepository;
import com.core.zyter.sms.entities.SMSHistory;
import com.core.zyter.sms.service.SMSServiceImpl;
import com.core.zyter.sms.vos.SMSVo;
import com.core.zyter.util.ConfigProperties;
import com.core.zyter.util.Constants;
import com.core.zyter.vos.MemberCareManagerRequest;
import com.core.zyter.vos.MemberPreference;
import com.core.zyter.vos.Usermaster;
import com.twilio.rest.api.v2010.account.Message;

public class OmniChannelSMSServiceImplTest {

	final String CARE_MANAGER = "CARE_MANAGER";

	@Autowired
	ConfigProperties configProperties;

	@Mock
	SMSRepository smsRepository;
	@InjectMocks
	SMSServiceImpl smsServiceImpl;
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

		userMasters = List.of(UserMaster.builder().userType(CARE_MANAGER).userId("Dr Nefario").build(),
				UserMaster.builder().userType("MEMBER").userId("Jhon").build());

		omnichannelSMS = OmnichannelSMS.builder().to(userMasters.get(1)).from(userMasters.get(0))
				.message("Hi, \nHow you doing?").build();

	}

	@Test
	public void getAllUsers() {
		Mockito.when(userMasterRepository.findAll()).thenReturn(userMasters);
		List<UserMaster> masters = omniChannelSMSServiceImpl.getAllUsers();
		assertEquals(CARE_MANAGER, masters.get(0).getUserType());
		assertEquals("Dr Nefario", masters.get(0).getUserId());
	}

	@Test
	public void sendMessage() throws OmnichannelException {

		Mockito.when(userMasterRepository.getUserMasterByUserIdAndUserType(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(userMasters.get(0));
		Mockito.when(twilioSMSOperationService.fetchTwilioPhonesNumbers(anyString())).thenReturn(phoneNumbers);
		Mockito.when(twilioSMSOperationService.sendMessage(Mockito.any(), Mockito.any()))
				.thenReturn(Mockito.any(Message.class));

		omniChannelSMSServiceImpl.sendMessage(omnichannelSMS, null);

	}

	@Test
	void testCreateUserWithExistingUserId() {
		UserMaster userMaster = new UserMaster();
		userMaster.setUserId("testUser");
		userMaster.setUserType("member");

		when(userMasterRepository.findByUserId(userMaster.getUserId())).thenReturn(userMaster);

		OmnichannelException exception = assertThrows(OmnichannelException.class, () -> {
			omniChannelSMSServiceImpl.createUser(userMaster);
		});

		assertEquals("User ID already exists.", exception.getMessage());
		assertEquals(Constants.FAILURE, exception.getStatus());
		assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

		verify(userMasterRepository, times(1)).findByUserId(userMaster.getUserId());
		verify(userMasterRepository, never()).save(userMaster);
	}

	@Test
	void testCreateUserWithMissingUserId() {
		UserMaster userMaster = new UserMaster();
		userMaster.setUserType("member");

		OmnichannelException exception = assertThrows(OmnichannelException.class, () -> {
			omniChannelSMSServiceImpl.createUser(userMaster);
		});

		assertEquals("USERID is required.", exception.getMessage());
		assertEquals(Constants.FAILURE, exception.getStatus());
		assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

		verify(userMasterRepository, never()).findByUserId(userMaster.getUserId());
		verify(userMasterRepository, never()).save(userMaster);
	}

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);

	}

	@Test
	void createMemberCareManagerMappingTest() throws OmnichannelException {
		String memberId = "123";
		String careManagerId = "456";

		UserMaster member = new UserMaster();
		member.setUserId(memberId);
		member.setPhoneNumber("1234567890");

		UserMaster careManager = new UserMaster();
		careManager.setUserId(careManagerId);

		List<MemberCareManagerMapping> mappings = new ArrayList<>();
		MemberCareManagerMapping mapping = new MemberCareManagerMapping();
		mapping.setCareManagerPhoneNumber("9876543210");
		mappings.add(mapping);

		when(memberCareManagerRepository.findByMember(memberId)).thenReturn(mappings);
		when(twilioSMSOperationService.fetchTwilioPhonesNumbers(anyString())).thenReturn(new ArrayList<String>());

		MemberCareManagerRequest request = new MemberCareManagerRequest();
		request.setMember(member);
		request.setCareManager(careManager);
		assertThrows(NullPointerException.class, () -> {
			omniChannelSMSServiceImpl.createMemberCareManagerMapping(request);
		});

	}

	@Test
	public void testUpdateMemberCareManagerMapping() throws OmnichannelException {
		MemberCareManagerMapping inputMapping = new MemberCareManagerMapping();
		inputMapping.setMember("member1");
		inputMapping.setCareManager("caremanager1");
		inputMapping.setMemberPhoneNumber("1234567890");

		MemberCareManagerMapping existingMapping = new MemberCareManagerMapping();
		existingMapping.setMember("member1");
		existingMapping.setCareManager("caremanager1");
		existingMapping.setMemberPhoneNumber("9876543210");

		List<MemberCareManagerMapping> mappings = new ArrayList<>();
		mappings.add(existingMapping);

		when(memberCareManagerRepository.findByMember("member1")).thenReturn(mappings);
		when(memberCareManagerRepository.save(existingMapping)).thenReturn(existingMapping);

		MemberCareManagerMapping updatedMapping = omniChannelSMSServiceImpl
				.updateMemberCareManagerMapping(inputMapping);

		verify(memberCareManagerRepository, times(1)).findByMember("member1");
		verify(memberCareManagerRepository, times(1)).save(existingMapping);

		assertEquals(inputMapping.getMemberPhoneNumber(), updatedMapping.getMemberPhoneNumber());
		assertEquals(existingMapping.getMember(), updatedMapping.getMember());
		assertEquals(existingMapping.getCareManager(), updatedMapping.getCareManager());
	}

	@Test
	public void testUpdateMemberCareManagerMappingWithMissingParameters() {
		MemberCareManagerMapping inputMapping = new MemberCareManagerMapping();
		inputMapping.setMember("member1");

		OmnichannelException exception = assertThrows(OmnichannelException.class, () -> {
			omniChannelSMSServiceImpl.updateMemberCareManagerMapping(inputMapping);
		});

		assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
		assertEquals(Constants.FAILURE, exception.getStatus());
		assertTrue(exception.getMessage().contains("care manager"));
	}

	@Test
	public void testUpdateMemberCareManagerMappingWithMappingNotFound() {
		MemberCareManagerMapping inputMapping = new MemberCareManagerMapping();
		inputMapping.setMember("member1");
		inputMapping.setCareManager("caremanager1");
		inputMapping.setMemberPhoneNumber("1234567890");

		List<MemberCareManagerMapping> mappings = new ArrayList<>();

		when(memberCareManagerRepository.findByMember("member1")).thenReturn(mappings);

		OmnichannelException exception = assertThrows(OmnichannelException.class, () -> {
			omniChannelSMSServiceImpl.updateMemberCareManagerMapping(inputMapping);
		});

		assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
		assertEquals(Constants.FAILURE, exception.getStatus());
		assertTrue(exception.getMessage().contains("Mapping not found"));
	}

	private UserMaster userMaster;

	@BeforeEach
	void setUp_() {
		userMaster = new UserMaster();
		userMaster.setUserId("testuser");
		userMaster.setFirstName("Test");
		userMaster.setLastName("User");
		userMaster.setUserType("customer");
	}

	private UserMaster users;
	Usermaster masters;

	@BeforeEach
	void init() {
		users = UserMaster.builder().userId("MANOJ").phoneNumber("+919876543221").emailId("man@gmail.com").build();
		masters = Usermaster.builder().userId("MANOJ").phoneNumber("+919876543221").emailId("man@gmail.com").build();
	}

	@Test
	public void testUserExistance() throws OmnichannelException {

		Mockito.when(userMasterRepository.findByUserId("MANOJ")).thenReturn(users);

		UserMaster user = omniChannelSMSServiceImpl.updateUser(masters);

		assertThat(user).isNotNull();

	}

	@Test
	@DisplayName("update user by phoneNumber")
	public void updateUserByPhoneNumber() throws OmnichannelException {

		Mockito.when(userMasterRepository.findByUserId("MANOJ")).thenReturn(users);

		masters.setPhoneNumber("+917865343438");

		UserMaster updatedUser = omniChannelSMSServiceImpl.updateUser(masters);

		assertThat(updatedUser.getPhoneNumber()).isEqualTo("+917865343438");
		assertEquals("+917865343438", updatedUser.getPhoneNumber());
	}

	@Test
	@DisplayName("update user by EmailId")
	public void updateUserByEmailId() throws OmnichannelException {

		Mockito.when(userMasterRepository.findByUserId("MANOJ")).thenReturn(users);

		masters.setEmailId("manoj@gmail.com");

		UserMaster updatedUser = omniChannelSMSServiceImpl.updateUser(masters);

		assertThat(updatedUser.getEmailId()).isEqualTo("manoj@gmail.com");

	}

	@Test
	public void updateUserException() throws OmnichannelException {
		UserMaster user = null;
		Usermaster master = new Usermaster();
		try {
			Mockito.when(userMasterRepository.findByUserId("Narayan")).thenReturn(user);
			user = omniChannelSMSServiceImpl.updateUser(master);
			assertNotNull(user);
		} catch (OmnichannelException e) {
			System.out.println("Exception occured");
			assertThat(user).isNull();
			assertThrows(OmnichannelException.class, () -> {
				omniChannelSMSServiceImpl.updateUser(masters);
			}, "exception occured due to user doesn't exist");
		}

	}

	@Test
	public void TestUserByPhoneNumberAndEmail() throws OmnichannelException {

		Mockito.when(userMasterRepository.findByUserId("MANOJ")).thenReturn(users);
		masters.setEmailId("manoj@gmail.com");
		masters.setPhoneNumber("7865343438");
		UserMaster updatedUser = omniChannelSMSServiceImpl.updateUser(masters);

		assertThat(updatedUser.getPhoneNumber()).isNotEqualTo("8699345262");
		assertThat(updatedUser.getEmailId()).isNotEqualTo("m@gmail.com");
	}

	SMSVo smsVo;
	SMSServiceImpl smsService;
	List<String> phoneNumber;

	@BeforeEach
	void setupMethod() {

		phoneNumber = List.of("+13019179102", "+15292920901", "+15292920902");

		smsVo = SMSVo.builder().from("Nefario").to("Manoj").toPhoneNumber("+918088993974").msg("Hi,How you doing?")
				.build();
	}

	@Test
	public void testSendMessage() throws OmnichannelException {

		ConfigProperties configProperties = new ConfigProperties();
		configProperties.setMessageSid("MGdcv99");
		configProperties.setGenericMessageSid("MGSW456");
		configProperties.setAccountSid("ACFE1234");

		SMSHistory smsHistory = new SMSHistory();

		Mockito.when(twilioSMSOperationService.fetchTwilioPhonesNumbers(anyString())).thenReturn(phoneNumber);
		Mockito.when(
				twilioSMSOperationService.sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Mockito.any(Message.class));

		smsHistory = smsServiceImpl.sendMessage(smsVo);

		assertThat(configProperties.getGenericMessageSid()).isNotNull();

		assertEquals("Hi,How you doing?", smsHistory.getBody());

		assertEquals("MGSW456", configProperties.getGenericMessageSid());

	}

	@Test
	public void testGenericMessageSidIsNull() throws OmnichannelException {
		ConfigProperties configProperties = new ConfigProperties();
		configProperties.setGenericMessageSid(null);
		configProperties.setAccountSid("ACFFE1234");

		Mockito.when(
				twilioSMSOperationService.sendMessage(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(Mockito.any(Message.class)).thenReturn(null);
		smsServiceImpl.sendMessage(smsVo);

		assertThat(configProperties.getGenericMessageSid()).isNull();

		assertThrows(OmnichannelException.class, () -> {
			smsServiceImpl.sendMessage(smsVo);
		}, "exception occured due to genericMessageSid doesn't exist");

	}

	@Test
	public void testCreateMemberCareManagerMapping_NullRequest() throws OmnichannelException {
		// Arrange
		MemberCareManagerRequest request = null;

		// Act and Assert
		assertThrows(NullPointerException.class,
				() -> omniChannelSMSServiceImpl.createMemberCareManagerMapping(request));
	}

	@Test
	public void testCreateMemberCareManagerMapping_NullMember() throws OmnichannelException {
		// Arrange
		MemberCareManagerRequest request = new MemberCareManagerRequest();
		request.setCareManager(new UserMaster());

		// Act and Assert
		assertThrows(NullPointerException.class,
				() -> omniChannelSMSServiceImpl.createMemberCareManagerMapping(request));
	}

	@Test
	public void testCreateMemberCareManagerMapping_NullCareManager() throws OmnichannelException {
		// Arrange
		MemberCareManagerRequest request = new MemberCareManagerRequest();
		request.setMember(new UserMaster());

		// Act and Assert
		assertThrows(OmnichannelException.class,
				() -> omniChannelSMSServiceImpl.createMemberCareManagerMapping(request));
	}

	@Test
	public void testCreateMemberCareManagerMapping_NonExistingMember() throws OmnichannelException {
		// Arrange
		MemberCareManagerRequest request = new MemberCareManagerRequest();
		UserMaster member = new UserMaster();
		member.setUserId("nonExistingUserId");
		request.setMember(member);
		request.setCareManager(new UserMaster());

		// Act and Assert
		assertThrows(OmnichannelException.class,
				() -> omniChannelSMSServiceImpl.createMemberCareManagerMapping(request));
	}

	@Test
	public void testCreateMemberCareManagerMapping_NonExistingCareManager() throws OmnichannelException {
		// Arrange
		MemberCareManagerRequest request = new MemberCareManagerRequest();
		UserMaster careManager = new UserMaster();
		careManager.setUserId("nonExistingUserId");
		request.setMember(new UserMaster());
		request.setCareManager(careManager);

		// Act and Assert
		assertThrows(OmnichannelException.class,
				() -> omniChannelSMSServiceImpl.createMemberCareManagerMapping(request));
	}

	@Mock
	MemberChannelOptinRepository memberChannelOptinRepository;

	@Test
	public void updatePreferenceTest() throws OmnichannelException {
		MemberPreference memberPreference = new MemberPreference();
		memberPreference.setChannelType("VOICE");
		memberPreference.setOptinStatus(Constants.ACCEPTED);

		UserMaster user = new UserMaster();
		user.setUserId("MANOJ");
		user.setUserType("MEMBER");
		user.setActive(true);
		UserMaster master = user;

		MemberChannelOptinStatus memberChannelOptinStatus = MemberChannelOptinStatus.builder()
				.member(master.getUserId()).channelType(memberPreference.getChannelType())
				.optinStatus(memberPreference.getOptinStatus()).createdOn(new Date()).build();
		memberChannelOptinRepository.save(memberChannelOptinStatus);

		Mockito.when(userMasterRepository.findByUserId(anyString())).thenReturn(master);

		MemberChannelOptinStatus channelOptinStatus = omniChannelSMSServiceImpl.updatePreference(memberPreference,
				memberChannelOptinStatus.getMember());

		assertEquals("VOICE", memberChannelOptinStatus.getChannelType());
		assertEquals(Constants.ACCEPTED, memberChannelOptinStatus.getOptinStatus());
		assertEquals("MANOJ", memberChannelOptinStatus.getMember());

		if (memberChannelOptinStatus.getMember().isEmpty()) {
			assertThrows(OmnichannelException.class, () -> {
				omniChannelSMSServiceImpl.updatePreference(memberPreference, memberChannelOptinStatus.getMember());
			});
		}

	}

}
