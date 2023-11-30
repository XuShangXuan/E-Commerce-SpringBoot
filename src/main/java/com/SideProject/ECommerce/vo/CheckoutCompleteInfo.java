package com.SideProject.ECommerce.vo;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CheckoutCompleteInfo {
	
	private OrderCustomer customer;
	
	private List<ShoppingCartGoodsVo> cartGoodsList;
	
}
