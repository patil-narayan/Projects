package com.core.zyter.bulk.service.impl;

import com.core.zyter.bulk.dao.BulkDeliveryStatusRepository;
import com.core.zyter.bulk.dao.BulkTemplateRepository;
import com.core.zyter.bulk.dao.DistributionListRepository;
import com.core.zyter.bulk.dao.NotifySMSAuditDetailsRepository;
import com.core.zyter.bulk.dao.NotifySMSAuditRepository;
import com.core.zyter.bulk.entites.BulkDeliveryStatus;
import com.core.zyter.bulk.entites.BulkTemplate;
import com.core.zyter.bulk.entites.CampaignHistory;
import com.core.zyter.bulk.entites.DistributionList;
import com.core.zyter.bulk.entites.Audit;
import com.core.zyter.bulk.entites.AuditDetails;
import com.core.zyter.bulk.service.OmniChannelBulkSMSService;
import com.core.zyter.bulk.vos.BulkMessage;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.util.ConfigProperties;
import com.core.zyter.util.Constants;
import com.core.zyter.util.DateTimeUtil;
import com.core.zyter.util.OmniChannelSMSUtil;
import com.google.common.collect.Lists;
import com.twilio.rest.notify.v1.service.Notification;

import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Data
@Service
public class OmniChannelBulkSMSServiceImpl implements OmniChannelBulkSMSService {

    private static final String DELIVERY_STATUS_ENDPOINT = "/api/v1/bulk/deliveryStatus";
    private static final Integer BATCH_SIZE = 100;

    @Autowired
    DistributionListRepository distributionListRepository;
    @Autowired
    ConfigProperties configProperties;
    @Autowired
    NotifySMSAuditDetailsRepository notifySMSAuditDetailsRepository;
    @Autowired
    NotifySMSAuditRepository notifySMSAuditRepository;
    @Autowired
    BulkDeliveryStatusRepository bulkDeliveryStatusRepository;
    @Autowired
    BulkTemplateRepository bulkTemplateRepository;

    @Override
    @Transactional
    @Async
    public void notify(boolean saveMessage, BulkMessage message) {

    	 Audit audit = Audit.builder().careManager(message.getCareManager())
                 .note(message.getNote()).mode(Constants.SMS).createdOn(new Date())
                 .status(Constants.INPROGRESS).build();
    	 Optional<DistributionList> distributionList=Optional.empty();
    	 Optional<BulkTemplate> bulkTemplate=Optional.empty();
        try {
        	 distributionList = distributionListRepository.findById(message.getDistributionList());
        	 bulkTemplate = bulkTemplateRepository.findById(message.getTemplate()); 
             notifySMSAuditRepository.save(audit);
             
            if (distributionList.isPresent() && bulkTemplate.isPresent()) {
            	Path fileName = Path.of(bulkTemplate.get().getFilePath());
                String templateContent = Files.readString(fileName);
                List<String> toBindings = OmniChannelSMSUtil.createBindingForNotify(distributionList.get().getFilePath());
                List<List<String>> partition = Lists.partition(toBindings, BATCH_SIZE);
                partition.forEach(toBinding -> {
                    Notification notification = Notification.creator(configProperties.getNotifySid())
                            .setBody(templateContent).setToBinding(toBinding).setTitle(message.getNote())
                            .setDeliveryCallbackUrl(configProperties.getCallbackUrl() + DELIVERY_STATUS_ENDPOINT).create();
                    AuditDetails notifySMSAuditDetails = AuditDetails.builder().AuditId(audit.getId())
                            .createdOn(new Date()).SSID(notification.getSid()).build();
                    notifySMSAuditDetailsRepository.save(notifySMSAuditDetails);
                });
                audit.setStatus(Constants.COMPLETE);
                audit.setDistributionList(distributionList.get().getName());
                audit.setTemplate(bulkTemplate.get().getName());
                notifySMSAuditRepository.save(audit);

            } else {
                audit.setStatus(Constants.FAILURE);
                audit.setReason("Invalid distributionList or template");
                notifySMSAuditRepository.save(audit);
            }
        } catch (Exception ex) {
            log.error("", ex);
            audit.setStatus(Constants.FAILURE);
            audit.setDistributionList(distributionList.get().getName());
            audit.setTemplate(bulkTemplate.get().getName());
            audit.setReason("Failed while sending bulk messages");
            notifySMSAuditRepository.save(audit);

        }

    }

    @Override
    public CampaignHistory getCampaignHistory(String caremanager,String mode, int page, int size, Long clientOffset) throws OmnichannelException {

    	CampaignHistory campaignHistory;
    	try {
    		Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
    		Page notifySMSAuditPage = notifySMSAuditRepository.findByCareManager(caremanager, pageable);
    		List<Audit> notifySMSAuditList = new ArrayList<>(notifySMSAuditPage.getContent());
    		notifySMSAuditList.forEach(notifySMSAudit -> {
    			try {
    				if (null != clientOffset) {
    					// Timezone conversion for notifySMSAudit
    					long serverOffset = DateTimeUtil.getServerOffset(new Date());
    					notifySMSAudit.setCreatedOn(DateTimeUtil.calculateDate(serverOffset, clientOffset, notifySMSAudit.getCreatedOn()));
    					// Timezone conversion for notifySMSAuditDetail
    					notifySMSAudit.getDetails().forEach(notifySMSAuditDetail ->
    					notifySMSAuditDetail.setCreatedOn(
    							DateTimeUtil.calculateDate(serverOffset, clientOffset, notifySMSAuditDetail.getCreatedOn()))

    							);
    				}


    			} catch (ParseException e) {
    				throw new RuntimeException(e);
    			}

    		});
    		if(StringUtils.isNotBlank(mode)) {
    			Page auditPage = notifySMSAuditRepository.findByCareManagerAndMode(caremanager, mode, pageable);
    			List<Audit> auditList = new ArrayList<>(auditPage.getContent());

    			campaignHistory = CampaignHistory.builder()
    					.audits(auditList)
    					.totalPages(auditPage.getTotalPages())
    					.totalItems(auditPage.getTotalElements())
    					.currentPage(auditPage.getNumber())
    					.size(auditPage.getSize()).build();
    		}else {
    			campaignHistory = CampaignHistory.builder()
    					.audits(notifySMSAuditList)
    					.totalPages(notifySMSAuditPage.getTotalPages())
    					.totalItems(notifySMSAuditPage.getTotalElements())
    					.currentPage(notifySMSAuditPage.getNumber())
    					.size(notifySMSAuditPage.getSize()).build();
    		}
    	} catch (Exception e) {
    		log.error("", e);
    		throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
    	}

    	return campaignHistory;
    }

    @Override
    public String receiveBulkDeliveryStatus(BulkDeliveryStatus bulkDeliveryStatus) {
        try {
            bulkDeliveryStatusRepository.save(bulkDeliveryStatus);
        } catch (Exception e) {
            log.error("", e);
            return Constants.FAILURE;
        }
        return Constants.SUCCESS;
    }
}
