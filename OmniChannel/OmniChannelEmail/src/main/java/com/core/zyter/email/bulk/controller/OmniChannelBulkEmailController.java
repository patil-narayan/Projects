package com.core.zyter.email.bulk.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.bulk.service.OmniChannelBulkEmailService;
import com.core.zyter.email.bulk.vos.AuditCount;
import com.core.zyter.email.bulk.vos.BulkEmailRequest;
import com.core.zyter.email.bulk.vos.BulkEmailResponse;
import com.core.zyter.email.bulk.vos.CampaignHistory;
import com.core.zyter.email.util.Constants;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Bulk Email", description = "APIs related to Bulk Email")
@Slf4j
@RestController
@RequestMapping("/api/v2/email/bulk")
@SecurityRequirement(name = Constants.BEARER_AUTH_HEADER)
public class OmniChannelBulkEmailController {
	
	@Autowired
	OmniChannelBulkEmailService omniChannelBulkEmailService;

	@PostMapping("/bulkEmail")
	public ResponseEntity<BulkEmailResponse> sendBulkEmail(@RequestBody BulkEmailRequest bulkEmailRequest) throws IOException, CsvException, OmnichannelException{

		omniChannelBulkEmailService.bulkeEmailSend(bulkEmailRequest);
		log.debug("bulkEmailRequest {} :",bulkEmailRequest);
		return ResponseEntity.ok().body(BulkEmailResponse.builder()
				.status("Job submitted successfully").build());

	}
	
	 @GetMapping("/campaignHistory")
	    public CampaignHistory getCampaignHistory(@RequestParam("careManager") String careManager,
	    		                                  @RequestParam (required = false, name="mode") String mode,
	                                              @RequestParam("page") int page,
	                                              @RequestParam("size") int size,
	                                              @RequestParam(required = false, name = "clientOffset") Long clientOffset)
	            throws OmnichannelException {

	        return omniChannelBulkEmailService.getCampaignHistory(careManager,mode, page, size, clientOffset);

	    }
	 @GetMapping("/historyCount")
	 public List<AuditCount> getHistoryCount(@RequestParam String careManager) {
		 
		return omniChannelBulkEmailService.historyCount(careManager);
		 
	 }
	
}
