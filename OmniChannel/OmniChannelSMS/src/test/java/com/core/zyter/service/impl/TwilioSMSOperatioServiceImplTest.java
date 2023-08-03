package com.core.zyter.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.core.zyter.dao.WebhookRepository;
import com.core.zyter.entites.WebhookPayload;

import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TwilioSMSOperatioServiceImplTest {

	@Mock
	private WebhookRepository webhookRepository;

	@InjectMocks
	private TwilioSMSOperationServiceImpl twilioSMSOperationServiceImpl;

	// create a mock object for the repository
	WebhookRepository webhookRepositoryMock = Mockito.mock(WebhookRepository.class);



	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	 @Test
	    public void testSave() throws Exception {
	        // Arrange
	        Map<String, String> payload = new HashMap<>();
	        payload.put("Payload", "test payload");
	        payload.put("Level", "info");
	        payload.put("Timestamp", "2022-01-01T00:00:00.000Z");
	        payload.put("PayloadType", "test");
	        payload.put("ParentAccountSid", "parent_sid");
	        payload.put("AccountSid", "account_sid");
	        payload.put("Sid", "sid");

	        when(webhookRepository.save(any(WebhookPayload.class))).thenReturn(null);

	        // Act
	        twilioSMSOperationServiceImpl.save(payload);

	        // Assert
	        verify(webhookRepository).save(any(WebhookPayload.class));
	    }
	

	@Test
	public void testSaveWithValidPayload() throws Exception {
		// Arrange
		Map<String, String> payload = new HashMap<>();
		payload.put("Payload", "test payload");
		payload.put("Level", "info");
		payload.put("Timestamp", "2022-01-01T00:00:00.000Z");
		payload.put("PayloadType", "test");
		payload.put("ParentAccountSid", "parent_sid");
		payload.put("AccountSid", "account_sid");
		payload.put("Sid", "sid");

		when(webhookRepository.save(any(WebhookPayload.class))).thenReturn(null);

		// Act
		twilioSMSOperationServiceImpl.save(payload);

		// Assert
		verify(webhookRepository).save(any(WebhookPayload.class));
	}

	@Test
	public void testSaveWithNullPayload() throws Exception {
		// Arrange
		Map<String, String> payload = new HashMap<>();
		payload.put("Payload", null);
		payload.put("Level", "info");
		payload.put("Timestamp", "2022-01-01T00:00:00.000Z");
		payload.put("PayloadType", "test");
		payload.put("ParentAccountSid", "parent_sid");
		payload.put("AccountSid", "account_sid");
		payload.put("Sid", "sid");

		// Act
		twilioSMSOperationServiceImpl.save(payload);

		// Assert
		verify(webhookRepositoryMock, never()).save(any(WebhookPayload.class));
	}

	@Test
	public void testSaveWithNullLevel() throws Exception {
		// Arrange
		Map<String, String> payload = new HashMap<>();
		payload.put("Payload", "test payload");
		payload.put("Level", null);
		payload.put("Timestamp", "2022-01-01T00:00:00.000Z");
		payload.put("PayloadType", "test");
		payload.put("ParentAccountSid", "parent_sid");
		payload.put("AccountSid", "account_sid");
		payload.put("Sid", "sid");

		// Act
		twilioSMSOperationServiceImpl.save(payload);

		// Assert
		verify(webhookRepositoryMock, never()).save(any(WebhookPayload.class));
	}

	@Test
	public void testSaveWithValidPayloadType() throws Exception {
		// Arrange
		Map<String, String> payload = new HashMap<>();
		payload.put("Payload", "test payload");
		payload.put("Level", "info");
		payload.put("Timestamp", "2022-01-01T00:00:00.000Z");
		payload.put("PayloadType", null);
		payload.put("ParentAccountSid", "parent_sid");
		payload.put("AccountSid", "account_sid");
		payload.put("Sid", "sid");

		when(webhookRepository.save(any(WebhookPayload.class))).thenReturn(null);

		// Act
		twilioSMSOperationServiceImpl.save(payload);

		// Assert
		verify(webhookRepository).save(any(WebhookPayload.class));
	}

	@Test
	public void testSaveWithNullAccountSid() throws Exception {
		// Arrange
		Map<String, String> payload = new HashMap<>();
		payload.put("Payload", "test payload");
		payload.put("Level", "info");
		payload.put("Timestamp", "2022-01-01T00:00:00.000Z");
		payload.put("PayloadType", "test");
		payload.put("ParentAccountSid", "parent_sid");
		payload.put("AccountSid", null);
		payload.put("Sid", "sid");

		when(webhookRepository.save(any(WebhookPayload.class))).thenReturn(null);

		// Act
		twilioSMSOperationServiceImpl.save(payload);

		// Assert
		verify(webhookRepository).save(any(WebhookPayload.class));
	}

	@Test
	public void testSaveWithNullSid() throws Exception {
		// Arrange
		Map<String, String> payload = new HashMap<>();
		payload.put("Payload", "test payload");
		payload.put("Level", "info");
		payload.put("Timestamp", "2022-01-01T00:00:00.000Z");
		payload.put("PayloadType", "test");
		payload.put("ParentAccountSid", "parent_sid");
		payload.put("AccountSid", "account_sid");
		payload.put("Sid", null);

		when(webhookRepository.save(any(WebhookPayload.class))).thenReturn(null);

		// Act
		twilioSMSOperationServiceImpl.save(payload);

		// Assert
		verify(webhookRepository).save(any(WebhookPayload.class));
	}

}
