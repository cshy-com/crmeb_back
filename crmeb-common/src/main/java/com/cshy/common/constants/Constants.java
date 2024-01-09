package com.cshy.common.constants;

import io.jsonwebtoken.Claims;

/**
 *  配置类

 */
public class Constants {
    //头部 token令牌key
    public static final String HEADER_AUTHORIZATION_KEY = "Authorization";

    //后台管理员操作资金mark
    public static final String USER_BILL_OPERATE_LOG_TITLE = "{$title}{$operate}了{$value}{$founds}";

    //用户登录密码加密混淆字符串
    public static final String USER_LOGIN_PASSWORD_MD5_KEYWORDS = "crmeb";

    //用户默认头像
    public static final String USER_DEFAULT_AVATAR_CONFIG_KEY = "h5_avatar";

    //用户默认推广人id
    public static final Integer USER_DEFAULT_SPREAD_ID = 0;

    //默认分页
    public static final int DEFAULT_PAGE = 1;

    //默认分页
    public static final int DEFAULT_LIMIT = 20;

    //导出最大数值
    public static final Integer EXPORT_MAX_LIMIT = 99999;

    //商品最多选择的分类数量
    public static final Integer PRODUCT_SELECT_CATEGORY_NUM_MAX = 10;

    //云智服 小程序插件
    public static final String CONFIG_KEY_YZF_H5_URL = "yzf_h5_url"; //云智服H5 url
    // 客服电话
    public static final String CONFIG_KEY_CONSUMER_HOTLINE = "consumer_hotline";
    // 客服电话服务开关
    public static final String CONFIG_KEY_TELEPHONE_SERVICE_SWITCH = "telephone_service_switch";
    //商品分类页配置
    public static final String CONFIG_CATEGORY_CONFIG = "category_page_config"; //商品分类页配置
    public static final String CONFIG_IS_SHOW_CATEGORY = "is_show_category"; //是否隐藏一级分类
    public static final String CONFIG_IS_PRODUCT_LIST_STYLE = "homePageSaleListStyle"; //首页商品列表模板配置
    // app 版本号
    public static final String CONFIG_APP_VERSION = "app_version";
    // android版本地址
    public static final String CONFIG_APP_ANDROID_ADDRESS = "android_address";
    // ios版本地址
    public static final String CONFIG_APP_IOS_ADDRESS = "ios_address";
    // 开放式升级
    public static final String CONFIG_APP_OPEN_UPGRADE = "open_upgrade";

    //分销
    public static final String CONFIG_KEY_DISTRIBUTION_TYPE = "brokerage_bindind";

    //验证码过期时间
    public static final String CONFIG_KEY_SMS_CODE_EXPIRE = "sms_code_expire";

    //短信服务key
    public static final String SMS_KEY = "sms_key";

    //短信服务secret
    public static final String SMS_SECRET = "sms_secret";

    //签到
    public static final Integer SIGN_TYPE_INTEGRAL = 1; //积分
    public static final Integer SIGN_TYPE_EXPERIENCE = 2; //经验
    public static final String SIGN_TYPE_INTEGRAL_TITLE = "签到积分奖励"; //积分
    public static final String SIGN_TYPE_EXPERIENCE_TITLE = "签到经验奖励"; //经验

    //分类服务类型  类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类， 6 配置分类， 7 秒杀配置
    public static final int CATEGORY_TYPE_PRODUCT = 1; //产品
    public static final int CATEGORY_TYPE_ATTACHMENT = 2; //附件分类
    public static final int CATEGORY_TYPE_ARTICLE = 3; //文章分类
    public static final int CATEGORY_TYPE_SET = 4; //设置分类
    public static final int CATEGORY_TYPE_MENU = 5; //菜单分类
    public static final int CATEGORY_TYPE_CONFIG = 6; //配置分类
    public static final int CATEGORY_TYPE_SKILL = 7; //秒杀配置

    //首页Banner图片
    public static final int INDEX_RECOMMEND_BANNER = 1; //首页精品推荐Banner图片
    public static final int INDEX_HOT_BANNER = 2; //热门榜单推荐Banner图片
    public static final int INDEX_NEW_BANNER = 3; //首页首发新品推荐Banner图片
    public static final int INDEX_BENEFIT_BANNER = 4; //首页促销单品推荐Banner图片
    public static final int INDEX_LIMIT_DEFAULT = 3; //首页默认list分页条数
    public static final int INDEX_GOOD_BANNER = 5; //优选推荐

    public static final String INDEX_BAST_LIMIT = "bastNumber"; //精品推荐个数
    public static final String INDEX_FIRST_LIMIT = "firstNumber"; //首发新品个数
    public static final String INDEX_SALES_LIMIT = "promotionNumber"; //促销单品个数
    public static final String INDEX_HOT_LIMIT = "hotNumber"; //热门推荐个数


    //用户资金
    public static final String USER_BILL_CATEGORY_MONEY = "now_money"; //用户余额
    public static final String USER_BILL_CATEGORY_INTEGRAL = "integral"; //积分
    public static final String USER_BILL_CATEGORY_SHARE = "share"; //分享
    public static final String USER_BILL_CATEGORY_EXPERIENCE = "experience"; //经验
    public static final String USER_BILL_CATEGORY_BROKERAGE_PRICE = "brokerage_price"; //佣金金额
    public static final String USER_BILL_CATEGORY_SIGN_NUM = "sign_num"; //签到天数

