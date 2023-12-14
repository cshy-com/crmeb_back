package com.cshy.service.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cshy.common.model.entity.express.ExpressDetail;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.model.entity.express.Express;
import com.cshy.common.model.request.express.ExpressSearchRequest;
import com.cshy.common.model.request.express.ExpressUpdateRequest;
import com.cshy.common.model.request.express.ExpressUpdateShowRequest;

import java.util.List;

/**
*  ExpressService 接口

*/
public interface ExpressService extends IService<Express> {

    /**
    * 列表
    * @param request 搜索条件
    * @param pageParamRequest 分页类参数
    * @return List<Express>
    */
    List<Express> getList(ExpressSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 编辑
     */
    Boolean updateExpress(ExpressUpdateRequest expressRequest);

    /**
     * 修改显示状态
     */
    Boolean updateExpressShow(ExpressUpdateShowRequest expressRequest);

    /**
     * 同步快递公司
     */
    Boolean syncExpress();

    /**
     * 查询全部快递公司
     * @param type 类型：normal-普通，elec-电子面单
     */
    List<Express> findAll(String type);

    /**
     * 查询快递公司面单模板
     * @param com 快递公司编号
     */
    JSONObject template(String com);

    /**
     * 查询快递公司
     * @param code 快递公司编号
     * @return Express
     */
    Express getByCode(String code);

    /**
     * 通过物流公司名称获取
     * @param name 物流公司名称
     */
    Express getByName(String name);

    /**
     * 获取快递公司详情
     * @param id 快递公司id
     */
    Express getInfo(Integer id);

    /**
     * 查询快递信息
     */
    ExpressDetail findExpressDetail(String trackingNo, Integer type, String mobile);

    void syncExpressStatus();
}
