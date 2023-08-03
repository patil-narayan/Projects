package com.core.zyter.email.bulk.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.bulk.entities.BulkTemplate;
import com.core.zyter.email.bulk.service.BulkEmailTemplateService;
import com.core.zyter.email.bulk.vos.EmailBulkMessageResponse;
import com.core.zyter.email.bulk.vos.EmailBulkTemplateResponse;
import com.core.zyter.email.bulk.vos.EmailBulkTemplates;
import com.core.zyter.email.util.Constants;
import com.core.zyter.email.vos.PlaceHolder;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/email/bulk")
@SecurityRequirement(name = Constants.BEARER_AUTH_HEADER)
public class EmailBulkTemplateController {

	@Autowired
	BulkEmailTemplateService bulkEmailTemplateService;

	@PostMapping(value = "/template",produces = "application/json")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<BulkTemplate> uploadTemplate(@RequestParam("name") String name,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,@RequestParam("subject") String subject) throws OmnichannelException {
		return ResponseEntity.status(HttpStatus.OK).body(bulkEmailTemplateService.uploadTemplate(name, file,type,subject));
	}


	/*
	 * @PostMapping(value = "/template/{name}", produces = "application/json")
	 * 
	 * @ResponseBody
	 * 
	 * @ResponseStatus(value = HttpStatus.OK) public ResponseEntity<BulkTemplate>
	 * createTemplate(@PathVariable("name") String name,
	 * 
	 * @RequestParam("template") String template, @RequestParam("type") String type)
	 * throws OmnichannelException { return
	 * ResponseEntity.status(HttpStatus.OK).body(bulkEmailTemplateService.
	 * createTemplate(name, template, type)); }
	 */

	@GetMapping(value = "/template",produces = "application/json")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<EmailBulkTemplates> bulkTemplates(@RequestParam(required = false, name = "page") Integer page,
			@RequestParam(required = false, name = "size") Integer size) throws OmnichannelException {
		return ResponseEntity.status(HttpStatus.OK).body(bulkEmailTemplateService.getTemplates(page, size));
	}

	@DeleteMapping(value = "/template/{id}")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<EmailBulkMessageResponse> deleteTemplate(@PathVariable("id") Long id)
			throws OmnichannelException {
		BulkTemplate bulkTemplate = bulkEmailTemplateService.deleteTemplate(id);
		return ResponseEntity.ok().body(EmailBulkMessageResponse.builder()
				.status(bulkTemplate.getName() + ":Email Template deleted Successfully").build());
	}

	@PostMapping(value = "/template/{id}",produces = "application/json")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<EmailBulkTemplateResponse> downloadTemplate(@PathVariable("id") Long id ,@RequestBody(required = false) PlaceHolder placeHolder)
			throws OmnichannelException, IOException {

		return ResponseEntity.ok().body(bulkEmailTemplateService.downloadBulkTemplate(id,placeHolder));

	}

}
