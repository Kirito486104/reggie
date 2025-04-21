package com.itheima.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用于保存和获取当前登录用户id
 */
public class BaseContext {//以线程为作用域，每个线程都对应一个threadlocal单独保存在自己的副本
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    //设置值在threadlocal存放用户的id
    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);//保存值设置进去
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();//取出来
    }
}
