package com.SideProject.ECommerce.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderCustomer {
	
	//收件人姓名
	private String cusName;
	
	//電話
	private String telNo;
	
	//手機
	private String phoneNo;
	
	//收件地址
	private String recipientAdd;
	
}
