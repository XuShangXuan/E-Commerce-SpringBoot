package com.SideProject.ECommerce.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.SideProject.ECommerce.entity.BeverageMember;

@Repository
public interface MemberInfoDao extends JpaRepository<BeverageMember,String> {
	
}
