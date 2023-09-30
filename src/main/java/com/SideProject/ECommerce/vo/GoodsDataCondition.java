package com.SideProject.ECommerce.vo;

import java.util.List;

import com.SideProject.ECommerce.entity.BeverageGoods;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoodsDataCondition {
	
	//private List<BeverageGoods> goodsData;
//	private Integer goodsTotalCounts;
//	private GenericPageable genericPageable;
	private Long goodsID;
	private String goodsNameKeyword;
	private Integer minPrice;
	private Integer maxPrice;
	private String priceSort;
	private Integer stock;
	private String status;
	private Integer startRowNo;
	private Integer endRowNo;
	
}
