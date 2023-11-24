package com.cshy.service.service.impl.sms;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cshy.common.constants.OnePassConstants;
import com.cshy.common.constants.SmsConstants;
import com.cshy.common.model.entity.sms.SmsRecord;
import com.cshy.common.model.entity.sms.SmsTemplate;
import com.cshy.common.model.entity.system.SystemAdmin;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.request.sms.SmsRecordsRequest;
import com.cshy.common.model.vo.OnePassLoginVo;
import com.cshy.common.model.vo.SendSmsVo;
import com.cshy.common.utils.*;
import com.cshy.common.constants.Constants;
import com.cshy.common.exception.CrmebException;
import com.cshy.service.dao.SmsRecordDao;
import com.cshy.service.service.sms.SmsRecordService;
import com.cshy.service.service.sms.SmsService;
import com.cshy.service.service.sms.SmsTemplateService;
import com.cshy.service.service.system.SystemAdminService;
import com.cshy.service.service.system.SystemConfigService;
import com.cshy.service.service.user.UserService;
import com.cshy.service.util.OnePassUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SmsServiceImpl 接口实现
 */
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private SmsRecordService smsRecordService;

    @Autowired
    private OnePassUtil onePassUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    @Resource
    private SmsRecordDao smsRecordDao;

    @Resource
    private SmsTemplateService smsTemplateService;

    @Resource
    private SystemAdminService systemAdminService;

    private static final Logger logger = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Override
    public com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        Client client = new Client(config);
        return client;
    }

    @Override
    public void sendCode(String phoneNumber, Integer triggerPosition, HttpServletRequest request, String... params) {
        if (StringUtils.isNotBlank(phoneNumber))
            ValidateFormUtil.isPhone(phoneNumber, "手机号码错误");
        SmsTemplate smsTemplate = smsTemplateService.getOne(new LambdaQueryWrapper<SmsTemplate>().eq(SmsTemplate::getTriggerPosition, triggerPosition));
        try {
            //查询accessKeyID / accessKeySecret
            String smsKey = systemConfigService.getValueByKey(Constants.SMS_KEY);
            String smsSecret = systemConfigService.getValueByKey(Constants.SMS_SECRET);

            //查询对应的消息模板
            List<String> phoneList = Lists.newArrayList();
            if (smsTemplate.getIsInternal() == 1) {
                //查询员工短信开关
                List<SystemAdmin> systemAdminList = systemAdminService.list(new LambdaQueryWrapper<SystemAdmin>().eq(SystemAdmin::getIsSms, 1));
                phoneList = systemAdminList.stream().map(SystemAdmin::getPhone).collect(Collectors.toList());
                String phoneStr = StringUtils.join(phoneList, ",");
                logger.info("向内部手机号 {} 发送短信，短信模板编码为：{}, 名称为：{}", phoneStr, smsTemplate.getTempCode(), smsTemplate.getTempName());
            } else {
                logger.info("向手机号 {} 发送短信，短信模板编码为：{}, 名称为：{}", phoneNumber, smsTemplate.getTempCode(), smsTemplate.getTempName());
            }
            Client client = this.createClient(smsKey, smsSecret);

            AtomicReference<SendSmsRequest> sendSmsRequest = new AtomicReference<>();
            if (0 == smsTemplate.getTriggerPosition())
                //验证码
                sendSmsRequest.set(this.sendVerificationCode(phoneNumber, smsTemplate));
            else {
                //其他模板
                if (CollUtil.isNotEmpty(phoneList)) {
                    phoneList.forEach(phone -> {
                        sendSmsRequest.set(this.sendCommonCode(phone, smsTemplate, params));
                        SendSmsResponse sendSmsResponse = null;
                        try {
                            sendSmsResponse = doSend(client, sendSmsRequest.get());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        addRecord(phone, request, smsTemplate, sendSmsResponse, params);

                        if (Objects.nonNull(sendSmsResponse) && !sendSmsResponse.getBody().getCode().equals("OK")) {
                            throw new CrmebException(sendSmsResponse.getBody().getMessage());
                        }
                    });
                } else {
                    sendSmsRequest.set(this.sendCommonCode(phoneNumber, smsTemplate, params));
                    SendSmsResponse sendSmsResponse = doSend(client, sendSmsRequest.get());
                    addRecord(phoneNumber, request, smsTemplate, sendSmsResponse, params);

                    if (!sendSmsResponse.getBody().getCode().equals("OK")) {
                        throw new CrmebException(sendSmsResponse.getBody().getMessage());
                    }
                }
            }


        } catch (Exception e) {
            if (Objects.isNull(smsTemplate))
                logger.error("向手机号（{}）发送短信失败， 找不到对应模板：{}", phoneNumber, e.getMessage());
            else
                logger.error("向手机号（{}）发送短信失败， 错误：{}", phoneNumber, e.getMessage());
            addRecord(phoneNumber, null, smsTemplate, null, params);
        }
    }

    private static SendSmsResponse doSend(Client client, SendSmsRequest sendSmsRequest) throws Exception {
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
        return sendSmsResponse;
    }

    private void addRecord(String phoneNumber, HttpServletRequest request, SmsTemplate smsTemplate, SendSmsResponse sendSmsResponse, String[] params) {
        //添加发送短信记录
        SmsRecord smsRecord = new SmsRecord()
                .setTemplate(smsTemplate.getTempCode())
                .setPhone(phoneNumber);

        if (Objects.nonNull(smsTemplate))
            smsRecord.setTemplateName(smsTemplate.getTempName());
        else
            smsRecord.setMemo("找不到对应模板");

        if (Objects.nonNull(sendSmsResponse)) {
            smsRecord.setMemo(JSON.toJSONString(sendSmsResponse.getBody()));
            smsRecord.setResultCode(sendSmsResponse.getBody().getCode());
        } else {
            smsRecord.setResultCode("ERROR");
        }

        if (Objects.nonNull(params))
            smsRecord.setContent(String.join(",", params));

        if (Objects.nonNull(request))
            smsRecord.setAddIp(CrmebUtil.getClientIp(request));
        smsRecordService.save(smsRecord);
    }

    private SendSmsRequest sendVerificationCode(String phone, SmsTemplate smsTemplate) {
        String code = RandomUtil.randomNumbers(4);
        //获取短信验证码过期时间
        String codeExpireStr = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SMS_CODE_EXPIRE);
        if (StrUtil.isBlank(codeExpireStr) || Integer.parseInt(codeExpireStr) == 0) {
            codeExpireStr = Constants.NUM_FIVE + "";// 默认5分钟过期
        }
        redisUtil.set(userService.getValidateCodeRedisKey(phone), code, Long.valueOf(codeExpireStr), TimeUnit.MINUTES);

        List<String> inputParams = findInputParams(smsTemplate.getContent());
        Map<Object, Object> map = new HashMap<>();
        AtomicInteger i = new AtomicInteger();
        inputParams.forEach(inputParam -> {
            map.put(inputParam, code);
            i.getAndIncrement();
        });
        String templateParam = JSON.toJSONString(map);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(smsTemplate.getSignName())
                .setTemplateCode(smsTemplate.getTempCode())
                .setPhoneNumbers(phone)
                .setTemplateParam(templateParam);
        logger.info("向手机号 {} 发送验证码：{}", phone, code);
        return sendSmsRequest;
    }

    private SendSmsRequest sendCommonCode(String phone, SmsTemplate smsTemplate, String... params) {
        //构建templateParam参数
        List<String> inputParams = findInputParams(smsTemplate.getContent());
        List<String> paramList = Arrays.asList(params);

        Map<Object, Object> map = new HashMap<>();
        AtomicInteger i = new AtomicInteger();
        inputParams.forEach(inputParam -> {
            String p;
            try {
                p = paramList.get(i.get());
                if (p.length() > 35)
                    p = p.substring(0, 32) + "...";
            } catch (IndexOutOfBoundsException e) {
                p = "";
            }
            map.put(inputParam, p);
            i.getAndIncrement();
        });
        String templateParam = JSON.toJSONString(map);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName(smsTemplate.getSignName())
                .setTemplateCode(smsTemplate.getTempCode())
                .setPhoneNumbers(phone)
                .setTemplateParam(templateParam);
        return sendSmsRequest;
    }

    private List<String> findInputParams(String content) {
        String regex = "\\$\\{[^}]+\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        List<String> list = Lists.newArrayList();
        while (matcher.find()) {
            String match = matcher.group();
            String replace = match.replace("${", "");
            replace = replace.replace("}", "");
            list.add(replace);
        }
        return list;
    }

    /**
     * 发送短信
     *
     * @param phone     手机号
     * @param tag       短信标识
     * @param msgTempId 短信模板id
     * @param pram      参数
     * @return Boolean
     */
    private Boolean sendMessages(String phone, Integer tag, Integer msgTempId, HashMap<String, Object> pram) {
        //发送手机验证码， 记录到redis  sms_validate_code_手机号
        switch (tag) {
            case SmsConstants.SMS_CONFIG_TYPE_VERIFICATION_CODE: // 验证码 特殊处理 code
                //获取短信验证码过期时间
                String codeExpireStr = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SMS_CODE_EXPIRE);
                if (StrUtil.isBlank(codeExpireStr) || Integer.parseInt(codeExpireStr) == 0) {
                    codeExpireStr = Constants.NUM_FIVE + "";// 默认5分钟过期
                }
                Integer code = CrmebUtil.randomCount(111111, 999999);
                HashMap<String, Object> justPram = new HashMap<>();
                justPram.put("code", code);
                justPram.put("time", codeExpireStr);
                push(phone, SmsConstants.SMS_CONFIG_VERIFICATION_CODE,
                        SmsConstants.SMS_CONFIG_VERIFICATION_CODE_TEMP_ID, justPram);

                // 将验证码存入redis
                redisUtil.set(userService.getValidateCodeRedisKey(phone), code, Long.valueOf(codeExpireStr), TimeUnit.MINUTES);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH: // 支付成功短信提醒 pay_price order_id
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH: // 发货短信提醒 nickname store_name
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_CONFIRM_TAKE_OVER_SWITCH: // 确认收货短信提醒 order_id store_name
                push(phone, SmsConstants.SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH,
                        SmsConstants.SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH: // 用户下单管理员短信提醒 admin_name order_id
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH: // 支付成功管理员短信提醒 admin_name order_id
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH: // 用户确认收货管理员短信提醒 admin_name order_id
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH: // 用户发起退款管理员短信提醒 admin_name order_id
                push(phone, msgTempId, pram);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH: // 改价短信提醒 order_id pay_price
                push(phone, SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH,
                        SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH_TEMP_ID, pram);
                break;
        }
        return true;
    }

    /**
     * 发送短信
     *
     * @param sendSmsVo 短信参数
     */
    private Boolean sendCode(SendSmsVo sendSmsVo) {
        String result;
        try {
            String token = onePassUtil.getToken();
            HashMap<String, String> header = onePassUtil.getCommonHeader(token);

            Map<String, Object> map = (Map<String, Object>) JSONObject.parseObject(sendSmsVo.getParam());
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("phone", sendSmsVo.getMobile());
            param.add("temp_id", sendSmsVo.getTemplate());
            map.entrySet().stream().forEach(entry -> param.add(StrUtil.format(SmsConstants.SMS_COMMON_PARAM_FORMAT, entry.getKey()), entry.getValue()));
            System.out.println("============发送短信=========header = " + header);
            result = restTemplateUtil.postFromUrlencoded(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_SEND_URI, param, header);
            checkResult(result);
        } catch (Exception e) {
            //接口请求异常，需要重新发送
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 组装发送对象
     *
     * @param phone     手机号
     * @param msgTempId 模板id
     * @param mapPram   参数map
     */
    private Boolean push(String phone, Integer msgTempId, HashMap<String, Object> mapPram) {
        if (StrUtil.isBlank(phone) || msgTempId <= 0) {
            return false;
        }
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        SendSmsVo smsVo = new SendSmsVo();
        smsVo.setUid(loginVo.getAccount());
        smsVo.setToken(loginVo.getSecret());
        smsVo.setMobile(phone);
        smsVo.setTemplate(msgTempId);
        smsVo.setParam(JSONObject.toJSONString(mapPram));
        return sendCode(smsVo);
    }

    /**
     * 添加待发送消息到redis队列
     *
     * @param phone     手机号
     * @param tempKey   模板key
     * @param msgTempId 模板id
     * @param mapPram   参数map
     */
    private Boolean push(String phone, String tempKey, Integer msgTempId, HashMap<String, Object> mapPram) {
        if (StrUtil.isBlank(phone) || StrUtil.isBlank(tempKey) || msgTempId <= 0) {
            return false;
        }
        OnePassLoginVo loginVo = onePassUtil.getLoginVo();
        SendSmsVo smsVo = new SendSmsVo();
        smsVo.setUid(loginVo.getAccount());
        smsVo.setToken(loginVo.getSecret());
        smsVo.setMobile(phone);
        smsVo.setTemplate(msgTempId);
        smsVo.setParam(JSONObject.toJSONString(mapPram));
        return sendCode(smsVo);
    }

    /**
     * 发送支付成功短信
     *
     * @param phone     手机号
     * @param orderNo   订单编号
     * @param payPrice  支付金额
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    @Override
    public Boolean sendPaySuccess(String phone, String orderNo, BigDecimal payPrice, Integer msgTempId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pay_price", payPrice);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH, msgTempId, map);
    }

    /**
     * 发送管理员下单短信提醒
     *
     * @param phone     手机号
     * @param orderNo   订单编号
     * @param realName  管理员名称
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    @Override
    public Boolean sendCreateOrderNotice(String phone, String orderNo, String realName, Integer msgTempId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("admin_name", realName);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH, msgTempId, map);
    }

    /**
     * 发送订单支付成功管理员提醒短信
     *
     * @param phone     手机号
     * @param orderNo   订单编号
     * @param realName  管理员名称
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    @Override
    public Boolean sendOrderPaySuccessNotice(String phone, String orderNo, String realName, Integer msgTempId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("admin_name", realName);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH, msgTempId, map);
    }

    /**
     * 发送用户退款管理员提醒短信
     *
     * @param phone     手机号
     * @param orderNo   订单编号
     * @param realName  管理员名称
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    @Override
    public Boolean sendOrderRefundApplyNotice(String phone, String orderNo, String realName, Integer msgTempId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("admin_name", realName);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH, msgTempId, map);
    }

    /**
     * 发送用户确认收货管理员提醒短信
     *
     * @param phone     手机号
     * @param orderNo   订单编号
     * @param realName  管理员名称
     * @param msgTempId 短信模板id
     */
    @Override
    public Boolean sendOrderReceiptNotice(String phone, String orderNo, String realName, Integer msgTempId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("admin_name", realName);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH, msgTempId, map);
    }

    /**
     * 发送订单改价提醒短信
     *
     * @param phone     手机号
     * @param orderNo   订单编号
     * @param price     修改后的支付金额
     * @param msgTempId 短信模板id
     * @return Boolean
     */
    @Override
    public Boolean sendOrderEditPriceNotice(String phone, String orderNo, BigDecimal price, Integer msgTempId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("order_id", orderNo);
        map.put("pay_price", price);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH, msgTempId, map);
    }

    /**
     * 发送订单发货提醒短信
     *
     * @param phone     手机号
     * @param nickName  用户昵称
     * @param storeName 商品名称
     * @param orderNo   订单编号
     * @param msgTempId 短信模板id
     */
    @Override
    public Boolean sendOrderDeliverNotice(String phone, String nickName, String storeName, String orderNo, Integer msgTempId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("nickname", nickName);
        map.put("store_name", storeName);
        map.put("order_id", orderNo);
        return sendMessages(phone, SmsConstants.SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH, msgTempId, map);
    }

    @Override
    public CommonPage<SmsRecord> page(SmsRecordsRequest smsRecordsRequest, PageParamRequest pageParamRequest) {
        Page<Object> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<SmsRecord> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(smsRecordsRequest.getPhone())) {
            lambdaQueryWrapper.like(SmsRecord::getPhone, smsRecordsRequest.getPhone());
        }
        lambdaQueryWrapper.orderByDesc(SmsRecord::getCreateTime);
        List<SmsRecord> smsRecords = smsRecordDao.selectList(lambdaQueryWrapper);
        CommonPage<SmsRecord> smsRecordCommonPage = CommonPage.restPage(CommonPage.copyPageInfo(startPage, smsRecords));
        return smsRecordCommonPage;
    }

    /**
     * post请求from表单模式提交
     */
    private JSONObject postFrom(String url, MultiValueMap<String, Object> param, Map<String, String> header) {
        String result = restTemplateUtil.postFromUrlencoded(url, param, header);
        return checkResult(result);
    }

    /**
     * 检测结构请求返回的数据
     *
     * @param result 接口返回的结果
     * @return JSONObject
     * @author Mr.Zhang
     * @since 2020-04-16
     */
    private JSONObject checkResult(String result) {
        if (StrUtil.isBlank(result)) {
            throw new CrmebException("短信平台接口异常，没任何数据返回！");
        }

        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(result);
        } catch (Exception e) {
            throw new CrmebException("短信平台接口异常！");
        }
        if (SmsConstants.SMS_ERROR_CODE.equals(jsonObject.getInteger("status"))) {
            throw new CrmebException("短信平台接口" + jsonObject.getString("msg"));
        }
        return jsonObject;
    }

    /**
     * 发送短信
     * 验证码特殊处理其他的参数自行根据要求处理
     * 参数处理逻辑 {code:value,code1:value1}
     *
     * @param phone String 手机号码
     * @return boolean
     */
    private Boolean sendSms(String phone, Integer tag, HashMap<String, Object> pram) {
        SendSmsVo sendSmsVo = new SendSmsVo();
        sendSmsVo.setMobile(phone);
        if (tag.equals(SmsConstants.SMS_CONFIG_TYPE_VERIFICATION_CODE)) {// 验证码 特殊处理 code
            //获取短信验证码过期时间
            String codeExpireStr = systemConfigService.getValueByKey(Constants.CONFIG_KEY_SMS_CODE_EXPIRE);
            if (StrUtil.isBlank(codeExpireStr) || Integer.parseInt(codeExpireStr) == 0) {
                codeExpireStr = Constants.NUM_FIVE + "";// 默认5分钟过期
            }
            Integer code = CrmebUtil.randomCount(111111, 999999);
            HashMap<String, Object> justPram = new HashMap<>();
            justPram.put("code", code);
            justPram.put("time", codeExpireStr);

            sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_VERIFICATION_CODE_TEMP_ID);
            sendSmsVo.setContent(JSONObject.toJSONString(justPram));
            Boolean aBoolean = commonSendSms(sendSmsVo);
            if (!aBoolean) {
                throw new CrmebException("发送短信失败，请联系后台管理员");
            }
            // 将验证码存入redis
            redisUtil.set(userService.getValidateCodeRedisKey(phone), code, Long.valueOf(codeExpireStr), TimeUnit.MINUTES);
            return aBoolean;
        }
        // 以下部分实时性不高暂时还是使用队列发送
        sendSmsVo.setContent(JSONObject.toJSONString(pram));
        switch (tag) {
            case SmsConstants.SMS_CONFIG_TYPE_LOWER_ORDER_SWITCH: // 支付成功短信提醒 pay_price order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_LOWER_ORDER_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_DELIVER_GOODS_SWITCH: // 发货短信提醒 nickname store_name
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_DELIVER_GOODS_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_CONFIRM_TAKE_OVER_SWITCH: // 确认收货短信提醒 order_id store_name
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_LOWER_ORDER_SWITCH: // 用户下单管理员短信提醒 admin_name order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_ADMIN_LOWER_ORDER_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_PAY_SUCCESS_SWITCH: // 支付成功管理员短信提醒 admin_name order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_ADMIN_PAY_SUCCESS_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_REFUND_SWITCH: // 用户确认收货管理员短信提醒 admin_name order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_ADMIN_REFUND_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ADMIN_CONFIRM_TAKE_OVER_SWITCH: // 用户发起退款管理员短信提醒 admin_name order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_ADMIN_CONFIRM_TAKE_OVER_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_PRICE_REVISION_SWITCH: // 改价短信提醒 order_id pay_price
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_PRICE_REVISION_SWITCH_TEMP_ID);
                break;
            case SmsConstants.SMS_CONFIG_TYPE_ORDER_PAY_FALSE: // 订单未支付 order_id
                sendSmsVo.setTemplate(SmsConstants.SMS_CONFIG_ORDER_PAY_FALSE_TEMP_ID);
                break;
        }
        return commonSendSms(sendSmsVo);
    }

    /**
     * 公共发送短信
     *
     * @param sendSmsVo 发送短信对象
     * @return 是否发送成功
     */
    private Boolean commonSendSms(SendSmsVo sendSmsVo) {
        try {
            String result;
            String token = onePassUtil.getToken();
            HashMap<String, String> header = onePassUtil.getCommonHeader(token);

            Map<String, Object> map = (Map<String, Object>) JSONObject.parseObject(sendSmsVo.getContent());
            MultiValueMap<String, Object> param = new LinkedMultiValueMap<>();
            param.add("phone", sendSmsVo.getMobile());
            param.add("temp_id", sendSmsVo.getTemplate());

            map.forEach((key, value) -> param.add(StrUtil.format(SmsConstants.SMS_COMMON_PARAM_FORMAT, key), value));
            logger.info("============发送短信=========header = " + header);
            result = restTemplateUtil.postFromUrlencoded(OnePassConstants.ONE_PASS_API_URL + OnePassConstants.ONE_PASS_API_SEND_URI, param, header);
            checkResult(result);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发送短信失败：" + e.getMessage());
            return false;
        }
        return true;
    }
}
