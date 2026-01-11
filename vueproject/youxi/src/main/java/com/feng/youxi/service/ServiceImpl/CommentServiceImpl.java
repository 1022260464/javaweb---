package com.feng.youxi.service.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.feng.youxi.mapper.CommentMapper;
import com.feng.youxi.pojo.Comment;
import com.feng.youxi.pojo.CommentVO;
import com.feng.youxi.service.CommentService;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public List<CommentVO> getTreeList(String newsId) {
        // 1. 查出所有评论
        List<Comment> allComments = this.list(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getNewsId, newsId)
                .orderByDesc(Comment::getCreateTime));

        if (allComments.isEmpty()) return new ArrayList<>();

        // 2. 转成 VO
        List<CommentVO> allVOs = allComments.stream().map(item -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());

        // 3. 组装树形结构
        // 3.1 找根节点
        List<CommentVO> rootList = allVOs.stream()
                .filter(c -> c.getRootId() == null || c.getRootId() == 0)
                .collect(Collectors.toList());

        // 3.2 找子节点并分组
        Map<Long, List<CommentVO>> childrenMap = allVOs.stream()
                .filter(c -> c.getRootId() != null && c.getRootId() != 0)
                .collect(Collectors.groupingBy(CommentVO::getRootId));

        // 3.3 填充子节点
        for (CommentVO root : rootList) {
            List<CommentVO> children = childrenMap.get(root.getId());
            if (children != null) {
                children.sort(Comparator.comparing(Comment::getCreateTime));
                root.setChildren(children);
            }
        }
        return rootList;
    }
}