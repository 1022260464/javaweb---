package com.feng.youxi.service;

import com.feng.youxi.pojo.*;

import java.util.List;


public interface NewsService {


    PageResult<News> page(NewsQueryParam newsQueryParam);

    News getById(String newsId);

    void add(News news);

    void update(News news);

    void delete(List<String> newsId);
     List<NewsFenxiResult> getlogicItemPrice(NewsFenxiQueryParam newsFenxiQueryParam);

    void updateLike(News newsId);
}
