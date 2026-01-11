package com.feng.youxi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//分页结果封装类

public class PageResult<T>{
    private long total;
    private List<T> rows;

}
