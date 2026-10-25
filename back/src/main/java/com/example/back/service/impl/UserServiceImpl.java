package com.example.back.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.back.Result.ResponseResult;
import com.example.back.config.ExceptionConfig.BusinessException;
import com.example.back.entity.User;
import com.example.back.mapper.UserMapper;
import com.example.back.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

/**
 * 用户服务实现类。
 *
 * <p>实现 {@link IUserService} 接口，提供用户注册、登录、信息维护、密码修改、头像上传等能力。
 * 密码使用固定 salt 的 MD5 单向加密存储；验证码通过 {@link MsgcodeServiceImpl} 进行内存级缓存管理，有效期 15 分钟。</p>
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    // 邮箱正则表达式
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    // 密码强度正则表达式(至少8位，包含字母和数字)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    // 用户名正则表达式(4-20位字母数字下划线)
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    @Value("${file.upload-path}")
    private String uploadPath;

    private final MsgcodeServiceImpl mailMsg;

    /**
     * 构造方法，注入验证码服务。
     *
     * @param mailMsg 验证码服务实现
     */
    @Autowired
    public UserServiceImpl(MsgcodeServiceImpl mailMsg) {
        this.mailMsg = mailMsg;
    }

    /**
     * 对明文密码进行单向加密。
     *
     * <p>采用固定 salt + MD5 的方式生成密码摘要，加密结果为小写十六进制字符串。
     * 该算法仅用于当前系统的密码存储，不建议在高安全场景下复用。</p>
     *
     * @param password 明文密码
     * @return 加密后的密码摘要
     * @throws IllegalArgumentException 当密码为空时抛出
     * @throws RuntimeException         当 MD5 算法不可用或加密过程异常时抛出
     */
    public String passwordEncoder(String password) {
        if (!StringUtils.hasLength(password)) {
            throw new IllegalArgumentException("密码不能为空");
        }

        String salt = "RandomSalt123!@#";
        String saltedPassword = password + salt;
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
        byte[] hashedBytes = md.digest(saltedPassword.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashedBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * 发送邮箱验证码。
     *
     * <p>若目标邮箱已存在未过期验证码则直接返回；否则生成 6 位数字验证码并缓存，有效期 15 分钟。</p>
     *
     * @param Email 目标邮箱地址
     * @return 包含验证码的响应结果
     */
    @Override
    public ResponseResult<String> sendEmailCode(String Email) {
        if (!StringUtils.hasLength(Email)) {
            return ResponseResult.fail("邮箱不能为空", null);
        }
        // 校验邮箱格式
        if (!EMAIL_PATTERN.matcher(Email).matches()) {
            return ResponseResult.fail("邮箱格式不正确", null);
        }

        String existing = mailMsg.getCode(Email);
        if (existing != null) {
            return ResponseResult.success("验证码已存在，还未过期", existing);
        }
        String code = mailMsg.sendMsgCodeByeMail(Email);
        if (code != null) {
            return ResponseResult.success("验证码发送成功！", code);
        }
        return ResponseResult.fail("验证码生成失败", null);
    }

    /**
     * 发送注册用邮箱验证码。
     *
     * <p>在发送验证码前校验邮箱是否已被绑定，避免重复注册。</p>
     *
     * @param Email 目标邮箱地址
     * @return 包含验证码的响应结果
     */
    public ResponseResult<String> sendEmailCodeForRegist(String Email) {
        if (!StringUtils.hasLength(Email)) {
            return ResponseResult.fail("邮箱不能为空", null);
        }
        if (!EMAIL_PATTERN.matcher(Email).matches()) {
            return ResponseResult.fail("邮箱格式不正确", null);
        }

        // 校验邮箱是否已被绑定
        User user = this.getOne(new QueryWrapper<User>().eq("email", Email));
        if(user != null){
            return ResponseResult.fail("邮箱已经被绑定！", null);
        }
        return sendEmailCode(Email);
    }

    /**
     * 通过邮箱验证码注册用户。
     *
     * <p>注册流程：</p>
     * <ol>
     *   <li>校验邮箱、用户名、密码、验证码格式；</li>
     *   <li>校验验证码正确性；</li>
     *   <li>对密码进行 MD5 加密并设置默认头像；</li>
     *   <li>保存用户，成功后清除验证码。</li>
     * </ol>
     *
     * @param user 待注册用户实体
     * @param code 邮箱验证码
     * @return 注册是否成功
     */
    @Override
    public ResponseResult<Boolean> registerByemail(User user, String code) {
        if (user == null) {
            return ResponseResult.fail("用户信息不能为空", false);
        }
        if (!StringUtils.hasLength(user.getEmail())) {
            return ResponseResult.fail("邮箱不能为空", false);
        }
        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return ResponseResult.fail("邮箱格式不正确", false);
        }
        if (!StringUtils.hasLength(user.getUserName())) {
            return ResponseResult.fail("用户名不能为空", false);
        }
        if (!USERNAME_PATTERN.matcher(user.getUserName()).matches()) {
            return ResponseResult.fail("用户名必须是4-20位字母数字下划线", false);
        }
        if (!StringUtils.hasLength(user.getPassword())) {
            return ResponseResult.fail("密码不能为空", false);
        }
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            return ResponseResult.fail("密码至少8位且包含字母和数字", false);
        }
        if (!StringUtils.hasLength(code)) {
            return ResponseResult.fail("验证码不能为空", false);
        }

        // 校验验证码是否存在且未过期
        String trueCode = mailMsg.getCode(user.getEmail());
        if (!StringUtils.hasLength(trueCode)) {
            return ResponseResult.fail("验证码已过期！",false);
        }

        if(code.equals(trueCode)){
            // 对密码进行加密并设置默认头像
            user.setPassword(passwordEncoder(user.getPassword()));
            user.setAvatar("/media/userAvatar/default.jpg");

            try {
                if (this.save(user)) {
                    // 注册成功后清除该邮箱验证码
                    mailMsg.removeCode(user.getEmail());
                    return ResponseResult.success("注册成功！",true);
                }
            } catch (DuplicateKeyException e) {
                return ResponseResult.fail("该用户名已被注册",false);
            }
        } else {
            return ResponseResult.fail("验证码错误！",false);
        }

        return ResponseResult.fail("注册失败！",false);
    }

    /**
     * 使用用户名和密码登录。
     *
     * <p>校验用户状态，若用户被禁用（status=1）则拒绝登录；校验通过后使用 Sa-Token 生成登录 token。</p>
     *
     * @param user 包含用户名和密码的用户实体
     * @return 登录成功后的 token
     */
    @Override
    public ResponseResult<String> loginByuser(User user) {
        if (user == null) {
            return ResponseResult.fail("用户信息不能为空");
        }
        if (!StringUtils.hasLength(user.getUserName())) {
            return ResponseResult.fail("用户名不能为空");
        }
        if (!StringUtils.hasLength(user.getPassword())) {
            return ResponseResult.fail("密码不能为空");
        }

        // 按用户名与加密后的密码查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        String password = passwordEncoder(user.getPassword());
        queryWrapper.eq("user_name", user.getUserName()).eq("password", password);
        User user1 = this.getOne(queryWrapper);

        if (user1 != null) {
            // 校验用户是否被禁用
            if("1".equals(user1.getStatus())){
                return ResponseResult.fail("用户已被禁用！");
            }
            StpUtil.login(user.getUserName());
            return ResponseResult.success("登录成功！", StpUtil.getTokenValue());
        }

        return ResponseResult.fail("用户名或密码错误！");
    }

    /**
     * 使用邮箱验证码登录。
     *
     * <p>校验验证码正确性，校验通过后使用 Sa-Token 生成登录 token。</p>
     *
     * @param email 用户邮箱
     * @param code  邮箱验证码
     * @return 登录成功后的 token
     */
    @Override
    public ResponseResult<String> loginByemail(String email, String code) {
        if (!StringUtils.hasLength(email)) {
            return ResponseResult.fail("邮箱不能为空");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ResponseResult.fail("邮箱格式不正确");
        }
        if (!StringUtils.hasLength(code)) {
            return ResponseResult.fail("验证码不能为空");
        }

        // 校验验证码是否存在且未过期
        String trueCode = mailMsg.getCode(email);
        if (!StringUtils.hasLength(trueCode)) {
            return ResponseResult.fail("验证码已过期！");
        }

        if(code.equals(trueCode)){
            User user = this.getOne(new QueryWrapper<User>().eq("email", email));
            if (user != null){
                // 校验用户是否被禁用
                if("1".equals(user.getStatus())){
                    return ResponseResult.fail("用户已被禁用！");
                }
                StpUtil.login(user.getUserName());
                return ResponseResult.success("登录成功！",StpUtil.getTokenValue());
            } else {
                return ResponseResult.fail("邮箱不存在！");
            }
        } else {
            return ResponseResult.fail("验证码错误！");
        }
    }

    /**
     * 用户登出。
     *
     * @param token 当前登录 token
     * @return 登出是否成功
     */
    @Override
    public ResponseResult<Boolean> logout(String token) {
        if (!StringUtils.hasLength(token)) {
            return ResponseResult.fail("token不能为空", false);
        }

        if (StpUtil.isLogin()) {
            StpUtil.logout(token);
            return ResponseResult.success("注销成功！", true);
        }
        return ResponseResult.fail("注销失败！", false);
    }


    /**
     * 根据 token 获取当前登录用户信息。
     *
     * @param token 用户登录 token
     * @return 当前用户信息
     */
    @Override
    public ResponseResult<User> getUserByToken(String token) {
        if (!StringUtils.hasLength(token)) {
            return ResponseResult.fail("token不能为空");
        }

        String loginId = (String) StpUtil.getLoginIdByToken(token);
        if (!StringUtils.hasLength(loginId)) {
            return ResponseResult.fail("无效的token");
        }

        // 通过用户名或邮箱查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", loginId).or().eq("email", loginId);
        User user = this.getOne(queryWrapper);
        if (user != null) {
            return ResponseResult.success("获取用户信息成功！",user);
        }
        return ResponseResult.fail("获取用户信息失败！");
    }

    /**
     * 更新用户信息。
     *
     * <p>校验用户名、邮箱格式，并校验邮箱是否被其他用户占用，避免重复绑定。</p>
     *
     * @param user 待更新的用户实体
     * @return 更新是否成功
     * @throws BusinessException 当邮箱已被其他用户使用时抛出
     */
    @Override
    public ResponseResult<Boolean> updateUserInfo(User user) {
        if (user == null) {
            return ResponseResult.fail("用户信息不能为空", false);
        }
        if (user.getId() == null) {
            return ResponseResult.fail("用户ID不能为空", false);
        }
        if (StringUtils.hasLength(user.getUserName()) && !USERNAME_PATTERN.matcher(user.getUserName()).matches()) {
            return ResponseResult.fail("用户名必须是4-20位字母数字下划线", false);
        }
        if (StringUtils.hasLength(user.getEmail()) && !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return ResponseResult.fail("邮箱格式不正确", false);
        }
        // 校验邮箱是否被其他用户占用
        if (StringUtils.hasText(user.getEmail())) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("email", user.getEmail());
            User existingUser = this.getOne(queryWrapper);
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                // 抛出业务异常，提示“邮箱已被使用”
                throw new BusinessException("邮箱已被使用");
            }
        }
        if(this.updateById(user)){
                return ResponseResult.success("更新用户信息成功！",true);
        }

        return ResponseResult.fail("更新用户信息失败！",false);
    }

    /**
     * 修改用户密码。
     *
     * <p>校验 token 有效性、旧密码正确性以及新密码强度，校验通过后对新密码进行加密存储。</p>
     *
     * @param token       用户登录 token
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 密码修改是否成功
     */
    @Override
    public ResponseResult<Boolean> updatePassword(String token, String oldPassword, String newPassword) {
        if (!StringUtils.hasLength(token)) {
            return ResponseResult.fail("token不能为空", false);
        }
        if (!StringUtils.hasLength(oldPassword)) {
            return ResponseResult.fail("旧密码不能为空", false);
        }
        if (!StringUtils.hasLength(newPassword)) {
            return ResponseResult.fail("新密码不能为空", false);
        }
        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            return ResponseResult.fail("新密码至少8位且包含字母和数字", false);
        }

        ResponseResult<User> userResponse = this.getUserByToken(token);
        if (!userResponse.isSuccess()) {
            return ResponseResult.fail(userResponse.getMessage(), false);
        }

        User user = userResponse.getData();
        // 校验旧密码是否正确
        oldPassword = passwordEncoder(oldPassword);
        if(user != null && user.getPassword().equals(oldPassword)){
            // 对新密码进行加密并更新
            newPassword = passwordEncoder(newPassword);
            user.setPassword(newPassword);
            if(this.updateById(user)){
                return ResponseResult.success("更新密码成功！",true);
            }
        } else {
            return ResponseResult.fail("旧密码错误！",false);
        }
        return ResponseResult.fail("更新密码失败！",false);
    }

    /**
     * 更新用户头像。
     *
     * <p>校验文件类型为 jpg/jpeg/png/gif，大小限制由调用方控制，头像保存到 uploadPath/userAvatar 目录下。</p>
     *
     * @param token 用户登录 token
     * @param file  头像文件
     * @return 头像更新是否成功
     * @throws IllegalArgumentException 当 token 无效、文件为空或格式不支持时抛出
     * @throws RuntimeException         当文件写入失败时抛出
     */
    @Override
    public boolean updateAvatar(String token, MultipartFile file) {
        if (!StringUtils.hasLength(token)) {
            throw new IllegalArgumentException("token不能为空");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        ResponseResult<User> userResponse = this.getUserByToken(token);
        if (!userResponse.isSuccess()) {
            throw new IllegalArgumentException("获取用户信息失败");
        }

        User user = userResponse.getData();
        String fileType = getString(file);

        String filePath = uploadPath + File.separator + "userAvatar" + File.separator + user.getUserName() + "." + fileType;
        File destFile = new File(filePath);

        try {
            // 确保目录存在
            destFile.getParentFile().mkdirs();
            file.transferTo(destFile);
            user.setAvatar("/media/userAvatar/" + user.getUserName() + "." + fileType);
            this.updateById(user);
        } catch (IOException e) {
            throw new RuntimeException("头像上传失败", e);
        }
        return true;
    }

    /**
     * 校验并提取 MultipartFile 的图片扩展名。
     *
     * @param file 上传文件
     * @return 小写的图片扩展名
     * @throws IllegalArgumentException 当文件名无效或扩展名不支持时抛出
     */
    private static String getString(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.lastIndexOf(".") == -1) {
            throw new IllegalArgumentException("文件格式不支持");
        }

        String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!"jpg".equals(fileType) && !"jpeg".equals(fileType) && !"png".equals(fileType) && !"gif".equals(fileType)) {
            throw new IllegalArgumentException("只支持jpg, jpeg, png, gif格式的图片");
        }
        return fileType;
    }

    /**
     * 分页查询用户列表。
     *
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param userName 用户名模糊查询关键字，可为空
     * @return 用户分页结果
     */
    @Override
    public Page<User> getAllUserListByPage(Integer pageNum, Integer pageSize, String userName) {
        int currentPage = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int size = (pageSize == null || pageSize < 1) ? 10 : pageSize;

        Page<User> page = new Page<>(currentPage, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasLength(userName)) {
            queryWrapper.like("user_name", userName);
        }

        return baseMapper.selectPage(page, queryWrapper);
    }
}
