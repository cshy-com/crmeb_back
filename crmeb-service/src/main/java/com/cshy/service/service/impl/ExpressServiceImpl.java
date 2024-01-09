package com.cshy.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.constants.Constants;
import com.cshy.common.constants.StoreOrderStatusConstants;
import com.cshy.common.model.entity.express.ExpressDetail;
import com.cshy.common.model.entity.giftCard.GiftCardOrder;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.common.model.entity.user.UserAddress;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.constants.OnePassConstants;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.service.giftCard.GiftCardOrderService;
import com.cshy.service.service.store.StoreOrderService;
import com.cshy.service.service.user.UserAddressService;
import com.cshy.service.util.OnePassUtil;
import com.github.pagehelper.PageHelper;
import com.cshy.common.utils.RedisUtil;
import com.cshy.common.model.entity.express.Express;
import com.cshy.common.model.request.express.ExpressSearchRequest;
import com.cshy.common.model.request.express.ExpressUpdateRequest;
import com.cshy.common.model.request.express.ExpressUpdateShowRequest;
import com.cshy.service.dao.ExpressDao;
import com.cshy.service.service.ExpressService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * ExpressServiceImpl 接口实现
 */
@Service
public class ExpressServiceImpl extends ServiceImpl<ExpressDao, Express> implements ExpressService {

    private static final Logger logger = LoggerFactory.getLogger(ExpressServiceImpl.class);

    @Resource
    private ExpressDao dao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OnePassUtil onePassUtil;

    @Autowired
    private GiftCardOrderService giftCardOrderService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserAddressService userAddressService;

    @Value("${express.host}")
    private String host;

    @Value("${express.path}")
    private String path;

    @Value("${express.appcode}")
    private String appcode;


    /**
     * 分页显示快递公司表
     *
     * @param request          搜索条件
     * @param pageParamRequest 分页参数
     */
    @Override
    public List<Express> getList(ExpressSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Express> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lambdaQueryWrapper.like(Express::getCode, request.getKeywords()).or().like(Express::getName, request.getKeywords());
        }
        // 排序：sort字段倒序，正常id正序，方便展示常用物流公司
        lambdaQueryWrapper.orderByDesc(Express::getSort);
        lambdaQueryWrapper.orderByAsc(Express::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 编辑
     */
    @Override
    public Boolean updateExpress(ExpressUpdateRequest expressRequest) {
        Express temp = getById(expressRequest.getId());
        if (ObjectUtil.isNull(temp)) throw new CrmebException("快递公司不存在!");

        if (StrUtil.isBlank(expressRequest.getAccount()) && temp.getPartnerId().equals(true)) {
            throw new CrmebException("请输入月结账号");
        }
        if (StrUtil.isBlank(expressRequest.getPassword()) && temp.getPartnerKey().equals(true)) {
            throw new CrmebException("请输入月结密码");
        }
        if (StrUtil.isBlank(expressRequest.getNetName()) && temp.getNet().equals(true)) {
            throw new CrmebException("请输入取件网点");
        }
        Express express = new Express();
        BeanUtils.copyProperties(expressRequest, express);
        return updateById(express);
    }

    /**
     * 修改显示状态
     */
    @Override
    public Boolean updateExpressShow(ExpressUpdateShowRequest expressRequest) {
        Express temp = getById(expressRequest.getId());
        if (ObjectUtil.isNull(temp)) throw new CrmebException("编辑的记录不存在!");
        if (temp.getIsShow().equals(expressRequest.getIsShow())) {
            return Boolean.TRUE;
        }
        Express express = new Express();
        BeanUtils.copyProperties(expressRequest, express);
        return updateById(express);
    }

    /**
     * 同步物流公司
     */
    @Override
    public Boolean syncExpress() {
        if (redisUtil.exists(OnePassConstants.ONE_PASS_EXPRESS_CACHE_KEY)) {
            return Boolean.TRUE;
        }
        getExpressList();

        redisUtil.set(OnePassConstants.ONE_PASS_EXPRESS_CACHE_KEY, 1, 3600L, TimeUnit.SECONDS);
        return Boolean.TRUE;
    }

    /**
     * 查询全部物流公司
     *
     * @param type 类型：normal-普通，elec-电子面单
     */
    @Override
    public List<Express> findAll(String type) {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Express::getIsShow, true);
        if ("elec".equals(type)) {
            lqw.eq(Express::getStatus, true);
        }
        lqw.orderByDesc(Express::getSort);
        lqw.orderByAsc(Express::getId);
        return dao.selectList(lqw);
    }

