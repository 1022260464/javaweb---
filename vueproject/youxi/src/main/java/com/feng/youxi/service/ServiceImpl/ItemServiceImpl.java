package com.feng.youxi.service.ServiceImpl;

import com.feng.youxi.mapper.ItemMapper;
import com.feng.youxi.pojo.*;
import com.feng.youxi.service.ItemService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemMapper itemMapper;
    @Override
    public PageResult<Items> page(ItemsQueryParam itemsQueryParam) {

        // 添加空值检查，防止NullPointerException
        int pageNum = (itemsQueryParam.getPage() != null) ? itemsQueryParam.getPage() : 1;
        int pageSize = (itemsQueryParam.getPageSize() != null) ? itemsQueryParam.getPageSize() : 10;

        log.info("准备开始分页查询: page={}, pageSize={}", pageNum, pageSize);
        PageHelper.startPage(pageNum, pageSize);
        //pagehelper 初始化

        // 检查PageHelper是否正确设置
        com.github.pagehelper.Page<Object> page = com.github.pagehelper.PageHelper.getLocalPage();
        if (page != null) {
            log.info("PageHelper本地页面对象创建成功: pageNum={}, pageSize={}", page.getPageNum(), page.getPageSize());
        } else {
            log.warn("PageHelper本地页面对象未正确创建");
        }

        log.info("即将执行查询...");
        List<Items> itemsList = itemMapper.list(itemsQueryParam);
        log.info("查询执行完成，结果列表大小: {}", itemsList.size());

        PageInfo<Items> pageInfo = new PageInfo<>(itemsList);
        log.info("PageInfo构建完成: 总记录数={}, 当前页码={}, 每页大小={}, 总页数={}",
                pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getPages());

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());


    }
    @Override
    public Items getById(String productId) {

        return itemMapper.getById(productId);
    }

    @Transactional
    @Override
    public void createItem(Items items) {
        items.setProductId(UUID.randomUUID().toString());//采用随机uuid生成为一商品id
        items.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        items.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        items.setLastUpdated(Timestamp.valueOf(LocalDateTime.now()));
        items.setStatus(1); //默认设置状态为1
      itemMapper.createItem(items);//事务管理，一个不对则直接回滚
      itemMapper.createRatting(items);
    }
    @Transactional
    @Override
    public void updateItem(Items items) {
        items.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        items.setLastUpdated(Timestamp.valueOf(LocalDateTime.now()));
        itemMapper.updateItem(items);
        itemMapper.updateRatting(items);
    }

    @Override
    public void deleteItem(List<String> productID) {
        itemMapper.deleteItem(productID);
    }

    @Override
    public List<Price> getItemPrice(String productId) {
        return itemMapper.getItemPrice(productId);
    }


}
