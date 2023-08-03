/*
 * @WhiteListedOauth2ProvidersRepository.java@
 * Created on 10Mar2023
 *
 * Copyright (c) 2023 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.dao;

import com.core.zyter.entities.WhiteListedOauth2Providers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhiteListedOauth2ProvidersRepository extends JpaRepository<WhiteListedOauth2Providers, Integer>{

	List<WhiteListedOauth2Providers> getWhiteListedOauth2ProvidersByTenantIdAndTenantIndex(String tenantId,String tenantIndex);
}
