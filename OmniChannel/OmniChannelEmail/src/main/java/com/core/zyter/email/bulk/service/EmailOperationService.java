package com.core.zyter.email.bulk.service;

import java.io.IOException;

import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;

public interface EmailOperationService {
	 
	  void sendGridLogic(String templateContent,String fromEmail, String subject);

	Response sendGridRespose(Mail mail) throws IOException;

}
