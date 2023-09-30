package com.SideProject.ECommerce.vo;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GoodsVo {
	
	private long goodsID;
	
	private String goodsName;
	
	private String description;
	
	private long price;
	
	private long quantity;
	
	private MultipartFile file;
	
	private String imageName;
	
	private String status;
	
}
