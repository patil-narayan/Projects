package com.core.zyter.bulk.controller;


import com.core.zyter.bulk.entites.BulkDeliveryStatus;
import com.core.zyter.bulk.entites.CampaignHistory;
import com.core.zyter.bulk.service.OmniChannelBulkSMSService;
import com.core.zyter.bulk.vos.BulkMessage;
import com.core.zyter.bulk.vos.BulkMessageResponse;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.util.Constants;
import com.core.zyter.util.OmniChannelSMSUtil;
import com.opencsv.exceptions.CsvException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "Bulk SMS", description = "APIs related to Bulk SMS")
@Slf4j
@RestController
@RequestMapping("/api/v1/bulk")
@SecurityRequirement(name = Constants.BEARER_AUTH_HEADER)
public class OmniChannelBulkSMSController {

    @Autowired
    OmniChannelBulkSMSService omniChannelBulkSMSService;
    @Autowired
    OmniChannelSMSUtil omniChannelSMSUtil;
    

    @PostMapping("/bulkMessage")
    public ResponseEntity<BulkMessageResponse> sendBulkMessage(@RequestBody BulkMessage message,
                                                               @Parameter(description = "to save messages on server select True/False")
                                                               @RequestParam("saveMessage") boolean saveMessage)
            throws IOException, CsvException, OmnichannelException {

        omniChannelBulkSMSService.notify(saveMessage, message);

        return ResponseEntity.ok().body(BulkMessageResponse.builder()
                .bulkMessage(message).status("Job submitted successfully").build());

    }

    @GetMapping("/campaignHistory")
    public CampaignHistory getCampaignHistory(@RequestParam("careManager") String careManager,
    		                                  @RequestParam (required = false, name="mode") String mode,
                                              @RequestParam("page") int page,
                                              @RequestParam("size") int size,
                                              @RequestParam(required = false, name = "clientOffset") Long clientOffset)
            throws OmnichannelException {

        return omniChannelBulkSMSService.getCampaignHistory(careManager,mode, page, size, clientOffset);

    }
    
    @GetMapping("/campaignHistories")
    public ResponseEntity<CampaignHistory> getCampaignHistories(@RequestHeader String Authorization, @RequestParam("careManager") String careManager,
    		                                  @RequestParam (required = false, name="mode") String mode,
                                              @RequestParam("page") int page,
                                              @RequestParam("size") int size,
                                              @RequestParam(required = false, name = "clientOffset") Long clientOffset)
            throws OmnichannelException {
    	String endPoint = "campaignHistory";
    	ResponseEntity<CampaignHistory> responseEntity = omniChannelSMSUtil.omniEmailConnectionRequest(CampaignHistory.class, endPoint,Authorization);
    			
    	return new ResponseEntity<CampaignHistory>(responseEntity.getBody(),HttpStatus.OK);

        //return omniChannelBulkSMSService.getCampaignHistories(careManager,mode, page, size, clientOffset);

    }

    @RequestMapping(value = "/deliveryStatus", method = RequestMethod.POST, produces = "text/xml")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public String receiveDeliveryStatus(BulkDeliveryStatus bulkDeliveryStatus) {

        return omniChannelBulkSMSService.receiveBulkDeliveryStatus(bulkDeliveryStatus);
    }


}
