package com.SideProject.ECommerce.vo;

import java.util.List;

import com.SideProject.ECommerce.entity.BeverageGoods;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoodsDataInfo {
	
	private List<BeverageGoods> goodsDatas;
	
	private GenericPageable genericPageable;
	
}
