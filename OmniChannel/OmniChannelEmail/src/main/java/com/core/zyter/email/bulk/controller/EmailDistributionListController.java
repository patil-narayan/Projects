package com.core.zyter.email.bulk.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.bulk.entities.DistributionList;
import com.core.zyter.email.bulk.service.EmailDistributionListService;
import com.core.zyter.email.bulk.vos.EmailBulkMessageResponse;
import com.core.zyter.email.bulk.vos.EmailDistributionListResponse;
import com.core.zyter.email.bulk.vos.EmailDistributionLists;
import com.core.zyter.email.bulk.vos.DistributionListRequest;
import com.core.zyter.email.util.Constants;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v2/email/bulk")
@SecurityRequirement(name = Constants.BEARER_AUTH_HEADER)
public class EmailDistributionListController {

	@Autowired
	EmailDistributionListService emaildistributionListService;

	@PostMapping(value = "/distributionList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ResponseEntity<DistributionList> uploadDistributionList(@RequestParam(required = false, name = "clientOffset") Long clientOffset,
			@RequestBody DistributionListRequest distributionListRequest)
			throws IOException, OmnichannelException {

		return ResponseEntity.ok()
				.body(emaildistributionListService.uploadDistributionList(distributionListRequest));

	}

	@GetMapping(value = "/distributionList")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<EmailDistributionLists> getDistributionList(
			@RequestParam(required = false, name = "page") Integer page,
			@RequestParam(required = false, name = "size") Integer size,
			@RequestParam(required = false, name = "clientOffset") Long clientOffset) throws OmnichannelException {

		return ResponseEntity.ok().body(emaildistributionListService.getDistributionList(page, size,clientOffset));
	}

	@GetMapping(value = "/distributionList/{id}")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<List<EmailDistributionListResponse>> downloadDistributionList(@PathVariable("id") Long id)
			throws OmnichannelException, IOException, CsvException {

		List<EmailDistributionListResponse> resource = emaildistributionListService.downloadDistributionList(id);

		return ResponseEntity.ok().body(resource);

	}

	@DeleteMapping(value = "/distributionList/{id}")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<EmailBulkMessageResponse> deleteDistributionList(@PathVariable("id") Long id)
			throws OmnichannelException {

		DistributionList distributionList = emaildistributionListService.deleteDistributionList(id);

		return ResponseEntity.ok().body(EmailBulkMessageResponse.builder()
				.status(distributionList.getName() + ": DistributionList deleted Successfully").build());

	}

}
