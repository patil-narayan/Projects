package com.core.zyter.email.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.entities.EmailTemplate;
import com.core.zyter.email.service.EmailTemplateService;
import com.core.zyter.email.vos.PlaceHolder;
import com.core.zyter.email.vos.TemplateResponse;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/email")
@SecurityRequirement(name = "Bearer Authentication")
public class EmailTemplateController {

	@Autowired
	EmailTemplateService emailTemplateService;

	@GetMapping(value = "/template")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<List<EmailTemplate>> template()
			throws OmnichannelException, IOException	{
		return ResponseEntity.ok().body(emailTemplateService.getTemplates());
	}
	
	@PostMapping(value = "/template/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<TemplateResponse> templates(@PathVariable Long id ,@RequestBody(required = false) PlaceHolder placeHolder) throws OmnichannelException, IOException {
		return ResponseEntity.ok().body(emailTemplateService.getTemplates(id,placeHolder));
	}
	
}
