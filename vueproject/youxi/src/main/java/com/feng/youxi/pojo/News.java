package com.feng.youxi.pojo;
import lombok.Data;
import java.time.LocalDateTime;

    /**
     * 游戏资讯实体类
     * 对应数据库表：game_news
     */
    @Data
    public class News {
        private String newsId;
        /**
         * 标题
         * VARCHAR(200), 非空
         */
        private String title;
        /**
         * 内容
         * LONGTEXT, 非空
         */
        private String content;
        /**
         * 分类
         * VARCHAR(50), 非空
         */
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
         * 浏览量
         * INT, 默认 0
         */
        private Integer viewCount;

        /**
         * 点赞数
         * INT, 默认 0
         */
        private Integer likeCount;

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
         * 标签（逗号分隔）
         * VARCHAR(500), 可为空
         */
        private String tags;
        /**
         * 创建时间
         * TIMESTAMP, 自动填充
         */
        private LocalDateTime createTime;

        /**
         * 更新时间
         * TIMESTAMP, 自动更新
         */
        private LocalDateTime updateTime;
    }

