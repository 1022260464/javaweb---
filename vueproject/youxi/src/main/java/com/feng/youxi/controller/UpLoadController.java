package com.feng.youxi.controller;

import com.feng.youxi.pojo.Result;
import com.feng.youxi.utils.aliyun.AliyunOSSOperator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class UpLoadController {
    
    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;
    
    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws Exception {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        
        log.info("上传文件：{}, 大小：{} bytes", file.getOriginalFilename(), file.getSize());
        String url = aliyunOSSOperator.upload(file.getBytes(), file.getOriginalFilename());
        log.info("文件上传后url：{}", url);
        return Result.success(url);
    }
}
