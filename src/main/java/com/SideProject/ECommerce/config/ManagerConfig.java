package com.SideProject.ECommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import com.SideProject.ECommerce.vo.ManagerInfoVo;

@Configuration
public class ManagerConfig {

	@Bean
	@SessionScope
	public ManagerInfoVo sessionManagerInfo() {
		return ManagerInfoVo.builder().isLogin(false).loginMessage("管理者未登入").build();
	}
	
}
