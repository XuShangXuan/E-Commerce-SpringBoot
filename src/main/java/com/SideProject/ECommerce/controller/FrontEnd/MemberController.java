package com.SideProject.ECommerce.controller.FrontEnd;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.SideProject.ECommerce.dao.GoodsInfoDao;
import com.SideProject.ECommerce.dao.MemberInfoDao;
import com.SideProject.ECommerce.entity.BeverageGoods;
import com.SideProject.ECommerce.entity.BeverageMember;
import com.SideProject.ECommerce.vo.MemberInfoVo;
import com.SideProject.ECommerce.vo.ShoppingCartGoodsVo;

import io.swagger.annotations.ApiOperation;

@CrossOrigin(value = "http://localhost:3000",  allowCredentials = "true")
@RestController
@RequestMapping("/MemberController")
public class MemberController {
	
	private static Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	//@Autowired
	//@Qualifier("sessionMemberInfo")
	@Resource(name = "sessionMemberInfo")
	private MemberInfoVo memberInfo;
	
	@Resource(name = "sessionCartGoods")
	private List<ShoppingCartGoodsVo> cartGoods;
	
	@Autowired
	private HttpSession httpSession; 
	
	@Autowired
	private MemberInfoDao memberDao;
	
	@Autowired
	private GoodsInfoDao goodsInfoDao;

	@ApiOperation(value = "購物網-會員-檢查登入")
	@GetMapping(value = "/checkLogin")
	public ResponseEntity<MemberInfoVo> checkLogin() {
		
		logger.info("HttpSession checkLogin:" + httpSession.getId());
		logger.info("CheckLogin:" + memberInfo.toString());
		
		MemberInfoVo member = MemberInfoVo.builder()
				.isLogin(memberInfo.getIsLogin())
				.loginMessage(memberInfo.getLoginMessage())
				.identificationNo(memberInfo.getIdentificationNo())
				.cusName(memberInfo.getCusName())
				.build();
		
		return ResponseEntity.ok(member);
	}
	
	@ApiOperation(value = "購物網-會員-登入")
	@PostMapping(value = "/login")
	public ResponseEntity<MemberInfoVo> login(@RequestBody MemberInfoVo inputMember) {
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
		logger.info("Before:" + memberInfo.toString());
		
		String inputID = inputMember.getIdentificationNo();
		String inputPwd = inputMember.getCusPassword();
		
		// Step2:依使用者所輸入的帳戶名稱取得 Member
		BeverageMember dbMember = memberDao.findById(inputID).orElse(null);
		
		Boolean isLogin = false;
		String loginMessage = null;
		String identificationNo = null;
		String cusName = null;

		if (null != dbMember) { //檢查有無此帳號

			String dbId = dbMember.getIdentificationNo();
			String dbPwd = dbMember.getPassword();
			String dbCusName = dbMember.getCustomerName();

			if (dbId.equals(inputID) && dbPwd.equals(inputPwd)) { //檢查帳密是否正確

				isLogin = true;
				loginMessage = "登入成功";
				identificationNo = dbId;
				cusName = dbCusName;

				memberInfo.setIsLogin(true);
				memberInfo.setLoginMessage("已登入");
				memberInfo.setIdentificationNo(dbId);
				memberInfo.setCusName(cusName);

			} else {
				loginMessage = "帳號或密碼錯誤";
			}

		} else {
			loginMessage = "無此帳戶名稱,請重新輸入!";
		}
		
		logger.info("After:" + memberInfo.toString());
		
		MemberInfoVo outPutMember = MemberInfoVo.builder()
				.isLogin(isLogin).loginMessage(loginMessage)
				.identificationNo(identificationNo)
				.cusName(cusName).build();
		
		return ResponseEntity.ok(outPutMember);
	}
	
	@ApiOperation(value = "購物網-會員-登出")
	@GetMapping(value = "/logout")
	public ResponseEntity<MemberInfoVo> logout() {
		
		logger.info("HttpSession logout:" + httpSession.getId());

		MemberInfoVo member = MemberInfoVo.builder()
				.isLogin(false).loginMessage("已登出")
				.build();
		
		memberInfo.setIsLogin(false);
		memberInfo.setLoginMessage("尚未登入");
		memberInfo.setIdentificationNo(null);
		memberInfo.setCusName(null);
		
		return ResponseEntity.ok(member);
	}
	
	@ApiOperation(value = "商品加入購物車")
	@PostMapping(value = "/addCartGoods")
	public ResponseEntity<List<ShoppingCartGoodsVo>> addCartGoods(@RequestBody ShoppingCartGoodsVo shoppingCartGoodsVo) {
		/*
			{
			  "goodsID": 28,
			  "goodsName": "Java Chip",
			  "description": "暢銷口味之一，以摩卡醬、乳品及可可碎片調製，加上細緻鮮奶油及摩卡醬，濃厚的巧克力風味。",
			  "imageName": "20130813154445805.jpg",
			  "price": 145,
			  "quantity": 1
			}

			{
			  "goodsID": 3,
			  "goodsName": "柳橙檸檬蜂蜜水",
			  "description": "廣受喜愛的蜂蜜水，搭配柳橙與檸檬汁，酸甜的好滋味，尾韻更帶有柑橘清香。",
			  "imageName": "2021110210202761.jpg",
			  "price": 20,
			  "quantity": 30
			}
		 */
		
		if (shoppingCartGoodsVo.getQuantity() > 0) {
			cartGoods.add(shoppingCartGoodsVo);
		}

		return ResponseEntity.ok(cartGoods);
	}
	
	@ApiOperation(value = "查尋購物車商品")
	@GetMapping(value = "/queryCartGoods")
	public ResponseEntity<List<ShoppingCartGoodsVo>> queryCartGoods() {

		List<ShoppingCartGoodsVo> cartGoodsInfos = cartGoods.stream()
	            .map(singleCartGoods -> {
	            	
	                BeverageGoods goods = goodsInfoDao.findById(singleCartGoods.getGoodsID()).orElse(null);

	                return ShoppingCartGoodsVo.builder()
	                        .goodsID(goods.getGoodsID())
	                        .goodsName(goods.getGoodsName())
	                        .price(goods.getPrice())
	                        .quantity(singleCartGoods.getQuantity())
	                        .imageName(goods.getImageName())
	                        .build();
	                
	            })
	            .collect(Collectors.toList());
		
		return ResponseEntity.ok(cartGoodsInfos);
	}
	
	@ApiOperation(value = "清空購物車商品")
	@DeleteMapping(value = "/clearCartGoods")
	public ResponseEntity<List<ShoppingCartGoodsVo>> clearCartGoods() {
		
		cartGoods.clear();

		return ResponseEntity.ok(cartGoods);
	}
	
}
