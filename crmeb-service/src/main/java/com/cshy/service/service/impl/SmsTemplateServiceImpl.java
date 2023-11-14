package com.cshy.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.QuerySmsTemplateListResponse;
import com.aliyun.dysmsapi20170525.models.QuerySmsTemplateListResponseBody;
import com.aliyun.tea.TeaException;
import com.cshy.common.constants.Constants;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.dto.sms.SmsTemplateDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.sms.SmsTemplate;
import com.cshy.common.model.query.sms.SmsTemplateQuery;
import com.cshy.common.model.vo.sms.SmsTemplateVo;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.dao.SmsTemplateDao;
import com.cshy.service.service.SmsService;
import com.cshy.service.service.SmsTemplateService;
import com.cshy.service.service.SystemConfigService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    /**
     * 获取详情
     *
     * @param id 模板id
     * @return SmsTemplate
     */
    @Override
    public SmsTemplate getDetail(Integer id) {
        SmsTemplate smsTemplate = getById(id);
        if (ObjectUtil.isNull(smsTemplate)) {
            throw new CrmebException("短信模板不存在");
        }
        return smsTemplate;
    }

    @Override
    @Async
    public void sync() throws Exception {
        //查询accessKeyID / accessKeySecret
        String smsKey = systemConfigService.getValueByKey(Constants.SMS_KEY);
        String smsSecret = systemConfigService.getValueByKey(Constants.SMS_SECRET);
        logger.info("正在同步短信模板数据");
        Client client = smsService.createClient(smsKey, smsSecret);
        int pageIndex = 1;
        try {
            // 复制代码运行请自行打印 API 的返回值
            List<QuerySmsTemplateListResponseBody.QuerySmsTemplateListResponseBodySmsTemplateList> smsTemplateList = Lists.newArrayList();
            totalTemplate(smsTemplateList, pageIndex, client);
            //查询现有模板
            List<SmsTemplate> list = this.list();
            smsTemplateList.stream().forEach(smsTemplate -> {
                Optional<SmsTemplate> first = list.stream().filter(temp -> temp.getTempCode().equals(smsTemplate.getTemplateCode())).findFirst();
                SmsTemplate template;
                if (first.isPresent())
                    template = first.get();
                else
                    template = SmsTemplate.builder()
                            .tempName(smsTemplate.getTemplateName())
                            .tempCode(smsTemplate.getTemplateCode())
                            .content(smsTemplate.getTemplateContent())
                            .build();

                String type = switchType(smsTemplate);
                template.setType(type);

                String auditStatus = switchAuditStatus(smsTemplate);
                template.setStatus(auditStatus);
//                if (StringUtils.isBlank(template.getId()))
//                    this.save(template);
//                else
//                    this.updateById(template);
            });
        } catch (TeaException error) {
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error(error.message);
        }
    }

    private static String switchAuditStatus(QuerySmsTemplateListResponseBody.QuerySmsTemplateListResponseBodySmsTemplateList smsTemplate) {
        String auditStatus;
        switch (smsTemplate.getAuditStatus()) {
            case Constants.AUDIT_STATE_INIT:
                auditStatus = "审核中";
                break;
            case Constants.AUDIT_STATE_PASS:
                auditStatus = "审核通过";
                break;
            case Constants.AUDIT_STATE_NOT_PASS:
                auditStatus = "审核未通过";
                break;
            case Constants.AUDIT_STATE_CANCEL:
            case Constants.AUDIT_SATE_CANCEL:
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
            // 复制代码运行请自行打印 API 的返回值
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
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error(error.message);
        }
        return Lists.newArrayList();
    }
}

