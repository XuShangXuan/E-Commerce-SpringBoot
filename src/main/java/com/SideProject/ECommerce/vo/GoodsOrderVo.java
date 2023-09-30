package com.SideProject.ECommerce.vo;

import java.util.List;
import lombok.Data;

@Data
public class GoodsOrderVo {

	private long goodsID;
	
	private String goodsName;

	private String description;	

	private long price;

	private long quantity;

	private String imageName;

	private String status;
	
	private List<OrderVo> orderVos;
	
}
