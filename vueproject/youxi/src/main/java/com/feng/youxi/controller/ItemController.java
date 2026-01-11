package com.feng.youxi.controller;

import com.feng.youxi.pojo.*;
import com.feng.youxi.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//游戏商品模块
@RequestMapping("/items")
@Slf4j
@RestController
public class ItemController {

@Autowired
private ItemService itemService;
    @GetMapping
    public Result getItems(ItemsQueryParam itemsQueryParam){
     PageResult<Items> pageResult = itemService.page(itemsQueryParam);
     return Result.success(pageResult);
    }

    @GetMapping("/{productId}")
    public Result getItem(@PathVariable("productId") String productId){
        return Result.success(itemService.getById(productId));
    }

    @PostMapping
    public Result createItem(@RequestBody Items items){
        itemService.createItem(items);
        return Result.success();
    }
    @PutMapping
    public Result updateItem(@RequestBody Items items){
        itemService.updateItem(items);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteItem(@RequestParam List<String> productId){
        itemService.deleteItem(productId);
        return Result.success();
    }
   @GetMapping("/price/{productId}")
    public Result getItemPrice(@PathVariable("productId") String productId){
      List <Price> priceList= itemService.getItemPrice(productId);
        return Result.success(priceList);
   }

}
