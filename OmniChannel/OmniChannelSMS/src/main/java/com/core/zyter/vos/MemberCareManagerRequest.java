package com.core.zyter.vos;

import com.core.zyter.entites.UserMaster;
import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCareManagerRequest {
	UserMaster careManager;
	UserMaster member;

}
