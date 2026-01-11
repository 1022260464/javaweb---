package com.feng.youxi.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class NewsFenxiQueryParam {
   //展示前多少浏览的游戏资讯
    private Integer topN = 10;

    // 游戏分类，用于筛选特定类型的资讯
    private String category;

    // 时间范围筛选（分析接口常用）
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    // 排序维度：view_count 或 like_count
    private String orderBy = "view_count";
}
