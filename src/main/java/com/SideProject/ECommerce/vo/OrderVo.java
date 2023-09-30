package com.SideProject.ECommerce.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OrderVo {

	private long orderID;
	
	private LocalDateTime orderDate;

	private String customerID;

	private long goodsID;

	private long goodsBuyPrice;

	private long buyQuantity;
	
}
