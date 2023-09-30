package com.SideProject.ECommerce.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

//@Builder //建構者模式將所有的建構子設為private因此無法直接new預設建構子(空參數)
//這兩個為一組，提供建構者模式的功能也能使用預設建構子(空參數)
@SuperBuilder
@NoArgsConstructor

@Data
@ToString
@EqualsAndHashCode(of = {"orderID"})

//這兩個為一組，提供DB參照table
@Entity
@Table(name = "BEVERAGE_ORDER", schema="LOCAL")
public class BeverageOrder {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER_ID_SEQ_GEN")
    @SequenceGenerator(name = "ORDER_ID_SEQ_GEN", sequenceName = "BEVERAGE_ORDER_SEQ", allocationSize = 1)
	@Column(name = "ORDER_ID")
	private long orderID;
	
	@Column(name = "ORDER_DATE")
	private LocalDateTime orderDate;
	
	@Column(name = "GOODS_BUY_PRICE")
	private long goodsBuyPrice;
	
	@Column(name = "BUY_QUANTITY")
	private long buyQuantity;
	
	//EAGER(在查詢時立刻載入關聯的物件)
	//當設置為急切加載時，當獲取實體A時，相關的實體B會立即加載到內存中，不論是否真正需要使用它們。
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "GOODS_ID")
	private BeverageGoods beverageGoods; //查詢使用這個欄位
	
	/*
	當使用JPA配置實體時，如果有兩個屬性（一個是一般屬性，一個是多對一的屬性）映射到數據庫的同一列，就會報錯。
	org.hibernate.MappingException: Repeated column in mapping for entity:
	com.training.jpa.oracle.entity.StoreInfo column: 
	customer_id (should be mapped with insert="false" update="false")
	*/
	@Column(name = "CUSTOMER_ID")
	private String customerID;
	
	/*
	   customerID、beverageMember 都映射到資料庫裏面的 CUSTOMER_ID 欄位
	       加上了 insertable = false, updatable = false
	       在新增和修改時，beverageMember 就不會被儲存到數據庫。
	*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CUSTOMER_ID", insertable = false, updatable = false)
	private BeverageMember beverageMember; //查詢使用這個欄位
	
}
