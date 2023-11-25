package com.SideProject.ECommerce.controller.BackEnd;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SideProject.ECommerce.entity.BeverageGoods;
import com.SideProject.ECommerce.service.BackEndService;
import com.SideProject.ECommerce.vo.GenericPageable;
import com.SideProject.ECommerce.vo.GoodsDataCondition;
import com.SideProject.ECommerce.vo.GoodsDataInfo;
import com.SideProject.ECommerce.vo.GoodsSalesReportInfo;
import com.SideProject.ECommerce.vo.GoodsSalesReportCondition;
import com.SideProject.ECommerce.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.ApiOperation;

//http://localhost:8085/E-Commerce-SpringBoot/swagger-ui/index.html

@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequestMapping("/BackEndController")
public class BackEndController {
	
	private static Logger logger = LoggerFactory.getLogger(BackEndController.class);
	
	@Autowired
	private BackEndService backEndService;
	
	@ApiOperation(value = "購物網-後臺-查詢商品列表")
	@GetMapping(value = "/queryGoodsData")
	public ResponseEntity<GoodsDataInfo> queryGoodsData(@RequestParam(required = false) Long goodsID, 
			 @RequestParam(required = false) String goodsNameKeyword, @RequestParam(required = false) String priceSort,
			 @RequestParam(required = false) Integer minPrice, @RequestParam(required = false) Integer maxPrice, 
			 @RequestParam(required = false) Integer stock, @RequestParam(required = false) String status,
			 @RequestParam int currentPage, @RequestParam int showDataCount, @RequestParam int showPageSize) {

		GoodsDataCondition condition = GoodsDataCondition.builder().goodsID(goodsID).goodsNameKeyword(goodsNameKeyword)
				.minPrice(minPrice).maxPrice(maxPrice).priceSort(priceSort).stock(stock).status(status).build();
		
		GenericPageable genericPageable = GenericPageable.builder().currentPage(currentPage)
				.showDataCount(showDataCount).showPageSize(showPageSize).build();
				
		GoodsDataInfo goodsDataInfo = backEndService.queryGoodsData(condition, genericPageable);		
		
		return ResponseEntity.ok(goodsDataInfo);
	}
	
	@ApiOperation(value = "購物網-後臺-商品新增作業")
	@PostMapping(value = "/createGoods", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<BeverageGoods> createGoods(@ModelAttribute GoodsVo goodsVo) throws IOException {
		
		logger.info("使用者傳入要新增的商品資訊:" + goodsVo);
		BeverageGoods goods = backEndService.createGoods(goodsVo);
		
		return ResponseEntity.ok(goods);
	}
	
	@ApiOperation(value = "購物網-後臺-商品維護作業-查詢全部商品清單")
	@GetMapping(value = "/queryAllGoods")
	public ResponseEntity<List<BeverageGoods>> queryAllGoods() {
		
		List<BeverageGoods> goodsDatas = backEndService.queryAllGoods();
		
		return ResponseEntity.ok(goodsDatas);
	}
	
	@ApiOperation(value = "購物網-後臺-商品維護作業-查詢單一商品資料")
	@GetMapping(value = "/queryGoodsByID")
	public ResponseEntity<BeverageGoods> queryGoodsByID(@RequestParam long goodsID){
		
		BeverageGoods goodsData = backEndService.queryGoodsByID(goodsID);
		
		return ResponseEntity.ok(goodsData);
	}
	
	@ApiOperation(value = "購物網-後臺-商品維護作業-更新商品資料")
	@PutMapping(value = "/updateGoods", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<BeverageGoods> updateGoods(@ModelAttribute GoodsVo goodsVo) throws IOException {
		
		logger.info("使用者傳入要更新的商品資訊:" + goodsVo);
		BeverageGoods goods = backEndService.updateGoods(goodsVo);
		
		return ResponseEntity.ok(goods);
	}
	
	@ApiOperation(value = "購物網-後臺-商品訂單查詢(一個商品對應到多筆訂單)")
	@GetMapping(value = "/queryGoodsSales")
	public ResponseEntity<GoodsSalesReportInfo> queryGoodsSales(
			 @RequestParam String startDate, @RequestParam String endDate,  
			 @RequestParam int currentPage, @RequestParam int showDataCount, @RequestParam int showPageSize) {
		/*
		 startDate:2022/09/19
		 endDate:2022/09/19
		 currentPageNo:1
		 pageDataSize: 3
		 pagesIconSize: 3
		 */	
		
		GoodsSalesReportCondition condition = GoodsSalesReportCondition.builder().startDate(startDate + " 00:00:00").endDate(endDate + " 23:59:59").build();
		
		GenericPageable genericPageable = GenericPageable.builder().currentPage(currentPage)
				.showDataCount(showDataCount).showPageSize(showPageSize).build();
		
		GoodsSalesReportInfo goodsReportSalesInfo = backEndService.queryGoodsSales(condition, genericPageable);
		
		return ResponseEntity.ok(goodsReportSalesInfo);
	}
	
}