    /**
     * 查询物流公司面单模板
     *
     * @param com 快递公司编号
     */
    @Override
    public JSONObject template(String com) {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        param.add("com", com);
        return onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_TEMP_URI, param, header);
    }

    /**
     * 查询快递公司
     *
     * @param code 快递公司编号
     */
    @Override
    public Express getByCode(String code) {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Express::getCode, code);
        return dao.selectOne(lqw);
    }

    /**
     * 通过物流公司名称获取
     *
     * @param name 物流公司名称
     */
    @Override
    public Express getByName(String name) {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Express::getName, name);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取快递公司详情
     *
     * @param id 快递公司id
     */
    @Override
    public Express getInfo(Integer id) {
        Express express = getById(id);
        if (ObjectUtil.isNull(express)) {
            throw new CrmebException("快递公司不存在");
        }
        return express;
    }

    @Override
    public ExpressDetail findExpressDetail(String trackingNo, Integer type, String mobile) {
        String urlSend = host + path + "?no=" + trackingNo + "&type=";
        //顺丰需要特殊处理
        if (trackingNo.contains("SF")) {
            mobile = mobile.substring(mobile.length() - 4);
            urlSend = host + path + "?no=" + trackingNo + ":" + mobile + "&type=";
        }
        try {
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);
            int httpCode = httpURLCon.getResponseCode();
            if (httpCode == 200) {
                String json = read(httpURLCon.getInputStream());
//                System.out.print(json);
                JSONObject jsonObject = JSON.parseObject(json);
                JSONObject result = (JSONObject) jsonObject.get("result");
                if (!"0".equals(jsonObject.get("status")))
                    throw new CrmebException((String) jsonObject.get("msg"));
                ExpressDetail expressDetail = JSONObject.parseObject(result.toJSONString(), ExpressDetail.class);
                if (type == 0) {
                    List<StoreOrder> storeOrderList = this.storeOrderService.list(new LambdaQueryWrapper<StoreOrder>().eq(StoreOrder::getTrackingNo, trackingNo));
                    if (CollUtil.isNotEmpty(storeOrderList)) {
                        storeOrderList.forEach(storeOrder -> {
                            storeOrder.setStatus(transferOrderStatus(expressDetail.getDeliveryStatus()));
                            this.storeOrderService.updateById(storeOrder);
                        });
                    }
                } else if (type == 1) {
                    //查询礼品卡订单
                    List<GiftCardOrder> giftCardOrderList = this.giftCardOrderService.list(new LambdaQueryWrapper<GiftCardOrder>().eq(GiftCardOrder::getTrackingNo, trackingNo));
                    if (CollUtil.isNotEmpty(giftCardOrderList)) {
                        giftCardOrderList.forEach(giftCardOrder -> {
                            giftCardOrder.setOrderStatus(transferOrderStatus(expressDetail.getDeliveryStatus()));
                            this.giftCardOrderService.updateById(giftCardOrder);
                        });
                    }
                }
                return expressDetail;
            } else {
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                    logger.error("AppCode错误");
                    throw new CrmebException("AppCode错误");
                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                    logger.error("请求的 Method、Path 或者环境错误");
                    throw new CrmebException("请求的 Method、Path 或者环境错误");
                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                    logger.error("参数错误");
                    throw new CrmebException("参数错误");
                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                    logger.error("服务未被授权（或URL和Path不正确）");
                    throw new CrmebException("服务未被授权（或URL和Path不正确）");
                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                    logger.error("套餐包次数用完 ");
                    throw new CrmebException("套餐包次数用完");
                } else if (httpCode == 403 && error.equals("Api Market Subscription quota exhausted")) {
                    logger.error("套餐包次数用完，请续购套餐");
                    throw new CrmebException("套餐包次数用完，请续购套餐");
                } else {
                    logger.error("参数名错误 或 其他错误");
                    logger.error(error);
                    throw new CrmebException("参数名错误 或 其他错误");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void syncExpressStatus() {
        //查询商城订单并更新物流状态
        List<StoreOrder> storeOrderList = this.storeOrderService.list(new LambdaQueryWrapper<StoreOrder>().eq(StoreOrder::getStatus, StoreOrderStatusConstants.ORDER_STATUS_INT_SPIKE));
        logger.info("正在同步礼品卡订单数据， 共{}条", storeOrderList.size());
        storeOrderList.stream().filter(giftCardOrder -> StringUtils.isNotBlank(giftCardOrder.getTrackingNo())).forEach(storeOrder -> {
            try {
                this.findExpressDetail(storeOrder.getTrackingNo(), 0, storeOrder.getUserMobile());
            } catch (Exception e) {
                if (!e.getMessage().equals("没有信息"))
                    throw new RuntimeException(e);
            }
        });
        //查询礼品卡订单并更新状态
        List<GiftCardOrder> giftCardOrderList = this.giftCardOrderService.list(new LambdaQueryWrapper<GiftCardOrder>()
                .in(GiftCardOrder::getOrderStatus, Lists.newArrayList(1, 2)));
        logger.info("正在同步礼品卡订单数据， 共{}条", giftCardOrderList.size());
        giftCardOrderList.stream().filter(giftCardOrder -> StringUtils.isNotBlank(giftCardOrder.getTrackingNo())).forEach(giftCardOrder -> {
            UserAddress userAddress = userAddressService.getById(giftCardOrder.getAddressId(), true);
            this.findExpressDetail(giftCardOrder.getTrackingNo(), 1, userAddress.getPhone());
        });
    }

    private Integer transferOrderStatus(String status) {
        switch (status) {
            case "0":
            case "1":
            case "2":
                return StoreOrderStatusConstants.ORDER_STATUS_INT_SPIKE;
            case "3":
                return StoreOrderStatusConstants.ORDER_STATUS_INT_BARGAIN;
        }
        return StoreOrderStatusConstants.ORDER_STATUS_INT_PAID;
    }

    /**
     * 从平台获取物流公司
     * 并存入数据库
     */
    private void getExpressList() {
        String token = onePassUtil.getToken();
        HashMap<String, String> header = onePassUtil.getCommonHeader(token);
        MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
        //        param.add("type", 1);// 快递类型：1，国内运输商；2，国际运输商；3，国际邮政 不传获取全部
        param.add("page", 0);
        param.add("limit", 1000);

        JSONObject post = onePassUtil.postFrom(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_EXPRESS_URI, param, header);
        logger.error("OnePass Express ALL post = " + post);
        JSONObject jsonObject = post.getJSONObject("data");
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (CollUtil.isEmpty(jsonArray)) return;

        List<Express> expressList = CollUtil.newArrayList();
        List<String> codeList = getAllCode();
        jsonArray.forEach(temp -> {
            JSONObject object = (JSONObject) temp;
            if (StrUtil.isNotBlank(object.getString("code")) && !codeList.contains(object.getString("code"))) {
                Express express = new Express();
                express.setName(Optional.ofNullable(object.getString("name")).orElse(""));
                express.setCode(Optional.ofNullable(object.getString("code")).orElse(""));
                express.setPartnerId(false);
                express.setPartnerKey(false);
                express.setNet(false);
                if (ObjectUtil.isNotNull(object.getInteger("partner_id"))) {
                    express.setPartnerId(object.getInteger("partner_id") == 1);
                }
                if (ObjectUtil.isNotNull(object.getInteger("partner_key"))) {
                    express.setPartnerKey(object.getInteger("partner_key") == 1);
                }
                if (ObjectUtil.isNotNull(object.getInteger("net"))) {
                    express.setNet(object.getInteger("net") == 1);
                }
                express.setIsShow(true);
                express.setStatus(false);
                if (!express.getPartnerId() && !express.getPartnerKey() && !express.getNet()) {
                    express.setStatus(true);
                }
                expressList.add(express);
            }
        });

        if (CollUtil.isNotEmpty(expressList)) {
            boolean saveBatch = saveBatch(expressList);
            if (!saveBatch) throw new CrmebException("同步物流公司失败");
        }
    }

    /**
     * 获取所有物流公司code
     */
    private List<String> getAllCode() {
        LambdaQueryWrapper<Express> lqw = new LambdaQueryWrapper<>();
        lqw.select(Express::getCode);
        List<Express> expressList = dao.selectList(lqw);
        if (CollUtil.isEmpty(expressList)) {
            return CollUtil.newArrayList();
        }
        return expressList.stream().map(Express::getCode).collect(Collectors.toList());
    }

    private static String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}

