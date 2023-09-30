package com.SideProject.ECommerce.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.SideProject.ECommerce.entity.BeverageGoods;

@Repository
public interface GoodsInfoDao extends JpaRepository<BeverageGoods, Long>{

	//Containing是傳入的Keyword前後都加%
	//IgnoreCase資料庫與傳入的Keyword都做大寫比對
	int countByGoodsNameContainingIgnoreCaseAndStatus(String goodsNameKeyword, String status);
	
	List<BeverageGoods> findByGoodsNameContainingIgnoreCaseAndStatus(Pageable pageable, String goodsNameKeyword, String status);

	//JPQL
	@Query(
			value = "SELECT bg.goodsID, bg.goodsName, bg.description, "
					+ " bg.price, bg.quantity, bg.imageName, bg.status "
					+ " FROM BeverageGoods bg "
					+ " WHERE bg.goodsID = ?1 AND bg.status = ?2 "
	)
	List<BeverageGoods> findByGoodsIDAndStatus(long goodsID, String status);
	
}


