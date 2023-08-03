package com.core.zyter.email.service;

import java.io.IOException;
import java.util.List;

import com.core.zyter.email.entities.EmailEventDetails;
import com.core.zyter.email.entities.EmailHistory;
import org.springframework.stereotype.Service;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.vos.EmailHistoryVos;
import com.core.zyter.email.vos.EmailRequest;

@Service
public interface EmailService {

	EmailHistoryVos getEmailHistory(String careManager, String member, int page, int size,Long clientOffset) throws OmnichannelException;

	EmailHistory sendEmail(EmailRequest request,Long clientOffset) throws OmnichannelException, IOException;
	
	String deliveryEvent(List<EmailEventDetails> payload) throws OmnichannelException;
	
}
