package com.SideProject.ECommerce.controller.FrontEnd;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.SideProject.ECommerce.service.FrontEndService;
import com.SideProject.ECommerce.vo.CheckoutCompleteInfo;
import com.SideProject.ECommerce.vo.GenericPageable;
import com.SideProject.ECommerce.vo.GoodsDataCondition;
import com.SideProject.ECommerce.vo.GoodsDataInfo;
import com.SideProject.ECommerce.vo.MemberInfoVo;
import com.SideProject.ECommerce.vo.OrderCustomer;
import com.SideProject.ECommerce.vo.ShoppingCartGoodsVo;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/FrontEndController")
public class FrontEndController {
	
	private static Logger logger = LoggerFactory.getLogger(FrontEndController.class);
	
	@Autowired
	private HttpSession httpSession;
	
	@Resource(name = "sessionMemberInfo")
	private MemberInfoVo memberInfo;
	
	@Resource(name = "sessionCartGoods")
	private List<ShoppingCartGoodsVo> cartGoods;
	
	@Autowired
	private FrontEndService frontEndService;
	
	@ApiOperation(value = "購物網-前臺-查詢商品列表")
	@GetMapping(value = "/queryGoodsData")
	public ResponseEntity<GoodsDataInfo> queryGoodsData(@RequestParam(required = false) String searchKeyword,
			 @RequestParam int currentPage, @RequestParam int showDataCount, @RequestParam int showPageSize) {
	
		GoodsDataCondition condition = GoodsDataCondition.builder()
				.goodsNameKeyword(searchKeyword).status("1")
				.build();
		
		GenericPageable genericPageable = GenericPageable.builder().currentPage(currentPage)
				.showDataCount(showDataCount).showPageSize(showPageSize).build();
				
		GoodsDataInfo goodsDataInfo = frontEndService.queryGoodsData(condition, genericPageable);		
		
		return ResponseEntity.ok(goodsDataInfo);
	}
	
	@ApiOperation(value = "購物網-前臺-結帳購物車商品")
	@PostMapping(value = "/checkoutGoods")
	public ResponseEntity<CheckoutCompleteInfo> checkoutGoods(@RequestBody OrderCustomer customer) {
		
		logger.info("HttpSession checkoutGoods:" + httpSession.getId());
		logger.info("CheckoutGoods:" + memberInfo.toString());
		
		CheckoutCompleteInfo checkoutCompleteInfo = frontEndService.checkoutGoods(memberInfo, customer, cartGoods);
		
		return ResponseEntity.ok(checkoutCompleteInfo);
	}

}
