package com.feng.youxi.service;

import com.feng.youxi.pojo.*;

import java.util.List;

public interface ItemService {
     PageResult<Items> page(ItemsQueryParam itemsQueryParam);

     Items getById(String productId);

     void createItem(Items items);

     void updateItem(Items items);

     void deleteItem(List<String> productID);

     List<Price> getItemPrice(String productId);


}
