package com.itheima.reggie.controller;


import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@Slf4j
@RequestMapping("/common")
@RestController
public class CommonColltroller {


    @Value("${reggei.path}")
    private String basePath;
    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping ("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        log.info(file.toString());
        InputStream inputStream = file.getInputStream();
        //原文件名
        String filename = file.getOriginalFilename();
        //截取文件名使用uuid生成文件名
        String s = UUID.randomUUID().toString()+ filename.substring(filename.lastIndexOf("."));
        File dir = new File(basePath);//创建一个目录对象
        if (!dir.exists()) {//判断目录是否存在
            dir.mkdirs();
        }

            //将临时文件转存到指定位置
            file.transferTo(new File(basePath+s));

        // 上传文件到 OSS
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//        ossClient.putObject(bucketName, s,inputStream );
//
//        // 关闭 OSS 客户端
//        ossClient.shutdown();

        return R.success(s);
    }
    /**
     * 文件下载
     * @param name
     * @param response
     * @return
     */

    @GetMapping("/download")
    public void download(String name , HttpServletResponse response ){
        //输入流，通过输入流读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));
            //把这文件读到浏览器，在浏览器展示图片
            //输出流，通过输出流将文件写回浏览器 ，在浏览器展示图片
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");//设置响应类型
            int len =0;
            byte[] bytes = new byte[4096];//把读到的数据放到byte数组中
            while ((len=fileInputStream.read(bytes))!=-1){
                outputStream.write(bytes,0,len);//写出来从0开始，读到len为止
                outputStream.flush();//刷新
            }
            outputStream.close();
            fileInputStream.close();
        }  catch (Exception e) {
           e.printStackTrace();
        }


    }
}
