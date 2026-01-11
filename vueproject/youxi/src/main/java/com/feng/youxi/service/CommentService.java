package com.feng.youxi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.feng.youxi.pojo.Comment;
import com.feng.youxi.pojo.CommentVO;

import java.util.List;

public interface CommentService extends IService<Comment> {
    // 获取树形评论列表
    List<CommentVO> getTreeList(String newsId);
}