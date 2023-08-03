package com.core.zyter.email.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.core.zyter.email.entities.MemberChannelOptinStatus;

public interface MemberChannelOptinRepository extends JpaRepository<MemberChannelOptinStatus, Long> {
	
	List<MemberChannelOptinStatus> findByMemberAndChannelType(String member,String channeltype);
}
