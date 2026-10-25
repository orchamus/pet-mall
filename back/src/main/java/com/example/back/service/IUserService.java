package com.example.back.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.back.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.back.Result.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务接口。
 *
 * <p>定义用户全生命周期相关操作，包括注册、登录、登出、信息维护、密码修改、头像上传及用户列表查询。</p>
 */
public interface IUserService extends IService<User> {

    /**
     * 向指定邮箱发送验证码。
     *
     * @param Email 目标邮箱地址
     * @return 包含验证码的响应结果
     */
    public ResponseResult<String> sendEmailCode(String Email);

    /**
     * 通过邮箱验证码注册用户。
     *
     * @param user 待注册用户实体
     * @param code 邮箱验证码
     * @return 注册是否成功
     */
    public ResponseResult<Boolean> registerByemail(User user,String code);

    /**
     * 使用用户名和密码登录。
     *
     * @param user 包含用户名和密码的用户实体
     * @return 登录成功后的 token
     */
    public ResponseResult<String> loginByuser(User user);

    /**
     * 使用邮箱验证码登录。
     *
     * @param email 用户邮箱
     * @param code  邮箱验证码
     * @return 登录成功后的 token
     */
    public ResponseResult<String> loginByemail(String email,String code);

    /**
     * 用户登出。
     *
     * @param token 当前登录 token
     * @return 登出是否成功
     */
    public ResponseResult<Boolean> logout(String token);

    /**
     * 根据 token 获取当前登录用户信息。
     *
     * @param token 用户登录 token
     * @return 当前用户信息
     */
    public ResponseResult<User> getUserByToken(String token);

    /**
     * 更新用户信息。
     *
     * @param user 待更新的用户实体
     * @return 更新是否成功
     */
    public ResponseResult<Boolean> updateUserInfo(User user);

    /**
     * 修改用户密码。
     *
     * @param token       用户登录 token
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 密码修改是否成功
     */
    public ResponseResult<Boolean> updatePassword(String token,String oldPassword,String newPassword);

    /**
     * 更新用户头像。
     *
     * @param token 用户登录 token
     * @param file  头像文件
     * @return 头像更新是否成功
     */
    public boolean updateAvatar(String token, MultipartFile file);

    /**
     * 分页查询用户列表。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param userName 用户名模糊查询关键字，可为空
     * @return 用户分页结果
     */
    public Page<User> getAllUserListByPage(Integer pageNum, Integer pageSize, String userName);
}
