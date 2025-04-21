package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
* 检查用户是否已经登陆的过滤器
*
* */
@Component
@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")//filterName是拦截器名字，，urlPatterns是拦截路径
public class LoginCheckFilter implements Filter//过滤器
{   //路径匹配器、支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
        @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;//直接对servletRequest强转
       HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URl,,定义不需要处理的路径
        String requestURl =request.getRequestURI();//backend/index.html
            log.info("拦截到请求:{}",requestURl);
        //getRequestURI  和 urls 用一个新对象来转接
        String[] urls =  new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",//文件上传需要登录验证路径
                "/user/sendMsg",
                "/user/login"
        };
        //2、判断本次请求是否需要处理
        boolean check = check(urls,requestURl);
        //3.如果不需要处理直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURl);
            filterChain.doFilter(request,response);
            return;
        }
            //4.1判断登录状态，如果已登录直接放行
            if (request.getSession().getAttribute("employee") != null) {
                log.info("用户已登录，用户id为{}", request.getSession().getAttribute("employee"));

                long empId = (long) request.getSession().getAttribute("employee");
                BaseContext.setCurrentId(empId);//将当前线程id存入到BaseContext中

                long id = Thread.currentThread().getId();
                log.info("线程id为: {}", id);

                filterChain.doFilter(request, response);
                return;
            }
            //4.2判断登录状态，如果已登录直接放行
            if( request.getSession().getAttribute("user")!= null){
                log.info("用户已登录，用户id为{}",request.getSession().getAttribute("user"));

                long userId = (long) request.getSession().getAttribute("user");
                BaseContext.setCurrentId(userId);//将当前线程id存入到BaseContext中
//
//                long id = Thread.currentThread().getId();
//                log.info("线程id为: {}",id);

                filterChain.doFilter(request,response);
                return;
            }


            log.info("用户未登录");
         //5.如果未登录直接返回未登录结果，通过输出流的方式给客户端页面响应数据

            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        }



    /**
     *
     * 进行路径匹配，检查本次请求需要放行
     * @param urls
     * @param requestURl
     * @return
     */
    public boolean check(String[] urls,String requestURl )//遍历上面传过来请求url
    {
        for(String url:urls){
            boolean match = PATH_MATCHER.match(url,requestURl);
            if(match){
                return true;
            }
        }
        return false;
    }

}
