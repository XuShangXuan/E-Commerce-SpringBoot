package com.SideProject.ECommerce.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import com.SideProject.ECommerce.vo.MemberInfoVo;
import com.SideProject.ECommerce.vo.ShoppingCartGoodsVo;

@Configuration
public class MemberConfig {
	
	@Bean
	@SessionScope
	public MemberInfoVo sessionMemberInfo() {
		return MemberInfoVo.builder().isLogin(false).loginMessage("").build();
	}
	
	@Bean
	@SessionScope
	public List<ShoppingCartGoodsVo> sessionCartGoods(){
		return new ArrayList<ShoppingCartGoodsVo>();
	}
	
}
