package com.SideProject.ECommerce.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@SqlResultSetMapping(
    name = "GoodsSalesReportMapping",
    entities={
        @EntityResult(
	        entityClass = com.SideProject.ECommerce.entity.GoodsSalesReportMapping.class,
	        fields = {
	        	@FieldResult(name="orderID", column="ORDER_ID"),
	            @FieldResult(name="orderDate",  column="ORDER_DATE"),
	        	@FieldResult(name="customerID",  column="CUSTOMER_ID"),
	            @FieldResult(name="customerName",  column="CUSTOMER_NAME"),
	            @FieldResult(name="goodsName",  column="GOODS_NAME"),
	            @FieldResult(name="goodsBuyPrice",  column="GOODS_BUY_PRICE"),
	        	@FieldResult(name="buyQuantity",  column="BUY_QUANTITY"),
	        	@FieldResult(name="buyAmount",  column="BUY_AMOUNT")
	        }
        )
    }
) 

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class GoodsSalesReportMapping {
	
	@Id
	private long orderID;
	
	private LocalDateTime orderDate;
	
	private String customerID;
	
	private String customerName;
	
	private String goodsName;
	
	private long goodsBuyPrice;
	
	private long buyQuantity;
	
	private long buyAmount;
	
}
