package com.cshy.common.constants;

public class RedisKey {
    // 限流 redis key
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    //验证码redis key前缀
    public static final String VALIDATE_REDIS_KEY_PREFIX = "validate_code_";

    //用户登token redis存储前缀
    public static final String USER_TOKEN_REDIS_KEY_PREFIX = "TOKEN_USER:";

    //城市数据 redis key
    public static final String CITY_LIST = "city_list";
    //城市数据 tree redis key
    public static final String CITY_LIST_TREE = "city_list_tree";
    //城市数据 tree redis key
    public static final String CITY_LIST_LEVEL_1 = "city_list_level_1";

    //商品库存变动队列key
    public static final String PRODUCT_STOCK_UPDATE = "product_stock_update";
    // 商品秒杀库存队列Key
    public static final String PRODUCT_SEC_KILL_STOCK_UPDATE = "product_seckill_stock_update";
    // 商品砍价库存队列Key
    public static final String PRODUCT_BARGAIN_STOCK_UPDATE = "product_bargain_stock_update";
    // 商品拼团库存队列Key
    public static final String PRODUCT_COMBINATION_STOCK_UPDATE = "product_combination_stock_update";
    //商品库存redis key
    public static final String PRODUCT_STOCK_LIST = "product_stock_list";

    //礼品卡序列号redis key
    public static final String GIFT_CARD_SERIAL_NUMBER = "gift_card_serial_number";


    //后台首页登录图片
    /** 登录页LOGO */
    public static final String CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP = "site_logo_lefttop";
    public static final String CONFIG_KEY_ADMIN_LOGIN_LOGO_LOGIN = "site_logo_login";
    /** 登录页背景图 */
    public static final String CONFIG_KEY_ADMIN_LOGIN_BACKGROUND_IMAGE = "admin_login_bg_pic";

    /** 微信分享图片（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_IMAGE = "wechat_share_img";
    /** 微信分享标题（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_TITLE = "wechat_share_title";
    /** 微信分享简介（公众号） */
    public static final String CONFIG_KEY_ADMIN_WECHAT_SHARE_SYNOSIS = "wechat_share_synopsis";


    //消息模板队列key
    public static final String WE_CHAT_MESSAGE_KEY_PUBLIC = "we_chat_public_message_list";
    public static final String WE_CHAT_MESSAGE_KEY_PROGRAM = "we_chat_program_message_list";
    public static final String WE_CHAT_MESSAGE_INDUSTRY_KEY = "we_chat_message_industry";

    //config配置的key
    public static final String CONFIG_KEY_SITE_URL = "site_url"; //域名
    public static final String CONFIG_KEY_API_URL = "api_url"; //admin接口地址
    public static final String CONFIG_KEY_FRONT_API_URL = "front_api_url"; //移动商城接口地址
    public static final String CONFIG_KEY_SITE_LOGO = "mobile_top_logo"; //logo地址
    public static final String CONFIG_KEY_MOBILE_LOGIN_LOGO = "mobile_login_logo"; // 移动端登录 logo
    public static final String CONFIG_KEY_SITE_NAME = "site_name"; //网站名称
    public static final String CONFIG_SITE_TENCENT_MAP_KEY = "tencent_map_key"; //腾讯地图key
    public static final String CONFIG_BANK_LIST = "user_extract_bank"; //可提现银行
    public static final String CONFIG_EXTRACT_FREEZING_TIME = "extract_time"; //提现冻结时间
    public static final String CONFIG_EXTRACT_MIN_PRICE = "user_extract_min_price"; //提现最低金额
    public static final String CONFIG_RECHARGE_ATTENTION = "recharge_attention"; //充值注意事项

    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_ID = "pay_weixin_appid"; //公众号appId
    public static final String CONFIG_KEY_PAY_WE_CHAT_MCH_ID = "pay_weixin_mchid"; //公众号配的商户号
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_SECRET = "pay_weixin_appsecret"; //公众号秘钥
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_KEY = "pay_weixin_key"; //公众号支付key
    public static final String CONFIG_KEY_PAY_ROUTINE_APP_ID = "pay_routine_appid"; //小程序appId
    public static final String CONFIG_KEY_PAY_ROUTINE_MCH_ID = "pay_routine_mchid"; //小程序分配的商户号
    public static final String CONFIG_KEY_PAY_ROUTINE_APP_SECRET = "pay_routine_appsecret"; //小程序秘钥
    public static final String CONFIG_KEY_PAY_ROUTINE_APP_KEY = "pay_routine_key"; //小程序支付key

    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_APP_ID = "pay_weixin_app_appid"; //公众号appId
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_MCH_ID = "pay_weixin_app_mchid"; //公众号配的商户号
    public static final String CONFIG_KEY_PAY_WE_CHAT_APP_APP_KEY = "pay_weixin_app_key"; //公众号支付key
    public static final String SYS_CONFIG_KEY = "sys_config:";

