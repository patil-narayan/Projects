/*
 * @OmniChannelBulkService.java@
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

package com.core.zyter.bulk.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.core.zyter.bulk.entites.DistributionList;
import com.core.zyter.bulk.vos.DistributionListResponse;
import com.core.zyter.bulk.vos.DistributionLists;
import com.core.zyter.exceptions.OmnichannelException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

public interface OmniChannelBulkDistributionListService {

    DistributionList uploadDistributionList(String name, MultipartFile file, String type) throws IOException, OmnichannelException;

    DistributionList deleteDistributionList(Long id) throws OmnichannelException;

    List<DistributionListResponse> downloadDistributionList(Long id) throws OmnichannelException, FileNotFoundException, IOException, CsvValidationException, CsvException;

	DistributionLists getDistributionList(Integer page, Integer size, String type) throws OmnichannelException;
}
