<template>
  <div class="personal-center">
    <el-card class="user-card">
      <!-- 用户头像与昵称头部 -->
      <div class="user-header">
        <el-upload
            class="avatar-uploader"
            action
            accept=".jpg,.jpeg,.png,.gif"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :http-request="updateAvatar">
          <img  :src="this.BaseUrl + userInfo.avatar" class="avatar" :key="userInfo.avatar">
        </el-upload>
        <h2>{{ userInfo.nickName }}</h2>
      </div>

      <!-- 基本信息 / 修改密码 标签页 -->
      <el-tabs v-model="activeTab">
        <!-- 基本信息面板 -->
        <el-tab-pane label="基本信息" name="info">
          <el-form
              :model="userInfo"
              :rules="userRules"
              ref="userForm"
              label-width="100px"
          >
            <el-form-item label="用户名" prop="userName">
              <el-input v-model="userInfo.userName" disabled></el-input>
            </el-form-item>

            <el-form-item label="昵称" prop="nickName">
              <el-input v-model="userInfo.nickName"></el-input>
            </el-form-item>

            <el-form-item label="性别" prop="sex">
              <el-radio-group v-model="userInfo.sex">
                <el-radio label="0">男</el-radio>
                <el-radio label="1">女</el-radio>
                <el-radio label="2">其他</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="邮箱" prop="email">
              <el-input v-model="userInfo.email" type="email"></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitUserInfo">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 修改密码面板 -->
        <el-tab-pane label="修改密码" name="password">
          <el-form
              :model="passwordForm"
              :rules="passwordRules"
              ref="passwordForm"
              label-width="100px"
          >
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="passwordForm.oldPassword" type="password"></el-input>
            </el-form-item>

            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordForm.newPassword" type="password"></el-input>
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordForm.confirmPassword" type="password"></el-input>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="submitPassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import {getUserInfo, updateUserAvatar, updateUserInfo, updateUserPassword} from "../../api/loginAndUser";
import {ElMessage} from "element-plus";

export default {
  data() {
    // 自定义校验：确认密码必须与新密码一致
    const validatePassword = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      // 后端 API 基础地址
      BaseUrl: import.meta.env.VITE_API_BASE_URL,
      // 当前激活的标签页
      activeTab: 'info',
      // 用户基本信息
      userInfo: {
        userName:'',
        nickName: '',
        sex: '0',
        avatar: '',
        email: '',
        tag: ''
      },
      // 密码表单
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      // 基本信息校验规则
      userRules: {
        nickName: [
          { required: true, message: '请输入昵称', trigger: 'blur' },
          { min: 2, max: 12, message: '长度在 2 到 12 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ]
      },
      // 密码校验规则
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入密码', trigger: 'blur' },
          { validator: validatePassword, trigger: 'blur' }
        ]
      }
    };
  },
  // 组件创建时加载用户信息
  created() {
    this.fetchUserInfo();
  },
  methods: {
    /**
     * 获取当前登录用户信息
     */
    fetchUserInfo() {
      getUserInfo().then(response => {
        this.userInfo = response.data;
        // 添加时间戳参数，防止头像缓存
        this.userInfo.avatar = this.userInfo.avatar + "?" + new Date().getTime().toString();
        console.log('用户信息:', this.userInfo);
      });
    },

    /**
     * 提交用户信息修改
     */
    submitUserInfo() {
      this.$refs.userForm.validate(valid => {
        if (valid) {
          // 调用API更新用户信息
          console.log('提交用户信息:', this.userInfo);
          updateUserInfo(this.userInfo).then(res=>{
            if(res.data){
              ElMessage.success('用户信息修改成功');
              this.fetchUserInfo();
            }else{
              ElMessage.error('用户信息修改失败');
            }
          })
        }
      });
    },

    /**
     * 提交密码修改
     */
    submitPassword() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          // 调用API修改密码
          console.log('修改密码:', this.passwordForm);
          updateUserPassword(this.passwordForm).then(res=>{
            if(res.data){
              ElMessage.success('密码修改成功');
              this.passwordForm.oldPassword = '';
              this.passwordForm.newPassword = '';
              this.passwordForm.confirmPassword = '';
            }else{
              ElMessage.error('密码修改失败');
            }
          })
        }
      });
    },

    /**
     * 上传用户头像
     * @param {Object} file 上传文件对象
     */
    updateAvatar(file) {
      updateUserAvatar(file.file).then(res => {
        if (res.data) {
          window.location.reload();
          ElMessage.success('头像上传成功');
        } else {
          ElMessage.error('头像上传失败');
        }
      });
    },

    /**
     * 头像上传成功后的本地预览
     * @param {Object} res 上传响应
     * @param {Object} file 上传文件对象
     */
    handleAvatarSuccess(res, file) {
      this.userInfo.avatar = URL.createObjectURL(file.raw);
    },

    /**
     * 头像上传前的格式与大小校验
     * @param {Object} file 待上传文件
     * @returns {Boolean} 是否允许上传
     */
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/PNG 格式!');
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    }
  },
  // 组件挂载后再次加载用户信息
  mounted() {
    this.fetchUserInfo();
  }
};
</script>

<style scoped>
/* 个人中心容器 */
.personal-center {
  min-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

/* 用户信息卡片 */
.user-card {
  padding: 20px;
}

/* 用户头部：头像 + 昵称 */
.user-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.user-header h2 {
  margin-left: 20px;
}

/* 头像上传组件 */
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
}

.avatar-uploader:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar {
  width: 100px;
  height: 100px;
  display: block;
}
</style>
