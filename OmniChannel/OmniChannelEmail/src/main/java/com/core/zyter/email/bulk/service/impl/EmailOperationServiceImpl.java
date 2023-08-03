package com.core.zyter.email.bulk.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.core.zyter.email.bulk.service.EmailOperationService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

@Service
public class EmailOperationServiceImpl implements EmailOperationService{

	@Autowired
	SendGrid sendgrid;
	
	@Override
	public Response sendGridRespose(Mail mail) throws IOException {
		Response  response= new Response();
		//Mail mail = new Mail();
		Request request = new Request();

		request.setMethod(Method.POST);
		request.setEndpoint("mail/send");
		request.setBody(mail.build());
		return response= sendgrid.api(request);

	}
	
	public void sendGridLogic(String templateContent, String fromEmail, String subject) {

		Personalization personalization =  new Personalization();

		Mail mail = new Mail();

		personalization.addTo(new Email("Team@OmniChannel","hello"));

		mail.addPersonalization(personalization);
		mail.setFrom(new Email(fromEmail));
		mail.setSubject(subject);
		
		Content content = new Content();
		content.setType("text/html");
		String emailSubject = subject;
		
		mail.setSubject(emailSubject);
		content.setValue(templateContent);
		mail.addContent(content);
		
		mail.addPersonalization(personalization);
		
		
	}


}
