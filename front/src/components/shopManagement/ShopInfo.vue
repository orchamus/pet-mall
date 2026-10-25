<template>
  <div class="shop-info">
    <!-- 已创建店铺：展示店铺基本信息 -->
    <div v-if="hasShop">
      <el-card>
        <div slot="header" class="clearfix">
          <span>店铺基本信息</span>
          <el-button
              style="float: right; padding: 3px 0"
              type="text"
              @click="showEditDialog">
            编辑店铺信息
          </el-button>
        </div>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="店铺名称">{{ shopData.shopName }}</el-descriptions-item>
          <el-descriptions-item label="营业状态">
            <el-tag :type="getStatusTagType(shopData.status)">
              {{ getStatusText(shopData.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="联系邮箱">{{ shopData.contactEmail }}</el-descriptions-item>
          <el-descriptions-item label="店铺地址">{{ shopData.address }}</el-descriptions-item>
          <el-descriptions-item label="店铺描述" :span="2">{{ shopData.description }}</el-descriptions-item>
          <el-descriptions-item label="店铺Logo" :span="2">
            <el-image
                v-if="shopData.showLogo"
                :src="shopData.showLogo"
                style="width: 120px; height: 120px"
                fit="contain"></el-image>
            <span v-else>未设置</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>

    <!-- 未创建店铺：引导创建店铺 -->
    <div v-else class="no-shop">
      <el-card>
        <div slot="header" class="clearfix">
          <span>您尚未创建店铺</span>
        </div>
        <div class="create-shop-content">
          <img
              src="../../assets/create-shop.png"
              style="width: 200px; height: 200px; margin-bottom: 20px;">
          </img>
          <h3>创建您的专属店铺</h3>
          <p>开始销售商品前，您需要先创建一个店铺</p>
          <el-button
              type="primary"
              size="medium"
              @click="showCreateDialog">
            立即创建店铺
          </el-button>
        </div>
      </el-card>
    </div>

    <!-- 创建/编辑店铺对话框 -->
    <el-dialog
        :title="shopDialogTitle"
        v-model="shopDialogVisible"
        width="50%"
        :close-on-click-modal="false">
      <el-form
          :model="shopForm"
          :rules="shopRules"
          ref="shopForm"
          label-width="120px">
        <el-form-item label="店铺名称" prop="shopName">
          <el-input v-model="shopForm.shopName"></el-input>
        </el-form-item>

        <el-form-item label="店铺描述" prop="description">
          <el-input type="textarea" v-model="shopForm.description" :rows="4"></el-input>
        </el-form-item>

        <el-form-item label="店铺Logo" prop="logo">
          <el-upload
              class="avatar-uploader"
              action
              accept=".jpg,.png,.gif"
              :http-request="uploadLogo"
              :show-file-list="false"
              :before-upload="beforeLogoUpload">
            <img v-if="shopForm.showLogo" :src="shopForm.showLogo" class="avatar">
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input v-model="shopForm.contactEmail"></el-input>
        </el-form-item>

        <el-form-item label="所在地区" prop="region">
          <el-cascader
              v-model="shopForm.region"
              :options="regionData"
              :props="{ expandTrigger: 'hover' }"
              style="width: 100%"
          ></el-cascader>
        </el-form-item>

        <el-form-item label="店铺详细地址" prop="address">
          <el-input v-model="shopForm.address"></el-input>
        </el-form-item>

        <el-form-item label="营业状态" prop="status" v-if="hasShop">
          <el-radio-group v-model="shopForm.status">
            <el-radio :label="1">营业中</el-radio>
            <el-radio :label="0">已关闭</el-radio>
            <el-radio :label="2">暂停营业</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="shopDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitShopForm">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {addShop, getShop, updateShop, uploadShopLogo} from "../../api/shopManagement/shop";
import {ElMessage} from "element-plus";
import {codeToText, regionData} from 'element-china-area-data';
export default {
  name: 'ShopInfo',
  data() {
    return {
      // 中国地区级联数据
      regionData: regionData,
      // 后端 API 基础地址
      BaseUrl: import.meta.env.VITE_API_BASE_URL,
      // 是否已创建店铺
      hasShop: false,
      // 店铺详情数据
      shopData: {
        shop_id: '',
        shopName: '',
        description: '',
        logo: '',
        contactEmail: '',
        address: '',
        showLogo:'',
        status: ''
      },
      // 店铺对话框显示状态
      shopDialogVisible: false,
      // 店铺对话框标题
      shopDialogTitle: '创建店铺',
      // 店铺表单数据
      shopForm: {
        shopName: '',
        description: '',
        logo: '',
        contactEmail: '',
        address: '',
        region: [],
        status: 1
      },
      // 店铺表单校验规则
      shopRules: {
        shopName: [
          { required: true, message: '请输入店铺名称', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        contactEmail: [
          { required: true, message: '请输入联系邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        address: [
          { required: true, message: '请输入店铺地址', trigger: 'blur' }
        ],
        logo: [
          { required: true, message: '请上传店铺Logo', trigger: 'change' }
        ],
        description: [
          { required: true, message: '请输入店铺描述', trigger: 'blur' }
        ],
        region: [
          { required: true, message: '请选择所在地区', trigger: 'change' }
        ]
      }
    };
  },
  // 组件创建时获取店铺信息
  created() {
    this.getShopInfo();
  },
  methods: {
    // 获取当前商家的店铺信息
    getShopInfo() {
      getShop().then(res =>{
        if(res.code === 200){
          this.shopData = res.data;
          this.shopData.showLogo = this.BaseUrl + this.shopData.logo;
          this.hasShop = true;
          // 通知父组件店铺已创建
          this.$emit('shop-created', this.shopData);
          ElMessage.success('获取店铺信息成功');
        } else {
          ElMessage.error('获取店铺信息失败');
          this.hasShop = false;
        }
      })
    },
    // 打开创建店铺对话框
    showCreateDialog() {
      this.shopDialogTitle = '创建店铺';
      this.shopForm = {
        shopName: '',
        description: '',
        logo: '',
        contactEmail: '',
        address: '',
        showLogo:'',
        region: [],
        status: ''
      };
      this.shopDialogVisible = true;
    },

    // 打开编辑店铺对话框
    showEditDialog() {
      this.shopDialogTitle = '编辑店铺信息';
      this.shopForm = { ...this.shopData };
      this.shopForm.address = '';
      this.shopForm.showLogo = this.BaseUrl + this.shopForm.logo;
      this.shopDialogVisible = true;
    },

    // 提交店铺表单：创建或更新
    submitShopForm() {
      this.$refs.shopForm.validate((valid) => {
        if (valid) {
          if (this.shopDialogTitle === '创建店铺') {
            this.createShop();
          } else {
            this.updateShop();
          }
        }
      });
    },

    // 将地区编码转换为文本地址
    locationChange(id){
      let value = id;
      let dz = '';
      if (value[1] != null && value[2] != null) {
        dz = codeToText[value[0]]  + codeToText[value[1]]  + codeToText[value[2]]
      } else {
        if (value[1] != null) {
          dz = codeToText[value[0]]  + codeToText[value[1]]
        } else {
          dz = codeToText[value[0]]
        }
      }
      return dz;
    },

    // 创建店铺
    createShop() {
      setTimeout(() => {
        this.shopForm.address = this.locationChange(this.shopForm.region) + this.shopForm.address;
        addShop(this.shopForm).then(res => {
          if(res.data){
            this.$message.success('店铺创建成功');
            ElMessage.success('店铺创建成功');
            this.hasShop = true;
            this.shopDialogVisible = false;
            // 通知父组件店铺状态变化
            this.$emit('shop-created');
            this.getShopInfo()
          }else{
            this.$message.error('店铺创建失败');
            ElMessage.error('店铺创建失败');
          }
        });
      }, 3000);
    },

    // 更新店铺信息
    updateShop() {
      setTimeout(() => {
        this.shopForm.address = this.locationChange(this.shopForm.region) + this.shopForm.address;
        updateShop(this.shopForm).then(res => {
          if(res.data){
            this.$message.success('店铺信息更新成功');
            this.shopDialogVisible = false;
            this.getShopInfo();
          }else{
            this.$message.error('店铺信息更新失败');
            ElMessage.error('店铺信息更新失败');
          }
        })
      }, 1000);
    },

    // Logo 上传前校验
    beforeLogoUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        ElMessage.error('上传头像图片只能是 JPG/PNG 格式!');
      }
      if (!isLt2M) {
        ElMessage.error('上传头像图片大小不能超过 2MB!');
      }
      return isJPG && isLt2M;
    },

    // 上传店铺 Logo
    uploadLogo(file){
      uploadShopLogo(file.file).then(res => {
        if(res.data !== false){
          this.shopForm.logo = res.data;
          this.shopForm.showLogo = this.BaseUrl + res.data;
          ElMessage.success('上传logo成功');
        }else{
          ElMessage.error(res.data);
        }
      })
    },

    // 根据状态码获取状态文本
    getStatusText(status) {
      const statusMap = {
        0: '已关闭',
        1: '营业中',
        2: '暂停营业'
      };
      return statusMap[status] || '未知状态';
    },

    // 根据状态码获取标签样式
    getStatusTagType(status) {
      const typeMap = {
        0: 'danger',
        1: 'success',
        2: 'warning'
      };
      return typeMap[status] || '';
    }
  }
};
</script>

<style scoped>
.shop-info {
  max-width: 800px;
  margin: 0 auto;
}

/* 未创建店铺引导区 */
.no-shop {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
}

.create-shop-content {
  padding: 20px;
}

.create-shop-content h3 {
  margin-bottom: 10px;
}

.create-shop-content p {
  margin-bottom: 20px;
  color: #666;
}

/* Logo 上传组件 */
.avatar-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader :hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
}

.avatar {
  width: 120px;
  height: 120px;
  display: block;
}
</style>
