package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 进行全局异常处理
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})//关于annotations注解处理RestController和Controller
@ResponseBody//写一个方法返回json数据
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        log.error(e.getMessage());
        if(e.getMessage().contains("Duplicate entry")) {//e.getMessage()错误信息中包含Duplicate entry
            String[] split = e.getMessage().split(" ");//根据数组进行分隔来区分元素
            String msg =split[2]+"该账号已存在了哦请重新输入";//来拼接
            return R.error(msg);

        }
        return R.error("未知错误");
    }
    @ExceptionHandler(CustomExcption.class)
    public R<String> exceptionHandler(CustomExcption e) {
        log.error(e.getMessage());
        return R.error(e.getMessage());
    }

}
