package com.feng.youxi.controller;

import com.feng.youxi.pojo.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.feng.youxi.service.NewsService;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService;

    @GetMapping
    public Result news(NewsQueryParam newsQueryParam) {
        log.info("请求参数：{},{},{},{},{},{}",newsQueryParam);
        PageResult<News> pageResult = newsService.page(newsQueryParam);
        return Result.success(pageResult);
    }
    @GetMapping("/{newsId}")
    public Result getById(@PathVariable String newsId) {
        return Result.success(newsService.getById(newsId));
    }
    @PostMapping
    public Result add (@RequestBody News news) {
        newsService.add(news);
        return Result.success();
    }
    //增加点赞量
    @PutMapping("/addLike")
    public Result addLike(@RequestBody News news){
        newsService.updateLike(news);
        return Result.success();
    }
    @PutMapping
    public Result update(@RequestBody News news) {
        newsService.update(news);
        return Result.success();
    }
    @DeleteMapping
    public Result delete(@RequestParam List<String> newsId) {
        newsService.delete(newsId);
        return Result.success();
    }
/**
 * 新闻分析接口
 * 根据查询参数获取新闻分析结果
 *
 * @param newsFenxiQueryParam 新闻分析查询参数对象，包含查询所需的各项条件
 * @return 返回一个Result对象，其中包含新闻分析结果列表
 */
    @GetMapping("/newsFenxi")
    public Result getlogicItemPrice(NewsFenxiQueryParam newsFenxiQueryParam){
    // 调用newsService的getlogicItemPrice方法，传入查询参数，获取新闻分析结果列表
        List<NewsFenxiResult> newsFenxiResultList = newsService.getlogicItemPrice(newsFenxiQueryParam);
    // 将获取到的结果列表封装到Result对象中并返回
        return Result.success(newsFenxiResultList);

    }
}