package com.feng.youxi.pojo;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemsQueryParam {
    private Integer page =1;
    private Integer pageSize =10;
    private String productName;
//     * 分类
    private String category;

    /**
     * 状态（0:下架, 1:上架, 2:缺货）
     */
    private Byte status;
    /**
     * 评分（<=）
     */
    private BigDecimal avgRating;

    private String supplierInfo;
    private BigDecimal price;




}
