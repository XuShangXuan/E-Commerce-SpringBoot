package com.SideProject.ECommerce.vo;

import java.util.List;

import com.SideProject.ECommerce.entity.GoodsSalesReportMapping;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoodsSalesReportInfo {

	List<GoodsSalesReportMapping> goodsSalesDatas;
	
	private GenericPageable genericPageable;
	
}
