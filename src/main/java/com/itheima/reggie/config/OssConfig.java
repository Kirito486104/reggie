//package com.itheima.reggie.config;
//
//import com.aliyun.oss.OSS;
//import com.aliyun.oss.OSSClientBuilder;
//import lombok.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.bind.annotation.RestController;
//
//@Configuration
//public class OssConfig {
//
//    @Value("${aliyun.oss.endpoint}")
//    private String endpoint;
//    @Value("${aliyun.oss.access-key-id}")
//    private String accessKeyId;
//
//    @Value("${aliyun.oss.access-key-secret}")
//    private String accessKeySecret;
//
//    @Bean
//    public OSS ossClient() {
//        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//    }
//
//}
