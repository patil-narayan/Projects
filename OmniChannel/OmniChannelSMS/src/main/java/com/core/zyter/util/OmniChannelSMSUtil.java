/*
 * @OmniChannelSMSUtil.java@
 * Created on 08Dec2022
 *
 * Copyright (c) 2022 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.util;


import com.core.zyter.bulk.entites.CampaignHistory;
import com.core.zyter.entites.User;
import com.core.zyter.securityconfig.TenantAuthenticationManagerResolver;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class OmniChannelSMSUtil {
	
	@Value(value = "${omniChannelEmail}")
	private String omniChannelEmail;
	
	@Autowired
	TenantAuthenticationManagerResolver tenantAuthenticationManagerResolver;
	
	private final BearerTokenResolver resolver = new DefaultBearerTokenResolver();

    public static List<String> createBinding(List<User> users) {

        List<String> toBindings = new ArrayList<>();
        users.stream().map(User::getPhoneNumber).collect(Collectors.toList()).forEach(phoneNumber -> {
            toBindings.add("{\"binding_type\":\"sms\",\"address\":\"" + phoneNumber + "\"}");
        });

        return toBindings;
    }
    
    public static List<String> createBindingForNotify(String disList)
			throws IOException, CsvValidationException, CsvException {

    	 List<String> toBindings = new ArrayList<>();
		CSVReader reader = new CSVReader(new FileReader(disList));
		if ((reader.readNext()) != null) {
			List<String[]> lines = reader.readAll();
			if (null != lines && !lines.isEmpty()) {
				lines.stream().collect(Collectors.toList()).forEach(phoneNumber -> {
					toBindings.add("{\"binding_type\":\"sms\",\"address\":\"" + phoneNumber[3] + "\"}");
				});
			}
		}
		return toBindings;
	}
    
    public <T> ResponseEntity<T> omniEmailConnectionRequest(Class<T> cls, String method ,String authToken){
    	String auth ="Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ3UEpiMjZNOXFGb3pzc3EyUnhuei1zdWR4eVF5QUFPZElySHdqV0MwYnZBIn0.eyJleHAiOjE2ODEzNDM4MDQsImlhdCI6MTY4MTMwNzgwNCwianRpIjoiYzg0YmYxMDctMzFiZC00ZjE1LTk5M2EtMTc0N2Y0YzhlYzdiIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgxL2F1dGgvcmVhbG1zL3p5dGVyIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImM0MTA5ZTBlLWEzZTgtNDlhZS1hZjlhLWE5NjM4NjA5ODFkNyIsInR5cCI6IkJlYXJlciIsImF6cCI6Inp5dGVyLWFwaS1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiNGYxN2JmZTAtOGNiNi00ODYyLThkZDUtNDEwY2NhMzg1NWIwIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy16eXRlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoiZW1haWwgcHJvZmlsZSIsInNpZCI6IjRmMTdiZmUwLThjYjYtNDg2Mi04ZGQ1LTQxMGNjYTM4NTViMCIsInRlbmFudF9pZCI6IkFudGhlbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJuYXJheWFuIiwidGVuYW50X2luZGV4IjoiMCIsImVtYWlsIjoibmFyYXlhbkB4eXoifQ.uPUdK3TwSFVTWhuFLXGDt2YdDU36ADC3jwA9Aql2cjvoFJwSZxAX-tJCRm9F1ulq5oORGal5q48cCV0IwUtz4GL5sepyuQCWDM1dXUGZYlthuWPmIx4UCn5ArGTIjLvyMCsuxCtfEnA8lVTx7ncZrQq0mZIVkWFb9aFQpU6Kpf0PWFHzGaiq1J1VbWfq9_7w4mo7ul9o1M1r8ZGgHVkStC8yjvpyZTWRyi_3gT4C0AHl6myrodvpyLqYX0tkiH5pJEHRrsHcuByrHh2dliaFDj_t-IxOHBKFyTE0bE5Dt7aYPSsGMnEhA2HPSR8tLw0SpN3VZqAIyqKa3N93rMjaPw";
    	String url = omniChannelEmail+"/bulk" + "/" +method;
    	HttpHeaders headers = getJwtToken(auth);
    	HttpEntity<String> entity = new HttpEntity<String>(headers);
    	//authToken = getToken(null);
    	RestTemplate restTemplate = new RestTemplate();
    	ResponseEntity<T> responseEntity = null;
    	try{
    		responseEntity = restTemplate.exchange("http://localhost:8090/api/v2/email/bulk/campaignHistory?careManager=Dr Nefario&page=0&size=6&mode=SMS",HttpMethod.GET,entity,cls);
    	}catch(Exception e) {
    		log.error("",e);
   
    	}
    	
		return responseEntity;
    	
    }
    
    public String getToken(HttpServletRequest request) {
    	return this.resolver.resolve(request);
    	}
    
    public HttpHeaders getJwtToken(String jwtToken) {
    	
    	HttpHeaders headers = new HttpHeaders();
    	headers.add("Authorization", jwtToken);
		return headers;
    	
    }

}
