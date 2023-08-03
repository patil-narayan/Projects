/*
 * @WhiteListedOauth2ProvidersRepository.java@
 * Created on 15Dec2022
 *
 * Copyright (c) 2022 Infinite Computer Solutions
 *
 * All Right Reserved.
 * THIS IS UNPUBLISHED PROPRIETARY
 * SOURCE CODE OF Infinite Computer Solutions
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 */

package com.core.zyter.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.core.zyter.entites.WhiteListedOauth2Providers;

@Repository
public interface WhiteListedOauth2ProvidersRepository extends JpaRepository<WhiteListedOauth2Providers, Integer>{

	public List<WhiteListedOauth2Providers> getWhiteListedOauth2ProvidersByTenantIdAndTenantIndex(String tenantId,String tenantIndex);
}
