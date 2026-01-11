package com.feng.youxi.pojo;

import lombok.Data;
import java.util.List;

@Data
public class NewsFenxiResult {
    private String title;       // 对应数据库字段 title
    private Integer viewCount;    // 对应数据库字段 view_count (使用驼峰命名)
    private Integer likeCount;    // 对应数据库字段 like_count (使用驼峰命名)
}
