package com.core.zyter.email.bulk.service;

import java.io.IOException;
import java.util.List;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.bulk.vos.AuditCount;
import com.core.zyter.email.bulk.vos.BulkEmailRequest;
import com.core.zyter.email.bulk.vos.CampaignHistory;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.sendgrid.Response;

public interface OmniChannelBulkEmailService {
	
	Response bulkeEmailSend(BulkEmailRequest bulkEmailRequest) throws IOException, CsvValidationException, CsvException, OmnichannelException;

	CampaignHistory getCampaignHistory(String caremanager, String mode, int page, int size, Long clientOffset)
			throws OmnichannelException;

	List<AuditCount> historyCount(String careManager);

}
