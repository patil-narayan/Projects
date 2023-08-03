package com.core.zyter.email.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.core.zyter.email.vos.DynamicPlaceHolder;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

public class OmniChannelEmailUtil {
	
		
	 public static List<String> createBindingForPersonalization(String disList)
				throws IOException, CsvValidationException, CsvException {

	    	 List<String> toBindings = new ArrayList<>();
			CSVReader reader = new CSVReader(new FileReader(disList));
			if ((reader.readNext()) != null) {
				List<String[]> lines = reader.readAll();
				if (null != lines && !lines.isEmpty()) {
					lines.stream().collect(Collectors.toList()).forEach(phoneNumber -> {
						toBindings.add( phoneNumber[6]);
					});
				}
			}
			return toBindings;
		}
	 
	 public static DynamicPlaceHolder placeHolderForEmail(String templateContent, String emailSubject,Map<String, String> placeHolders) {
		 DynamicPlaceHolder dynamicPlaceHolder = null;
	 for (Map.Entry<String, String> entry : placeHolders.entrySet()) {
			String placeholder = entry.getKey();
			String value = entry.getValue();
			if(value != null) {
			emailSubject = emailSubject.replace("{{" + placeholder + "}}", value);
			templateContent = templateContent.replace("{{" + placeholder + "}}", value);
			}
			dynamicPlaceHolder = DynamicPlaceHolder.builder().templateContent(templateContent).emailSubject(emailSubject).build();
		}
	return dynamicPlaceHolder;
	 }

}
