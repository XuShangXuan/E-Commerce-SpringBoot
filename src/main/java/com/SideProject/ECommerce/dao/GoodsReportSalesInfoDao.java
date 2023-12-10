package com.SideProject.ECommerce.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.SideProject.ECommerce.entity.BeverageOrder;
import com.SideProject.ECommerce.entity.GoodsSalesReportMapping;
import com.SideProject.ECommerce.vo.GenericPageable;
import com.SideProject.ECommerce.vo.GoodsSalesReportCondition;

@Repository
public class GoodsReportSalesInfoDao {

	@PersistenceContext(name = "oracleEntityManager")
    private EntityManager entityManager;
	
	public int criteriaQueryOrderCountByDate(GoodsSalesReportCondition condition) {
		
		String date = LocalDateTime.now().toString();
	    String startDate = (null != condition.getStartDate()) ? condition.getStartDate() : date;
		String endDate = (null != condition.getEndDate()) ? condition.getEndDate() : date;
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class); //要查詢的欄位
		Root<BeverageOrder> beverageOrder = cq.from(BeverageOrder.class); //要查詢的Table
		
		Expression<Long> countOrderID = cb.count(beverageOrder.get("orderID"));
		
		Expression<String> orderDate = cb.function(
	            "TO_CHAR",
	            String.class,
	            beverageOrder.get("orderDate"),
	            cb.literal("YYYY-MM-DD HH24:MI:SS")
	    );
		
		Predicate betweenOrderDate = cb.between(orderDate, startDate, endDate);
		
		cq.select(countOrderID).where(betweenOrderDate);

		/*
			SELECT COUNT(ORDER_ID)
			FROM BEVERAGE_ORDER
			WHERE TO_CHAR(ORDER_DATE, 'YYYY-MM-DD HH24:MI:SS')
			BETWEEN '2023-01-01 00:00:00' AND '2023-12-31 23:59:59';
		*/
		
		// 執行查詢
		TypedQuery<Long> query = entityManager.createQuery(cq);
		long dataCount = query.getSingleResult();
		
