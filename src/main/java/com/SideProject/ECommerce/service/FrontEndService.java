package com.SideProject.ECommerce.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.SideProject.ECommerce.dao.GoodsInfoDao;
import com.SideProject.ECommerce.dao.OrderInfoDao;
import com.SideProject.ECommerce.entity.BeverageGoods;
import com.SideProject.ECommerce.entity.BeverageOrder;
import com.SideProject.ECommerce.tool.Page;
import com.SideProject.ECommerce.vo.CheckoutCompleteInfo;
import com.SideProject.ECommerce.vo.GenericPageable;
import com.SideProject.ECommerce.vo.GoodsDataCondition;
import com.SideProject.ECommerce.vo.GoodsDataInfo;
import com.SideProject.ECommerce.vo.MemberInfoVo;
import com.SideProject.ECommerce.vo.OrderCustomer;
import com.SideProject.ECommerce.vo.ShoppingCartGoodsVo;

@Service
public class FrontEndService {
	
	private static Logger logger = LoggerFactory.getLogger(FrontEndService.class);

	@Autowired
	private GoodsInfoDao goodsInfoDao;
	
	@Autowired
	private OrderInfoDao orderInfoDao;
	
	public GoodsDataInfo queryGoodsData(GoodsDataCondition condition, GenericPageable genericPageable) {

		String keyword = null != condition.getGoodsNameKeyword() ? condition.getGoodsNameKeyword() : "";
		final String STATUS = "1";
		
		// 資料總筆數
		int dataTotalCount = goodsInfoDao.countByGoodsNameContainingIgnoreCaseAndStatus(keyword, STATUS);
		
		genericPageable = Page.calculatePageable(dataTotalCount, genericPageable);

		logger.info("計算後的分頁資訊:" + genericPageable);
		
		int currentPage = genericPageable.getCurrentPage();
		int ShowDataCount = genericPageable.getShowDataCount();
		
		// PageRequest.of(int page, int size, Sort sort)
		// page 頁數從零開始代表第一頁
		Pageable pageable = PageRequest.of(currentPage - 1, ShowDataCount, 
			Sort.by("goodsID").ascending()
		);
		
		List<BeverageGoods> goodsDatas = goodsInfoDao.findByGoodsNameContainingIgnoreCaseAndStatus(pageable, keyword, STATUS);
		
		GoodsDataInfo goodsDataInfo = GoodsDataInfo.builder().goodsDatas(goodsDatas).genericPageable(genericPageable).build();
		
		return goodsDataInfo;

	}
	
	/*
	 1.oracleTransactionManager有設@Primary所以可以不寫
	 2.預設只有rollback RuntimeException，Exception要rollback就要手動寫
	 3.預設就是REQUIRED所以可以不寫
	 使用Transactional開啟交易事務，統一使用同一個connection，發生交易錯誤或例外錯誤就統一rollback
	*/
	@Transactional(transactionManager = "oracleTransactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public CheckoutCompleteInfo checkoutGoods(MemberInfoVo memberInfo, OrderCustomer customer, List<ShoppingCartGoodsVo> cartGoods) {
		
		// 只取顧客選的商品ID及選購數量
		Map<Long, Long> newCarGoods = cartGoods.stream()
                .collect(Collectors.toMap(
                        ShoppingCartGoodsVo::getGoodsID,  // 取得ID作為key
                        ShoppingCartGoodsVo::getQuantity, // 取得數量作為value
                        Long::sum)); // 如果有重複的ID，將數量相加
		
		List<ShoppingCartGoodsVo> realCartGoods = new ArrayList();
		List<BeverageGoods> goodsList = new ArrayList();
		List<BeverageOrder> ordersList = new ArrayList();
		
		LocalDateTime currentTime = LocalDateTime.now();
		String customerID = memberInfo.getIdentificationNo();
		
		// 遍歷購物車
		for (Map.Entry<Long, Long> singleCarGoods : newCarGoods.entrySet()) {
			
			Optional<BeverageGoods> optGoods = goodsInfoDao.findById(singleCarGoods.getKey());
			
			if (optGoods.isPresent()) {
			
				BeverageGoods dbGoods = optGoods.get();
				
				// 取得DB中該商品庫存數量
				long dbGoodsQuantity = dbGoods.getQuantity();
		
				// 購買的數量不得超過資料庫中的庫存數量，最多只能買當下的庫存的數量
		        long purchaseQuantity = Math.min(dbGoodsQuantity, singleCarGoods.getValue());
				
		        // 更新後庫存的數量
		        long updateStock = dbGoodsQuantity - purchaseQuantity;
		        
				// 如果能購買的數量小於0或是更新後庫存數量為負數,就不建立該商品的訂單
				if(purchaseQuantity > 0 && updateStock >= 0) {
					
					// 更新商品數量
					// 不直接更新dbGoods以免造成資料庫連動
					BeverageGoods goods = BeverageGoods.builder()
							.goodsID(dbGoods.getGoodsID()).goodsName(dbGoods.getGoodsName())
							.description(dbGoods.getDescription()).price(dbGoods.getPrice())
							.quantity(updateStock).imageName(dbGoods.getImageName())
							.status(dbGoods.getStatus()).beverageOrders(dbGoods.getBeverageOrders())
							.build();
					
					goodsList.add(goods);
					
					// 新增訂單
					long buyPrice = dbGoods.getPrice() * purchaseQuantity;
					
					BeverageOrder order = BeverageOrder.builder()
							.orderDate(currentTime).customerID(customerID)
							.beverageGoods(dbGoods).goodsBuyPrice(buyPrice)
							.buyQuantity(purchaseQuantity).build();
					
					ordersList.add(order);
					
					// 客人購買的商品資訊
					ShoppingCartGoodsVo goodsInfo = ShoppingCartGoodsVo.builder()
							.goodsID(dbGoods.getGoodsID()).goodsName(dbGoods.getGoodsName())
							.description(dbGoods.getDescription()).price(dbGoods.getPrice())
							.quantity(purchaseQuantity).imageName(dbGoods.getImageName())
							.build();
					
					realCartGoods.add(goodsInfo);
					
				}
			}
		}
		
		// 新增和更新數量不能為空，且新增和更新的數量必須一樣
		if(!goodsList.isEmpty() && goodsList.size() == ordersList.size()) {
			
			// Batch Input,Batch Update
			goodsInfoDao.saveAll(goodsList);
			orderInfoDao.saveAll(ordersList);
			
		}
		
		CheckoutCompleteInfo checkoutCompleteInfo = CheckoutCompleteInfo.builder()
				.customer(customer).cartGoodsList(realCartGoods)
				.build();
		
		return checkoutCompleteInfo;
	}
	
}
