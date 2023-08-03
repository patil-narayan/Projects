package com.core.zyter.email.bulk.service.impl;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
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

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.bulk.dao.BulkEmailDistributionListRepository;
import com.core.zyter.email.bulk.entities.DistributionList;
import com.core.zyter.email.bulk.service.EmailDistributionListService;
import com.core.zyter.email.bulk.vos.EmailDistributionListResponse;
import com.core.zyter.email.bulk.vos.EmailDistributionLists;
import com.core.zyter.email.bulk.vos.DistributionListRequest;
import com.core.zyter.email.util.ConfigProperties;
import com.core.zyter.email.util.Constants;
import com.core.zyter.email.util.DateTimeUtil;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailDistributionServiceImpl implements EmailDistributionListService {

	private final String DISTRIBUTION_LIST_FOLDER = "/distributionList/";
	
	@Autowired
	BulkEmailDistributionListRepository bulkEmailDistributionListRepository;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	ConfigProperties configProperties;

	@Override
	public DistributionList uploadDistributionList(DistributionListRequest distributionListRequest)
			throws IOException, OmnichannelException {
		
    	if (null != bulkEmailDistributionListRepository.findByNameAndType(distributionListRequest.getName(),distributionListRequest.getType())) {
            throw new OmnichannelException("Distribution list Title already exists, please enter different name",
                    Constants.FAILURE, HttpStatus.BAD_REQUEST);  
        }
		
		if (!distributionListRequest.getFile().getOriginalFilename().matches(Constants.CSV_REGEX)) {
			log.error("Invalid File format, please upload csv file");
			throw new OmnichannelException("Invalid file format, upload csv file format", Constants.FAILURE,
					HttpStatus.BAD_REQUEST);
		}
		if (!distributionListRequest.getFile().getContentType().equalsIgnoreCase(Constants.MIME_TYPE_CSV)) {
			throw new OmnichannelException("Invalid file format, upload csv file format", Constants.FAILURE,
					HttpStatus.BAD_REQUEST);
		}
		if (!isTemplateValid(distributionListRequest.getFile())) {
			log.error("Invalid file template, upload csv file with right format");
			throw new OmnichannelException("Invalid file template, upload csv file with right format",
					Constants.FAILURE, HttpStatus.BAD_REQUEST);
		}
		if( Constants.MAX_FILE_SIZE_IN_KB < distributionListRequest.getFile().getSize() ) {
        	throw new OmnichannelException("Maximum upload size exceeded or size of .csv file more than 2kb",
        			Constants.FAILURE, HttpStatus.BAD_REQUEST);
        }
		String fileName = distributionListRequest.getFile().getOriginalFilename();
		fileName = (fileName.substring(0, fileName.lastIndexOf(".")) + System.currentTimeMillis()).replace(" ", "_")
				+ ".csv";

		String path = configProperties.getUploadFilePath() + DISTRIBUTION_LIST_FOLDER + fileName;
		DistributionList bulkEmailDistributionList = DistributionList.builder().name(distributionListRequest.getName())
				.filePath(path).createdOn(new Date()).type(distributionListRequest.getType()).recordStatus(true).build();

		Path filePath = Paths.get(configProperties.getUploadFilePath() + DISTRIBUTION_LIST_FOLDER + fileName)
				.toAbsolutePath().normalize();
		Files.createDirectories(filePath);
		Files.copy(distributionListRequest.getFile().getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		bulkEmailDistributionListRepository.save(bulkEmailDistributionList);
		return bulkEmailDistributionList;
	}

	@Override
	public EmailDistributionLists getDistributionList(Integer page, Integer size,Long clientOffset) throws OmnichannelException {

		EmailDistributionLists emaildistributionLists;
		List<DistributionList> bulkEmailDistributionList;
		try {

			if (page != null && size != null) {
				Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
				Page<DistributionList> emaildistributionListPage = bulkEmailDistributionListRepository
						.getTemplate(pageable);
				bulkEmailDistributionList = new ArrayList<>(emaildistributionListPage.getContent());
				emaildistributionLists = EmailDistributionLists.builder()
						.distributionListArray(bulkEmailDistributionList)
						.totalPages(emaildistributionListPage.getTotalPages())
						.totalItems(emaildistributionListPage.getTotalElements())
						.currentPage(emaildistributionListPage.getNumber()).size(emaildistributionListPage.getSize())
						.build();
			} else {
				bulkEmailDistributionList = new ArrayList<>(bulkEmailDistributionListRepository.getAllTemplate());
				Collections.reverse(bulkEmailDistributionList);
				emaildistributionLists = EmailDistributionLists.builder()
						.distributionListArray(bulkEmailDistributionList)
						.totalItems(bulkEmailDistributionListRepository.getTotalElements()).build();
			}
			bulkEmailDistributionList.forEach(bulkEmailDistributionLists -> {
				try {
					if (null != clientOffset) {
						long serverOffset = DateTimeUtil.getServerOffset(new Date());
						bulkEmailDistributionLists.setCreatedOn(
								DateTimeUtil.calculateDate(serverOffset, clientOffset, bulkEmailDistributionLists.getCreatedOn()));
					}
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			});
			

		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return emaildistributionLists;
	}

	@Override
	public DistributionList deleteDistributionList(Long id) throws OmnichannelException {
		Optional<DistributionList> distributionList = bulkEmailDistributionListRepository.findById(id);
		if (distributionList.isEmpty()) {
			throw new OmnichannelException("Distribution List Id: " + id + " doest exist", Constants.FAILURE,
					HttpStatus.NOT_FOUND);
		}

		try {
			distributionList.get().setRecordStatus(false);
			bulkEmailDistributionListRepository.save(distributionList.get());
		} catch (Exception e) {
			log.error("", e);
			throw new OmnichannelException(e.getMessage(), Constants.FAILURE, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return distributionList.get();
	}

	@Override
	public List<EmailDistributionListResponse> downloadDistributionList(Long id)
			throws OmnichannelException, IOException, CsvValidationException, CsvException {
		Optional<DistributionList> distributionList = bulkEmailDistributionListRepository.findById(id);
		if (distributionList.isEmpty()) {
			throw new OmnichannelException(String.format("distributionList: %s does not exist", id), Constants.FAILURE,
					HttpStatus.NOT_FOUND);
		}
		CSVReader reader = new CSVReader(new FileReader(distributionList.get().getFilePath()));
		List<EmailDistributionListResponse> emaildistributionListResponse = new ArrayList<>();
		if ((reader.readNext()) != null) {
			List<String[]> records = reader.readAll();
			if (null != records && !records.isEmpty()) {
				records.stream().collect(Collectors.toList()).forEach(record -> {
					emaildistributionListResponse.add(EmailDistributionListResponse.builder().firstName(record[0])
							.lastName(record[1]).dob(record[2]).Email(record[3]).build());
				});
			}
		}
		
		return emaildistributionListResponse;

	}

	boolean isTemplateValid(MultipartFile file) {

		try {
			Reader reader = new InputStreamReader(file.getInputStream());
			CSVReader csvReader = new CSVReaderBuilder(reader).build();
			String[] headerRecord;
			String[] headerArray = { "First Name", "Last Name", "DOB","Email" };

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
