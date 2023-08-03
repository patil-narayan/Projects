package com.infinite.omniapigateway.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.infinite.omniapigateway.entites.WhiteListedOauth2Providers;

import reactor.core.publisher.Flux;



public interface WhiteListedOauth2ProvidersRepository extends ReactiveCrudRepository<WhiteListedOauth2Providers, Integer>{
	
	
	@Query("SELECT * FROM oauth2_providers_whitelist where tenant_id = :tenanatId and tenant_index = :tenantIndex")
	public Flux<WhiteListedOauth2Providers> findByTenantIdAndTenantIndex(String tenanatId, String tenantIndex);
}
