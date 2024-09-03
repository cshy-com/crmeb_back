package com.cshy.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.cshy.common.config.CrmebConfig;
import com.cshy.common.constants.DateConstants;
import com.cshy.common.constants.UploadConstants;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.common.model.entity.system.SystemAdmin;
import com.cshy.common.model.entity.system.SystemStore;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.constants.Constants;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.request.*;
import com.cshy.common.model.request.store.StoreBargainSearchRequest;
import com.cshy.common.model.request.store.StoreCombinationSearchRequest;
import com.cshy.common.model.request.store.StoreOrderSearchRequest;
import com.cshy.common.model.request.store.StoreProductSearchRequest;
import com.cshy.common.model.response.StoreOrderDetailResponse;
import com.cshy.common.model.response.StoreProductResponse;
import com.cshy.common.model.vo.order.OrderInfoDetailVo;
import com.cshy.service.service.*;
import com.cshy.service.service.category.CategoryService;
import com.cshy.service.service.store.StoreBargainService;
import com.cshy.service.service.store.StoreCombinationService;
import com.cshy.service.service.store.StoreOrderService;
import com.cshy.service.service.store.StoreProductService;
import com.cshy.service.service.system.SystemAdminService;
import com.cshy.service.service.system.SystemConfigService;
import com.cshy.service.service.system.SystemStoreService;
import com.cshy.service.service.system.SystemStoreStaffService;
import com.cshy.service.service.user.UserService;
import com.github.pagehelper.PageInfo;
import com.cshy.common.utils.CrmebUtil;
import com.cshy.common.utils.DateUtil;
import com.cshy.common.utils.ExportUtil;
import com.cshy.common.model.response.StoreBargainResponse;
import com.cshy.common.model.response.StoreCombinationResponse;
import com.cshy.common.model.vo.BargainProductExcelVo;
import com.cshy.common.model.vo.CombinationProductExcelVo;
import com.cshy.common.model.vo.order.OrderExcelVo;
import com.cshy.common.model.vo.ProductExcelVo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ExcelServiceImpl 接口实现
 */
