package com.cshy.service.impl.store;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.vo.order.OrderInfoDetailVo;
import com.cshy.common.model.vo.order.StoreOrderInfoOldVo;
import com.cshy.common.model.vo.order.StoreOrderInfoVo;
import com.cshy.common.model.entity.order.StoreOrderInfo;
import com.cshy.service.dao.store.StoreOrderInfoDao;
import com.cshy.service.service.store.StoreOrderInfoService;
import com.cshy.service.service.store.StoreProductReplyService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * StoreOrderInfoServiceImpl 接口实现

 */
@Service
public class StoreOrderInfoServiceImpl extends ServiceImpl<StoreOrderInfoDao, StoreOrderInfo>
        implements StoreOrderInfoService {

    @Resource
    private StoreOrderInfoDao dao;

    @Autowired
    private StoreProductReplyService storeProductReplyService;

    @Override
    public HashMap<Integer, List<StoreOrderInfoOldVo>> getMapInId(List<Integer> orderList){
        HashMap<Integer, List<StoreOrderInfoOldVo>> map = new HashMap<>();
        if(orderList.size() < 1){
            return map;
        }
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(StoreOrderInfo::getOrderId, orderList);
        List<StoreOrderInfo> storeOrderInfoList = dao.selectList(lambdaQueryWrapper);
        if(storeOrderInfoList.size() < 1){
            return map;
        }
        for (StoreOrderInfo storeOrderInfo : storeOrderInfoList) {
            //解析商品详情JSON
            StoreOrderInfoOldVo StoreOrderInfoVo = new StoreOrderInfoOldVo();
            BeanUtils.copyProperties(storeOrderInfo, StoreOrderInfoVo, "info");
            StoreOrderInfoVo.setInfo(JSON.parseObject(storeOrderInfo.getInfo(), OrderInfoDetailVo.class));
            StoreOrderInfoVo.getInfo().setShipNum(storeOrderInfo.getShipNum());
            if(map.containsKey(storeOrderInfo.getOrderId())){
                map.get(storeOrderInfo.getOrderId()).add(StoreOrderInfoVo);
            }else{
                List<StoreOrderInfoOldVo> storeOrderInfoVoList = new ArrayList<>();
                storeOrderInfoVoList.add(StoreOrderInfoVo);
                map.put(storeOrderInfo.getOrderId(), storeOrderInfoVoList);
            }
        }
        return map;
    }

    @Override
    public List<StoreOrderInfoOldVo> getOrderListByOrderId(Integer orderId){
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreOrderInfo::getOrderId, orderId);
        List<StoreOrderInfo> storeOrderInfoList = dao.selectList(lambdaQueryWrapper);
        if(storeOrderInfoList.size() < 1){
            return null;
        }

        List<StoreOrderInfoOldVo> storeOrderInfoVoList = new ArrayList<>();
        for (StoreOrderInfo storeOrderInfo : storeOrderInfoList) {
            //解析商品详情JSON
            StoreOrderInfoOldVo storeOrderInfoVo = new StoreOrderInfoOldVo();
            BeanUtils.copyProperties(storeOrderInfo, storeOrderInfoVo, "info");
            storeOrderInfoVo.setInfo(JSON.parseObject(storeOrderInfo.getInfo(), OrderInfoDetailVo.class));
            storeOrderInfoVo.setShipNum(storeOrderInfo.getShipNum());
            storeOrderInfoVo.getInfo().setIsReply(
                    storeProductReplyService.isReply(storeOrderInfoVo.getUnique(), storeOrderInfoVo.getOrderId()) ? 1 : 0
            );
            storeOrderInfoVoList.add(storeOrderInfoVo);
        }
        return storeOrderInfoVoList;
    }

    /**
     * 根据id集合查询数据，返回 map
     * @param orderId 订单id
     * @return HashMap<Integer, StoreCart>
     */
    @Override
    public List<StoreOrderInfoVo> getVoListByOrderId(Integer orderId){
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreOrderInfo::getOrderId, orderId);
        List<StoreOrderInfo> systemStoreStaffList = dao.selectList(lambdaQueryWrapper);
        if(systemStoreStaffList.size() < 1){
            return null;
        }

        List<StoreOrderInfoVo> storeOrderInfoVoList = new ArrayList<>();
        for (StoreOrderInfo storeOrderInfo : systemStoreStaffList) {
            //解析商品详情JSON
            StoreOrderInfoVo storeOrderInfoVo = new StoreOrderInfoVo();
            BeanUtils.copyProperties(storeOrderInfo, storeOrderInfoVo, "info");
            storeOrderInfoVo.setInfo(JSON.parseObject(storeOrderInfo.getInfo(), OrderInfoDetailVo.class));
            storeOrderInfoVoList.add(storeOrderInfoVo);
        }
        return storeOrderInfoVoList;
    }

    /**
     * 获取订单详情-订单编号
     * @param orderNo 订单编号
     * @return List
     */
    @Override
    public List<StoreOrderInfo> getListByOrderNo(String orderNo) {
        LambdaQueryWrapper<StoreOrderInfo> lqw = Wrappers.lambdaQuery();
        lqw.eq(StoreOrderInfo::getOrderNo, orderNo);
        return dao.selectList(lqw);
    }

    /**
     * 根据时间、商品id获取销售件数
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return Integer
     */
    @Override
    public Integer getSalesNumByDateAndProductId(String date, Integer proId) {
        return dao.getSalesNumByDateAndProductId(date, proId);
    }

    /**
     * 根据时间、商品id获取销售额
     * @param date 时间，格式'yyyy-MM-dd'
     * @param proId 商品id
     * @return BigDecimal
     */
    @Override
    public BigDecimal getSalesByDateAndProductId(String date, Integer proId) {
        return dao.getSalesByDateAndProductId(date, proId);
    }

    /**
     * 新增订单详情
     * @param storeOrderInfos 订单详情集合
     * @return 订单新增结果
     */
    @Override
    public boolean saveOrderInfos(List<StoreOrderInfo> storeOrderInfos) {
        return saveBatch(storeOrderInfos);
    }

    /**
     * 通过订单编号和规格号查询
     * @param uni 规格号
     * @param orderId 订单编号
     * @return StoreOrderInfo
     */
    @Override
    public StoreOrderInfo getByUniAndOrderId(String uni, Integer orderId) {
        //带 StoreOrderInfo 类的多条件查询
        LambdaQueryWrapper<StoreOrderInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(StoreOrderInfo::getOrderId, orderId);
        lambdaQueryWrapper.eq(StoreOrderInfo::getUnique, uni);
        return dao.selectOne(lambdaQueryWrapper);
    }
}

