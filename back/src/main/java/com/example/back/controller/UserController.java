package com.example.back.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.Result.ResponseResult;
import com.example.back.config.ExceptionConfig.BusinessException;
import com.example.back.entity.User;
import com.example.back.service.impl.UserServiceImpl;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户模块 Controller。
 *
 * <p>提供用户注册、登录、信息维护、密码修改、头像上传及用户列表查询能力，基础路径为 /user。</p>
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserServiceImpl userService;

    /**
     * 发送注册验证码。
     *
     * @param email 用户邮箱
     * @return 验证码发送结果
     */
    @PostMapping("/sendEmailCodeForRegister")
    public ResponseResult<String> sendEmailcode(@RequestParam String email){
        return userService.sendEmailCodeForRegist(email);
    }

    /**
     * 发送登录验证码。
     *
     * @param email 用户邮箱
     * @return 验证码发送结果
     */
    @PostMapping("/sendEmailCodeForLogin")
    public ResponseResult<String> sendEmailcodeForLogin(@RequestParam String email){
        return userService.sendEmailCode(email);
    }

    /**
     * 用户注册。
     *
     * <p>接收 JSON 格式的用户信息字符串与验证码，完成用户注册；缺失字段使用默认值填充。</p>
     *
     * @param userJson 用户信息 JSON 字符串
     * @param code     邮箱验证码
     * @return 注册是否成功
     */
    @PostMapping("/register")
    public ResponseResult<Boolean> register(@RequestParam("user") String userJson, @RequestParam("code") String code){
        // 将 JSON 字符串反序列化为 User 对象
        User user =  new Gson().fromJson(userJson, User.class);
        // 初始化默认状态与删除标识
        user.setStatus("0");
        user.setDelFlag("0");
        // 用户类型与性别为空时赋予默认值
        if (!StringUtils.hasLength(user.getUserType())) {
            user.setUserType("0");
        }
        if (!StringUtils.hasLength(user.getSex())) {
            user.setSex("0");
        }
        return userService.registerByemail(user,code);
    }

    /**
     * 账号密码登录。
     *
     * @param user 包含账号密码的用户对象
     * @return 登录成功后返回 token
     */
    @PostMapping("/login")
    public ResponseResult<String> login(@RequestBody User user){
        return userService.loginByuser(user);
    }

    /**
     * 邮箱验证码登录。
     *
     * @param code  邮箱验证码
     * @param email 用户邮箱
     * @return 登录成功后返回 token
     */
    @PostMapping("/loginByEmail")
    public ResponseResult<String> loginByEmail(@RequestParam("code") String code, @RequestParam("email") String email){
        return userService.loginByemail(email,code);
    }

    /**
     * 获取当前登录用户信息。
     *
     * @param token 登录凭证（请求头 satoken）
     * @return 当前用户信息
     */
    @GetMapping("/getUserInfo")
    public ResponseResult<User> getUser(@RequestHeader("satoken") String token){
        return userService.getUserByToken(token);
    }

    /**
     * 退出登录。
     *
     * @param token 登录凭证（请求头 satoken）
     * @return 退出是否成功
     */
    @GetMapping("/logout")
    public ResponseResult<Boolean> logout(@RequestHeader("satoken") String token){
        return userService.logout(token);
    }

    /**
     * 更新用户信息。
     *
     * @param user 用户对象
     * @return 更新是否成功
     */
    @PostMapping("/updateUserInfo")
    public ResponseResult<Boolean> updateUserInfo(@RequestBody User user){
        try {
            return userService.updateUserInfo(user);
        } catch (BusinessException e) {
            // 业务异常返回具体错误信息
            return ResponseResult.fail(e.getMessage(),false);
        } catch (Exception e) {
            // 系统异常返回通用提示
            return ResponseResult.fail("系统异常，请稍后重试", false);
        }
    }

    /**
     * 修改用户密码。
     *
     * @param token       登录凭证（请求头 satoken）
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 密码修改是否成功
     */
    @PostMapping("/updateUserPassword")
    public ResponseResult<Boolean> updateUserPassword(@RequestHeader("satoken") String token, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        return userService.updatePassword(token,oldPassword,newPassword);
    }

    /**
     * 更新用户头像。
     *
     * @param token  登录凭证（请求头 satoken）
     * @param avatar 头像文件
     * @return 头像更新是否成功
     */
    @PostMapping("/updateAvatar")
    public ResponseResult<Boolean> updateAvatar(@RequestHeader("satoken") String token, @RequestParam("avatar")MultipartFile avatar){
        if(userService.updateAvatar(token,avatar)){
            return ResponseResult.success("头像更新成功",true);
        }else{
            return ResponseResult.fail("头像更新失败",false);
        }
    }

    /**
     * 分页查询全部用户列表。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param userName 用户名关键字（可选）
     * @return 用户分页数据
     */
    @GetMapping("/getAllUser")
    public ResponseResult<Page<User>> getAllUser(Integer pageNum, Integer pageSize,String userName){
        Page<User> page = userService.getAllUserListByPage(pageNum, pageSize,userName);
        if(page != null){
            return ResponseResult.success("获取成功",page);
        }else{
            return ResponseResult.fail("获取失败",null);
        }
    }
}
