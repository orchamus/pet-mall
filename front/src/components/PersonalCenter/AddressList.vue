<template>
  <div class="address-list">
    <el-card>
      <!-- 页面头部：标题与添加按钮 -->
      <div class="header">
        <h2>收货地址</h2>
        <el-button
            type="primary"
            icon="el-icon-plus"
            @click="showAddDialog"
        >
          添加新地址
        </el-button>
      </div>

      <!-- 空地址提示 -->
      <div v-if="addresses.length === 0" class="empty-tip">
        <el-empty description="暂无收货地址"></el-empty>
      </div>

      <!-- 地址列表 -->
      <div v-else class="address-container">
        <AddressItem
            v-for="address in addresses"
            :key="address.addressId"
            :address="address"
            @edit="handleEdit"
            @delete="handleDelete"
            @set-default="handleSetDefault"
        />
      </div>
    </el-card>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog
        :title="dialogTitle"
        v-model = "dialogVisible"
        width="50%"
    >
      <el-form
          :model="currentAddress"
          :rules="addressRules"
          ref="addressForm"
          label-width="100px"
      >
        <el-form-item label="收货人" prop="consignee">
          <el-input v-model="currentAddress.consignee"></el-input>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="currentAddress.email"></el-input>
        </el-form-item>

        <el-form-item label="所在地区" prop="region">
          <el-cascader
              v-model="currentAddress.region"
              :options="regionData"
              :props="{ expandTrigger: 'hover' }"
              style="width: 100%"
          ></el-cascader>
        </el-form-item>

        <el-form-item label="地址标签" prop="tag">
          <el-select v-model="currentAddress.tag" placeholder="请选择地址标签">
            <el-option label="家" value="家"></el-option>
            <el-option label="公司" value="公司"></el-option>
            <el-option label="学校" value="学校"></el-option>
            <el-option label="其他" value="其他"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="详细地址" prop="detail">
          <el-input
              v-model="currentAddress.detail"
              type="textarea"
              :rows="3"
          ></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitAddress">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {codeToText, regionData} from 'element-china-area-data';
import AddressItem from './AddressItem.vue';
import {
  ADDaddress,
  deleteAddress,
  getAddressList,
  setDefaultAddress,
  updateAddress
} from "../../api/PersonalCenter/Address";
import {ElMessage} from "element-plus";

export default {
  components: {
    AddressItem
  },
  data() {
    return {
      // 地址列表
      addresses: [],
      // 对话框显示状态
      dialogVisible: false,
      // 当前是否为编辑模式
      isEditing: false,
      // 当前编辑或新增的地址表单
      currentAddress: {
        addressId: '',
        consignee: '',
        email: '',
        region: [],
        detail: '',
        tag: '',
        isDefault: '0'
      },
      // 中国地区级联数据
      regionData: regionData,
      // 地址表单校验规则
      addressRules: {
        consignee: [
          { required: true, message: '请输入收货人姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        email: [
          { required: true, message: '请输入邮箱地址', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        region: [
          { required: true, message: '请选择所在地区', trigger: 'change' }
        ],
        detail: [
          { required: true, message: '请输入详细地址', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 5 到 50 个字符', trigger: 'blur' }
        ],
        tag: [
          { required: true, message: '请输入选择标签', trigger: 'blur' },
        ]
      }
    };
  },
  computed: {
    /**
     * 根据编辑状态动态生成对话框标题
     * @returns {String} 对话框标题
     */
    dialogTitle() {
      return this.isEditing ? '编辑地址' : '添加新地址';
    }
  },
  methods: {
    /**
     * 打开添加地址对话框
     */
    showAddDialog() {
      this.isEditing = false;
      this.currentAddress = {
        addressId: '',
        consignee: '',
        email: '',
        region: '',
        detail: '',
        isDefault: false
      };
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.addressForm?.clearValidate();
      });
    },

    /**
     * 打开编辑地址对话框
     * @param {Object} address 待编辑地址对象
     */
    handleEdit(address) {
      this.isEditing = true;
      this.currentAddress = JSON.parse(JSON.stringify(address));
      this.currentAddress.region = '';
      this.dialogVisible = true;
    },

    /**
     * 删除指定地址
     * @param {Object} address 待删除地址对象
     */
    handleDelete(address) {
      this.$confirm('确定要删除该地址吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteAddress(address.addressId).then(res => {
          if(res.data){
            ElMessage.success('地址删除成功');
            this.getAddresses();
          }else{
            ElMessage.error('地址删除失败');
          }
        })
      }).catch(() => {
        this.$message.info('已取消删除');
      });
    },

    /**
     * 设置默认地址
     * @param {Object} address 待设为默认的地址对象
     */
    handleSetDefault(address) {
      setDefaultAddress(address.addressId).then(res => {
        if(res.data){
          ElMessage.success('默认地址设置成功');
          this.getAddresses();
        }else{
          ElMessage.error('默认地址设置失败');
        }
      })
    },

    /**
     * 将地区编码数组转换为文本地址
     * @param {Array} id 地区编码数组
     * @returns {String} 文本地址
     */
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

    /**
     * 提交地址表单：新增或更新地址
     */
    submitAddress() {
      this.$refs.addressForm.validate(valid => {
        if (valid) {
          if (this.isEditing) {
            this.currentAddress.region = this.locationChange(this.currentAddress.region);
            // 更新地址
            updateAddress(this.currentAddress).then(res => {
              if(res.data){
                ElMessage.success('地址更新成功');
                this.getAddresses();
              }else{
                ElMessage.error('地址更新失败');
              }
            })
          } else {
            this.currentAddress.region = this.locationChange(this.currentAddress.region);
            this.currentAddress.isDefault = '0';
            // 添加新地址
            ADDaddress(this.currentAddress).then(res => {
              if(res.data){
                ElMessage.success('地址添加成功');
                this.getAddresses();
              }else{
                ElMessage.error('地址添加失败');
              }
            })
          }
          this.dialogVisible = false;
        }
      });
    },

    /**
     * 获取当前用户地址列表
     */
    getAddresses(){
      getAddressList().then(res => {
        this.addresses = res.data;
        console.log(res.data)
      })
    }
  },
  // 组件挂载后加载地址列表
  mounted() {
    this.getAddresses();
  }
};
</script>

<style scoped>
/* 地址列表容器 */
.address-list {
  max-width: 1600px;
  min-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 页面头部 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* 地址卡片网格布局 */
.address-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(450px, 1fr));
  gap: 20px;
}

/* 空地址提示 */
.empty-tip {
  padding: 50px 0;
}
</style>
