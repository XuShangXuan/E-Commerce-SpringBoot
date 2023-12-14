package com.SideProject.ECommerce.controller.BackEnd;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SideProject.ECommerce.manager.dao.ManagerInfoDao;
import com.SideProject.ECommerce.manager.entity.BeverageManager;
import com.SideProject.ECommerce.vo.ManagerInfoVo;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(value = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/ManagerController")
public class ManagerController {
	
	private static Logger logger = LoggerFactory.getLogger(ManagerController.class);

	// @Autowired
	// @Qualifier("sessionManagerInfo")
	@Resource(name = "sessionManagerInfo")
	private ManagerInfoVo managerInfo;

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private ManagerInfoDao managerDao;


	@ApiOperation(value = "購物網-管理者-檢查登入")
	@GetMapping(value = "/checkLogin")
	public ResponseEntity<ManagerInfoVo> checkLogin() {
		
		logger.info("HttpSession checkLogin:" + httpSession.getId());
		logger.info("CheckLogin:" + managerInfo.toString());
		
		ManagerInfoVo manager = ManagerInfoVo.builder()
				.isLogin(managerInfo.getIsLogin())
				.loginMessage(managerInfo.getLoginMessage())
				.identificationNo(managerInfo.getIdentificationNo())
				.cusName(managerInfo.getCusName())
				.build();
		
		return ResponseEntity.ok(manager);
	}
	
	@ApiOperation(value = "購物網-管理者-登入")
	@PostMapping(value = "/login")
	public ResponseEntity<ManagerInfoVo> login(@RequestBody ManagerInfoVo inputManager) {
		/*
			{
			  "identificationNo": "A124243295",
			  "cusPassword": "123"
			}
			{
			  "identificationNo": "G436565447",
			  "cusPassword": "123"
			}
		 */
		logger.info("HttpSession Login:" + httpSession.getId());
		logger.info("Before:" + managerInfo.toString());
		
		String inputID = inputManager.getIdentificationNo();
		String inputPwd = inputManager.getCusPassword();
		
		// Step2:依使用者所輸入的帳戶名稱取得 Manager
		BeverageManager dbManager = managerDao.findById(inputID).orElse(null);
		
		Boolean isLogin = false;
		String loginMessage = null;
		String identificationNo = null;
		String cusName = null;

		if (null != dbManager) { //檢查有無此帳號

			String dbId = dbManager.getIdentificationNo();
			String dbPwd = dbManager.getPassword();
			String dbCusName = dbManager.getCustomerName();

			if (dbId.equals(inputID) && dbPwd.equals(inputPwd)) { //檢查帳密是否正確

				isLogin = true;
				loginMessage = "";
				identificationNo = dbId;
				cusName = dbCusName;

				managerInfo.setIsLogin(true);
				managerInfo.setLoginMessage("");
				managerInfo.setIdentificationNo(dbId);
				managerInfo.setCusName(cusName);

			} else {
				loginMessage = "帳號或密碼錯誤";
			}

		} else {
			loginMessage = "無此帳戶名稱,請重新輸入!";
		}
		
		logger.info("After:" + managerInfo.toString());
		
		ManagerInfoVo outPutManager = ManagerInfoVo.builder()
				.isLogin(isLogin).loginMessage(loginMessage)
				.identificationNo(identificationNo)
				.cusName(cusName).build();
		
		return ResponseEntity.ok(outPutManager);
	}
	
	@ApiOperation(value = "購物網-管理者-登出")
	@GetMapping(value = "/logout")
	public ResponseEntity<ManagerInfoVo> logout() {
		
		logger.info("HttpSession logout:" + httpSession.getId());

		ManagerInfoVo manager = ManagerInfoVo.builder()
				.isLogin(false).loginMessage("")
				.build();
		
		managerInfo.setIsLogin(false);
		managerInfo.setIdentificationNo(null);
		managerInfo.setCusName(null);
		
		return ResponseEntity.ok(manager);
	}
	
}
