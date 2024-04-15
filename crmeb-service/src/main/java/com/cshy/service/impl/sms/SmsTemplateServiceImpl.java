package com.cshy.service.impl.sms;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.QuerySmsTemplateListResponse;
import com.aliyun.dysmsapi20170525.models.QuerySmsTemplateListResponseBody;
import com.aliyun.tea.TeaException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cshy.common.constants.AliSMSTemplateStatus;
import com.cshy.common.constants.Constants;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.dto.sms.SmsTemplateDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.sms.SmsSign;
import com.cshy.common.model.entity.sms.SmsTemplate;
import com.cshy.common.model.query.sms.SmsTemplateQuery;
import com.cshy.common.model.vo.sms.SmsTemplateVo;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.dao.sms.SmsTemplateDao;
import com.cshy.service.service.sms.SmsService;
import com.cshy.service.service.sms.SmsSignService;
import com.cshy.service.service.sms.SmsTemplateService;
import com.cshy.service.service.system.SystemConfigService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * SmsTemplateServiceImpl 接口实现
 */
@Service
public class SmsTemplateServiceImpl extends BaseServiceImpl<SmsTemplate, SmsTemplateDto,
        SmsTemplateQuery, SmsTemplateVo, SmsTemplateDao> implements SmsTemplateService {
    private static final Logger logger = LoggerFactory.getLogger(SmsTemplateServiceImpl.class);

    @Resource
    private SmsService smsService;

    @Resource
    private SystemConfigService systemConfigService;

    @Resource
    private SmsTemplateDao smsTemplateDao;

    @Resource
    private SmsSignService smsSignService;

    /**
     * 获取详情
     *
     * @param id 模板id
     * @return SmsTemplate
     */
    @Override
    public SmsTemplate getDetail(Serializable id) {
        SmsTemplate smsTemplate = getById(id);
        return smsTemplate;
    }

    @Override
    public void sync() throws Exception {
        //查询accessKeyID / accessKeySecret
        String smsKey = systemConfigService.getValueByKey(Constants.SMS_KEY);
        String smsSecret = systemConfigService.getValueByKey(Constants.SMS_SECRET);
        logger.info("正在同步短信模板数据");
        Client client = smsService.createClient(smsKey, smsSecret);
        int pageIndex = 1;
        try {
            List<QuerySmsTemplateListResponseBody.QuerySmsTemplateListResponseBodySmsTemplateList> smsTemplateList = Lists.newArrayList();
            totalTemplate(smsTemplateList, pageIndex, client);

            //查询现有模板
            List<SmsTemplate> list = this.list();

            //数据赋值
            smsTemplateList.stream().forEach(smsTemplate -> {
                Optional<SmsTemplate> first = list.stream().filter(temp -> temp.getTempCode().equals(smsTemplate.getTemplateCode())).findFirst();
                SmsTemplate template;
                template = first.orElseGet(() -> SmsTemplate.builder()
                        .tempName(smsTemplate.getTemplateName())
                        .tempCode(smsTemplate.getTemplateCode())
                        .content(smsTemplate.getTemplateContent())
                        .isInternal(0)
                        .build());

                String type = switchType(smsTemplate);
                template.setType(type);

                String auditStatus = switchAuditStatus(smsTemplate);
                template.setStatus(auditStatus);

                template.setTempName(smsTemplate.getTemplateName());

                if (StringUtils.isBlank(template.getId())){
                    SmsTemplateDto smsTemplateDto = new SmsTemplateDto();
                    BeanUtils.copyProperties(template, smsTemplateDto);
                    this.add(smsTemplateDto);
                }
                else
                    this.updateById(template);
            });

            //数据删除
            List<String> idList = list.stream().filter(temp ->
                    !smsTemplateList.stream().map(QuerySmsTemplateListResponseBody.QuerySmsTemplateListResponseBodySmsTemplateList::getTemplateCode)
                            .collect(Collectors.toList())
                            .contains(temp.getTempCode())).map(SmsTemplate::getId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(idList))
                this.smsTemplateDao.batchDeleteByIds(idList);

        } catch (TeaException error) {
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error(error.message);
        }
    }

    @Override
    public void update(String id, Integer triggerPosition, String signId) {
        //查询签名
        SmsSign smsSign = smsSignService.getById(signId);

        //保证triggerPosition唯一性
        List<SmsTemplate> smsTemplates = this.list(new LambdaQueryWrapper<SmsTemplate>().eq(SmsTemplate::getTriggerPosition, triggerPosition));
        if (CollUtil.isNotEmpty(smsTemplates)) {
            smsTemplates.forEach(temp -> {
                this.smsTemplateDao.initTriggerPosition(temp.getId());
            });
        }

        SmsTemplate smsTemplate = this.getById(id);
        Assert.isTrue("审核通过".equals(smsTemplate.getStatus()), "不能修改审核未通过的数据");

        smsTemplate.setTriggerPosition(triggerPosition);
        smsTemplate.setSignId(signId);
        smsTemplate.setSignName(smsSign.getTempName());
        this.updateById(smsTemplate);
    }

    @Override
    public void updateIsInterNal(String id, Integer isInterNal) {
        this.update(null, new LambdaUpdateWrapper<SmsTemplate>().eq(SmsTemplate::getId, id).set(SmsTemplate::getIsInternal, isInterNal));
    }

    private static String switchAuditStatus(QuerySmsTemplateListResponseBody.QuerySmsTemplateListResponseBodySmsTemplateList smsTemplate) {
        String auditStatus;
        switch (smsTemplate.getAuditStatus()) {
            case AliSMSTemplateStatus.INIT:
                auditStatus = "审核中";
                break;
            case AliSMSTemplateStatus.PASS:
                auditStatus = "审核通过";
                break;
            case AliSMSTemplateStatus.NOT_PASS:
                auditStatus = "审核未通过";
                break;
            case AliSMSTemplateStatus.STATE_CANCEL:
            case AliSMSTemplateStatus.SATE_CANCEL:
                auditStatus = "取消审核";
                break;
            default:
                auditStatus = "未知审核状态";
        }
        return auditStatus;
    }

    private static String switchType(QuerySmsTemplateListResponseBody.QuerySmsTemplateListResponseBodySmsTemplateList smsTemplate) {
        String type;
        switch (smsTemplate.getOuterTemplateType()) {
            case 0:
                type = "验证码短信";
                break;
            case 1:
                type = "通知短信";
                break;
            case 2:
                type = "推广短信";
                break;
            case 3:
                type = "国际/港澳台短信";
                break;
            case 7:
                type = "数字短信";
                break;
            default:
                type = "未知类型";
        }
        return type;
    }

    private List<QuerySmsTemplateListResponseBody.QuerySmsTemplateListResponseBodySmsTemplateList> totalTemplate(List<QuerySmsTemplateListResponseBody.QuerySmsTemplateListResponseBodySmsTemplateList> smsTemplateList, Integer pageIndex, Client client) {
        int pageSize = 5;
        com.aliyun.dysmsapi20170525.models.QuerySmsTemplateListRequest querySmsTemplateListRequest = new com.aliyun.dysmsapi20170525.models.QuerySmsTemplateListRequest()
                .setPageIndex(pageIndex)
                .setPageSize(pageSize);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            QuerySmsTemplateListResponse querySmsTemplateListResponse = client.querySmsTemplateListWithOptions(querySmsTemplateListRequest, runtime);
            if (200 == querySmsTemplateListResponse.getStatusCode()) {
                smsTemplateList.addAll(querySmsTemplateListResponse.getBody().getSmsTemplateList());
                while (smsTemplateList.size() < querySmsTemplateListResponse.getBody().getTotalCount()) {
                    pageIndex++;
                    this.totalTemplate(smsTemplateList, pageIndex, client);
                }
                return smsTemplateList;
            }
        } catch (TeaException error) {
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error(error.message);
        }
        return Lists.newArrayList();
    }
}