    public static final String CONFIG_KEY_RECHARGE_MIN_AMOUNT = "store_user_min_recharge"; //最小充值金额
//    public static final String CONFIG_KEY_PROGRAM_LOGO = "routine_logo"; //小程序logo
//    public static final String CONFIG_KEY_PUBLIC_LOGO = "wechat_avatar"; //公众号logo

    //订单操作redis队列
    public static final String ORDER_TASK_REDIS_KEY_AFTER_DELETE_BY_USER = "alterOrderDeleteByUser"; // 用户删除订单后续操作
    public static final String ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER = "alterOrderCompleteByUser"; // 用户完成订单后续操作
    public static final String ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER = "alterOrderCancelByUser"; // 用户取消订单后续操作
    public static final String ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER = "alterOrderRefundByUser"; // 用户订单退款后续操作

    /** 用户注册信息缓存Key */
    public static final String USER_REGISTER_KEY = "USER:REGISTER:";

    /** 商品浏览量（每日） */
    public static final String PRO_PAGE_VIEW_KEY = "statistics:product:page_view:";
    public static final String PRO_PRO_PAGE_VIEW_KEY = "statistics:product:pro_page_view:{}:{}";

    /** 商品加购量（每日） */
    public static final String PRO_ADD_CART_KEY = "statistics:product:add_cart:";
    public static final String PRO_PRO_ADD_CART_KEY = "statistics:product:pro_add_cart:{}:{}";

    //分销
    public static final String CONFIG_KEY_STORE_BROKERAGE_LEVEL = "store_brokerage_rate_num"; //返佣比例前缀
    public static final String CONFIG_KEY_STORE_BROKERAGE_RATE_ONE = "store_brokerage_ratio"; //一级返佣比例前缀
    public static final String CONFIG_KEY_STORE_BROKERAGE_RATE_TWO = "store_brokerage_two"; //二级返佣比例前缀
    public static final String CONFIG_KEY_STORE_BROKERAGE_USER_EXTRACT_MIN_PRICE = "user_extract_min_price"; //提现最低金额
    public static final String CONFIG_KEY_STORE_BROKERAGE_MODEL = "store_brokerage_status"; //分销模式1-指定分销2-人人分销
    public static final String CONFIG_KEY_STORE_BROKERAGE_USER_EXTRACT_BANK = "user_extract_bank"; //提现银行卡
    public static final String CONFIG_KEY_STORE_BROKERAGE_EXTRACT_TIME = "extract_time"; //佣金冻结时间
    public static final String CONFIG_KEY_STORE_INTEGRAL_EXTRACT_TIME = "freeze_integral_day"; //积分冻结时间
    public static final String CONFIG_KEY_STORE_BROKERAGE_PERSON_PRICE = "store_brokerage_price"; //人人分销满足金额
    public static final String CONFIG_KEY_STORE_BROKERAGE_IS_OPEN = "brokerage_func_status"; //分销启用
    public static final String CONFIG_KEY_STORE_BROKERAGE_BIND_TYPE = "brokerageBindind"; //分销关系绑定0-所有游湖，2-新用户

}
