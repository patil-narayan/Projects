package com.core.zyter.email.bulk.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.bulk.dao.BulkEmailDistributionListRepository;
import com.core.zyter.email.bulk.dao.BulkEmailTemplateRepository;
import com.core.zyter.email.bulk.dao.EmailAuditDetailsRepository;
import com.core.zyter.email.bulk.dao.EmailAuditRepository;
import com.core.zyter.email.bulk.entities.Audit;
import com.core.zyter.email.bulk.entities.AuditDetails;
import com.core.zyter.email.bulk.entities.BulkTemplate;
import com.core.zyter.email.bulk.entities.DistributionList;
import com.core.zyter.email.bulk.service.OmniChannelBulkEmailService;
import com.core.zyter.email.bulk.vos.AuditCount;
import com.core.zyter.email.bulk.vos.BulkEmailRequest;
import com.core.zyter.email.bulk.vos.CampaignHistory;
import com.core.zyter.email.dao.MemberCareManagerRepository;
import com.core.zyter.email.entities.MemberCareManagerMapping;
import com.core.zyter.email.util.Constants;
import com.core.zyter.email.util.DateTimeUtil;
import com.core.zyter.email.util.OmniChannelEmailUtil;
import com.core.zyter.email.vos.DynamicPlaceHolder;
import com.google.common.collect.Lists;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OmniChannelBulkEmailServiceImpl implements OmniChannelBulkEmailService {
	
	private final Integer BATCH_SIZE = 100;

	@Autowired
	SendGrid sendgrid;
	@Autowired
	MemberCareManagerRepository memberCareManagerRepository;
	@Autowired
	BulkEmailDistributionListRepository bulkEmailDistributionListRepository;
	@Autowired
	BulkEmailTemplateRepository bulkEmailTemplateRepository;
	@Autowired
	EmailAuditDetailsRepository emailAuditDetailsRepository;
	@Autowired
	EmailAuditRepository emailAuditRepository;
	
	
	@Override
	public Response bulkeEmailSend(BulkEmailRequest bulkEmailRequest) throws IOException, CsvValidationException, CsvException, OmnichannelException{

		Audit emailAudit = Audit.builder().careManager(bulkEmailRequest.getCareManager())
				.note(bulkEmailRequest.getNote()).mode(Constants.EMAIL).createdOn(new Date())
				.status(Constants.INPROGRESS).build();

		Response response = null;
		DynamicPlaceHolder dynamicPlaceHolder = null;
		Mail mail = new Mail();

		List<MemberCareManagerMapping> careManager=	memberCareManagerRepository.getByCareManagerEmailId(bulkEmailRequest.getCareManager());
		if (careManager.isEmpty() || careManager == null) {
			throw new OmnichannelException("CareManager Not found ", Constants.FAILURE, HttpStatus.NOT_FOUND);
		}
		if(StringUtils.isBlank(bulkEmailRequest.getNote())) {
			throw new OmnichannelException("Note cannot be Null", Constants.FAILURE, HttpStatus.NOT_FOUND);
		}

		Optional<BulkTemplate> bulkEmailTemplates = bulkEmailTemplateRepository.findById(bulkEmailRequest.getTemplate());
		Optional<DistributionList> bulkEmailDistributionList  =bulkEmailDistributionListRepository.findById(bulkEmailRequest.getDistributionList());
		emailAuditRepository.save(emailAudit);

		if (bulkEmailTemplates.isPresent() && bulkEmailDistributionList.isPresent() && bulkEmailTemplates.get().isRecordStatus() && bulkEmailDistributionList.get().isRecordStatus()) {
			try {
				Path fileName = Path.of(bulkEmailTemplates.get().getFilePath());
				String templateContent = Files.readString(fileName);
				List<String> toBindings = OmniChannelEmailUtil.createBindingForPersonalization(bulkEmailDistributionList.get().getFilePath());
				List<List<String>> partition = Lists.partition(toBindings, BATCH_SIZE);
				Personalization personalization =  new Personalization();
				personalization.addTo(new Email("OmniChannel@gmail.com","hello"));

				mail.addPersonalization(personalization);
				mail.setFrom(new Email(careManager.get(0).getCareManagerEmailId()));
				mail.setSubject(bulkEmailTemplates.get().getSubject());

				Content content = new Content();
				content.setType("text/html");
				String emailSubject = bulkEmailTemplates.get().getSubject();
				if(bulkEmailRequest.getPlaceholders() !=null) {
					dynamicPlaceHolder=	OmniChannelEmailUtil.placeHolderForEmail(templateContent, emailSubject, bulkEmailRequest.getPlaceholders());
					mail.setSubject(dynamicPlaceHolder.getEmailSubject());
					content.setValue(dynamicPlaceHolder.getTemplateContent());
					mail.addContent(content);
				}else {
				mail.setSubject(emailSubject);
				content.setValue(templateContent);
				mail.addContent(content);
				}

				Request request = new Request();
				for(List<String> str :partition) {
					str.stream().forEach(listOfEmail ->{
						personalization.addBcc(new Email(listOfEmail));
					});
					AuditDetails emailAuditDetails = AuditDetails.builder().AuditId(emailAudit.getId()).createdOn(new Date()).build();
					emailAuditDetailsRepository.save(emailAuditDetails);
					request.setMethod(Method.POST);
					request.setEndpoint("mail/send");
					request.setBody(mail.build());
					response= sendgrid.api(request);
				}
				emailAudit.setStatus(Constants.COMPLETE);
				emailAudit.setDistributionList(bulkEmailDistributionList.get().getName());
				emailAudit.setTemplate(bulkEmailTemplates.get().getName());
				emailAuditRepository.save(emailAudit);
			}catch (Exception e) {
				log.error("", e);
				emailAudit.setStatus(Constants.FAILURE);
				emailAudit.setDistributionList(bulkEmailDistributionList.get().getName());
				emailAudit.setTemplate(bulkEmailTemplates.get().getName());
				emailAudit.setReason("Failed while sending Campaign");
				emailAuditRepository.save(emailAudit);
				throw new OmnichannelException("Failed while sending Campaign", Constants.FAILURE, HttpStatus.NOT_FOUND);
			}
		}else {
			emailAudit.setStatus(Constants.FAILURE);
			emailAudit.setReason("DistributionList OR Template is Invalid");
			emailAuditRepository.save(emailAudit);
			throw new OmnichannelException("DistributionList OR Template is Invalid", Constants.FAILURE, HttpStatus.NOT_FOUND);

		}
		return response;
	}
	
	@Override
	public CampaignHistory getCampaignHistory(String caremanager,String mode, int page, int size, Long clientOffset) throws OmnichannelException {

		CampaignHistory campaignHistory;
		try {
			Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");

			Page emailAudiPage = emailAuditRepository.findByCareManager(caremanager, pageable);
			List<Audit> emailAuditList = new ArrayList<>(emailAudiPage.getContent());
			emailAuditList.forEach(emailAudit -> {
				try {
					if (null != clientOffset) {
						// Timezone conversion for notifySMSAudit
						long serverOffset = DateTimeUtil.getServerOffset(new Date());
						emailAudit.setCreatedOn(DateTimeUtil.calculateDate(serverOffset, clientOffset, emailAudit.getCreatedOn()));
						// Timezone conversion for notifySMSAuditDetail
						emailAudit.getDetails().forEach(emailAuditDetail ->
						emailAuditDetail.setCreatedOn(
								DateTimeUtil.calculateDate(serverOffset, clientOffset, emailAuditDetail.getCreatedOn()))

								);
					}

				} catch (ParseException e) {
					throw new RuntimeException(e);
				}

			});
			if(mode != null) {
				Page auditPage = emailAuditRepository.findByCareManagerAndMode(caremanager, mode, pageable);
				List<Audit> auditList = new ArrayList<>(auditPage.getContent());

				campaignHistory = CampaignHistory.builder()
						.audits(auditList)
						.totalPages(auditPage.getTotalPages())
						.totalItems(auditPage.getTotalElements())
						.currentPage(auditPage.getNumber())
						.size(auditPage.getSize()).build();
			}else {
				campaignHistory = CampaignHistory.builder()
						.audits(emailAuditList)
						.totalPages(emailAudiPage.getTotalPages())
						.totalItems(emailAudiPage.getTotalElements())
						.currentPage(emailAudiPage.getNumber())
						.size(emailAudiPage.getSize()).build();
			}
		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return campaignHistory;
	}
	@Override	
	public List<AuditCount> historyCount(String careManager) {

		List<AuditCount> auditList = new ArrayList<>();
		List<Object[]> audits = emailAuditRepository.getByMode(careManager);
		audits.forEach(audit ->{
			AuditCount auditPage = new AuditCount();
			auditPage = AuditCount.builder()
					.count((Long) audit[0])
					.mode((String) audit[1])
					.build();
			auditList.add(auditPage);				

		});
		log.debug("audit:{} ",auditList);

		return auditList;

	}


}
