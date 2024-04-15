package com.cshy.service.impl.system;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.constants.RedisKey;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.entity.system.SysTemplateColumnConfig;
import com.cshy.common.model.entity.system.SystemNotification;
import com.cshy.common.model.entity.wechat.SysTemplateMessage;
import com.cshy.common.model.vo.*;
import com.cshy.common.utils.RedisUtil;
import com.cshy.service.dao.SysTemplateMessageDao;
import com.cshy.service.service.system.SysTemplateColumnConfigService;
import com.cshy.service.service.system.SystemNotificationService;
import com.cshy.service.service.system.SysTemplateMessageService;
import com.cshy.service.service.wechat.WechatCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TemplateMessageServiceImpl 接口实现
 */
@Service
public class SysSysTemplateMessageServiceImpl extends ServiceImpl<SysTemplateMessageDao, SysTemplateMessage> implements SysTemplateMessageService {

    private static final Logger logger = LoggerFactory.getLogger(SysSysTemplateMessageServiceImpl.class);

    @Resource
    private SysTemplateMessageDao dao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private WechatCommonService wechatCommonService;

    @Autowired
    private SystemNotificationService systemNotificationService;

    @Autowired
    private SysTemplateColumnConfigService sysTemplateColumnConfigService;

    /**
     * 公众号消费队列消费
     */
    @Override
    public void consumePublic() {
        String redisKey = RedisKey.WE_CHAT_MESSAGE_KEY_PUBLIC;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("TemplateMessageServiceImpl.consumePublic | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (null == data) {
                continue;
            }
            try {
                TemplateMessageVo templateMessage = JSONObject.toJavaObject(JSONObject.parseObject(data.toString()), TemplateMessageVo.class);
                boolean result = wechatCommonService.sendPublicTemplateMessage(templateMessage);
//                boolean result = weChatService.sendPublicTempMessage(templateMessage);
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 小程序消费队列消费
     */
    @Override
    public void consumeProgram() {
        String redisKey = RedisKey.WE_CHAT_MESSAGE_KEY_PROGRAM;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("TemplateMessageServiceImpl.consumeProgram | size:" + size);
        if (size < 1) {
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if (null == data) {
                continue;
            }
            try {
                TemplateMessageVo templateMessage = JSONObject.toJavaObject(JSONObject.parseObject(data.toString()), TemplateMessageVo.class);
                boolean result = wechatCommonService.sendMiniSubscribeMessage(templateMessage);
                if (!result) {
                    redisUtil.lPush(redisKey, data);
                }
            } catch (Exception e) {
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 发送模板消息
     *
     * @param templateId 模板消息编号
     * @param temMap     内容Map
     * @param openId     微信用户openid
     */
    @Override
    public void pushTemplateMessage(Integer templateId, HashMap<String, String> temMap, String openId) {
        TemplateMessageVo templateMessageVo = new TemplateMessageVo();

        SysTemplateMessage sysTemplateMessage = info(templateId);
        if (ObjectUtil.isNull(sysTemplateMessage) || StrUtil.isBlank(sysTemplateMessage.getContent())) {
            return;
        }
        templateMessageVo.setTemplate_id(sysTemplateMessage.getTempId());

        HashMap<String, SendTemplateMessageItemVo> hashMap = new HashMap<>();
        for (Map.Entry<String, String> entry : temMap.entrySet()) {
            hashMap.put(entry.getKey(), new SendTemplateMessageItemVo(entry.getValue()));
        }

        templateMessageVo.setData(hashMap);
        templateMessageVo.setTouser(openId);
        redisUtil.lPush(RedisKey.WE_CHAT_MESSAGE_KEY_PUBLIC, JSONObject.toJSONString(templateMessageVo));
    }

    /**
     * 发送小程序订阅消息
     *
     * @param templateId 模板消息编号
     * @param temMap     内容Map
     * @param openId     微信用户openId
     */
    @Override
    public void pushMiniTemplateMessage(Integer templateId, HashMap<String, String> temMap, String openId) {
        SysTemplateMessage sysTemplateMessage = info(templateId);
        if (ObjectUtil.isNull(sysTemplateMessage) || StrUtil.isBlank(sysTemplateMessage.getContent())) {
            return;
        }

        ProgramTemplateMessageVo programTemplateMessageVo = new ProgramTemplateMessageVo();
        programTemplateMessageVo.setTemplate_id(sysTemplateMessage.getTempId());

        //组装关键字数据
        HashMap<String, SendProgramTemplateMessageItemVo> hashMap = new HashMap<>();
        temMap.forEach((key, value) -> hashMap.put(key, new SendProgramTemplateMessageItemVo(value)));

        programTemplateMessageVo.setData(hashMap);
        programTemplateMessageVo.setTouser(openId);
        redisUtil.lPush(RedisKey.WE_CHAT_MESSAGE_KEY_PROGRAM, JSONObject.toJSONString(programTemplateMessageVo));
    }

    /**
     * 修改模板状态
     *
     * @param id     模板id
     * @param status 状态
     */
    @Override
    public Boolean updateStatus(Integer id, Integer status) {
        SysTemplateMessage sysTemplateMessage = getById(id);
        if (ObjectUtil.isNull(sysTemplateMessage)) {
            throw new CrmebException("此模板" + id + " 不存在或者已删除");
        }
        sysTemplateMessage.setStatus(status);
        return updateById(sysTemplateMessage);
    }

    /**
     * 公众号模板消息同步
     *
     * @return Boolean
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Boolean wechatPublicSync() {
        // 获取公众平台所有的微信模板
        List<PublicMyTemplateVo> templateVoList = wechatCommonService.getPublicMyTemplateList();
        //更新数据
        List<SysTemplateMessage> sysTemplateMessageList = getListByIdList(true);
        sysTemplateMessageList.forEach(temp -> {
            Optional<PublicMyTemplateVo> first = templateVoList.stream().filter(vo -> vo.getTemplate_id().equals(temp.getTempId())).findFirst();
            if (first.isPresent()) {
                PublicMyTemplateVo publicMyTemplateVo = first.get();
                temp.setContent(publicMyTemplateVo.getContent());
                temp.setName(publicMyTemplateVo.getTitle());
                this.updateById(temp);
                //置空关联的模板字段配置
                sysTemplateColumnConfigService.update(new LambdaUpdateWrapper<SysTemplateColumnConfig>()
                        .set(SysTemplateColumnConfig::getTempColumn, null)
                        .eq(SysTemplateColumnConfig::getTempId, temp.getId()));
            }
        });
        //删除不存在的模板消息
        if (CollUtil.isNotEmpty(sysTemplateMessageList)) {
            List<SysTemplateMessage> removeList = sysTemplateMessageList.stream()
                    .filter(obj2 -> templateVoList.stream().noneMatch(obj1 -> obj1.getTemplate_id().equals(obj2.getTempId())))
                    .collect(Collectors.toList());
            List<Integer> removeIdList = removeList.stream().map(SysTemplateMessage::getId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(removeIdList)) {
                this.removeByIds(removeIdList);
                //设置消息通知的开关为禁用
                systemNotificationService.update(new LambdaUpdateWrapper<SystemNotification>()
                        .set(SystemNotification::getIsWechat, 0)
                        .set(SystemNotification::getWechatId, 0)
                        .in(SystemNotification::getWechatId, removeIdList));
                //清空模板字段配置
                sysTemplateColumnConfigService.update(new LambdaUpdateWrapper<SysTemplateColumnConfig>()
                        .set(SysTemplateColumnConfig::getTempColumn, null)
                        .set(SysTemplateColumnConfig::getTempId, null)
                        .in(SysTemplateColumnConfig::getTempId, removeIdList));
            }
        }
        //新增模板消息
        List<PublicMyTemplateVo> addList = templateVoList.stream()
                .filter(obj2 -> sysTemplateMessageList.stream().noneMatch(obj1 -> obj2.getTemplate_id().equals(obj1.getTempId())))
                .collect(Collectors.toList());
        addList.forEach(temp -> {
            SysTemplateMessage sysTemplateMessage = new SysTemplateMessage();
            sysTemplateMessage.setName(temp.getTitle());
            sysTemplateMessage.setContent(temp.getContent());
            sysTemplateMessage.setTempId(temp.getTemplate_id());
            sysTemplateMessage.setType(true);
            this.save(sysTemplateMessage);
        });
        return true;
    }

    /**
     * 小程序订阅消息同步
     *
     * @return Boolean
     */
    @Override
    public Boolean routineSync() {
        List<SystemNotification> notificationList = systemNotificationService.getListByWechat("routine");
        List<Integer> routineIdList = notificationList.stream().map(SystemNotification::getRoutineId).collect(Collectors.toList());
        List<SysTemplateMessage> sysTemplateMessageList = getListByIdList(false);
        if (CollUtil.isEmpty(sysTemplateMessageList)) {
            throw new CrmebException("请先配置小程序订阅消息");
        }
        // 获取当前帐号下的个人模板列表
        List<RoutineMyTemplateVo> templateVoList = wechatCommonService.getRoutineMyTemplateList();
        // 删除原有模板
        templateVoList.forEach(e -> wechatCommonService.delRoutineMyTemplate(e.getPriTmplId()));
        // 将现在的模板保存到小程序平台
        sysTemplateMessageList.forEach(e -> {
            // 获取小程序平台上的标准模板
            List<RoutineTemplateKeyVo> templateKeyVoList = wechatCommonService.getRoutineTemplateByWechat(e.getTempKey());
            List<Integer> kidList = getRoutineKidList(e.getContent(), templateKeyVoList);
            String priTmplId = wechatCommonService.apiAddRoutineTemplate(e.getTempKey(), kidList);
            e.setTempId(priTmplId);
        });
        return updateBatchById(sysTemplateMessageList);
    }

    /**
     * 获取小程序订阅消息kidList
     *
     * @param content           本地保存的内容
     * @param templateKeyVoList 小程序模板key对象数组
     * @return List
     */
    private List<Integer> getRoutineKidList(String content, List<RoutineTemplateKeyVo> templateKeyVoList) {
        // 分解出本地的关键词内容数组
        String replace = content.replace("\r\n", "");
        String[] split = replace.split("}}");
        List<String> collect = Stream.of(split).map(s -> {
            s = s.substring(0, s.indexOf("{"));
            return s;
        }).collect(Collectors.toList());

        Map<String, Integer> map = new HashMap<>();
        templateKeyVoList.forEach(e -> map.put(e.getName(), e.getKid()));

        List<Integer> kidList = new ArrayList<>();
        collect.forEach(e -> {
            if (map.containsKey(e)) {
                kidList.add(map.get(e));
            }
        });
        return kidList;
    }

    /**
     * 通过模板编号获取列表
     *
     * @param type 类型
     * @return List
     */
    private List<SysTemplateMessage> getListByIdList(Boolean type) {
        LambdaQueryWrapper<SysTemplateMessage> lqw = Wrappers.lambdaQuery();
        lqw.eq(SysTemplateMessage::getType, type);
        return dao.selectList(lqw);
    }

    /**
     * 查询单条数据
     *
     * @param id Integer id
     */
    @Override
    public SysTemplateMessage info(Integer id) {
        return getById(id);
    }

    /**
     * 获取模板列表
     *
     * @param tidList id数组
     * @return List
     */
    @Override
    public List<SysTemplateMessage> getByIdList(List<Integer> tidList) {
        LambdaQueryWrapper<SysTemplateMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SysTemplateMessage::getId, tidList);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public void send(SysTemplateMessage sysTemplateMessage, String openId, List<String> params) {
        //查询模板和模板字段
        List<SysTemplateColumnConfig> list = sysTemplateColumnConfigService.list(new LambdaQueryWrapper<SysTemplateColumnConfig>().eq(SysTemplateColumnConfig::getTempId, sysTemplateMessage.getId()));
        list = list.stream().sorted(Comparator.comparing(SysTemplateColumnConfig::getSort)).collect(Collectors.toList());

        HashMap<String, SendTemplateMessageItemVo> temMap = new HashMap<>();

        AtomicInteger i = new AtomicInteger(0);
        List<SysTemplateColumnConfig> finalList = list;
        params.forEach(param -> {
            temMap.put(finalList.get(i.get()).getTempColumn(), new SendTemplateMessageItemVo(param));
            i.getAndIncrement();
        });

        TemplateMessageVo templateMessageVo = new TemplateMessageVo();
        templateMessageVo.setTemplate_id(sysTemplateMessage.getTempId());
        templateMessageVo.setTouser(openId);
        templateMessageVo.setData(temMap);
        wechatCommonService.sendPublicTemplateMessage(templateMessageVo);
    }
}

