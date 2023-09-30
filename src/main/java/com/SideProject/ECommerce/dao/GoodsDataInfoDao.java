package com.SideProject.ECommerce.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.SideProject.ECommerce.entity.BeverageGoods;
import com.SideProject.ECommerce.vo.GenericPageable;
import com.SideProject.ECommerce.vo.GoodsDataCondition;

@Repository
public class GoodsDataInfoDao {

	@PersistenceContext(name = "oracleEntityManager")
    private EntityManager entityManager;
	
	public List<BeverageGoods> queryGoodsByGoodsDataCondition(GoodsDataCondition condition ,GenericPageable pageable){
		
		String pirceSort = null;
		Long goodsID = null;
		String keyword = null;
		Integer minPrice = null;
		Integer maxPrice = null;
		Integer stock = null;
		String status = null;
		Integer startRowNo = null;
		Integer endRowNo = null;

		if (condition != null) {
			pirceSort = condition.getPriceSort();
			goodsID = condition.getGoodsID();
			keyword = condition.getGoodsNameKeyword();
			minPrice = condition.getMinPrice();
			maxPrice = condition.getMaxPrice();
			stock = condition.getStock();
			status = condition.getStatus();
			startRowNo = condition.getStartRowNo();
			endRowNo = condition.getEndRowNo();
		}
		
		List<Object> params = new ArrayList<>();
		
		StringBuilder querySQL = new StringBuilder();
		querySQL.append(" SELECT GOODS_ID, GOODS_NAME, DESCRIPTION, PRICE, QUANTITY, IMAGE_NAME, STATUS ")
				.append(" FROM ")
				.append(" ( ")
				.append("  SELECT ")
				.append("  BG.GOODS_ID, ")
				.append("  BG.GOODS_NAME, ")
				.append("  BG.DESCRIPTION, ")
				.append("  BG.PRICE, ")
				.append("  BG.QUANTITY, ")
				.append("  BG.IMAGE_NAME, ")
				.append("  BG.STATUS, ");
		if (null != pirceSort && !pirceSort.replaceAll(" ", "").isEmpty()) {
			querySQL.append(" ROW_NUMBER() OVER(ORDER BY BG.PRICE " + pirceSort + " ,BG.GOODS_ID) NUM ");
		}else {
			querySQL.append(" ROW_NUMBER() OVER(ORDER BY BG.GOODS_ID) NUM ");
		}
		querySQL.append("  FROM BEVERAGE_GOODS BG ")
				.append("  WHERE GOODS_ID IS NOT NULL ");
		if (null != goodsID && goodsID > 0) {
			querySQL.append(" AND GOODS_ID=? ");
			params.add(goodsID);
		}
		if (null != keyword && !keyword.replaceAll(" ", "").isEmpty()) {
			querySQL.append(" AND UPPER(GOODS_NAME) LIKE UPPER(?) ");
			params.add("%" + keyword + "%");
		}
		if (null != minPrice && minPrice > 0) {
			querySQL.append(" AND BG.PRICE >= ? ");
			params.add(minPrice);
		}
		if (null != maxPrice && maxPrice > 0) {
			querySQL.append(" AND BG.PRICE <= ? ");
			params.add(maxPrice);
		}
		if (null != stock && stock > 0) {
			querySQL.append(" AND BG.QUANTITY <= ? ");
			params.add(stock);
		}
		if (null != status && !status.replaceAll(" ", "").isEmpty()) {
			querySQL.append(" AND BG.STATUS = ? ");
			params.add(status);
		}
		if (startRowNo > 0 && endRowNo > 0) {
		querySQL.append(" ) "
					  + " WHERE NUM BETWEEN ? AND ? ");
			params.add(startRowNo);
			params.add(endRowNo);
		}
		
		Query query = entityManager.createNativeQuery(querySQL.toString(), BeverageGoods.class);
		for (int i = 0; i < params.size(); i++) {
			query.setParameter(i + 1, params.get(i));
		}

		Stream<BeverageGoods> goodsStream = query.getResultStream().map(g -> (BeverageGoods) g);
		List<BeverageGoods> goods = goodsStream.collect(Collectors.toList());
		goods.stream().forEach(System.out::println);

		return goods;
	}
	
	public int queryGoodsCountByGoodsDataCondition(GoodsDataCondition condition) {
		
		Long goodsID = null;
		String keyword = null;
		Integer minPrice = null;
		Integer maxPrice = null;
		Integer stock = null;
		String status = null;

		if (condition != null) {
			goodsID = condition.getGoodsID();
			keyword = condition.getGoodsNameKeyword();
			minPrice = condition.getMinPrice();
			maxPrice = condition.getMaxPrice();
			stock = condition.getStock();
			status = condition.getStatus();
		}
		
		// SQL NativeQuery ResultSetMapping
		// 支援可動態組SQL又可自訂義物件欄位對應查詢欄位資料取得查詢結果
		// 須建立欄位相對應的  @SqlResultSetMapping @Entity 實體物件
		
		StringBuilder querySQL = new StringBuilder();
		querySQL.append(" SELECT COUNT(GOODS_ID) FROM BEVERAGE_GOODS ")
				.append(" WHERE GOODS_ID IS NOT NULL ");
		if (null != goodsID && goodsID > 0) {
			querySQL.append(" AND GOODS_ID= :goodsID ");
		}
		if (null != keyword && !keyword.replaceAll(" ", "").isEmpty()) {
			querySQL.append(" AND UPPER(GOODS_NAME) LIKE UPPER(:keyword) ");
		}
		if (null != minPrice && minPrice > 0) {
			querySQL.append(" AND PRICE >= :minPrice ");
		}
		if (null != maxPrice && maxPrice > 0) {
			querySQL.append(" AND PRICE <= :maxPrice ");
		}
		if (null != stock && stock > 0) {
			querySQL.append(" AND QUANTITY <= :stock ");
		}
		if (null != status && !status.replaceAll(" ", "").isEmpty()) {
			querySQL.append(" AND STATUS = :status ");
		}
		
		Query query = entityManager.createNativeQuery(querySQL.toString());
		
		if (null != goodsID && goodsID > 0) {
			query.setParameter("goodsID", goodsID);
		}
		if (null != keyword && !keyword.replaceAll(" ", "").isEmpty()) {
			query.setParameter("keyword", "%" + keyword + "%");
		}
		if (null != minPrice && minPrice > 0) {
			query.setParameter("minPrice", minPrice);
		}
		if (null != maxPrice && maxPrice > 0) {
			query.setParameter("maxPrice", maxPrice);
		}
		if (null != stock && stock > 0) {
			query.setParameter("stock", stock);
		}
		if (null != status && !status.replaceAll(" ", "").isEmpty()) {
			query.setParameter("status", status);
		}
		
		int goodsTotalCount = ((BigDecimal) query.getSingleResult()).intValue();
		
		return goodsTotalCount;
	}
	
}