    public static final String USER_BILL_TYPE_BROKERAGE = "brokerage"; //推广佣金
    public static final String USER_BILL_TYPE_DEDUCTION = "deduction"; //抵扣
    public static final String USER_BILL_TYPE_EXTRACT = "extract"; //提现
    public static final String USER_BILL_TYPE_TRANSFER_IN = "transferIn"; //佣金转入余额
    public static final String USER_BILL_TYPE_GAIN = "gain"; //购买商品赠送
    public static final String USER_BILL_TYPE_PAY_MONEY = "pay_money"; //购买
    public static final String USER_BILL_TYPE_PAY_PRODUCT = "pay_product"; //购买商品
    public static final String USER_BILL_TYPE_PAY_PRODUCT_INTEGRAL_BACK = "pay_product_integral_back"; //商品退积分
    public static final String USER_BILL_TYPE_PAY_PRODUCT_REFUND = "pay_product_refund"; //商品退款
    public static final String USER_BILL_TYPE_RECHARGE = "recharge"; //佣金转入
    public static final String USER_BILL_TYPE_PAY_RECHARGE = "pay_recharge"; //充值
    public static final String USER_BILL_TYPE_SHARE = "share"; //用户分享记录
    public static final String USER_BILL_TYPE_SIGN = "sign"; //签到
    public static final String USER_BILL_TYPE_ORDER = "order"; //订单
    public static final String USER_BILL_TYPE_PAY_ORDER = "pay_order"; //订单支付
    public static final String USER_BILL_TYPE_SYSTEM_ADD = "system_add"; //系统增加
    public static final String USER_BILL_TYPE_SYSTEM_SUB = "system_sub"; //系统减少
    public static final String USER_BILL_TYPE_PAY_MEMBER = "pay_member";// 会员支付
    public static final String USER_BILL_TYPE_OFFLINE_SCAN = "offline_scan";// 线下支付
    public static final String USER_BILL_TYPE_USER_RECHARGE_REFUND = "user_recharge_refund";// 用户充值退款

    //后台微信登录类型
    public static final String ADMIN_LOGIN_TYPE_WE_CHAT_FROM_PUBLIC = "admin_public"; //公众号

    //需要支付的业务类型
    public static final String SERVICE_PAY_TYPE_ORDER = "order"; //订单
    public static final String SERVICE_PAY_TYPE_RECHARGE = "recharge"; //充值

    // 订单缓存
    public static final long ORDER_CASH_CONFIRM = (60);

    //支付渠道 订单表
    public static final int ORDER_PAY_CHANNEL_PUBLIC = 0; //公众号
    public static final int ORDER_PAY_CHANNEL_PROGRAM = 1; //小程序
    public static final int ORDER_PAY_CHANNEL_H5 = 2; //H5
    public static final int ORDER_PAY_CHANNEL_YUE = 3; //余额
    public static final int ORDER_PAY_CHANNEL_APP_IOS = 4; //app-ios
    public static final int ORDER_PAY_CHANNEL_APP_ANDROID = 5; //app-android

    //微信消息模板 tempKey
    public static final String WE_CHAT_TEMP_KEY_FIRST = "first";
    public static final String WE_CHAT_TEMP_KEY_END = "remark";

    //CND  URL测试用
    public static String CND_URL = "https://wuht-1300909283.cos.ap-chengdu.myqcloud.com";

    // 砍价计算比例下行
    public static String BARGAIN_TATIO_DOWN = "0.2";
    // 砍价计算比例上行
    public static String BARGAIN_TATIO_UP = "0.8";

    // 时间类型开始时间
    public static String DATE_TIME_TYPE_BEGIN = "begin";

    // 时间类型结束时间
    public static String DATE_TIME_TYPE_END = "end";

    // 商品评论类型——普通商品
    public static String STORE_REPLY_TYPE_PRODUCT = "product";
    // 商品评论类型——秒杀
    public static String STORE_REPLY_TYPE_SECKILL = "seckill";
    // 商品评论类型——拼团
    public static String STORE_REPLY_TYPE_PINTUAN = "pintuan";
    // 商品评论类型——砍价
    public static String STORE_REPLY_TYPE_BARGAIN = "bargain";

    // 商品记录Key（pv、uv）用
    public static String PRODUCT_LOG_KEY = "visit_log_key";

    public static final String FAIL     = "FAIL";
    public static final String SUCCESS  = "SUCCESS";

    // 订单取消Key
    public static final String ORDER_AUTO_CANCEL_KEY = "order_auto_cancel_key";

    /** 公共开关：0关闭 */
    public static final String COMMON_SWITCH_CLOSE = "0";
    /** 公共开关：1开启 */
    public static final String COMMON_SWITCH_OPEN = "1";

    /** 公共JS配置 */
    // CRMEB chat 统计
    public  static final String JS_CONFIG_CRMEB_CHAT_TONGJI="crmeb_tongji_js";

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";
}
