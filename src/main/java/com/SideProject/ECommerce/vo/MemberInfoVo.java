package com.SideProject.ECommerce.vo;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Builder
@Data
@ToString
public class MemberInfoVo {
	
	private Boolean isLogin;
	
	private String loginMessage;
	
	private String identificationNo;
	
	private String cusName;
	
	private String cusPassword;
	
}
