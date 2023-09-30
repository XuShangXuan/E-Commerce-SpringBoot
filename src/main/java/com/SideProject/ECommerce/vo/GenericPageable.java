package com.SideProject.ECommerce.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenericPageable {

	private int currentPage;

	private int pageTotalCount;

	private int startPage;

	private int endPage;

	private int showPageSize;
	
	private int showDataCount;
	
	private int dataTotalCount;

}
