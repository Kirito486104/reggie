package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();//包装一个查询对象
        queryWrapper.eq(Employee::getUsername,employee.getUsername());//添加查询条件    等值查询根据用户名对象
        Employee emp = employeeService.getOne(queryWrapper);//调用employeeService中的getone方法  Employee::getUsername是规定的唯一约束

        //3、如果没有查询到则返回登录失败结果
       if(emp == null){
           return R.error("登录失败");
       }
        //4、密码比对，如果不一致则返回登录失败结果
        if(!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());//
        return R.success(emp);
    }

    /**
     * 员工退出0
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");//跟将员工id存入Session中一样怎么放的就怎么删除
        return R.success("退出成功");
    }
    /**
     * 新增员工
     * @param employee
     * @return
     */

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工，员工信息为：{}",employee.toString());
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

       //employee.setCreateTime(LocalDateTime.now());
       // employee.setUpdateTime(LocalDateTime.now());


        //将员工id存入Session并返回登录成功结果
     // Long empId= (Long) request.getSession().getAttribute("employee");

      // employee.setCreateUser(empId);
    //employee.setUpdateUser(empId);
       employeeService.save(employee);
        return R.success("新增员工成功嘿嘿");
    }

    /**
     *员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        if(!StringUtils.isEmpty(name)){
            queryWrapper.like(Employee::getUsername,name);
        }
        //添加排序条件
        queryWrapper.orderByAsc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据id修改
     * @param employee
     * @return
     */
   @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
      // Long empId= (Long) request.getSession().getAttribute("employee");//通过request获取session中的id，，，并且long传入到js浏览器中会丢失精度
       //employee.setCreateTime(LocalDateTime.now());
       //employee.setUpdateUser(empId);//通过request获取session中的id，然后赋值给employee的updateUser

       long id = Thread.currentThread().getId();
       log.info("线程id为: {}",id);

       employeeService.updateById(employee);//调用service中的update方法

        return R.success("员工修改信息成功");

    }
    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){//@PathVariable注解用于获取url中的参数,路径变量
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);//调用service中的getById方法
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
        //根据id查询员工信息
    }


}
