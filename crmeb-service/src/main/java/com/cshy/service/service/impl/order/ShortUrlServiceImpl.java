package com.cshy.service.service.impl.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.dto.order.ShortUrlDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.order.ShortUrl;
import com.cshy.common.model.entity.order.StoreOrder;
import com.cshy.common.model.query.order.ShortUrlQuery;
import com.cshy.common.model.vo.order.ShortUrlVo;
import com.cshy.service.dao.store.ShortUrlDao;
import com.cshy.service.service.order.ShortUrlService;
import com.cshy.service.service.store.StoreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ShortUrlServiceImpl extends BaseServiceImpl<ShortUrl, ShortUrlDto,
        ShortUrlQuery, ShortUrlVo, ShortUrlDao> implements ShortUrlService {

    @Autowired
    private StoreOrderService storeOrderService;

    @Value("${domainUrl}")
    private String domainUrl;

    private static final int SHORT_CODE_LENGTH = 7;

    private static final String hashTag = "#";

    private Random random;

    public ShortUrlServiceImpl() {
        this.random = new Random();
    }

    @Override
    public String expandUrl(String shortUrl) {
        String shortCode = shortUrl.replace(domainUrl, "");
        //校验
        String regex = "^[A-Z]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(shortCode);
        if (!matcher.matches())
            throw new CrmebException("短连接不匹配");

        //查询
        String param = this.expandURL(shortCode);

        String finalUrl = domainUrl + param;
        return finalUrl;
    }

    @Override
    public String shortenURL(String param, Integer location) {
        String shortCode = generateShortCode();
        //查询是否已存在关联的短链
        ShortUrl orderUrl = this.getOne(new LambdaQueryWrapper<ShortUrl>().eq(ShortUrl::getParam, param));
        if (Objects.nonNull(orderUrl))
            shortCode = orderUrl.getCode();
        // 将长URL转换为短URL
         else {
             //重复则重新生成
             orderUrl = this.getOne(new LambdaQueryWrapper<ShortUrl>().eq(ShortUrl::getCode, shortCode));
             while (Objects.nonNull(orderUrl))
                 shortCode = generateShortCode();
            ShortUrlDto orderUrlDto = new ShortUrlDto();
            orderUrlDto.setParam(param);
            orderUrlDto.setCode(shortCode);
            orderUrlDto.setLocation(location);
            this.add(orderUrlDto);
        }

        String shortURL = domainUrl + shortCode;
        return shortURL;
    }

    private String getH5OrderStatus(StoreOrder storeOrder) {
        if (!storeOrder.getPaid()) {
            return "待支付";
        }
        if (storeOrder.getRefundStatus().equals(1)) {
            return "申请退款中";
        }
        if (storeOrder.getRefundStatus().equals(2)) {
            return "已退款";
        }
        if (storeOrder.getRefundStatus().equals(3)) {
            return "退款中";
        }
        if (storeOrder.getStatus().equals(0)) {
            return "待发货";
        }
        if (storeOrder.getStatus().equals(1)) {
            return "待收货";
        }
        if (storeOrder.getStatus().equals(2)) {
            return "待评价";
        }
        if (storeOrder.getStatus().equals(3)) {
            return "已完成";
        }
        return "";
    }

    // 生成随机的短码
    private String generateShortCode() {
        StringBuilder shortCode = new StringBuilder();
        String characters = "QWERTYUIOPASDFGHJKLZXCVBNM";

        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int index = random.nextInt(characters.length());
            shortCode.append(characters.charAt(index));
        }

        return shortCode.toString();
    }

    // 根据短URL还原为长URL
    public String expandURL(String shortCode) {
        String expand;
        //查询数据库
        ShortUrl shortURL = this.getOne(new LambdaQueryWrapper<ShortUrl>().eq(ShortUrl::getCode, shortCode));
        if (Objects.nonNull(shortURL)) {
            expand = shortURL.getParam();
            //普通订单需要额外拼接数据
            if (shortURL.getLocation() == 0) {
                StoreOrder storeOrder = this.storeOrderService.getByOrderId(expand.substring(expand.lastIndexOf("=") + 1));
                String status = getH5OrderStatus(storeOrder);
                expand += "&status=" + status;
            }
            return expand;
        }
        throw new CrmebException("短连接解析错误");
    }
}
