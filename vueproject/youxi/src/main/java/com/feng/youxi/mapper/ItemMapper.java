package com.feng.youxi.mapper;

import com.feng.youxi.pojo.Items;
import com.feng.youxi.pojo.ItemsQueryParam;
import com.feng.youxi.pojo.NewsFenxiResult;
import com.feng.youxi.pojo.Price;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemMapper {
    public List<Items> list(ItemsQueryParam itemsQueryParam);

    Items getById(String productId);

    void createItem(Items items);

    void createRatting(Items items);

    void updateItem(Items items);

    void updateRatting(Items items);

    void deleteItem(List<String> productID);

    List<Price> getItemPrice(String productId);


}
