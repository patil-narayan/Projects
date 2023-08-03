package com.core.zyter.sms.service;

import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.sms.entities.SMSDeliveryStatus;
import com.core.zyter.sms.entities.SMSHistory;
import com.core.zyter.sms.vos.SMSVo;

public interface SMSService {
	
	SMSHistory sendMessage(SMSVo smsVo) throws OmnichannelException;
	
	String receiveDeliveryStatus(SMSDeliveryStatus smsOTPDeliveryStatus);

}
