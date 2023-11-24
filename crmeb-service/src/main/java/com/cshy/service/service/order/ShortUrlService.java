package com.cshy.service.service.order;

import com.cshy.common.model.dto.order.ShortUrlDto;
import com.cshy.common.model.entity.base.BaseService;
import com.cshy.common.model.entity.order.ShortUrl;
import com.cshy.common.model.query.order.ShortUrlQuery;
import com.cshy.common.model.vo.order.ShortUrlVo;

public interface ShortUrlService extends BaseService<ShortUrl, ShortUrlDto, ShortUrlQuery, ShortUrlVo> {
    String expandUrl(String shortUrl, Integer location);

    String shortenURL(String param, Integer location);
}
