package com.SideProject.ECommerce.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.SideProject.ECommerce.dao.GoodsInfoDao;
import com.SideProject.ECommerce.entity.BeverageGoods;
import com.SideProject.ECommerce.entity.BeverageOrder;
import com.SideProject.ECommerce.vo.GoodsOrderVo;
import com.SideProject.ECommerce.vo.OrderVo;

@Service
public class GoodsOrderService {
	
	private static Logger logger = LoggerFactory.getLogger(GoodsOrderService.class);
	
	@Autowired
	GoodsInfoDao goodsInfoDao;

	public BeverageGoods createGoodsOrder(GoodsOrderVo goodsOrderVo) {
		
		BeverageGoods goods = BeverageGoods.builder()
				.goodsName(goodsOrderVo.getGoodsName())
				.description(goodsOrderVo.getDescription())
				.price(goodsOrderVo.getPrice())
				.quantity(goodsOrderVo.getQuantity())
				.imageName(goodsOrderVo.getImageName())
				.status(goodsOrderVo.getStatus())
				.build();

		List<BeverageOrder> beverageOrders = new ArrayList();
		for (OrderVo orderVo : goodsOrderVo.getOrderVos()) {
			
			BeverageOrder beverageOrder = BeverageOrder.builder()
					.orderDate(orderVo.getOrderDate())
					.customerID(orderVo.getCustomerID())
					.goodsBuyPrice(orderVo.getGoodsBuyPrice())
					.buyQuantity(orderVo.getBuyQuantity())
					.beverageGoods(goods)
					.build();
			
			beverageOrders.add(beverageOrder);
		}
		goods.setBeverageOrders(beverageOrders);
		
		logger.info("即將新增的商品:" + goods);
		logger.info("即將新增的訂單:" + goods.getBeverageOrders());
		
		return goodsInfoDao.save(goods);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public BeverageGoods updateGoodsOrder(GoodsOrderVo goodsOrderVo) {
		
		BeverageGoods dbGoods = null;
		
		Optional<BeverageGoods> optBeverageGoods = goodsInfoDao.findById(goodsOrderVo.getGoodsID());
		
		if (optBeverageGoods.isPresent()) { // 確定DB原本有無此商品
			dbGoods = optBeverageGoods.get();//轉一手(但記憶體位置是相同，所以同樣是DB的物件)將原本DB中商品的資料存進空的dbGoods
			List<BeverageOrder> newOrders = new ArrayList<>();
			List<BeverageOrder> dbOrders = dbGoods.getBeverageOrders(); //取得原本DB中是此商品的訂單
			
			//轉一手將從「外部」傳入的goodsOrderVo的新的商店訂單存進空的newStoreInfos
			for(OrderVo orderVo : goodsOrderVo.getOrderVos()){ //取得將從「外部」傳入的每一筆新的訂單資訊
				BeverageOrder order = BeverageOrder.builder()
						.orderID(orderVo.getOrderID()).build();
				
				//判斷DB中原本有無該訂單(靠物件的HashCode)，如果有該訂單，JPA就知道是要做更新
				if(dbOrders.contains(order)) {
					order = dbOrders.get(dbOrders.indexOf(order));//將原本DB中的｢該訂單」的訂單資訊(該訂單的所有欄位)先全部取出
				}
				order.setOrderDate((orderVo.getOrderDate() != null) ? orderVo.getOrderDate() : order.getOrderDate());
				order.setCustomerID((orderVo.getCustomerID() != null) ? orderVo.getCustomerID() : order.getCustomerID());
				order.setGoodsBuyPrice((orderVo.getGoodsBuyPrice() > 0) ? orderVo.getGoodsBuyPrice() : order.getGoodsBuyPrice());
				order.setBuyQuantity((orderVo.getBuyQuantity() > 0) ? orderVo.getBuyQuantity() : order.getBuyQuantity());
				order.setBeverageGoods(dbGoods);
				
				newOrders.add(order);
			}
			dbOrders.clear();//先將DB原本屬於商品訂單的舊資料給清掉，@OneToMany那邊要加orphanRemoval才會將未參照到的商店從DB中刪除
			dbOrders.addAll(newOrders);
		}
		
		dbGoods.setGoodsName((goodsOrderVo.getGoodsName() != null) ? goodsOrderVo.getGoodsName() : dbGoods.getGoodsName());
		dbGoods.setDescription((goodsOrderVo.getDescription() != null) ? goodsOrderVo.getDescription() : dbGoods.getDescription());
		dbGoods.setPrice((goodsOrderVo.getPrice() > 0) ? goodsOrderVo.getPrice() : dbGoods.getPrice());
		dbGoods.setQuantity((goodsOrderVo.getQuantity() > 0) ? goodsOrderVo.getQuantity() : dbGoods.getQuantity());
		dbGoods.setStatus((goodsOrderVo.getStatus() != null) ? goodsOrderVo.getStatus() : dbGoods.getStatus());
		dbGoods.setImageName((goodsOrderVo.getImageName() != null) ? goodsOrderVo.getImageName() : dbGoods.getImageName());
		
		return dbGoods;
	}
	
}
