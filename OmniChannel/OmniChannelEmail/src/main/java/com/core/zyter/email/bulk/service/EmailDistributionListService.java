package com.core.zyter.email.bulk.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.core.zyter.email.Exceptions.OmnichannelException;
import com.core.zyter.email.bulk.entities.DistributionList;
import com.core.zyter.email.bulk.vos.EmailDistributionListResponse;
import com.core.zyter.email.bulk.vos.EmailDistributionLists;
import com.core.zyter.email.bulk.vos.DistributionListRequest;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

public interface EmailDistributionListService {
    DistributionList uploadDistributionList(DistributionListRequest uploadDistributionListPram) throws IOException, OmnichannelException;

    EmailDistributionLists getDistributionList(Integer page, Integer size,Long clientOffset) throws OmnichannelException;
    DistributionList deleteDistributionList(Long id) throws OmnichannelException;

    List<EmailDistributionListResponse> downloadDistributionList(Long id) throws OmnichannelException, FileNotFoundException, IOException, CsvValidationException, CsvException;

}
