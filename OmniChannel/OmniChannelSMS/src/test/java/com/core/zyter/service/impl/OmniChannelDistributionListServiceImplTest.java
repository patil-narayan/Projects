package com.core.zyter.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import com.core.zyter.bulk.dao.DistributionListRepository;
import com.core.zyter.bulk.entites.DistributionList;
import com.core.zyter.bulk.service.impl.OmniChannelBulkDistributionListServiceImpl;
import com.core.zyter.bulk.vos.DistributionListResponse;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.util.Constants;
import com.opencsv.exceptions.CsvException;

public class OmniChannelDistributionListServiceImplTest {

	@Mock
	DistributionListRepository distributionListRepository;
	@InjectMocks
	OmniChannelBulkDistributionListServiceImpl omniChannelBulkDistributionListServiceImpl;
	
	
	 @BeforeEach
	    void setup() {
	        MockitoAnnotations.openMocks(this);
	 }
	 
	 @Test
	    public void testDeleteDistriutionList() throws Exception {  
		 DistributionList distributionList= new DistributionList();
		 when(distributionListRepository.findById(distributionList.getId())).thenReturn(Optional.of(distributionList));
		 distributionList.setRecordStatus(false);
		 when(distributionListRepository.save(distributionList)).thenReturn(distributionList);
		 DistributionList deleteDistributionList = omniChannelBulkDistributionListServiceImpl.deleteDistributionList(distributionList.getId());
		 assertFalse(deleteDistributionList.isRecordStatus());
		 verify(distributionListRepository, times(1)).save(distributionList);
		 }
	 
	 @Test 

	 void testDeleteDistriutionListMissingId() throws OmnichannelException, IOException, CsvException { 

		 DistributionList distributionList= new DistributionList();
	 when(distributionListRepository.findById(distributionList.getId())).thenReturn(Optional.empty()); 

		 OmnichannelException exception = assertThrows(OmnichannelException.class, () -> {
			 omniChannelBulkDistributionListServiceImpl.deleteDistributionList(distributionList.getId());
			});

	 assertEquals(String.format("Distribution List Id: %s doest exist", distributionList.getId()), exception.getMessage()); 

	 assertEquals(Constants.FAILURE, exception.getStatus()); 

	 assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
	 } 

	 @BeforeEach 

	 void setUp() { 

	 MockitoAnnotations.openMocks(this); 
	 } 

	   
	 
	 @Test
	 void testDownloadDistributionLists() throws OmnichannelException, IOException, CsvException { 
		
		 DistributionList distributionList = new DistributionList(); 
		 long id=12L;
		 String  filePath="../OmniChannelSMS/src/test/resources/DistributionList Template/user.csv";
		 distributionList.setId(id);
		 distributionList.setFilePath(filePath);
		 when(distributionListRepository.save(distributionList)).thenReturn((distributionList));

	 Mockito.when(distributionListRepository.findById(id)).thenReturn(Optional.of(distributionList));
		List<DistributionListResponse> distributionLists = omniChannelBulkDistributionListServiceImpl.downloadDistributionList(id);
		assertEquals("sharadhi", distributionLists.get(0).getFirstName());
		assertEquals("lokesh", distributionLists.get(0).getLastName());
		assertEquals("05/02/2000",distributionLists.get(0).getDob());
	 
 }
	  

	 @Test 

	 void testDownloadDistributionListNotFound() throws OmnichannelException, IOException, CsvException { 

		 DistributionList distributionList= new DistributionList();
		 when(distributionListRepository.findById(distributionList.getId())).thenReturn(Optional.empty()); 

			 OmnichannelException exception = assertThrows(OmnichannelException.class, () -> {
				 omniChannelBulkDistributionListServiceImpl.downloadDistributionList(distributionList.getId());
				});

		 assertEquals(String.format("distributionList: %s does not exist", distributionList.getId()), exception.getMessage()); 

		 assertEquals(Constants.FAILURE, exception.getStatus()); 

		 assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
		 } 

}


