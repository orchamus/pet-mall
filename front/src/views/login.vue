<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">宠物商店登录</h2>
      <!-- 登录方式切换：用户名登录 / 邮箱登录 -->
      <el-tabs v-model="activeTab" class="login-tabs">
        <!-- 用户名密码登录面板 -->
        <el-tab-pane name="userName" label="用户名登录">
          <el-form :model="usernameForm" :rules="usernameRules" ref="usernameFormRef" label-width="0">
            <el-form-item prop="username">
              <el-input v-model="usernameForm.userName" prefix-icon="el-icon-user" placeholder="请输入用户名"></el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="usernameForm.password" prefix-icon="el-icon-lock" placeholder="请输入密码" show-password></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleUsernameLogin">登录</el-button>
              <el-button @click="handleResetUsernameForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 邮箱验证码登录面板 -->
        <el-tab-pane name="email" label="邮箱登录">
          <el-form :model="emailForm" :rules="emailRules" ref="emailFormRef" label-width="0">
            <el-form-item prop="email">
              <el-input v-model="emailForm.email" prefix-icon="el-icon-message" placeholder="请输入邮箱"></el-input>
            </el-form-item>
            <el-form-item prop="code">
              <!-- 验证码输入框，附带发送验证码按钮 -->
              <el-input v-model="emailForm.code" prefix-icon="el-icon-message" placeholder="请输入验证码" class="sms-code-input">
                <template #append>
                  <el-button @click="handleSendSmsCodeByEmail" :disabled="emailCodeDisabled">{{ emailCodeText }}</el-button>
                </template>
              </el-input>
              <!-- 模拟验证码提示，用于开发测试环境 -->
              <div v-if="mockCode" class="mock-code-tip">
                <span>模拟验证码：{{ mockCode }}</span>
                <el-button type="primary" link @click="copyCode">复制</el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleEmailLogin">登录</el-button>
              <el-button @click="handleResetEmailForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <!-- 跳转到注册页面 -->
      <div class="register-link">
        <router-link to="/register">没有账号？前往注册</router-link>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ElMessage } from "element-plus";
import {loginAndUser, senMsgByEmailForLogin, loginByemail} from "../api/loginAndUser";

export default {
  data() {
    return {
      // 当前选中的登录方式
      activeTab: "userName",
      // 邮箱验证码按钮是否禁用
      emailCodeDisabled: false,
      // 邮箱验证码按钮显示文本
      emailCodeText: "获取验证码",
      // 邮箱验证码倒计时定时器
      emailCodeTimer: null,
      // 后端返回的模拟验证码
      mockCode: "",
      // 用户名登录表单
      usernameForm: {
        userName: "",
        password: "",
      },
      // 用户名登录表单校验规则
      usernameRules: {
        userName: [
          { required: true, message: "请输入用户名", trigger: "blur" },
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, message: "密码长度不能少于6位", trigger: "blur" },
        ],
      },
      // 邮箱登录表单
      emailForm: {
        email: "",
        code: "",
      },
      // 邮箱登录表单校验规则
      emailRules: {
        email: [
          { required: true, message: "请输入邮箱", trigger: "blur" },
          { type: "email", message: "邮箱格式不正确", trigger: "blur" },
        ],
        code: [
          { required: true, message: "请输入验证码", trigger: "blur" },
          { len: 6, message: "验证码长度为6位", trigger: "blur" },
      ],
     },
  };
  },
  methods: {
    // 用户名密码登录
    handleUsernameLogin() {
      this.$refs.usernameFormRef.validate((valid) => {
        if (valid) {
          // 调用用户名登录 API，成功后保存 token 并跳转首页
          loginAndUser(this.usernameForm).then((res) => {
            if (res.code === 200) {
              sessionStorage.setItem('token', res.data);
              console.log(res.data);
              this.$router.push('/');
              ElMessage.success("登录成功");
            }
          });
        } else {
          ElMessage.error("表单验证失败");
        }
      });
    },
    // 重置用户名登录表单
    handleResetUsernameForm() {
      this.$refs.usernameFormRef.resetFields();
    },
    // 发送邮箱验证码
    handleSendSmsCodeByEmail() {
      if (!this.emailForm.email) {
        ElMessage.error("请输入邮箱");
        return;
      }
      // 校验邮箱格式
      if(!/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(this.emailForm.email)){
        ElMessage.error("邮箱格式不正确");
        return;
      }
      // 启动 60 秒倒计时
      this.emailCodeDisabled = true;
      this.emailCodeTimer = 60;
      this.emailCodeText = `${this.emailCodeTimer}s后重试`;
      const timer = setInterval(() => {
        if (this.emailCodeTimer > 0) {
          this.emailCodeTimer--;
          this.emailCodeText = `${this.emailCodeTimer}s后重试`;
        } else {
          clearInterval(timer);
          this.emailCodeDisabled = false;
          this.emailCodeText = "获取验证码";
        }
      }, 1000);
      // 发送验证码逻辑（模拟发送，后端直接返回验证码）
      senMsgByEmailForLogin(this.emailForm.email).then((res) => {
        if (res.data) {
          this.mockCode = res.data;
          this.emailForm.code = res.data;
          ElMessage.success("验证码已生成，可直接复制使用");
        } else {
          ElMessage.error(res.message || "验证码生成失败");
        }
      }).catch(err => {
        console.log(err);
      });
    },
    // 复制模拟验证码到剪贴板
    copyCode() {
      navigator.clipboard.writeText(this.mockCode).then(() => {
        ElMessage.success("验证码已复制");
      }).catch(() => {
        ElMessage.error("复制失败");
      });
    },
    // 邮箱验证码登录
    handleEmailLogin() {
      this.$refs.emailFormRef.validate((valid) => {
        if (valid) {
          // 调用邮箱登录 API
          loginByemail(this.emailForm.email, this.emailForm.code).then((res) => {
            if (res.data) {
              sessionStorage.setItem('token', res.data);
              this.$router.push('/');
              ElMessage.success(res.message);
            }else {
              ElMessage.error(res.message);
            }
          });
        } else {
          ElMessage.error("表单验证失败");
        }
      });
    },
    // 重置邮箱登录表单
    handleResetEmailForm() {
      this.$refs.emailFormRef.resetFields();
    },
  },
};
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url("http://localhost:8090/media/background.jpg");
  background-size: cover; /* 确保背景图覆盖整个容器 */
}

.login-card {
  width: 400px;
  padding: 20px;
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
}

.login-tabs {
  margin-bottom: 20px;
}

.sms-code-input .el-input-group__append {
  padding: 0 10px;
}

.register-link {
  text-align: right;
  margin-top: 10px;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
}

.register-link a:hover {
  text-decoration: underline;
}

.mock-code-tip {
  margin-top: 8px;
  color: #e6a23c;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
