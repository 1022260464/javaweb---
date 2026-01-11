package com.feng.youxi.mapper;

import com.feng.youxi.pojo.NewsFenxiQueryParam;
import com.feng.youxi.pojo.News;
import com.feng.youxi.pojo.NewsFenxiResult;
import com.feng.youxi.pojo.NewsQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NewsMapper {


    public List<News> list(NewsQueryParam newsQueryParam);


    Object getById(String newsId);

    void add(News news);

    void update(News news);

    void delete(List<String> newsId);

    List<NewsFenxiResult> NewsFenxi(NewsFenxiQueryParam newsFenxiQueryParam);

    void addViewCount(String newsId);

    void addLike(News news);
}
