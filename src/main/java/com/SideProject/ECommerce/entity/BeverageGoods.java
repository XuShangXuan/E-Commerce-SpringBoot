package com.SideProject.ECommerce.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = {"goodsID"})
@ToString(exclude = {"beverageOrders"})
@Entity
@Table(name = "BEVERAGE_GOODS", schema="LOCAL")
public class BeverageGoods {
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GOODS_ID_SEQ_GEN")
    @SequenceGenerator(name = "GOODS_ID_SEQ_GEN", sequenceName = "BEVERAGE_GOODS_SEQ", allocationSize = 1)
	@Column(name = "GOODS_ID")
	private long goodsID;
	
	@Column(name = "GOODS_NAME")
	private String goodsName;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	@Column(name = "PRICE")
	private long price;
	
	@Column(name = "QUANTITY")
	private long quantity;
	
	@Column(name = "IMAGE_NAME")
	private String imageName;
	
	@Column(name = "STATUS")
	private String status;
	
	/*
		CascadeType.PERSIST	  在儲存時一併"儲存"被參考的物件(更新)
		CascadeType.MERGE	  在合併修改時一併"合併"修改被參考的物件(新增)
		CascadeType.REMOVE	  在移除時一併"移除"被參考的物件。(刪除父項時連同子項一併刪除)
		CascadeType.REFRESH	  在更新時一併"更新"被參考的物件。(也是更新)
		CascadeType.DETACH   物件脫離persistence context管理
		CascadeType.ALL		   無論儲存、合併、更新、移除，一併對被參考物件作出對應動作。
		orphanRemoval = true 針對未被參照到的子項資料刪除(搭配 CascadeType.ALL)
	*/
	
	@JsonIgnore //被標註的屬性不會被轉換為 JSON，避免聯集查詢被動觸發「遞迴」查尋的問題(java.lang.StackOverflowError)
	// 雙向一對多關係(物件雙方各自都擁有對方的實體參照，雙方皆意識到對方的存在)
	@OneToMany(
		//透過mappedBy來連結雙向之間的關係，所對映的物件欄位名稱(除非關係是「單向」的，否則此參數「必須」)
		//有mappedBy這個參數，就「不用」@JoinColumn
		//字串內容寫使用多的哪一方的屬性名稱
		mappedBy = "beverageGoods",
		
		cascade = {CascadeType.ALL}, 
		orphanRemoval = true,//當某個主實體（或父對象）中的子實體被從關聯集合中移除時，也會被從資料庫中刪除
		
		//LAZY延遲載入(只在用到時才載入關聯的物件，也就是有用到才查多的那一方，預設就是LAZY，另一方就會預設及時載入)
		//當設置為延遲加載時，當獲取實體A(.getA)時，相關的實體B不會立即加載到內存中。只有在首次訪問實體B時，系統才會去數據庫中加載這些實體。
		fetch = FetchType.LAZY
	)
	@OrderBy(value = "orderID")
	private List<BeverageOrder> beverageOrders;
	
}
