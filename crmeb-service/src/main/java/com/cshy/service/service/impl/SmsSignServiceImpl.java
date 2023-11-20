package com.cshy.service.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.QuerySmsSignListResponse;
import com.aliyun.dysmsapi20170525.models.QuerySmsSignListResponseBody;
import com.aliyun.tea.TeaException;
import com.cshy.common.constants.Constants;
import com.cshy.common.model.dto.sms.SmsSignDto;
import com.cshy.common.model.entity.base.BaseServiceImpl;
import com.cshy.common.model.entity.sms.SmsSign;
import com.cshy.common.model.query.sms.SmsSignQuery;
import com.cshy.common.model.vo.sms.SmsSignVo;
import com.cshy.common.utils.StringUtils;
import com.cshy.service.dao.SmsSignDao;
import com.cshy.service.service.SmsService;
import com.cshy.service.service.SmsSignService;
import com.cshy.service.service.system.SystemConfigService;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * SmsSignServiceImpl 接口实现
 */
@Service
public class SmsSignServiceImpl extends BaseServiceImpl<SmsSign, SmsSignDto,
        SmsSignQuery, SmsSignVo, SmsSignDao> implements SmsSignService {
    private static final Logger logger = LoggerFactory.getLogger(SmsSignServiceImpl.class);

    @Resource
    private SmsService smsService;
    @Resource
    private SystemConfigService systemConfigService;

    @Override
    public void sync() throws Exception {
        //查询accessKeyID / accessKeySecret
        String smsKey = systemConfigService.getValueByKey(Constants.SMS_KEY);
        String smsSecret = systemConfigService.getValueByKey(Constants.SMS_SECRET);
        logger.info("正在同步短信签名数据");
        Client client = smsService.createClient(smsKey, smsSecret);
        int pageIndex = 1;
        try {
            List<QuerySmsSignListResponseBody.QuerySmsSignListResponseBodySmsSignList> smsSignList = Lists.newArrayList();
            totalSign(smsSignList, pageIndex, client);

            //查询现有签名
            List<SmsSign> list = this.list();

            //数据赋值
            smsSignList.stream().forEach(smsSign -> {
                Optional<SmsSign> first = list.stream().filter(temp -> temp.getTempName().equals(smsSign.getSignName())).findFirst();
                SmsSign smsSign1;
                if (first.isPresent())
                    smsSign1 = first.get();
                else
                    smsSign1 = SmsSign.builder()
                            .tempName(smsSign.getSignName())
                            .BusinessType(smsSign.getBusinessType())
                            .build();

                String auditStatus = switchAuditStatus(smsSign);
                smsSign1.setStatus(auditStatus);


                if (StringUtils.isBlank(smsSign1.getId())){
                    SmsSignDto SmsSignDto = new SmsSignDto();
                    BeanUtils.copyProperties(smsSign1, SmsSignDto);
                    this.add(SmsSignDto);
                }
                else
                    this.updateById(smsSign1);
            });
        } catch (TeaException error) {
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.error(error.message);
        }
    }

    private static String switchAuditStatus(QuerySmsSignListResponseBody.QuerySmsSignListResponseBodySmsSignList SmsSign) {
        String auditStatus;
        switch (SmsSign.getAuditStatus()) {
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

    private List<QuerySmsSignListResponseBody.QuerySmsSignListResponseBodySmsSignList> totalSign(List<QuerySmsSignListResponseBody.QuerySmsSignListResponseBodySmsSignList> smsTemplateList, Integer pageIndex, Client client) {
        int pageSize = 5;
        com.aliyun.dysmsapi20170525.models.QuerySmsSignListRequest querySmsSignListRequest = new com.aliyun.dysmsapi20170525.models.QuerySmsSignListRequest()
                .setPageIndex(pageIndex)
                .setPageSize(pageSize);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            QuerySmsSignListResponse querySmsSignListResponse = client.querySmsSignListWithOptions(querySmsSignListRequest, runtime);
            if (200 == querySmsSignListResponse.getStatusCode()) {
                smsTemplateList.addAll(querySmsSignListResponse.getBody().getSmsSignList());
                while (smsTemplateList.size() < querySmsSignListResponse.getBody().getTotalCount()) {
                    pageIndex++;
                    this.totalSign(smsTemplateList, pageIndex, client);
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

