package com.cshy.service.impl;

import com.cshy.common.constants.Constants;
import com.cshy.common.constants.DateConstants;
import com.cshy.common.constants.NumConstants;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.common.model.response.HomeRateResponse;
import com.cshy.common.utils.DateUtil;
import com.cshy.service.service.HomeService;
import com.cshy.service.service.store.StoreOrderService;
import com.cshy.service.service.user.UserService;
import com.cshy.service.service.user.UserVisitRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 用户表 服务实现类

 */
@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserVisitRecordService userVisitRecordService;

    @Override
    public Map<Object, Object> chartUser() {
        return dataFormat(userService.getAddUserCountGroupDate(DateConstants.SEARCH_DATE_LATELY_30), DateConstants.SEARCH_DATE_LATELY_30);
    }

    @Override
    public Map<String, Object> chartOrder() {
        Map<String, Object> map = new HashMap<>();

        List<StoreOrder> list = storeOrderService.getOrderGroupByDate(DateConstants.SEARCH_DATE_LATELY_30, NumConstants.NUM_TEN);

        map.put("quality",
                dataFormat(getOrderCountGroupByDate(list), DateConstants.SEARCH_DATE_LATELY_30)
        );
        map.put("price",
                dataFormat(getOrderPriceGroupByDate(list), DateConstants.SEARCH_DATE_LATELY_30)
        );
        return map;
    }

    private Map<Object, Object> getOrderCountGroupByDate(List<StoreOrder> list) {
        Map<Object, Object> map = new HashMap<>();

        if (list.size() < 1) {
            return map;
        }

        for (StoreOrder storeOrder : list) {
            map.put(storeOrder.getOrderId(), storeOrder.getId());
        }

        return map;
    }

    private Map<Object, Object> getOrderPriceGroupByDate(List<StoreOrder> list) {
        Map<Object, Object> map = new HashMap<>();

        if (list.size() < 1) {
            return map;
        }

        for (StoreOrder storeOrder : list) {
            map.put(storeOrder.getOrderId(), storeOrder.getPayPrice());
        }

        return map;
    }

    private Map<Object, Object> dataFormat(Map<Object, Object> countGroupDate, String dateLimit) {
        Map<Object, Object> map = new LinkedHashMap<>();
        List<String> listDate = DateUtil.getListDate(dateLimit);

        String[] weekList = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        int i = 0;
        for (String date : listDate) {
            Object count = 0;
            if (countGroupDate.containsKey(date)) {
                count = countGroupDate.get(date);
            }
            String key;

            //周格式化
            switch(dateLimit) {
                //格式化周
                case DateConstants.SEARCH_DATE_WEEK:
                case DateConstants.SEARCH_DATE_PRE_WEEK:
                    key = weekList[i];
                    break;
                    //格式化月
                case DateConstants.SEARCH_DATE_PRE_MONTH:
                case DateConstants.SEARCH_DATE_MONTH:
                    key = i + 1 + "";
                    break;
                    //默认显示两位日期
                default:
                    key = date.substring(5, 10);
            }
            map.put(key, count);
            i++;
        }
        return map;
    }

    private Map<Object, Object> dataFormatYear(Map<Object, Object> countGroupDate, String dateLimit) {
        Map<Object, Object> map = new LinkedHashMap<>();
        List<Object> listDate = new ArrayList<>();
        String year = "";
        if (dateLimit.equals(DateConstants.SEARCH_DATE_YEAR)) {
            year = DateUtil.nowYear();
        }

        if (dateLimit.equals(DateConstants.SEARCH_DATE_PRE_YEAR)) {
            year = DateUtil.lastYear();
        }

        //处理年
        //12个月份数据
        for (int i = 1; i <= 12; i++) {
            String month = i + "";
            if (i < 10) {
                month = "0" + i;
            }
            listDate.add(year + "-" + month);
        }

        String[] monthList = new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月" , "十一月", "十二月" };

        int i = 0;
        for (Object date : listDate) {
            Object count = 0;
            if (countGroupDate.containsKey(date)) {
                count = countGroupDate.get(date);
            }
            map.put(monthList[i], count);
            i++;
        }
        return map;
    }

    @Override
    public Map<String, Integer> chartUserBuy() {
        Map<String, Integer> map = new HashMap<>();
        //未消费用户
        map.put("zero", userService.getCountByPayCount(NumConstants.NUM_ZERO, NumConstants.NUM_ZERO));

        //消费一次用户
        map.put("one", userService.getCountByPayCount(NumConstants.NUM_ONE, NumConstants.NUM_ONE));

        //留存客户
        map.put("history", userService.getCountByPayCount(NumConstants.NUM_TWO, NumConstants.NUM_THREE));

        //回流客户
        map.put("back", userService.getCountByPayCount(NumConstants.NUM_ONE, Constants.EXPORT_MAX_LIMIT));

        return map;
    }

    @Override
    public Map<String, Object> chartOrderInWeek() {
        return returnOrderDate(DateConstants.SEARCH_DATE_WEEK, DateConstants.SEARCH_DATE_PRE_WEEK, NumConstants.NUM_TEN);


    }

    @Override
    public Map<String, Object> chartOrderInMonth() {
        return returnOrderDate(DateConstants.SEARCH_DATE_MONTH, DateConstants.SEARCH_DATE_PRE_MONTH, NumConstants.NUM_TEN);
    }

    @Override
    public Map<String, Object> chartOrderInYear() {
        Map<String, Object> map = new HashMap<>();

        //查询本年订单量
        List<StoreOrder> list = storeOrderService.getOrderGroupByDate(DateConstants.SEARCH_DATE_YEAR, NumConstants.NUM_SEVEN);

        map.put("quality",
                dataFormatYear(getOrderCountGroupByDate(list), DateConstants.SEARCH_DATE_YEAR)
        );
        map.put("price",
                dataFormatYear(getOrderPriceGroupByDate(list), DateConstants.SEARCH_DATE_YEAR)
        );

        //查询上年订单量
        List<StoreOrder> preList = storeOrderService.getOrderGroupByDate(DateConstants.SEARCH_DATE_PRE_YEAR, NumConstants.NUM_SEVEN);

        map.put("preQuality",
                dataFormatYear(getOrderCountGroupByDate(preList), DateConstants.SEARCH_DATE_PRE_YEAR)
        );
        map.put("prePrice",
                dataFormatYear(getOrderPriceGroupByDate(preList), DateConstants.SEARCH_DATE_PRE_YEAR)
        );

        return map;
    }

    /**
     * 首页数据
     * 今日/昨日
     * 销售额
     * 用户访问量
     * 订单量
     * 新增用户
     * @return HomeRateResponse
     */
    @Override
    public HomeRateResponse indexDate() {
        String today = cn.hutool.core.date.DateUtil.date().toString("yyyy-MM-dd");
        String yesterday = cn.hutool.core.date.DateUtil.yesterday().toString("yyyy-MM-dd");
        HomeRateResponse response = new HomeRateResponse();
        response.setSales(storeOrderService.getPayOrderAmountByDate(today));
        response.setYesterdaySales(storeOrderService.getPayOrderAmountByDate(yesterday));
        response.setPageviews(userVisitRecordService.getPageviewsByDate(today));
        response.setYesterdayPageviews(userVisitRecordService.getPageviewsByDate(yesterday));
        response.setOrderNum(storeOrderService.getOrderNumByDate(today));
        response.setYesterdayOrderNum(storeOrderService.getOrderNumByDate(yesterday));
        response.setNewUserNum(userService.getRegisterNumByDate(today));
        response.setYesterdayNewUserNum(userService.getRegisterNumByDate(yesterday));
        return response;
    }

    private Map<String, Object> returnOrderDate(String dateLimit, String preDateLimit, int leftTime) {
        Map<String, Object> map = new HashMap<>();

        //查询本周周订单量
        List<StoreOrder> list = storeOrderService.getOrderGroupByDate(dateLimit, leftTime);

        map.put("quality",
                dataFormat(getOrderCountGroupByDate(list), dateLimit)
        );
        map.put("price",
                dataFormat(getOrderPriceGroupByDate(list), dateLimit)
        );

        //查询上周周订单量
        List<StoreOrder> preList = storeOrderService.getOrderGroupByDate(preDateLimit, leftTime);

        map.put("preQuality",
                dataFormat(getOrderCountGroupByDate(preList), preDateLimit)
        );
        map.put("prePrice",
                dataFormat(getOrderPriceGroupByDate(preList), preDateLimit)
        );

        return map;
    }

}
