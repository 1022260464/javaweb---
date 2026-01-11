package com.feng.youxi.pojo;

import com.feng.youxi.pojo.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentVO extends Comment {
    // 存放子评论
    private List<CommentVO> children = new ArrayList<>();
}