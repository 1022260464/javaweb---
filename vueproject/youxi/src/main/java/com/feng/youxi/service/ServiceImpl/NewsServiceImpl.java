package com.feng.youxi.service.ServiceImpl;

import com.feng.youxi.mapper.NewsMapper;
import com.feng.youxi.pojo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsServiceImpl implements com.feng.youxi.service.NewsService {
    private static final Logger log = LoggerFactory.getLogger(NewsServiceImpl.class);
    
    @Autowired
    NewsMapper newsMapper;

    @Override
    public PageResult<News> page(NewsQueryParam newsQueryParam) {
        // 添加空值检查，防止NullPointerException
        int pageNum = (newsQueryParam.getPage() != null) ? newsQueryParam.getPage() : 1;
        int pageSize = (newsQueryParam.getPageSize() != null) ? newsQueryParam.getPageSize() : 10;
        
        log.info("准备开始分页查询: page={}, pageSize={}", pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        
        // 检查PageHelper是否正确设置
        com.github.pagehelper.Page<Object> page = com.github.pagehelper.PageHelper.getLocalPage();
        if (page != null) {
            log.info("PageHelper本地页面对象创建成功: pageNum={}, pageSize={}", page.getPageNum(), page.getPageSize());
        } else {
            log.warn("PageHelper本地页面对象未正确创建");
        }
        
        log.info("即将执行查询...");
        List<News> newsList = newsMapper.list(newsQueryParam);
        log.info("查询执行完成，结果列表大小: {}", newsList.size());
        
        PageInfo<News> pageInfo = new PageInfo<>(newsList);
        log.info("PageInfo构建完成: 总记录数={}, 当前页码={}, 每页大小={}, 总页数={}", 
                 pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages());
        
        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public News getById(String newsId) {
        newsMapper.addViewCount(newsId);
        return (News) newsMapper.getById(newsId);
    }

    @Override
    public void add(News news) {
        // 检查title是否为空
        if (news.getTitle() == null || news.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("新闻标题不能为空");
        }
        
        // 生成newsId，格式为 GN+完整时间戳，如 GN1712345678901
        if (news.getNewsId() == null || news.getNewsId().trim().isEmpty()) {
            long timestamp = System.currentTimeMillis();
            news.setNewsId("GN" + timestamp);
        }
        
        //publishTime如果为空，设置默认值为createTime
        if (news.getPublishTime() == null) {
            news.setPublishTime(java.time.LocalDateTime.now());
        }
        // 如果status为null，则设置默认值1（发布状态）
        if (news.getStatus() == null) {
            news.setStatus(1);
        }
        
        news.setCreateTime(java.time.LocalDateTime.now());
        news.setUpdateTime(java.time.LocalDateTime.now());
        newsMapper.add(news);
    }

    @Override
    public void updateLike(News news) {
        newsMapper.addLike(news);
    }

    @Override
    public void update(News news) {
        news.setUpdateTime(java.time.LocalDateTime.now());
        newsMapper.update(news);
    }

    @Override
    public void delete(List<String> newsId) {
        newsMapper.delete(newsId);
    }

    @Override
    public List<NewsFenxiResult> getlogicItemPrice(NewsFenxiQueryParam newsFenxiQueryParam) {
        try {
            // 强制清空当前线程的分页状态，确保分析 SQL 不被拦截改写
            PageHelper.clearPage();
            return newsMapper.NewsFenxi(newsFenxiQueryParam);
        } finally {
            // 确保彻底清理
            PageHelper.clearPage();
        }
    }
}
