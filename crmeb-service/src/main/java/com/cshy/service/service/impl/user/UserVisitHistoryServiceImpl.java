package com.cshy.service.service.impl.user;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cshy.common.constants.Constants;
import com.cshy.common.model.dto.user.UserVisitHistoryDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.product.StoreProduct;
import com.cshy.common.model.entity.user.UserVisitHistory;
import com.cshy.common.model.query.user.UserVisitHistoryQuery;
import com.cshy.common.model.vo.user.UserVisitHistoryVo;
import com.cshy.service.dao.user.UserVisitHistoryDao;
import com.cshy.service.service.store.StoreProductService;
import com.cshy.service.service.user.UserVisitHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class UserVisitHistoryServiceImpl extends BaseServiceImpl<UserVisitHistory, UserVisitHistoryDto,
        UserVisitHistoryQuery, UserVisitHistoryVo, UserVisitHistoryDao> implements UserVisitHistoryService {

    @Autowired
    private StoreProductService storeProductService;

    @Override
    protected void onBeforePage(UserVisitHistoryQuery query, QueryWrapper<UserVisitHistory> queryWrapper) {
        String date = query.getDate();
        DateTime dateTime = DateUtil.parse(date, Constants.DATE_FORMAT_DATE);
        String start = DateUtil.format(dateTime, Constants.DATE_FORMAT_START);
        String end = DateUtil.format(dateTime, Constants.DATE_FORMAT_END);
        queryWrapper.between("create_time", start, end);
        super.onBeforePage(query, queryWrapper);
    }

    @Override
    protected void onAfterPage(Page<UserVisitHistoryVo> page) {
        List<UserVisitHistoryVo> records = page.getRecords();
        records.forEach(userVisitHistoryVo -> {
            StoreProduct storeProduct = storeProductService.getById(userVisitHistoryVo.getProductId());
            Map<String, Object> map = new HashMap<>();
            map.put("productName", storeProduct.getStoreName());
            map.put("image", storeProduct.getImage());
            map.put("price", storeProduct.getPrice());
            userVisitHistoryVo.setProductInfo(map);
        });
        super.onAfterPage(page);
    }
}
