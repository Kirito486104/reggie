package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.extensions.compactnotation.PackageCompactConstructor;

/**
 * 配置MP的分页插件
 */
@Configuration
public class MybatisPlusConfig {
    @Bean//让spring来管理
    public MybatisPlusInterceptor mybatisPlusInterceptor() { //MybatisPlusInterceptor是个拦截器
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());//通过加入拦截器
        return mybatisPlusInterceptor;
    }

}
