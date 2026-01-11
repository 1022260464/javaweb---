package com.feng.youxi.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
public class NewsQueryParam {
    private Integer page =1;
    private Integer pageSize =10;
    private String newsId;
    /**
     * 标题
     * VARCHAR(200), 非空
     */
    private String title;
    /**
     * 分类
     * VARCHAR(50), 非空
/**/
    private String category;
    /**
     * 发布时间
     * DATETIME, 非空
     */
    private LocalDateTime publishTime;
    /**
     * 作者
     * VARCHAR(100), 非空
     */
    private String author;
    /**
     * 状态 (0:草稿, 1:发布, 2:隐藏)
     * TINYINT, 默认 1
     */
    private Integer status;

    /**
     * 封面图片 URL
     * VARCHAR(255), 可为空
     */
    private String coverImage;

    /**
     * 摘要
     * TEXT, 可为空
     */
    private String summary;

    /**
     * 创建时间
     * TIMESTAMP, 自动填充
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createTime;

    /**
     * 更新时间
     * TIMESTAMP, 自动更新
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime updateTime;
}