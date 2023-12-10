package com.SideProject.ECommerce.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.SideProject.ECommerce.dao.GoodsDataInfoDao;
import com.SideProject.ECommerce.dao.GoodsInfoDao;
import com.SideProject.ECommerce.dao.GoodsReportSalesInfoDao;
import com.SideProject.ECommerce.entity.BeverageGoods;
import com.SideProject.ECommerce.entity.GoodsSalesReportMapping;
import com.SideProject.ECommerce.tool.Page;
import com.SideProject.ECommerce.vo.GenericPageable;
import com.SideProject.ECommerce.vo.GoodsDataCondition;
import com.SideProject.ECommerce.vo.GoodsDataInfo;
import com.SideProject.ECommerce.vo.GoodsSalesReportInfo;
import com.SideProject.ECommerce.vo.GoodsSalesReportCondition;
import com.SideProject.ECommerce.vo.GoodsVo;

@Service
public class BackEndService {

	private static Logger logger = LoggerFactory.getLogger(BackEndService.class);

	@Autowired
	private GoodsInfoDao goodsInfoDao;

	@Autowired
	private GoodsDataInfoDao goodsDataInfoDao;

	@Autowired
	GoodsReportSalesInfoDao goodsReportSalesInfoDao;

	public GoodsDataInfo queryGoodsData(GoodsDataCondition condition, GenericPageable pageable) {

		// 資料總筆數
		int dataTotalCount = goodsDataInfoDao.queryGoodsCountByGoodsDataCondition(condition);

		pageable = Page.calculatePageable(dataTotalCount, pageable);

		logger.info("計算後的分頁資訊:" + pageable);

		// 計算查詢範圍
		int endRowNo = pageable.getShowDataCount() * pageable.getCurrentPage();
		int startRowNo = endRowNo - pageable.getShowDataCount() + 1;
		condition.setEndRowNo(endRowNo);
		condition.setStartRowNo(startRowNo);

		logger.info("計算後的條件資訊:" + condition);

		List<BeverageGoods> goodsData = goodsDataInfoDao.queryGoodsByGoodsDataCondition(condition, pageable);
		GoodsDataInfo goodsDataInfo = GoodsDataInfo.builder().goodsDatas(goodsData).genericPageable(pageable).build();

		return goodsDataInfo;
	}

	public BeverageGoods createGoods(GoodsVo goodsVo) throws IOException {

		// 複制檔案
		MultipartFile file = goodsVo.getFile();

		// 上傳檔案原始名稱
		String fileName = file.getOriginalFilename();

		// 前端傳入檔案名稱
		// String fileName = goodsVo.getImageName();

		Files.copy(file.getInputStream(), Paths.get("src\\main\\webapp\\goodsImg").resolve(fileName));

		BeverageGoods createGoods = BeverageGoods.builder()
				.goodsName(goodsVo.getGoodsName())
				.description(goodsVo.getDescription())
				.price(goodsVo.getPrice())
				.quantity(goodsVo.getQuantity())
				.imageName(fileName)
				.status(goodsVo.getStatus())
				.build();

		logger.info("準備要新增的商品資訊:" + createGoods);

		return goodsInfoDao.save(createGoods);
	}

	public List<BeverageGoods> queryAllGoods() {

		return goodsInfoDao.findAll();
	}

	public BeverageGoods queryGoodsByID(long goodsID) {

		return goodsInfoDao.findById(goodsID).orElse(null);
	}

	@Transactional
	public BeverageGoods updateGoods(GoodsVo goodsVo) throws IOException {

		Optional<BeverageGoods> optGoods = goodsInfoDao.findById(goodsVo.getGoodsID());

		BeverageGoods updateGoods = null;
		if (optGoods.isPresent()) {

			updateGoods = optGoods.get();
			logger.info("更新前的商品資訊:" + updateGoods);
			updateGoods.setGoodsName(goodsVo.getGoodsName());
			updateGoods.setDescription(goodsVo.getDescription());
			updateGoods.setPrice(goodsVo.getPrice());
			updateGoods.setQuantity(goodsVo.getQuantity());
			updateGoods.setStatus(goodsVo.getStatus());

			MultipartFile file = goodsVo.getFile();
			if (file != null && file.getSize() > 0) {

				// 刪除原本的圖檔
				Files.delete(Paths.get("src\\main\\webapp\\goodsImg").resolve(updateGoods.getImageName()));
				Files.copy(file.getInputStream(), Paths.get("src\\main\\webapp\\goodsImg").resolve(file.getOriginalFilename()));

				// 更新DB的ImgName為新上傳的圖片名
				updateGoods.setImageName(file.getOriginalFilename());

			} else {

				// 如果沒有上傳要更新的圖片，就保留原檔名
				updateGoods.setImageName(goodsVo.getImageName());

			}

		}
		logger.info("準備要更新的商品資訊:" + updateGoods);

		return updateGoods;
	}

	public GoodsSalesReportInfo queryGoodsSales(GoodsSalesReportCondition condition, GenericPageable pageable) {

		logger.info("查詢日期':" + condition);

		int goodsSalesReportsCount = goodsReportSalesInfoDao.queryOrderCountByDate(condition);
		// int goodsSalesReportsCount2 = goodsReportSalesInfoDao.criteriaQueryOrderCountByDate(condition);

		pageable = Page.calculatePageable(goodsSalesReportsCount, pageable);

		logger.info("計算後的分頁資訊:" + pageable);

		// 計算查詢範圍
		int endRowNo = pageable.getShowDataCount() * pageable.getCurrentPage();
		int startRowNo = endRowNo - pageable.getShowDataCount() + 1;
		condition.setEndRowNo(endRowNo);
		condition.setStartRowNo(startRowNo);

		logger.info("計算後的條件資訊:" + condition);

		// List<GoodsSalesReportMapping> goodsSalesReports = goodsReportSalesInfoDao.queryGoodsSalesReportByDate(condition);
		List<GoodsSalesReportMapping> goodsSalesReports = goodsReportSalesInfoDao.criteriaQueryGoodsSalesReportByDate(condition, pageable);
		
		GoodsSalesReportInfo goodsReportSalesInfo = GoodsSalesReportInfo.builder()
				.goodsSalesDatas(goodsSalesReports).genericPageable(pageable).build();

		return goodsReportSalesInfo;
	}

}
