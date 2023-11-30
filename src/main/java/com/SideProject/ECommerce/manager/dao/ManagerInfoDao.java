package com.SideProject.ECommerce.manager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SideProject.ECommerce.manager.entity.BeverageManager;

@Repository
public interface ManagerInfoDao extends JpaRepository<BeverageManager, String> {
	
}