@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StoreBargainService storeBargainService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private StoreCombinationService storeCombinationService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private SystemStoreService systemStoreService;

    @Autowired
    private UserService userService;

    @Autowired
    private CrmebConfig crmebConfig;

    /**
     * 导出砍价商品
     *
     * @param request 请求参数
     * @return 导出地址
     */
    @Override
    public String exportBargainProduct(StoreBargainSearchRequest request) {
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(Constants.DEFAULT_PAGE);
        pageParamRequest.setLimit(Constants.EXPORT_MAX_LIMIT);
        PageInfo<StoreBargainResponse> page = storeBargainService.getList(request, pageParamRequest);
        if (CollUtil.isEmpty(page.getList())) throw new CrmebException("没有可导出的数据!");
        List<StoreBargainResponse> list = page.getList();
        List<BargainProductExcelVo> voList = list.stream().map(temp -> {
            BargainProductExcelVo vo = new BargainProductExcelVo();
            BeanUtils.copyProperties(temp, vo);
            vo.setPrice("￥".concat(temp.getPrice().toString()));
            vo.setStatus(temp.getStatus() ? "开启" : "关闭");
            vo.setStartTime(temp.getStartTime());
            vo.setStopTime(temp.getStopTime());
            vo.setAddTime(temp.getAddTime());
            return vo;
        }).collect(Collectors.toList());

        // 上传设置
        ExportUtil.setUpload(crmebConfig.getImagePath(), UploadConstants.UPLOAD_MODEL_PATH_EXCEL, UploadConstants.UPLOAD_TYPE_FILE);

        // 文件名
        String fileName = "砍价".concat(DateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM)).concat(CrmebUtil.randomCount(111111111, 999999999).toString()).concat(".xlsx");

        //自定义标题别名
        LinkedHashMap<String, String> aliasMap = new LinkedHashMap<>();
        aliasMap.put("title", "砍价活动名称");
        aliasMap.put("info", "砍价活动简介");
        aliasMap.put("price", "砍价金额");
        aliasMap.put("bargainNum", "用户每次砍价的次数");
        aliasMap.put("status", "砍价状态");
        aliasMap.put("startTime", "砍价开启时间");
        aliasMap.put("stopTime", "砍价结束时间");
        aliasMap.put("sales", "销量");
        aliasMap.put("quotaShow", "库存");
        aliasMap.put("giveIntegral", "返多少积分");
        aliasMap.put("addTime", "添加时间");

        return ExportUtil.exportExcel(fileName, "砍价商品导出", voList, aliasMap);
    }

    /**
     * 导出拼团商品
     *
     * @param request 请求参数
     * @return 导出地址
     */
    @Override
    public String exportCombinationProduct(StoreCombinationSearchRequest request) {
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(Constants.DEFAULT_PAGE);
        pageParamRequest.setLimit(Constants.EXPORT_MAX_LIMIT);
        PageInfo<StoreCombinationResponse> page = storeCombinationService.getList(request, pageParamRequest);
        if (CollUtil.isEmpty(page.getList())) throw new CrmebException("没有可导出的数据!");
        List<StoreCombinationResponse> list = page.getList();
        List<CombinationProductExcelVo> voList = list.stream().map(temp -> {
            CombinationProductExcelVo vo = new CombinationProductExcelVo();
            BeanUtils.copyProperties(temp, vo);
            vo.setIsShow(temp.getIsShow() ? "开启" : "关闭");
            vo.setStopTime(DateUtil.timestamp2DateStr(temp.getStopTime(), DateConstants.DATE_FORMAT_DATE));
            return vo;
        }).collect(Collectors.toList());

        // 上传设置
        ExportUtil.setUpload(crmebConfig.getImagePath(), UploadConstants.UPLOAD_MODEL_PATH_EXCEL, UploadConstants.UPLOAD_TYPE_FILE);

        // 文件名
        String fileName = "拼团".concat(DateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM)).concat(CrmebUtil.randomCount(111111111, 999999999).toString()).concat(".xlsx");

        //自定义标题别名
        LinkedHashMap<String, String> aliasMap = new LinkedHashMap<>();
        aliasMap.put("id", "编号");
        aliasMap.put("title", "拼团名称");
        aliasMap.put("otPrice", "原价");
        aliasMap.put("price", "拼团价");
        aliasMap.put("quotaShow", "库存");
        aliasMap.put("countPeople", "拼团人数");
        aliasMap.put("countPeopleAll", "参与人数");
        aliasMap.put("countPeoplePink", "成团数量");
        aliasMap.put("sales", "销量");
        aliasMap.put("isShow", "商品状态");
        aliasMap.put("stopTime", "拼团结束时间");

        return ExportUtil.exportExcel(fileName, "拼团商品导出", voList, aliasMap);
    }

    /**
     * 商品导出
     *
     * @param request 请求参数
     * @return 导出地址
     */
    @Override
    public String exportProduct(StoreProductSearchRequest request) {
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(Constants.DEFAULT_PAGE);
        pageParamRequest.setLimit(Constants.EXPORT_MAX_LIMIT);
        PageInfo<StoreProductResponse> storeProductResponsePageInfo = storeProductService.getAdminList(request, pageParamRequest);
        List<StoreProductResponse> list = storeProductResponsePageInfo.getList();
        if (list.size() < 1) {
            throw new CrmebException("没有可导出的数据！");
        }

        //产品分类id
        List<String> cateIdListStr = list.stream().map(StoreProductResponse::getCateId).distinct().collect(Collectors.toList());

        HashMap<Integer, String> categoryNameList = new HashMap<Integer, String>();
        if (cateIdListStr.size() > 0) {
            String join = StringUtils.join(cateIdListStr, ",");
            List<Integer> cateIdList = CrmebUtil.stringToArray(join);
            categoryNameList = categoryService.getListInId(cateIdList);
        }
        List<ProductExcelVo> voList = CollUtil.newArrayList();
        for (StoreProductResponse product : list) {
            ProductExcelVo vo = new ProductExcelVo();
            vo.setStoreName(product.getStoreName());
            vo.setStoreInfo(product.getStoreInfo());
            vo.setCateName(CrmebUtil.getValueByIndex(categoryNameList, product.getCateId()));
            vo.setPrice("￥" + product.getPrice());
            vo.setStock(product.getStock().toString());
            vo.setSales(product.getSales().toString());
            vo.setBrowse(product.getBrowse().toString());
            vo.setSupplier(product.getSupplier());
            voList.add(vo);
        }

        /**
         * ===============================
         * 以下为存储部分
         * ===============================
         */
        // 上传设置
        ExportUtil.setUpload(crmebConfig.getImagePath(), UploadConstants.UPLOAD_MODEL_PATH_EXCEL, UploadConstants.UPLOAD_TYPE_FILE);

        // 文件名
        String fileName = "商品导出_".concat(DateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM)).concat(CrmebUtil.randomCount(111111111, 999999999).toString()).concat(".xlsx");

        //自定义标题别名
        LinkedHashMap<String, String> aliasMap = new LinkedHashMap<>();
        aliasMap.put("storeName", "商品名称");
        aliasMap.put("storeInfo", "商品简介");
        aliasMap.put("cateName", "商品分类");
        aliasMap.put("price", "价格");
        aliasMap.put("stock", "库存");
        aliasMap.put("sales", "销量");
        aliasMap.put("supplier", "供应商名称");
        aliasMap.put("browse", "浏览量");

        return ExportUtil.exportExcel(fileName, "商品导出", voList, aliasMap);
    }

    /**
     * 订单导出
     *
     * @param request 查询条件
     * @return 文件名称
     */
    @Override
    public String exportOrder(StoreOrderSearchRequest request) {
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(Constants.DEFAULT_PAGE);
        pageParamRequest.setLimit(Constants.EXPORT_MAX_LIMIT);
        CommonPage<StoreOrderDetailResponse> adminList = storeOrderService.getAdminList(request, pageParamRequest);
        List<StoreOrderDetailResponse> list = adminList.getList();
        if (list.size() < 1) {
            throw new CrmebException("没有可导出的数据！");
        }
        //管理员查询
        List<Integer> clerkIdList = list.stream().map(StoreOrderDetailResponse::getClerkId).distinct().collect(Collectors.toList());
        HashMap<Integer, SystemAdmin> systemStoreStaffList = systemAdminService.getMapInId(clerkIdList);

        //用户查询
        List<User> userList = userService.list();

        //自提点查询
        List<SystemStore> systemStoreList = systemStoreService.list();

        List<OrderExcelVo> voList = CollUtil.newArrayList();
        for (StoreOrderDetailResponse order : list) {
            OrderExcelVo vo = new OrderExcelVo();
            BeanUtils.copyProperties(order, vo);
            vo.setCreateTime(DateUtil.dateToStr(order.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            vo.setPayTime(DateUtil.dateToStr(order.getPayTime(), "yyyy-MM-dd HH:mm:ss"));
            vo.setRefundReasonTime(DateUtil.dateToStr(order.getRefundReasonTime(), "yyyy-MM-dd HH:mm:ss"));
            vo.setPaid(order.getPaid() ? "已支付" : "未支付");
            vo.setPayTypeStr(order.getPayTypeStr());
//            vo.setProductName(order.getProductList().stream().map(item -> item.getInfo().getProductName()).collect(Collectors.joining(",")));
            vo.setRealName(order.getRealName());
            vo.setStatusStr(order.getStatusStr().get("value"));
            vo.setRefundStatus(order.getRefundStatus().equals(0) ? "" : order.getRefundStatus().equals(1) ? "退款中" : "已退款");
            //商家自配处理
            if ("zp666".equals(order.getTrackingNo())){
                vo.setShippingType("商家送货");
                vo.setTrackingNo("");
            }else {
                vo.setShippingType(order.getShippingType().equals(1) ? "商家配送" : "门店自提");
                if (StringUtils.isNotBlank(order.getTrackingNo()) && order.getTrackingNo().contains("zp666"))
                    vo.setTrackingNo(order.getTrackingNo().replace("zp666,", "").replace(",zp666", ""));
            }

            vo.setDeductionPostage(Objects.isNull(vo.getDeductionPostage()) ? BigDecimal.ZERO : vo.getDeductionPostage());

            //核销人
            String clerkName = "";
            if (systemStoreStaffList.containsKey(order.getClerkId())) {
                clerkName = systemStoreStaffList.get(order.getClerkId()).getRealName();
            }
            vo.setClerkName(clerkName);

            //用户真实姓名
            Optional<User> userOptional = userList.stream().filter(user -> user.getUid().equals(order.getUid())).findFirst();
            userOptional.ifPresent(user -> vo.setUserName(user.getRealName()));

            //自提点
            Optional<SystemStore> systemStoreOptional = systemStoreList.stream().filter(systemStore -> Objects.equals(systemStore.getId(), order.getStoreId())).findFirst();
            systemStoreOptional.ifPresent(systemStore -> vo.setPickUpAddress(systemStore.getName()));

            if (order.getProductList().size() > 1){
                order.getProductList().forEach(product -> {
                    OrderExcelVo orderExcelVo = new OrderExcelVo();
                    BeanUtils.copyProperties(vo, orderExcelVo);
                    OrderInfoDetailVo info = product.getInfo();
                    orderExcelVo.setProductName(info.getProductName());
                    orderExcelVo.setProductPrice(info.getPrice());
                    orderExcelVo.setPayNum(info.getPayNum());
                    orderExcelVo.setSupplier(product.getSupplier());
                    voList.add(orderExcelVo);
                });
            } else {
                OrderInfoDetailVo info = order.getProductList().get(0).getInfo();
                vo.setProductPrice(info.getPrice());
                vo.setProductName(info.getProductName());
                vo.setPayNum(info.getPayNum());
                vo.setSupplier(order.getProductList().get(0).getSupplier());
                voList.add(vo);
            }
        }

        /*
          ===============================
          以下为存储部分
          ===============================
         */
        // 上传设置
        ExportUtil.setUpload(crmebConfig.getImagePath(), UploadConstants.UPLOAD_MODEL_PATH_EXCEL, UploadConstants.UPLOAD_TYPE_FILE);

        // 文件名
        String fileName = "订单导出_".concat(DateUtil.nowDateTime(DateConstants.DATE_TIME_FORMAT_NUM)).concat(CrmebUtil.randomCount(111111111, 999999999).toString()).concat(".xlsx");

        //自定义标题别名
        LinkedHashMap<String, String> aliasMap = new LinkedHashMap<>();
        aliasMap.put("orderId", "订单号");
        aliasMap.put("createTime", "下单时间");
        aliasMap.put("orderType", "订单类型");
        aliasMap.put("paid", "支付状态");
        aliasMap.put("payTime", "支付时间");
        aliasMap.put("totalPrice", "订单总金额");
        aliasMap.put("proTotalPrice", "商品总价");
        aliasMap.put("deductionPrice", "抵扣金额");
        aliasMap.put("payPrice", "实际支付金额");
        aliasMap.put("totalPostage", "运费");
        aliasMap.put("payPostage", "实际支付运费");
        aliasMap.put("deductionPostage", "抵扣运费");
        aliasMap.put("outTradeNo", "商户订单号");
        aliasMap.put("productName", "商品信息");
        aliasMap.put("productPrice", "商品价格");
        aliasMap.put("supplier", "供应商名称");
        aliasMap.put("payNum", "购买数量");
        aliasMap.put("statusStr", "订单状态");
        aliasMap.put("payTypeStr", "支付方式");
        aliasMap.put("mark", "买家备注");
        aliasMap.put("verifyCode", "核销码");
        aliasMap.put("realName", "收货人姓名");
        aliasMap.put("userMobile", "收货人电话");
        aliasMap.put("remark", "平台备注");
        aliasMap.put("shippingType", "配送方式");
        aliasMap.put("trackingNo", "快递单号");
        aliasMap.put("clerkName", "核销人");
        aliasMap.put("address", "地址");
        aliasMap.put("pickUpAddress", "自提点");
        aliasMap.put("userName", "用户真实姓名");
        aliasMap.put("refundStatus", "退款状态");
        aliasMap.put("refundReasonWapExplain", "用户退款说明");
        aliasMap.put("refundReasonTime", "申请退款时间");
        aliasMap.put("refundReasonWap", "管理员退款原因");
        aliasMap.put("refundReason", "拒绝退款理由");
        aliasMap.put("refundPrice", "退款金额");
        aliasMap.put("backIntegral", "退还积分");

        return ExportUtil.exportExcel(fileName, "订单导出", voList, aliasMap);

    }
}