		return (int)dataCount;
	}
	
	public int queryOrderCountByDate(GoodsSalesReportCondition condition) {
		
		String date = LocalDateTime.now().toString();
	    String startDate = (null != condition.getStartDate()) ? condition.getStartDate() : date;
		String endDate = (null != condition.getEndDate()) ? condition.getEndDate() : date;
		
		StringBuilder querySQL = new StringBuilder();
		querySQL.append(" SELECT COUNT(ORDER_ID) ")
				.append(" FROM BEVERAGE_ORDER ")
				.append(" WHERE ORDER_DATE ")
				.append(" BETWEEN TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') ")
				.append(" AND TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') ");
		
		Query query = entityManager.createNativeQuery(querySQL.toString());
		
		int position = 1;
		query.setParameter(position++, startDate);
		query.setParameter(position++, endDate);
		
		int orderCount = ((BigDecimal) query.getSingleResult()).intValue();
		
		return orderCount;
	}
	
	public List<GoodsSalesReportMapping> criteriaQueryGoodsSalesReportByDate(GoodsSalesReportCondition condition, GenericPageable pageable){
	    
		String date = LocalDateTime.now().toString();
		String startDate = (null != condition.getStartDate()) ? condition.getStartDate() : date;
		String endDate = (null != condition.getEndDate()) ? condition.getEndDate() : date;

		int currentPage = pageable.getCurrentPage() > 0 ? pageable.getCurrentPage() : 1;
		int showDataCount = pageable.getShowDataCount() > 0 ? pageable.getShowDataCount() : 10;
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<GoodsSalesReportMapping> cq = cb.createQuery(GoodsSalesReportMapping.class); //要查詢的欄位
		Root<BeverageOrder> beverageOrder = cq.from(BeverageOrder.class); //要查詢的Table

		CompoundSelection<GoodsSalesReportMapping> goodsSalesReport = cb.construct(
	            GoodsSalesReportMapping.class,
	            beverageOrder.get("orderID"),
	            cb.function("TO_CHAR", String.class, beverageOrder.get("orderDate"), cb.literal("YYYY/MM/DD HH24:MI:SS")),
	            beverageOrder.get("beverageMember").get("identificationNo"),//BeverageOrder的Entity有與BeverageMember正確關聯才可這樣寫
	            beverageOrder.get("beverageMember").get("customerName"),
	            beverageOrder.get("beverageGoods").get("goodsName"),//BeverageOrder的Entity有與BeverageGoods正確關聯才可這樣寫
	            beverageOrder.get("goodsBuyPrice"),
	            beverageOrder.get("buyQuantity"),
	            cb.prod(beverageOrder.get("goodsBuyPrice"), beverageOrder.get("buyQuantity"))
	    );
	    
	    Expression<String> orderDate = cb.function(
	            "TO_CHAR",
	            String.class,
	            beverageOrder.get("orderDate"),
	            cb.literal("YYYY-MM-DD HH24:MI:SS")
	    );
	    
	    Predicate betweenOrderDate = cb.between(orderDate, startDate, endDate);
	    
	    Order orderIDDesc = cb.desc(beverageOrder.get("orderID"));
	    
	    cq.select(goodsSalesReport).where(betweenOrderDate).orderBy(orderIDDesc);

	    TypedQuery<GoodsSalesReportMapping> query = entityManager.createQuery(cq);
	    
		query.setFirstResult((currentPage - 1) * showDataCount);// 設定分頁的起始位置
		query.setMaxResults(showDataCount);// 設定每頁的最大結果數

	    List<GoodsSalesReportMapping> goodsSalesReports = query.getResultList();
	    goodsSalesReports.forEach(System.out::println);
	    
	    /*
	     SELECT * FROM
		 (
		  SELECT NUM.*, ROWNUM ROWNUM_START
		  FROM
		  (
		   SELECT O.ORDER_ID, TO_CHAR(O.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') ORDER_DATE, M.CUSTOMER_NAME, O.CUSTOMER_ID, G.GOODS_NAME,
	       O.GOODS_BUY_PRICE, O.BUY_QUANTITY, (O.GOODS_BUY_PRICE * O.BUY_QUANTITY) BUY_AMOUNT
	       FROM
	       BEVERAGE_ORDER O
	       JOIN BEVERAGE_GOODS G ON O.GOODS_ID = G.GOODS_ID
	       JOIN BEVERAGE_MEMBER M ON O.CUSTOMER_ID = M.IDENTIFICATION_NO
	       WHERE TO_CHAR(O.ORDER_DATE, 'YYYY-MM-DD HH24:MI:SS')
	       BETWEEN '2023-01-01 00:00:00' AND '2023-09-29 23:59:59'
	       ORDER BY O.ORDER_ID DESC
		  ) NUM
		  WHERE ROWNUM <= 5 --顯示前5筆資料
		 )
		 WHERE ROWNUM_START > 2; --這5筆資料中，前2筆資料不顯示(從第3筆資料開始顯示)
	    */

	    return goodsSalesReports;

	}
	
	public List<GoodsSalesReportMapping> queryGoodsSalesReportByDate(GoodsSalesReportCondition condition){

		String date = LocalDateTime.now().toString();
	    String startDate = (null != condition.getStartDate()) ? condition.getStartDate() : date;
		String endDate = (null != condition.getEndDate()) ? condition.getEndDate() : date;
		
		Integer startRowNo = (null != condition.getStartRowNo() && condition.getStartRowNo() > 0) ? condition.getStartRowNo() : 1;
		Integer endRowNo = (null != condition.getEndRowNo() && condition.getEndRowNo() > 0) ? condition.getEndRowNo() : 10;
		
		StringBuilder querySQL = new StringBuilder();
		querySQL.append(" SELECT ")
				.append(" ORDER_ID, ORDER_DATE, CUSTOMER_ID, CUSTOMER_NAME, ")
				.append(" GOODS_NAME, GOODS_BUY_PRICE, BUY_QUANTITY, BUY_AMOUNT ")
				.append(" FROM ")
				.append(" ( ")
				.append("  SELECT O.ORDER_ID, TO_CHAR(O.ORDER_DATE, 'YYYY/MM/DD HH24:MI:SS') ORDER_DATE, M.CUSTOMER_NAME, ")
				.append("  O.CUSTOMER_ID, G.GOODS_NAME, O.GOODS_BUY_PRICE, ")
				.append("  O.BUY_QUANTITY, (O.GOODS_BUY_PRICE * O.BUY_QUANTITY) BUY_AMOUNT, ")
				.append("  ROW_NUMBER() OVER(ORDER BY O.ORDER_ID DESC) NUM ")
				.append("  FROM BEVERAGE_ORDER O ")
				.append("  JOIN BEVERAGE_GOODS G ")
				.append("  ON O.GOODS_ID=G.GOODS_ID ")
				.append("  JOIN BEVERAGE_MEMBER M ")
				.append("  ON O.CUSTOMER_ID=M.IDENTIFICATION_NO ")
				.append("  WHERE O.ORDER_DATE ")
				.append("  BETWEEN TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') ")
				.append("  AND TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') ")
				.append(" ) ")
				.append(" WHERE NUM BETWEEN ? AND ? ");
		
		Query query = entityManager.createNativeQuery(querySQL.toString(), "GoodsSalesReportMapping");

		int position = 1;
		query.setParameter(position++, startDate);
		query.setParameter(position++, endDate);
		query.setParameter(position++, startRowNo);
		query.setParameter(position++, endRowNo);

		Stream<GoodsSalesReportMapping> goodsSalesReportStream = query.getResultStream().map(o -> (GoodsSalesReportMapping) o);
		List<GoodsSalesReportMapping> goodsSalesRepors = goodsSalesReportStream.collect(Collectors.toList());
		goodsSalesRepors.forEach(System.out::println);
		
		return goodsSalesRepors;
	}
	
}
