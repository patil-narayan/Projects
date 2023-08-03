package com.core.zyter.bulk.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.core.zyter.bulk.entites.Attribute;
import com.core.zyter.bulk.entites.BulkTemplate;
import com.core.zyter.bulk.service.OmniChannelAttributeService;
import com.core.zyter.bulk.vos.BulkMessage;
import com.core.zyter.bulk.vos.BulkMessageResponse;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.util.Constants;
import com.opencsv.exceptions.CsvException;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v1/bulk/")
@SecurityRequirement(name = Constants.BEARER_AUTH_HEADER)
public class OmniChannelAttributeController {
	
@Autowired
OmniChannelAttributeService omniChannelAttributeService;

@PostMapping("/attribute")
public ResponseEntity<Attribute> createRecord(@RequestBody Attribute Attribute) throws OmnichannelException
         {

    return ResponseEntity.ok().body(omniChannelAttributeService.createAttribute(Attribute));

}

@GetMapping("/attribute")
public ResponseEntity<List<Attribute>> getRecord() throws OmnichannelException
         {

    return ResponseEntity.ok().body(omniChannelAttributeService.getAttributes());

}

@RequestMapping(value = "/attribute/{id}", method = RequestMethod.PUT)
@ResponseBody
@ResponseStatus(value = HttpStatus.OK)
public ResponseEntity<Attribute> updateRecord(@PathVariable("id")  Long Id,
        @RequestBody Attribute attribute) throws OmnichannelException
         {
	attribute.setId(Id);
	Attribute updated = omniChannelAttributeService.updateAttribute(attribute);
    return new ResponseEntity<>(updated, HttpStatus.OK);


}

@RequestMapping(value = "/attribute/{id}", method = RequestMethod.DELETE)
@ResponseBody
@ResponseStatus(value = HttpStatus.OK)
public ResponseEntity<String> deleteRecord(@PathVariable("id")  Long Id) throws OmnichannelException{
	Attribute attribute = omniChannelAttributeService.deleteAttribute(Id);
    return new ResponseEntity<>("ID:"+ attribute.getId()+" : Attribute successfully deleted!", HttpStatus.OK);
}

}
