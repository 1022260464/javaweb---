package com.feng.youxi.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * 对应数据库表: game_product_info
 */
@Data
public class Items {

    /**
     * 商品ID (主键)
     */
    private String productId;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 描述
     */
    private String description;

    /**
     * 商品图片 (JSON格式，存储多个图片URL)
     */
    private String productImages;
    /**
     * 分类
     */
    private String category;

    /**
     * 状态 (0:下架, 1:上架, 2:缺货)
     */
    private Integer status;

    /**
     * 供应商信息 (JSON格式)
     */
    private String supplierInfo;
    /**
     * 价格
     */
    private BigDecimal price;
    private String detailContent;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp updatedAt;
    
    /**
     * 平均评分 (来自评分聚合表)
     */
    private BigDecimal avgRating;
    
    /**
     * 总评论数 (来自评分聚合表)
     */
    private Integer totalReviews;
    
    /**
     * 最后更新时间 (来自评分聚合表)
     */
    private Timestamp lastUpdated;
}