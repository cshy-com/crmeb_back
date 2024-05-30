package com.cshy.common.model.vo.ydd;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class YddProductDetailVo {
    @ApiModelProperty(value = "商品ID")
    private String id;

    @ApiModelProperty(value = "供应商ID")
    private Integer supplierId;

    @ApiModelProperty(value = "商品类型")
    private String type;

    @ApiModelProperty(value = "SKU编号")
    private String skuSn;

    @ApiModelProperty(value = "商品名称")
    private String name;

    @ApiModelProperty(value = "主图片URL")
    private String mainImg;

    @ApiModelProperty(value = "是否启用")
    private Integer isEnable;

    @ApiModelProperty(value = "成本价格")
    private String costPrice;

    @ApiModelProperty(value = "市场价格")
    private String marketPrice;

    @ApiModelProperty(value = "库存")
    private Integer stock;

    @ApiModelProperty(value = "警告库存")
    private Integer warningStock;

    @ApiModelProperty(value = "单位")
    private String unit;

    @ApiModelProperty(value = "一级类目ID")
    private Integer i1CategoryId;

    @ApiModelProperty(value = "二级类目ID")
    private Integer i2CategoryId;

    @ApiModelProperty(value = "三级类目ID")
    private Integer i3CategoryId;

    @ApiModelProperty(value = "一级类目名称")
    private String i1Category;

    @ApiModelProperty(value = "二级类目名称")
    private String i2Category;

    @ApiModelProperty(value = "三级类目名称")
    private String i3Category;

    @ApiModelProperty(value = "卡供应商ID")
    private Integer cardSupplierId;

    @ApiModelProperty(value = "兑换金额")
    private Integer exchangeAmount;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "虚拟类型")
    private Integer virtualType;

    @ApiModelProperty(value = "充值价格")
    private Integer rechargePrice;

    @ApiModelProperty(value = "充值类型")
    private Integer rechargeType;

    @ApiModelProperty(value = "标签")
    private List<String> tags;

    @ApiModelProperty(value = "图片列表")
    private List<String> images;

    @ApiModelProperty(value = "规格数组")
    private List<String> specArr;

    @ApiModelProperty(value = "规格项")
    private List<String> specItems;

    @ApiModelProperty(value = "详情图片列表")
    private List<DetailImage> detailImages;

    @ApiModelProperty(value = "PC端HTML")
    private String pcHtml;

    @ApiModelProperty(value = "移动端HTML")
    private String mobileHtml;

    // DetailImage 内部类定义
    public static class DetailImage {

        @ApiModelProperty(value = "图片URL")
        private String url;

        @ApiModelProperty(value = "排序")
        private Integer sort;

        // Getters and setters
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Integer getSort() {
            return sort;
        }

        public void setSort(Integer sort) {
            this.sort = sort;
        }
    }

}

