//package com.itheima.reggie.controller;
//
//import com.itheima.reggie.common.R;
//import com.itheima.reggie.entity.User;
//import com.itheima.reggie.service.UserService;
//import com.itheima.reggie.utils.ValidateCodeUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.protocol.HTTP;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/user")
//@Slf4j
//public class UserColltroller {
//
//    @Autowired
//    private UserService userService;
//
//    /**
//     * 发送短信验证码
//     * @param user
//     * @param session
//     * @return
//     */
//    @PostMapping("/sendMsg")
//    public R<String> sendMsg(@RequestBody User user , HTTP session){
//        String phone = user.getPhone();
//         if(StringUtils.isNotEmpty(phone)){
//             String code = ValidateCodeUtils.generateValidateCode4String(4);
//             log.info("code={}",code);
//
//
//
//             //调用阿里云提供短信服务API发送短信验证码
//
//             //需要将生成码码保存到Session
//             session.setAttribute(phone,code);
//             return R.success("短信发送成功");
//         }
//
//
//        return R.error("短信发送失败");
//    }
//}
