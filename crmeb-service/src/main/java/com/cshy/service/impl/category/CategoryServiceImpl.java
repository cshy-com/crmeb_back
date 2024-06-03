package com.cshy.service.impl.category;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cshy.common.constants.RedisKey;
import com.cshy.common.model.request.PageParamRequest;
import com.cshy.common.constants.CategoryConstants;
import com.cshy.common.constants.Constants;
import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.vo.category.CategoryTreeVo;
import com.cshy.common.utils.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.cshy.common.utils.CrmebUtil;
import com.cshy.common.model.entity.category.Category;
import com.cshy.common.model.request.category.CategoryRequest;
import com.cshy.common.model.request.category.CategorySearchRequest;
import com.cshy.service.dao.category.CategoryDao;
import com.cshy.service.service.category.CategoryService;
import com.cshy.service.service.system.SystemAttachmentService;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CategoryServiceImpl 接口实现
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Resource
    private CategoryDao dao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Category> getList(CategorySearchRequest request, PageParamRequest pageParamRequest) {
        List<Category> categories = loadFromCacheByType(request.getType());
        return categories.stream().filter(category -> {
                    boolean result = true;
                    if (null != request.getPid()) {
                        result = result && category.getPid().equals(request.getPid());
                    }
                    if (null != request.getType()) {
                        result = result && category.getType().equals(request.getType());
                    }
                    if (ObjectUtil.isNotNull(request.getStatus()) && request.getStatus() >= 0) {
                        result = result && category.getStatus().equals(request.getStatus().equals(CategoryConstants.CATEGORY_STATUS_NORMAL));
                    }
                    if (null != request.getName()) {
                        result = result && category.getName().contains(request.getName());
                    }
                    return result;
                }).sorted(Comparator.comparing(Category::getSort).reversed().thenComparing(Category::getId))
                .collect(Collectors.toList());
    }

    private List<Category> loadFromCache() {
        Collection<String> keys = redisUtil.keys(RedisKey.SYS_CATEGORY_KEY + "*");

        List<Category> total = Lists.newArrayList();
        keys.forEach(key -> {
            Object o = redisUtil.get(key);
            List<Category> categories = (List<Category>) o;
            total.addAll(categories);
        });
        return total;
    }

    private List<Category> loadFromCacheByType(Integer type) {
        String matchKey = matchKey(type);
        if (StringUtils.isNotBlank(matchKey)) {
            Object o = redisUtil.get(RedisKey.SYS_CATEGORY_KEY + matchKey);
            if (Objects.nonNull(o)){
                List<Category> categories = (List<Category>) o;
                return categories;
            } else {
                load2CacheByType(type);
            }
        }
        return loadFromCache();
    }

    @Override
    public List<Category> getByIds(List<Integer> idList) {
        List<Category> categories = loadFromCache();
        return categories.stream().filter(category -> idList.contains(category.getId())).collect(Collectors.toList());
    }

    @Override
    public HashMap<Integer, String> getListInId(List<Integer> cateIdList) {
        HashMap<Integer, String> map = new HashMap<>();
        List<Category> list = getByIds(cateIdList);
        for (Category category : list) {
            map.put(category.getId(), category.getName());
        }

        return map;
    }

    @Override
    public boolean update(CategoryRequest request, Integer id) {
        try {
            //修改分类信息
            Category category = new Category();
            BeanUtils.copyProperties(request, category);
            category.setId(id);
            category.setPath(getPathByPId(category.getPid()));

            updateById(category);

            //如状态为关闭，那么所以子集的状态都关闭
            if (!request.getStatus()) {
                updateStatusByPid(id, false);
            } else {
                //如是开启，则父类的状态为开启
                updatePidStatusById(id);
            }

            redisUtil.delete(RedisKey.SYS_CATEGORY_KEY + matchKey(category.getType()));

            load2CacheByType(category.getType());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updatePidStatusById(Integer id) {
        Category category = getById(id);
        List<Integer> categoryIdList = CrmebUtil.stringToArrayByRegex(category.getPath(), "/");
        categoryIdList.removeIf(i -> i.equals(0));
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        if (categoryIdList.size() < 1) {
            return;
        }
        for (Integer categoryId : categoryIdList) {
            Category categoryVo = this.getById(categoryId);
            if (Objects.nonNull(categoryVo)) {
                categoryVo.setStatus(true);
                categoryArrayList.add(categoryVo);
            }
        }
        updateBatchById(categoryArrayList);
    }

    private int getChildCountByPid(Integer pid) {
        //查看是否有子类
        QueryWrapper<Category> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.like("path", "/" + pid + "/");
        return dao.selectCount(objectQueryWrapper);
    }

    private int updateStatusByPid(Integer pid, boolean status) {
        //查看是否有子类
        Category category = new Category();
        category.setStatus(status);

        QueryWrapper<Category> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.like("path", "/" + pid + "/");
        return dao.update(category, objectQueryWrapper);
    }

    private String getPathByPId(Integer pid) {
        Category category = getById(pid);
        if (null != category) {
            return category.getPath() + pid + "/";
        }
        return null;
    }

    @Override
    public List<CategoryTreeVo> getListTree(Integer type, Integer status, String name) {
        return getTree(type, status, name, null);
    }

    /**
     * 带权限的属性结构
     */
    @Override
    public List<CategoryTreeVo> getListTree(Integer type, Integer status, List<Integer> categoryIdList) {
        System.out.println("菜单列表:getListTree: type:" + type + "| status:" + status + "| categoryIdList:" + JSON.toJSONString(categoryIdList));
        return getTree(type, status, null, categoryIdList);
    }

    private List<CategoryTreeVo> getTree(Integer type, Integer status, String name, List<Integer> categoryIdList) {
        List<Category> categories = loadFromCacheByType(type);
        List<Category> categoryList = categories.stream().filter(category -> {
                    boolean result = true;
                    if (null != categoryIdList && categoryIdList.size() > 0) {
                        result = result && categoryIdList.contains(category.getId());
                    }
                    if (status >= 0) {
                        result = result && category.getStatus().equals(status.equals(CategoryConstants.CATEGORY_STATUS_NORMAL));
                    }
                    if (StringUtils.isNotBlank(name)) { // 根据名称模糊搜索
                        result = result && category.getName().contains(name);
                    }
                    return result;
                }).sorted(Comparator.comparing(Category::getSort).reversed().thenComparing(Category::getId))
                .collect(Collectors.toList());
        // 构建分类树
        return buildCategoryTree(categoryList, 0);
    }

    // 递归构建分类树
    public List<CategoryTreeVo> buildCategoryTree(List<Category> categories, Integer parentId) {
        Map<Integer, List<CategoryTreeVo>> categoryMap = new HashMap<>();
        // 将类别按照父类别 ID 分组
        for (Category category : categories) {
            CategoryTreeVo node = new CategoryTreeVo();
            node.setId(category.getId());
            node.setPid(category.getPid());
            node.setPath(category.getPath());
            node.setName(category.getName());
            node.setType(category.getType());
            node.setUrl(category.getUrl());
            node.setExtra(category.getExtra());
            node.setStatus(category.getStatus());
            node.setSort(category.getSort());

            List<CategoryTreeVo> children = categoryMap.getOrDefault(category.getPid(), new ArrayList<>());
            children.add(node);
            categoryMap.put(category.getPid(), children);
        }
        // 递归构建树
        return buildTree(categoryMap, parentId);
    }

    private List<CategoryTreeVo> buildTree(Map<Integer, List<CategoryTreeVo>> categoryMap, Integer parentId) {
        List<CategoryTreeVo> tree = categoryMap.get(parentId);
        if (tree == null) {
            return new ArrayList<>();
        }
        for (CategoryTreeVo node : tree) {
            List<CategoryTreeVo> children = buildTree(categoryMap, node.getId());
            if (!children.isEmpty()) {
                node.setChild(children);
            }
        }
        return tree;
    }

    @Override
    public int delete(Integer id) {
        Category category = getById(id);
        //查看是否有子类, 物理删除
        if (getChildCountByPid(id) > 0) {
            throw new CrmebException("当前分类下有子类，请先删除子类！");
        }
        dao.deleteById(id);

        redisUtil.delete(RedisKey.SYS_CATEGORY_KEY + matchKey(category.getType()));

        load2CacheByType(category.getType());

        return 1;
    }

    @Override
    public void load2Cache() {
        List<Category> list = this.list();
        Map<Integer, List<Category>> mapByType = list.stream().collect(Collectors.groupingBy(Category::getType));
        mapByType.forEach((k, v) -> {
            String type = matchKey(k);
            redisUtil.set(RedisKey.SYS_CATEGORY_KEY + type, v);
        });
    }

    @Override
    public void load2CacheByType(Integer type) {
        List<Category> list = this.list(new LambdaQueryWrapper<Category>().eq(Category::getType, type));
        String typeStr = matchKey(type);
        redisUtil.set(RedisKey.SYS_CATEGORY_KEY + typeStr, list);
    }

    private static String matchKey(Integer k) {
        String type = "";
        switch (k) {
            case CategoryConstants.CATEGORY_TYPE_PRODUCT:
                type = CategoryConstants.CATEGORY_TYPE_PRODUCT_STR;
                break;
            case CategoryConstants.CATEGORY_TYPE_ATTACHMENT:
                type = CategoryConstants.CATEGORY_TYPE_ATTACHMENT_STR;
                break;
            case CategoryConstants.CATEGORY_TYPE_ARTICLE:
                type = CategoryConstants.CATEGORY_TYPE_ARTICLE_STR;
                break;
            case CategoryConstants.CATEGORY_TYPE_SETTING:
                type = CategoryConstants.CATEGORY_TYPE_SET_STR;
                break;
            case CategoryConstants.CATEGORY_TYPE_MENU:
                type = CategoryConstants.CATEGORY_TYPE_MENU_STR;
                break;
            case CategoryConstants.CATEGORY_TYPE_CONFIG:
                type = CategoryConstants.CATEGORY_TYPE_CONFIG_STR;
                break;
            case CategoryConstants.CATEGORY_TYPE_SECKILL:
                type = CategoryConstants.CATEGORY_TYPE_SKILL_STR;
                break;
            default:
                type = null;
        }
        return type;
    }

    @Override
    public List<Category> getChildVoListByPid(Integer pid) {
        List<Category> total = loadFromCache();

        return total.stream().filter(category ->
                category.getStatus().equals(Boolean.TRUE) && (category.getPath().contains("/" + pid + "/") || category.getPath().contains("/" + pid))
        ).collect(Collectors.toList());
    }

    private int checkName(String name, Integer type) {
        String key = matchKey(type);
        Object o = redisUtil.get(RedisKey.SYS_CATEGORY_KEY + key);
        List<Category> categories = (List<Category>) o;
        long count = categories.stream().filter(category -> {
            if (ObjectUtil.isNotNull(type))
                return category.getName().equals(name) && category.getType().equals(type);
            else
                return category.getName().equals(name);
        }).count();
        return (int) count;
    }

    @Override
    public boolean updateStatus(Integer id) {
        Category category = getById(id);
        category.setStatus(!category.getStatus());
        boolean b = updateById(category);
        if (b) {
            load2CacheByType(category.getType());
            return true;
        }
        return false;
    }

    /**
     * 新增分类
     *
     * @param categoryRequest
     */
    @Override
    public Boolean create(CategoryRequest categoryRequest) {
        //检测标题是否存在
        if (checkName(categoryRequest.getName(), categoryRequest.getType()) > 0) {
            throw new CrmebException("此分类已存在");
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryRequest, category);
        category.setPath(getPathByPId(category.getPid()));
        category.setExtra(systemAttachmentService.clearPrefix(category.getExtra()));
        save(category);
        redisUtil.delete(RedisKey.SYS_CATEGORY_KEY + matchKey(category.getType()));
        load2CacheByType(category.getType());
        return Boolean.TRUE;
    }

    /**
     * 获取文章分类列表
     *
     * @return List<Category>
     */
    @Override
    public List<Category> findArticleCategoryList() {
        String key = matchKey(CategoryConstants.CATEGORY_TYPE_ARTICLE);
        Object o = redisUtil.get(RedisKey.SYS_CATEGORY_KEY + key);
        List<Category> categories = (List<Category>) o;
        return categories.stream().filter(category -> category.getStatus()).sorted(Comparator.comparing(Category::getSort).reversed().thenComparing(Category::getId))
                .collect(Collectors.toList());
    }
}

