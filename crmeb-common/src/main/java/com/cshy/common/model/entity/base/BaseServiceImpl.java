package com.cshy.common.model.entity.base;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.model.Order;
import com.cshy.common.constants.StatusConstants;
import com.cshy.common.model.page.CommonPage;
import com.cshy.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(rollbackFor = Exception.class)
public class BaseServiceImpl<T extends BaseModel<T>, D extends T, Q extends T, V extends T, M extends BaseMapper<T>> extends ServiceImpl<M, T> implements BaseService<T, D, Q, V> {

    @Override
    public String add(D dto) {
        // 之前
        onBeforeAdd(dto);

        Assert.isTrue(save(dto), StatusConstants.ADD_ERROR);

        // 之后
        onAfterAdd(dto);

        return dto.getId();
    }

    @Override
    public void update(D dto) {
        // 之前
        onBeforeUpdate(dto);

        Assert.isTrue(updateById(dto), StatusConstants.UPDATE_ERROR);

        // 之后
        onAfterUpdate(dto);
    }

    @Override
    public void delete(String id) {
        // 之前
        onBeforeDelete(id);

        Assert.isTrue(removeById(id), StatusConstants.DELETE_ERROR);

        // 之后
        onAfterDelete(id);
    }

    @Override
    public V obj(String id) {
        // 之前
        onBeforeObj(id);

        T entity = getById(id);
        Assert.notNull(entity, StatusConstants.DATA_NOT_FIND);
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[3];
        V vo = Convert.convert(type, entity);
        Assert.notNull(vo, StatusConstants.DATA_CONVERSION_FAILED);

        // 之后
        onAfterObj(vo);
        return vo;
    }

    @Override
    public V obj(T t) {
        // 之前
        onBeforeObj(t.getId());

        QueryWrapper<T> queryWrapper = new QueryWrapper<>(t);
        T entity = getOne(queryWrapper);
        Assert.notNull(entity, StatusConstants.DATA_NOT_FIND);
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[3];
        V vo = Convert.convert(type, entity);
        Assert.notNull(vo, StatusConstants.DATA_CONVERSION_FAILED);

        // 之后
        onAfterObj(vo);
        return vo;
    }

    @Override
    public List<V> list(Q query) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(query);

        // 之前
        onBeforeList(queryWrapper);

        // 如果没有自定义排序，就走默认排序
        if (queryWrapper.getExpression().getOrderBy().size() == 0) {
            defaultOrder(query, queryWrapper);
        }

        List<T> list = list(queryWrapper);
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[3];
        List<V> vos = list.stream().map(obj -> (V) Convert.convert(type, obj)).collect(Collectors.toList());

