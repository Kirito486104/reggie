package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */

@Slf4j
@RequestMapping("/dish")
@RestController
public class DishColltroller {
    @Autowired
    private DishService dishService;


    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询菜品信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>(); //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name); //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");//从pageInfo中拷贝除records以外的属性到dishDtoPage中
        List<Dish> records = pageInfo.getRecords();//获取records集合
        List<DishDto> list = records.stream().map((item) -> {//使用stream流，遍历集合，并修改集合中的每个元素
            DishDto dishDto = new DishDto();//创建dishDto对象
            BeanUtils.copyProperties(item,dishDto);//将item中的属性拷贝到dishDto中
            Long categoryId = item.getCategoryId();//获取item中的categoryId
            Category category = categoryService.getById(categoryId);//根据categoryId查询category
            if(category != null){//如果category不为空,则将categoryName赋值给dishDto
                String categoryName = category.getName();//获取categoryName
                dishDto.setCategoryName(categoryName);//将categoryName赋值给dishDto
            }
            return dishDto;
        }).collect(Collectors.toList());// 将修改后的集合赋值给dishDtoPage,并返回
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /**
     * 根据id查询对应口味和菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){//@PathVariable注解用于获取url中的参数,路径变量

        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 更新菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }
    /**
//    /**
//     * 删除菜品
//     * @param
//     * @return
//     */
    @DeleteMapping
    public R<String> delete(String ids){
        log.info("删除菜品，id为：{}",ids);
        dishService.removeByIds(Collections.singleton(ids));
        return R.success("删除成功");
    }
//    @PostMapping("/status/{status}")
//    public R<String> stopSale(
//            @PathVariable int status,  // 从URL路径中获取状态码
//            @RequestParam String ids) {
//
////        Long id = (Long) request.getSession().getAttribute("dish");
////        dish.setUpdateTime(LocalDateTime.now());
////        dish.setUpdateUser(dishId);
//        long id = Thread.currentThread().getId();
//        log.info("线程id为: {}",id);
//        dishService.updateById(status, ids);
//        return "Dish status updated successfully.";
//
//    }


    /**
     * 根据条件查询菜品数据
     * @param dish
     */

    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }
    @PostMapping("/status/{status}")
    public R<String> stopSale(@PathVariable int status, @RequestParam List<Long> ids) {
        log.info("status:{},ids:{}",status,ids);
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId,ids);
        updateWrapper.set(Dish::getStatus,status);
        dishService.update(updateWrapper);
        return R.success("批量停售成功");
    }


}


