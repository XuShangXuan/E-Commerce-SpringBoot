package com.SideProject.ECommerce.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ShoppingCartGoodsVo {

	private long goodsID;
	private String goodsName;
	private String description;
	private long price;
	private long quantity;
	private String imageName;
	
}
