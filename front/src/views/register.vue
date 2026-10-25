<template>
  <div class="register-container">
    <el-card class="register-card">
      <h2 class="register-title">宠物商店注册</h2>
      <el-form
          :model="registerForm"
          :rules="registerRules"
          ref="registerFormRef"
          label-width="100px"
      >
        <el-form-item label="用户名" prop="userName">
          <el-input
              v-model="registerForm.userName"
              placeholder="请输入用户名"
          ></el-input>
        </el-form-item>
        <el-form-item label="昵称" prop="nickName">
          <el-input
              v-model="registerForm.nickName"
              placeholder="请输入昵称"
          ></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="重复密码" prop="confirmPassword">
          <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="registerForm.sex">
            <el-radio label="0">男</el-radio>
            <el-radio label="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <!-- 用户类型选择：普通用户 / 商家 -->
          <el-select v-model="registerForm.userType" placeholder="请选择用户类型">
            <el-option label="普通用户" value="0"></el-option>
            <el-option label="商家" value="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item  label="绑定邮箱" prop="email">
          <!-- 邮箱输入框，附带获取验证码按钮 -->
          <el-input
              v-model="registerForm.email"
              placeholder="请输入邮箱"
          >
            <template #append>
              <el-button
                  :disabled="emailCodeDisabled"
                  @click="handleSendEmailCode"
              >
                {{ emailCodeText }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="emailCode">
          <el-input
              v-model="code"
              placeholder="请输入验证码"
          ></el-input>
          <!-- 模拟验证码提示，用于开发测试环境 -->
          <div v-if="mockCode" class="mock-code-tip">
            <span>模拟验证码：{{ mockCode }}</span>
            <el-button type="primary" link @click="copyCode">复制</el-button>
          </div>
        </el-form-item>
        <el-form-item label="用户协议" prop="acceptAgreement">
          <el-checkbox v-model="registerForm.acceptAgreement">我已阅读并接受用户协议</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">注册</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <!-- 跳转到登录页面 -->
      <div class="login-link">
        <router-link to="/loginAndUser">已有账号？前往登录</router-link>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ElMessage } from "element-plus";
import {register , senMsgByEmailForRegister} from "../api/loginAndUser";

export default {
  data() {
    // 自定义校验：确认密码是否一致
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== this.registerForm.password) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    // 自定义校验：是否接受用户协议
    const validateAcceptAgreement = (rule, value, callback) => {
      if (!value) {
        callback(new Error("请接受用户协议"));
      } else {
        callback();
      }
    };
    return {
      // 注册表单数据
      registerForm: {
        userName: "",
        nickName: "",
        password: "",
        confirmPassword: "",
        sex: "",
        userType: "",
        email: "",
        acceptAgreement: false,
      },
      // 邮箱验证码输入值
      code: "",
      // 后端返回的模拟验证码
      mockCode: "",
      // 注册表单校验规则
      registerRules: {
        userName: [
          { required: true, message: "请输入用户名", trigger: "blur" },
        ],
        nickName: [
          { required: true, message: "请输入昵称", trigger: "blur" },
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, message: "密码长度不能少于6位", trigger: "blur" },
        ],
        confirmPassword: [
          { required: true, message: "请再次输入密码", trigger: "blur" },
          { validator: validateConfirmPassword, trigger: "blur" },
        ],
        sex: [
          { required: true, message: "请选择性别", trigger: "change" },
        ],
        userType: [
          { required: true, message: "请选择用户类型", trigger: "change" },
        ],
        email: [
          { required: true, message: "请输入邮箱", trigger: "blur" },
          { type: "email", message: "邮箱格式不正确", trigger: "blur" },
        ],
        acceptAgreement: [
          { required: true, message: "请接受用户协议", trigger: "change" },
          { validator: validateAcceptAgreement, trigger: "change" },
        ],
      },
      // 邮箱验证码按钮是否禁用
      emailCodeDisabled: false,
      // 邮箱验证码按钮显示文本
      emailCodeText: "获取验证码",
      // 邮箱验证码倒计时定时器
      emailCodeTimer: null,
    };
  },
  methods: {
    // 提交注册
    handleSubmit() {
      this.$refs.registerFormRef.validate((valid) => {
         register(this.registerForm,this.code).then(res => {
          if(res.message === "注册成功！"){
            ElMessage.success(res.message);
            this.$router.push("/loginAndUser");
          }else {
            ElMessage.error(res.message);
          }
        }).catch(err => {
          console.log(err);
          ElMessage.error("注册失败");
        });
      });
    },
    // 重置注册表单
    handleReset() {
      this.$refs.registerFormRef.resetFields();
    },
    // 发送邮箱验证码
    handleSendEmailCode() {
      if (!this.registerForm.email) {
        ElMessage.error("请输入邮箱");
        return;
      }
      // 校验邮箱格式
      if(!/^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(this.registerForm.email)){
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
      senMsgByEmailForRegister(this.registerForm.email).then(res => {
        if (res.data) {
          this.mockCode = res.data;
          this.code = res.data;
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
  },
};
</script>


<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  background-image: url("http://localhost:8090/media/background.jpg");
  background-size: cover; /* 确保背景图覆盖整个容器 */
}

.register-card {
  width: 500px;
  padding: 20px;
}

.register-title {
  text-align: center;
  margin-bottom: 20px;
}

.login-link {
  text-align: right;
  margin-top: 10px;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
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
