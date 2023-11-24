package com.SideProject.ECommerce.vo;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenericPageable {

	private int currentPage;

	private int pageTotalCount;

	private int startPage;

	private int endPage;
	
	private List<Integer> pageRange;

	private int showPageSize;
	
	private int showDataCount;
	
	private int dataTotalCount;

}
