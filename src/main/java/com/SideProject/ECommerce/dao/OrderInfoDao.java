package com.SideProject.ECommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SideProject.ECommerce.entity.BeverageOrder;

@Repository
public interface OrderInfoDao extends JpaRepository<BeverageOrder, Long>{
	
}
