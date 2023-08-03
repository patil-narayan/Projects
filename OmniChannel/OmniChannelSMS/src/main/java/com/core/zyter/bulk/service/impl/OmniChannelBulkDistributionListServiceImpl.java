/*
 * @OmniChannelBulkServiceImpl.java@
 * Created on 20Jan2023
 *
 * Copyright (c) 2023 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.bulk.service.impl;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.core.zyter.bulk.dao.DistributionListRepository;
import com.core.zyter.bulk.entites.DistributionList;
import com.core.zyter.bulk.service.OmniChannelBulkDistributionListService;
import com.core.zyter.bulk.vos.DistributionListResponse;
import com.core.zyter.bulk.vos.DistributionLists;
import com.core.zyter.exceptions.OmnichannelException;
import com.core.zyter.util.ConfigProperties;
import com.core.zyter.util.Constants;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class OmniChannelBulkDistributionListServiceImpl implements OmniChannelBulkDistributionListService {

    private final String DISTRIBUTION_LIST_FOLDER = "/distributionList/";
    
    @Autowired
    DistributionListRepository distributionListRepository;
    @Autowired
    ConfigProperties configProperties;
    @Autowired
    private HttpServletRequest request;

    @Override
    public DistributionList uploadDistributionList(String name, MultipartFile file, String type)
            throws IOException, OmnichannelException {

    	if (null != distributionListRepository.findByNameAndType(name,type)) {
            throw new OmnichannelException("Distribution list Title already exists, please enter different name",
                    Constants.FAILURE, HttpStatus.BAD_REQUEST);  
        }
    	
        if (!file.getOriginalFilename().matches(Constants.CSV_REGEX)) {
            log.error("Invalid File format, please upload csv file");
            throw new OmnichannelException("Invalid file format, upload csv file format",
                    Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }
        if (!file.getContentType().equalsIgnoreCase(Constants.MIME_TYPE_CSV)) {
            throw new OmnichannelException("Invalid file format, upload csv file format",
                    Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }
        if (!isTemplateValid(file)) {
            log.error("Invalid file template, upload csv file with right format");
            throw new OmnichannelException("Invalid file template, upload csv file with right format",
                    Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }
        String fileName = file.getOriginalFilename();
        fileName = (fileName.substring(0, fileName.lastIndexOf(".")) +
                System.currentTimeMillis()).replace(" ", "_") + ".csv";
        if(Constants.MAX_FILE_SIZE_IN_KB < file.getSize()) {
        	throw new OmnichannelException("Maximum upload size exceeded or size of .csv file more than 2kb",
        			Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }
        String path = configProperties.getUploadFilePath() + DISTRIBUTION_LIST_FOLDER + fileName;
        DistributionList distribution = DistributionList.builder()
                .name(name)
                .filePath(path)
                .createdOn(new Date()).type(type).recordStatus(true).build();

        Path filePath = Paths.get(configProperties.getUploadFilePath() + DISTRIBUTION_LIST_FOLDER +
                fileName).toAbsolutePath().normalize();
        Files.createDirectories(filePath);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        distributionListRepository.save(distribution);
        return distribution;
    }

    @Override
    public DistributionLists getDistributionList(Integer page, Integer size,String type) throws OmnichannelException {

		DistributionLists distributionLists;
		List<DistributionList>	listOfDistribution;
		try {

			if (page != null && size != null) {
				Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
				Page<DistributionList> distributionListPage = distributionListRepository.getTemplate(pageable);
				listOfDistribution = new ArrayList<>(distributionListPage.getContent());
				if(type !=null) {
					Page<DistributionList> pageBytype = distributionListRepository.getTemplateAndType(pageable,type);
					listOfDistribution 	 = new ArrayList<>(pageBytype.getContent());
					distributionLists = DistributionLists.builder().distributionListArray(listOfDistribution)
							.totalPages(pageBytype.getTotalPages())
							.totalItems(pageBytype.getTotalElements())
							.currentPage(pageBytype.getNumber()).size(pageBytype.getSize()).build();
				}else {
				distributionLists = DistributionLists.builder().distributionListArray(listOfDistribution)
						.totalPages(distributionListPage.getTotalPages())
						.totalItems(distributionListPage.getTotalElements())
						.currentPage(distributionListPage.getNumber()).size(distributionListPage.getSize()).build();
				}
			} else {
			listOfDistribution= new ArrayList<>(distributionListRepository.getAllTemplate());
			Collections.reverse(listOfDistribution);
			distributionLists = DistributionLists.builder().distributionListArray(listOfDistribution)
					.totalItems(distributionListRepository.count()).build();
			}

        } catch (Exception e) {
            log.error("", e);
            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return distributionLists;
    }

    @Override
    public DistributionList deleteDistributionList(Long id) throws OmnichannelException {
        Optional<DistributionList> distributionList = distributionListRepository.findById(id);
        if (distributionList.isEmpty()) {
            throw new OmnichannelException("Distribution List Id: " + id + " doest exist", Constants.FAILURE, HttpStatus.NOT_FOUND);
        }

        try {
        	distributionList.get().setRecordStatus(false);
            distributionListRepository.save(distributionList.get());
        } catch (Exception e) {
            log.error("", e);
            throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return distributionList.get();
    }

    @Override
    public List<DistributionListResponse> downloadDistributionList(Long id) throws OmnichannelException, IOException, CsvValidationException, CsvException {
        Optional<DistributionList> distributionList = distributionListRepository.findById(id);
        if (distributionList.isEmpty()) {
            throw new OmnichannelException(String.format("distributionList: %s does not exist", id),
                    Constants.FAILURE, HttpStatus.NOT_FOUND);
        }
        CSVReader reader = new CSVReader(new FileReader(distributionList.get().getFilePath()));
        List<DistributionListResponse> distributionListResponse = new ArrayList<>()  ;
		if ((reader.readNext()) != null) {
			List<String[]> records = reader.readAll();
			if (null != records && !records.isEmpty()) {
				records.stream().collect(Collectors.toList())
				.forEach(record -> {
					distributionListResponse.add(
							DistributionListResponse.builder()
							.firstName(record[0])
							.lastName(record[1])
							.dob(record[2])
							.phoneNumber(record[3])
							.build());
				});
			}	
		}
	return distributionListResponse;
       
    }

    boolean isTemplateValid(MultipartFile file) {

        try {
            Reader reader = new InputStreamReader(file.getInputStream());
            CSVReader csvReader = new CSVReaderBuilder(reader).build();
            String[] headerRecord;
            String[] headerArray = {"First Name", "Last Name", "DOB", "Phonenumber"};

            if (null == (headerRecord = csvReader.readNext())) {
                return false;
            }
            if (headerArray.length != headerRecord.length) {
                return false;
            }
            int index = 0;
            for (String header : headerRecord) {
                if (!header.equalsIgnoreCase(headerArray[index])) {
                    return false;
                }
                index++;
            }

        } catch (Exception e) {
            log.error("Exception occurred while paring csv file:{}", file, e);
            return false;
        }
        return true;
    }
}
