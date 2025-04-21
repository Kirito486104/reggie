package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomExcption;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper,Setmeal> implements SetmealService {


   @Autowired
   private SetmealDishService setmealDishService;


    /**
    * 新增套餐，同时需要保存套餐和菜品的关联关系
    * @param setmealDto
    */

    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {

        this.save(setmealDto);// 保存套餐数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();// 获取套餐的菜品数据
        setmealDishes.stream().map((item) -> {// 批量保存套餐和菜品的关联关系
            item.setSetmealId(setmealDto.getId());// 设置套餐id
            return item;
        }).collect(Collectors.toList());// 将套餐和菜品的关联关系保存到数据库中

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态，确定是否可用删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if (count>0){//如果不能删除，抛出一个业务异常
            throw new CustomExcption("套餐正在售卖中，不能删除");
        }
        this.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishLambdaQueryWrapper);
        log.info("ids:{}",ids);


        //如果可以删除，先删除套餐表中的数据---setmeal

        //再删除套餐和菜品的关联表数据---setmeal_dish
    }
}
