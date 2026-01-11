package com.feng.youxi.controller;

import com.feng.youxi.pojo.Comment;
import com.feng.youxi.pojo.CommentVO;
import com.feng.youxi.pojo.Result;
import com.feng.youxi.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/list")
    public Result getList(@RequestParam String newsId) {
        List<CommentVO> list = commentService.getTreeList(newsId);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Comment comment) {
        // 模拟用户
        comment.setUserId(999L);
        comment.setUserName("测试员");
        comment.setUserAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");

        if (comment.getRootId() == null) comment.setRootId(0L);
        if (comment.getParentId() == null) comment.setParentId(0L);
        comment.setCreateTime(java.time.LocalDateTime.now());
        comment.setUpdateTime(java.time.LocalDateTime.now());
        commentService.save(comment);
        return Result.success("评论成功");
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        commentService.removeById(id);
        return Result.success("删除成功");
    }
}