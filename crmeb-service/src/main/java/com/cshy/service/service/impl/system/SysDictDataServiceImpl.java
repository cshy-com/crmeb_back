package com.cshy.service.service.impl.system;

import com.cshy.common.exception.CrmebException;
import com.cshy.common.model.entity.base.BasePage;
import com.cshy.common.model.entity.system.SysDictData;
import com.cshy.common.model.entity.system.SysDictType;
import com.cshy.common.utils.DictUtils;
import com.cshy.service.dao.system.SysDictDataMapper;
import com.cshy.service.service.system.ISysDictDataService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService
{
    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 根据条件分页查询字典数据
     * 
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData)
    {
        return dictDataMapper.selectDictDataList(dictData);
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue)
    {
        return dictDataMapper.selectDictLabel(dictType, dictValue);
    }

    /**
     * 根据字典数据ID查询信息
     * 
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Integer dictCode)
    {
        return dictDataMapper.selectDictDataById(dictCode);
    }

    /**
     * 批量删除字典数据信息
     * 
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Integer[] dictCodes)
    {
        for (Integer dictCode : dictCodes)
        {
            SysDictData data = selectDictDataById(dictCode);
            dictDataMapper.deleteDictDataById(dictCode);
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
    }

    /**
     * 新增保存字典数据信息
     * 
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData data)

    {
        //插入之前检查键值是否重复
        List<Integer> duplicated = dictDataMapper.isDuplicated(data.getDictValue(), data.getDictType());
        if (duplicated.isEmpty()){
            int row = dictDataMapper.insertDictData(data);
            if (row > 0)
            {
                List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
                DictUtils.setDictCache(data.getDictType(), dictDatas);
            }
            return row;
        }
        throw new CrmebException("键值重复，请重新填写");
    }

    /**
     * 修改保存字典数据信息
     * 
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData data)
    {
        int row = dictDataMapper.updateDictData(data);
        if (row > 0)
        {
            List<SysDictData> dictDatas = dictDataMapper.selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    @Override
    public String getUrl(Integer number) {
        List<SysDictData> accumulationFundVideo = dictDataMapper.selectDictDataByType("accumulation_fund_video");
        SysDictData sysDictData = null;
        String url = "";
        if (number == 1)
            sysDictData = accumulationFundVideo.stream().filter(a -> a.getDictLabel().equals("视频1")).collect(Collectors.toList()).get(0);
        if (number == 2)
            sysDictData = accumulationFundVideo.stream().filter(a -> a.getDictLabel().equals("视频2")).collect(Collectors.toList()).get(0);
        if (Objects.nonNull(sysDictData))
            url = sysDictData.getDictValue();
        return url;
    }

    @Override
    public PageInfo<SysDictData> selectDictTypePage(SysDictData dictData, BasePage basePage) {
        PageHelper.startPage(basePage.getCurrent().intValue(), basePage.getSize().intValue());

        List<SysDictData> sysDictData = dictDataMapper.selectDictDataList(dictData);
        return new PageInfo<>(sysDictData);
    }

}
