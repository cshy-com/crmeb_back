package com.cshy.common.token;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.cshy.common.constants.RedisKey;
import com.cshy.common.model.entity.user.User;
import com.cshy.common.utils.RedisUtil;
import com.cshy.common.utils.RequestUtil;
import com.cshy.common.model.vo.LoginUserVo;
import com.cshy.common.constants.Constants;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * token验证处理
 */
@Component
public class FrontTokenComponent {

    @Resource
    private RedisUtil redisUtil;

    @Value("${token.expireTime.front}")
    private long expireTime;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    private static final Long MILLIS_MINUTE = 60 * 1000L;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUserVo getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StrUtil.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            return redisUtil.get(userKey);
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUserVo loginUser) {
        if (ObjectUtil.isNotNull(loginUser) && StrUtil.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StrUtil.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            redisUtil.delete(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param user 用户信息
     * @return 令牌
     */
    public String createToken(User user) {
        String token = UUID.randomUUID().toString().replace("-", "");
        redisUtil.set(getTokenKey(token), user.getUid(), expireTime, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser LoginUserVo
     */
    public void verifyToken(LoginUserVo loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUserVo loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisUtil.set(userKey, loginUser, (long) expireTime, TimeUnit.MINUTES);
    }

    /**
     * 获取请求token
     *
     * @param request HttpServletRequest
     * @return token
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(Constants.HEADER_AUTHORIZATION_KEY);
        if (StrUtil.isNotEmpty(token) && token.startsWith(RedisKey.USER_TOKEN_REDIS_KEY_PREFIX)) {
            token = token.replace(RedisKey.USER_TOKEN_REDIS_KEY_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return RedisKey.USER_TOKEN_REDIS_KEY_PREFIX + uuid;
    }

    /**
     * 推出登录
     *
     * @param request HttpServletRequest
     */
    public void logout(HttpServletRequest request) {
        String token = getToken(request);
        delLoginUser(token);
    }

    /**
     * 获取当前登录用户id
     */
    public Integer getUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return null;
//            throw new CrmebException("登录信息已过期，请重新登录！");
        }
        return redisUtil.get(getTokenKey(token));
    }

    //路由在此处，则返回true，无论用户是否登录都可以访问
    public boolean checkRouter(String uri) {
        String[] routerList = {
                "api/front/product/detail",
                "api/front/coupons",
                "api/front/index",
                "api/front/bargain/list",
                "api/front/combination/list",
                "api/front/index/product",
                "api/front/combination/index",
                "api/front/bargain/index",
                "api/front/index/color/config",
                "api/front/product/list",
                "api/front/product/sku/detail",
                "api/front/index/get/version",
                "api/front/image/domain",
                "api/front/product/leaderboard",
                "api/front/giftCard",
                "api/front/express/findExpressDetail",
                "api/front/url/shortener/expand",
                "api/front/url/shortener/shorten",
                "api/front/common/contact/phone",
                "api/front/sys/banner/activity/config",
                "api/front/activity",
                "api/front/sys/home/config",
                "api/front/system/config",
                "api/front/integral/coupon/isUsed",
        };

        boolean present = Arrays.stream(routerList).anyMatch(router -> router.equals(uri) || validateApiPath(uri, router));
        return present;
    }

    private boolean validateApiPath(String apiPath, String apiPathRule) {
        // 将规则中的斜杠转义
        String escapedLastApiPathRule = apiPathRule.replace("/", "\\/");
        // 构建正则表达式
        String regex = escapedLastApiPathRule + ".*";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建匹配器
        Matcher matcher = pattern.matcher(apiPath);
        // 进行匹配
        return matcher.matches();
    }

    public Boolean check(String token, HttpServletRequest request) {

        try {
            boolean exists = redisUtil.exists(getTokenKey(token));
            if (exists) {
                Integer uid = redisUtil.get(getTokenKey(token));
                redisUtil.set(getTokenKey(token), uid, expireTime, TimeUnit.MINUTES);
            } else {
                //判断路由，部分路由不管用户是否登录/token过期都可以访问
                exists = checkRouter(RequestUtil.getUri(request));
            }
            return exists;
        } catch (Exception e) {
            return false;
        }
    }
}
