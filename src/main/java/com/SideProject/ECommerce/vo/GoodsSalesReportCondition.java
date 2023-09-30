package com.SideProject.ECommerce.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GoodsSalesReportCondition {
	
	private String startDate;
	
	private String endDate;

	private Integer startRowNo;
	
	private Integer endRowNo;
	
}
