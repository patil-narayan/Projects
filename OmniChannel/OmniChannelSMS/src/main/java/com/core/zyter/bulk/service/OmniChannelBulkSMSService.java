package com.core.zyter.bulk.service;

import java.io.IOException;

import com.core.zyter.bulk.entites.BulkDeliveryStatus;
import com.core.zyter.bulk.entites.CampaignHistory;
import com.core.zyter.bulk.vos.BulkMessage;
import com.core.zyter.exceptions.OmnichannelException;
import com.opencsv.exceptions.CsvException;

public interface OmniChannelBulkSMSService {
	
	 void notify(boolean saveMessage, BulkMessage message) throws IOException , CsvException,OmnichannelException ;

	 String receiveBulkDeliveryStatus(BulkDeliveryStatus bulkDeliveryStatus);

	CampaignHistory getCampaignHistory(String caremanager, String mode, int page, int size, Long clientOffset)
			throws OmnichannelException;


}