        // 之后
        onAfterList(vos);
        return vos;
    }

    @Override
    public CommonPage<V> page(Q query, BasePage basePage) {
        Page<T> page = Convert.convert(Page.class, basePage);

        //处理空字符串传参问题
        Class<? extends BaseModel> clazz = query.getClass();
        Class<?> superClazz = clazz.getSuperclass();

        Field[] declaredFields = clazz.getDeclaredFields();
        Field[] superDeclaredFields = new Field[0];
        if (!superClazz.getName().equals("com.baomidou.mybatisplus.extension.activerecord.Model"))
            superDeclaredFields = superClazz.getDeclaredFields();

        handleEmptyStr(query, declaredFields);
        handleEmptyStr(query, superDeclaredFields);

        QueryWrapper<T> queryWrapper = new QueryWrapper<>(query);

        // 之前
        onBeforePage(query, queryWrapper);

        // 如果没有自定义排序，就走默认排序
        if (queryWrapper.getExpression().getOrderBy().size() == 0) {
            defaultOrder(query, queryWrapper);
        }

        page = page(page, queryWrapper);
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[3];
        Page<V> vos = (Page<V>) Convert.convert(Page.class, page);
        vos.setRecords(vos.getRecords().stream().map(vo -> (V) Convert.convert(type, vo)).collect(Collectors.toList()));

        // 之后
        onAfterPage(vos);

        CommonPage<V> commonPage = CommonPage.restPage(vos.getRecords());
        commonPage.setTotal(page.getTotal());
        commonPage.setTotalPage((int) Math.ceil((double) page.getTotal() / (double) page.getSize()));
        return commonPage;
    }

    private void handleEmptyStr(Q query, Field[] declaredFields) {
        if (declaredFields.length > 0) {
            Arrays.asList(declaredFields).forEach(declaredField -> {
                try {
                    declaredField.setAccessible(true);
                    Object o = declaredField.get(query);
                    if (o instanceof String && StringUtils.isBlank((String) o))
                        declaredField.set(query, null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * 新增或者修改之前（共同事件）
     */
    protected void onBeforeAddOrUpdate(D dto) {
        dto.setIsDel(StatusConstants.EXISTENCE);
    }

    /**
     * 新增之前
     */
    protected void onBeforeAdd(D dto) {
        // 创建时间
        dto.setCreateTime(DateUtil.now());

        onBeforeAddOrUpdate(dto);
    }

    protected void onBeforeUpdateOrDelete(String id) {
        // 是否存在
        T t = getById(id);
        Assert.notNull(t, StatusConstants.DATA_NOT_FIND);
    }

    /**
     * 修改之前
     */
    protected void onBeforeUpdate(D dto) {
        // 修改时间
        dto.setUpdateTime(DateUtil.now());

        onBeforeAddOrUpdate(dto);
        onBeforeUpdateOrDelete(dto.getId());
    }

    /**
     * 新增或者修改之后（共同事件）
     */
    protected void onAfterAddOrUpdate(D dto) {
    }

    /**
     * 新增之后
     */
    protected void onAfterAdd(D dto) {
        onAfterAddOrUpdate(dto);
    }

    /**
     * 修改之后
     */
    protected void onAfterUpdate(D dto) {
        onAfterAddOrUpdate(dto);
    }

    /**
     * 删除之前
     */
    protected void onBeforeDelete(String id) {
        onBeforeUpdateOrDelete(id);
    }

    /**
     * 删除之后
     */
    protected void onAfterDelete(String id) {
    }

    /**
     * 对象之前
     */
    protected void onBeforeObj(String id) {
    }

    /**
     * 列表查询前共同事件
     */
    protected void onBeforeListOrPage(QueryWrapper<T> queryWrapper) {
    }

    /**
     * 列表之前
     */
    protected void onBeforeList(QueryWrapper<T> queryWrapper) {
        onBeforeListOrPage(queryWrapper);
    }

    /**
     * 分页之前
     */
    protected void onBeforePage(Q query, QueryWrapper<T> queryWrapper) {
        onBeforeListOrPage(queryWrapper);
    }

    /**
     * 查询后共同事件
     */
    protected void onAfterObjOrListOrPage(V vo) {
        vo.setIsDel(null);
    }

    /**
     * 对象之后
     */
    protected void onAfterObj(V vo) {
        onAfterObjOrListOrPage(vo);
    }

    /**
     * 列表之后
     */
    protected void onAfterList(List<V> vos) {
        vos.forEach(this::onAfterObjOrListOrPage);
    }

    /**
     * 分页之后
     */
    protected void onAfterPage(Page<V> page) {
        page.getRecords().forEach(this::onAfterObjOrListOrPage);
    }

    /**
     * 默认排序
     */
    protected void defaultOrder(Q query, QueryWrapper<T> queryWrapper) {
        if (query instanceof BaseOrder) {
            List<Order> orders = ((BaseOrder) query).getOrders();
            if (ObjectUtil.isNull(orders)) {
                return;
            }
            orders.forEach(order -> {
                // 正序
                if (Order.ASC.equals(order.getType())) {
                    queryWrapper.orderByAsc(order.getField());
                } else if (Order.DESC.equals(order.getType())) {
                    queryWrapper.orderByDesc(order.getField());
                }
            });
        }
    }
}
