package com.itheima.reggie.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.UUID;

/**
 * 阿里云 OSS 工具类
 */
@Component
public class AliOSSUtils {

    @Value("aliyun.oss.endpoint")
    private String endpoint;
    @Value("aliyun.oss.access-key-id")
    private String accessKeyId;
    @Value("aliyun.oss.access-key-secret")
    private String accessKeySecret;
    @Value("aliyun.oss.bucket-name")
    private String bucketName;
    /**
     * 实现上传图片到OSS
     */
    /**
     * 上传文件到 OSS
     * @param file 文件
     * @param localBasePath 本地存储路径
     * @return 文件访问路径
     */
    public String upload(MultipartFile file, String localBasePath) throws IOException {
        // 获取上传的文件的输入流
        InputStream inputStream = file.getInputStream();

        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));

        // 本地存储路径
        File localDir = new File(localBasePath);
        if (!localDir.exists()) {
            localDir.mkdirs();
        }
        File localFile = new File(localBasePath + fileName);

        // 将文件存储到本地
        file.transferTo(localFile);

        // 上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);

        // 关闭 OSS 客户端
        ossClient.shutdown();

        // 返回文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;
        return url;

    }

}
