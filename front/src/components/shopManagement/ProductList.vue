<template>
  <div class="product-management">
    <!-- 页面头部：标题与搜索操作 -->
    <div class="header">
      <h2>商品管理</h2>
      <div class="actions">
        <el-button type="primary" @click="showAddDialog" icon="el-icon-plus">添加商品</el-button>
        <el-input
            v-model="searchKeyword"
            placeholder="搜索商品名称"
            clearable
            style="width: 200px; margin-left: 10px;"
            @clear="fetchProducts"
            @keyup.enter="getSearchProducts">
          <el-button slot="append" icon="el-icon-search" @click="fetchProducts"></el-button>
        </el-input>
      </div>
    </div>

    <!-- 商品列表表格 -->
    <el-table
        :data="showProducts"
        border
        style="width: 100%"
        v-loading="loading"
        @sort-change="handleSortChange">
      <el-table-column prop="productId" label="ID" width="80" sortable="custom"></el-table-column>
      <el-table-column prop="name" label="商品名称" sortable="custom"></el-table-column>
      <el-table-column prop="category" label="类别" width="120" sortable="custom">
        <template #default="scope">
          <el-tag :type="scope.row.category === '0' ? 'success' : 'primary'">
            {{ scope.row.category === '0' ? '宠物' : '宠物周边' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="价格" width="120" sortable="custom" />
      <el-table-column prop="stockQuantity" label="库存" width="100" sortable="custom"></el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160" sortable="custom"></el-table-column>
      <el-table-column label="状态(上下架)" width="110">
        <template #default="scope">
          <el-switch
              v-model="scope.row.status"
              active-value="0"
              inactive-value="1"
              @change="handleStatusChange(scope.row.productId)">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button
              size="mini"
              type="danger"
              @click="handleDeleteProduct(scope.row.productId)"
              :disabled="scope.row.status === '0'">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total">
    </el-pagination>

    <!-- 商品编辑/添加对话框 -->
    <el-dialog
        :title="dialogTitle"
        v-model="dialogVisible"
        width="70%"
        :close-on-click-modal="false"
        :showClose="false"
        @closed="handleDialogClosed">
      <el-form
          :model="currentProduct"
          :rules="productRules"
          ref="productForm"
          label-width="120px"
          label-position="top">

        <!-- 基本信息表单部分 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="商品名称" prop="name">
              <el-input v-model="currentProduct.name" placeholder="请输入商品名称"></el-input>
            </el-form-item>

            <el-form-item label="商品类别" prop="category" >
              <el-select v-model="currentProduct.category" placeholder="请选择商品类别" style="width: 100%" :disabled= "dialogTitle !== '添加商品'">
                <el-option label="宠物" value="0"></el-option>
                <el-option label="宠物周边" value="1"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="价格" prop="price">
              <el-input-number
                  v-model="currentProduct.price"
                  :min="0"
                  :precision="2"
                  :controls="false"
                  style="width: 100%"></el-input-number>
            </el-form-item>

            <el-form-item label="库存数量" prop="stockQuantity">
              <el-input-number
                  v-model="currentProduct.stockQuantity"
                  :min="0"
                  :controls="false"
                  style="width: 100%"></el-input-number>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="商品描述" prop="description">
              <el-input
                  type="textarea"
                  v-model="currentProduct.description"
                  :rows="5"
                  placeholder="请输入商品详细描述"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 宠物特有字段 -->
        <template v-if="currentProduct.category === '0'">
          <el-divider content-position="left">宠物信息</el-divider>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="物种" prop="pets.species">
                <el-input v-model="currentProduct.pets.species" placeholder="如：狗、猫、鸟等"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="品种" prop="pets.breed">
                <el-input v-model="currentProduct.pets.breed" placeholder="如：金毛、布偶猫等"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="性别" prop="pets.gender">
                <el-radio-group v-model="currentProduct.pets.gender">
                  <el-radio label="0">雄性</el-radio>
                  <el-radio label="1">雌性</el-radio>
                  <el-radio label="2">两者都有</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="出生日期" prop="pets.birthDate">
                <el-date-picker
                    v-model="currentProduct.pets.birthDate"
                    type="date"
                    placeholder="选择出生日期"
                    style="width: 100%">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="健康状况" prop="pets.healthStatus">
                <el-input v-model="currentProduct.pets.healthStatus" placeholder="描述宠物的健康状况"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 周边特有字段 -->
        <template v-else-if="currentProduct.category === '1'">
          <el-divider content-position="left">周边信息</el-divider>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="周边类型" prop="supplies.supplyType">
                <el-input v-model="currentProduct.supplies.supplyType" placeholder="如：食品、玩具、窝垫等"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="材质" prop="supplies.material">
                <el-input v-model="currentProduct.supplies.material" placeholder="如：棉、塑料、金属等"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="尺寸" prop="supplies.size">
                <el-input v-model="currentProduct.supplies.size" placeholder="如：S/M/L或具体规格"></el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="适用宠物" prop="supplies.targetSpecies">
                <el-input v-model="currentProduct.supplies.targetSpecies" placeholder="如：狗、猫、通用等"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="重量(kg)" prop="supplies.weightKg">
                <el-input-number
                    v-model="currentProduct.supplies.weightKg"
                    :min="0"
                    :precision="2"
                    style="width: 100%"></el-input-number>
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <!-- 商品媒体资源 -->
        <el-divider content-position="left">商品媒体资源</el-divider>
        <el-form-item prop="productMedias">
          <div class="media-resource-container">
            <el-upload
                action
                :on-success="handleMediaSuccess"
                :on-remove="handleMediaRemove"
                :before-upload="beforeMediaUpload"
                :show-file-list="false"
                :limit="7"
                :http-request="uploadFileTemp"
                multiple
                accept="image/*,video/*">
              <el-button size="small" type="primary">点击上传</el-button>
              <div slot="tip" class="el-upload__tip">
                最多可7个文件，单文件不超过50MB
              </div>
            </el-upload>

            <el-table
                :data="currentProduct.productMedias"
                border
                style="width: 100%; margin-top: 15px;"
                size="small">
              <el-table-column prop="name" label="文件名"></el-table-column>
              <el-table-column label="封面" width="80">
                <template #default="scope">
                  <el-radio v-model="scope.row.mediaType" :label="'0'" @change="setAsCover(scope.row)" v-if="isImage(scope.row.media.url)">{{scope.row.name}}</el-radio>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                    <el-button
                        size="mini"
                        type="text"
                        @click="handlePreview(scope.row)">预览</el-button>
                    <el-button
                        size="mini"
                        type="text"
                        @click="handleDelete(scope.row)"
                        :disabled="scope.row.mediaType === '0'">
                      删除
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false;this.fetchProducts()">取 消</el-button>
        <el-button type="primary" @click="submitProduct" :loading="submitting">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 媒体预览对话框 -->
    <el-dialog v-model="previewVisible" :title="previewTitle" width="60%"  @close="handleMediaDialogClose">
      <div style="max-height: 60vh; max-width: 400px; margin: auto;" v-if="isImage(previewUrl)">
        <el-image :src="this.BaseUrl + previewUrl" fit="contain" style="max-height: 100%; max-width: 100%;"></el-image>
      </div>
      <div style="text-align: center;" v-else v-if="isVideo(previewUrl)">
        <video ref="videoPlayer" :src="this.BaseUrl + previewUrl" controls style="max-height: 60vh; max-width: 100%;"></video>
      </div>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog v-model="DeleteConfirm" :title="DeletedTitle" width="30%" >
        <div><span>删除不可逆，是否确认删除?</span></div>
        <el-radio label="true" style="margin-top: 10px;" v-model="rememberDelete">
            <span>此次登录不再提示</span>
        </el-radio>
        <p>
          <el-button type="text" size="mini" @click="DeleteConfirm = false">取消</el-button>
          <el-button type="primary" size="mini" @click="performDelete()">确认</el-button>
        </p>
    </el-dialog>
  </div>
</template>

<script>
import {
  addProduct, changeProductStatus,
  deletedMedia, deleteProduct,
  getProductByToken,
  updateProduct,
  uploadFileTemporary
} from "../../api/shopManagement/products";
import {ElMessage} from "element-plus";
import { h } from 'vue';

export default {
  name: 'ProductList',
  // 接收父组件传入的店铺 ID
  props: {
    shopId: {
      type: Number,
      required: true
    }
  },
  data() {
    return {
      // 后端 API 基础地址
      BaseUrl: import.meta.env.VITE_API_BASE_URL,
      // 全部商品数据
      products: [],
      // 当前展示的商品数据（支持搜索过滤）
      showProducts:[],
      // 表格加载状态
      loading: false,
      // 商品编辑/添加对话框显示状态
      dialogVisible: false,
      // 对话框标题
      dialogTitle: '添加商品',
      // 当前编辑或添加的商品对象
      currentProduct: this.getDefaultProduct(),
      // 封面资源 ID（当前未使用）
      coverResourceId: '',
      // 搜索关键词
      searchKeyword: '',
      // 是否记住删除确认
      rememberDelete: false,
      // 删除确认对话框显示状态
      DeleteConfirm:false,
      // 待删除的媒体数据
      DeleteMediaData:{},
      // 删除对话框标题
      DeletedTitle:'',
      // 待删除的商品 ID
      DeletedProductId:'',
      // 分页信息
      pagination: {
        current: 1,
        size: 10,
        total: 0
      },
      // 上传的媒体文件列表（当前未使用）
      mediaFiles:[],
      // 提交按钮加载状态
      submitting: false,
      // 媒体预览对话框显示状态
      previewVisible: false,
      // 预览媒体 URL
      previewUrl: '',
      // 预览标题
      previewTitle: '',
      // 预览类型（当前未使用）
      previewType: '',
      // 商品表单校验规则
      productRules: {
        name: [
          { required: true, message: '请输入商品名称', trigger: 'blur' },
          { min: 2, max: 100, message: '长度在2到100个字符', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请选择商品类别', trigger: 'change' }
        ],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' },
          { validator: this.validatePrice, trigger: 'blur' }
        ],
        stockQuantity: [
          { required: true, message: '请输入库存数量', trigger: 'blur' },
          { validator: this.validateStock, trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入商品描述', trigger: 'blur' },
          { min: 5, max: 500, message: '长度在5到500个字符', trigger: 'blur' }
        ],
        pets:{
          species: [
            { required: true, message: '请选择宠物种类', trigger: 'change' }
          ],
          breed: [
            { required: true, message: '请输入宠物品种', trigger: 'blur' }
          ],
          birthDate: [
            { required: true, message: '请选择出生日期', trigger: 'change' }
          ],
          gender: [
            { required: true, message: '请选择性别', trigger: 'change' }
          ],
          healthStatus: [
            { required: true, message: '请输入健康状况', trigger: 'blur'}
          ]
        },
        supplies:{
          supplyType: [
            { required: true, message: '请输入周边类型', trigger: 'blur' }
          ],
          material: [
            { required: true, message: '请输入材质', trigger: 'blur' }
          ],
          size: [
            { required: true, message: '请输入尺寸', trigger: 'blur' }
          ],
          weightKg: [
            { required: true, message: '请输入重量', trigger: 'blur' }
          ],
          targetSpecies: [
            { required: true, message: '请输入适用宠物', trigger: 'blur' }
          ],
        },
        productMedias: [
          { required: true, message: '请上传至少一张图片或视频', trigger: 'change' },
          { validator: (rule, value, callback) => {
              if (!value || value.length < 3) {
                callback(new Error('至少需要上传3个文件'));
              } else {
                callback();
              }
            },
            trigger: 'change'}
        ]
      }
    };
  },
  // 组件挂载时加载商品列表
  mounted() {
    this.fetchProducts();
  },
  methods: {
    // 获取默认商品对象
    getDefaultProduct() {
      return {
        productId: '',
        shopId:'',
        name:'',
        description:'',
        price: '',
        stockQuantity: '',
        category: '',
        createdAt:'',
        status: '',
        pets: {
          petId:'',
          species:'',
          breed:'',
          birthDate:'',
          gender:'',
          healthStatus:'',
        },
        supplies: {
          supplyType: '',
          material: '',
          size: '',
          weightKg: '',
          targetSpecies: '',
        },
        productMedias: [

        ],
      }
    },

    // 加载当前商家的商品列表
    fetchProducts() {
      this.loading = true;
      getProductByToken().then((res) => {
        this.products = res.data;
        this.showProducts = this.products;
        this.pagination.total = res.data.length;
      });

      this.loading = false;
    },

    // 根据关键词搜索商品
    getSearchProducts(){
      this.showProducts = this.products.filter(product => product.name.includes(this.searchKeyword));
      this.pagination.total = this.products.length;
    },

    // 打开添加商品对话框
    showAddDialog() {
      this.dialogTitle = '添加商品';
      this.currentProduct = this.getDefaultProduct();
      this.dialogVisible = true;
    },

    // 打开编辑商品对话框
    handleEdit(product) {
      this.dialogTitle = '编辑商品';
      this.currentProduct = JSON.parse(JSON.stringify(product));
      this.dialogVisible = true;
    },

    // 媒体上传前的校验
    beforeMediaUpload(file) {
      const isImage = file.type.includes('image');
      const isVideo = file.type.includes('video');
      const isLt50M = file.size / 1024 / 1024 < 50;

      // 检查文件类型
      if (!isImage && !isVideo) {
        this.$message.error('只能上传图片(JPG/PNG)或视频(MP4/WebM)文件!');
        return false;
      }

      // 检查文件大小
      if (!isLt50M) {
        this.$message.error('文件大小不能超过50MB!');
        return false;
      }
      return true;
    },

    // 媒体上传成功回调（当前为空实现）
    handleMediaSuccess(response, file, fileList) {

    },

    // 媒体移除回调（当前为空实现）
    handleMediaRemove(file, fileList) {

    },

    // 自定义上传：将文件临时上传并加入商品媒体列表
    uploadFileTemp(file){
      uploadFileTemporary(file.file).then(res => {
        if(res.data !== null){
          ElMessage.success(res.message);
          if(this.currentProduct.productMedias.length === 0){
            this.currentProduct.productMedias.push({
              name: res.data.name,
              sortOrder: 1,
              mediaType: '0',
              isTemporary: true,
              media: {
                url: res.data.url,
              },
            });
          }else{
            this.currentProduct.productMedias.push({
              name: res.data.name,
              mediaType: '1',
              sortOrder: this.currentProduct.productMedias.length + 1,
              isTemporary: true,
              media: {
                url: res.data.url,
              },
            });
          }
        }
      })
    },

    // 设置指定媒体为封面
    setAsCover(resource) {
      this.currentProduct.productMedias.forEach((item, index) => {
        if (item.mediaType === '0') {
          item.mediaType = '1';
        }
      });
      resource.mediaType = '0';
    },

    // 预览媒体资源
    handlePreview(resource) {
      this.previewUrl = resource.media.url;
      this.previewTitle = resource.name;
      this.previewType = resource.mediaType;
      this.previewVisible = true;
    },

    // 校验价格必须大于 0
    validatePrice(rule, value, callback) {
      if (value <= 0) {
        callback(new Error('价格必须大于0'));
      } else {
        callback();
      }
    },

    // 校验库存不能为负数
    validateStock(rule, value, callback) {
      if (value < 0) {
        callback(new Error('库存不能为负数'));
      } else {
        callback();
      }
    },

    // 提交商品表单：添加或更新
    submitProduct() {
      this.$refs.productForm.validate(valid => {
        if (valid) {
          if (this.dialogTitle === '添加商品') {
            addProduct(this.currentProduct).then(res => {
              if (res.data) {
                ElMessage.success(res.message)
                this.dialogVisible = false;
                this.getDefaultProduct();
                this.fetchProducts();
              } else {
                ElMessage.error(res.message)
              }
            });
          } else if(this.dialogTitle === '编辑商品'){
            console.log(this.currentProduct);
            updateProduct(this.currentProduct).then(res => {
              if(res.data){
                ElMessage.success(res.message);
                this.dialogVisible = false;
                this.getDefaultProduct();
                this.fetchProducts();
              }else{
                ElMessage.error(res.message);
              }
            });
          }
        }
      });
    },

    // 切换商品上下架状态
    handleStatusChange(productId) {
      changeProductStatus(productId).then(res => {
        if (res.data) {
          ElMessage.success(res.message);
          this.fetchProducts();
        } else {
          ElMessage.error(res.message);
        }
      });
    },

    // 删除商品媒体资源：弹出确认框
    handleDelete(row) {
      this.DeletedTitle = '删除商品媒体资源';
      // 检查是否设置了不再提示
      const skipConfirm = sessionStorage.getItem('skipDeleteConfirm');
      this.DeleteMediaData = row;
      if (skipConfirm) {
        this.performDelete();
      } else {
        this.DeleteConfirm = true;
      }
    },

    // 删除商品：弹出确认框
    handleDeleteProduct(id){
      this.DeletedTitle = '删除商品';
      const skipConfirm = sessionStorage.getItem('skipDeleteConfirm');
      this.DeletedProductId = id;
      if (skipConfirm) {
        this.performDelete();
      } else {
        this.DeleteConfirm = true;
      }
    },

    // 执行删除：媒体资源或商品
    performDelete() {
      // 实际执行删除操作的代码
      console.log('执行删除:', this.DeleteMediaData);

      if(this.rememberDelete){
        sessionStorage.setItem('skipDeleteConfirm', 'true');
      }
      this.DeleteConfirm = false;
      if(this.DeletedTitle === '删除商品媒体资源'){
        if(this.DeleteMediaData.isTemporary){
          this.DeleteFront();
          ElMessage.success('删除成功');
        }else{
          deletedMedia(this.DeleteMediaData.media).then((res)=>{
            if(res.data){
              this.DeleteFront();
              ElMessage.success(res.message);
            }else{
              ElMessage.error(res.message);
            }
          })
        }
      }  else if(this.DeletedTitle === '删除商品'){
        deleteProduct(this.DeletedProductId).then((res)=>{
          if(res.data){
            this.fetchProducts();
            ElMessage.success(res.message);
          }else{
            ElMessage.error(res.message);
          }
        })
      }
    },

    // 从前端媒体列表中移除已删除项
    DeleteFront(){
      const index = this.currentProduct.productMedias.findIndex(
          item => item.media.url === this.DeleteMediaData.media.url
      );
      if (index !== -1) {
        this.currentProduct.productMedias.splice(index, 1);
      }
    },
    // 每页条数变化
    handleSizeChange(size) {
      this.pagination.size = size;
      this.fetchProducts();
    },

    // 当前页码变化
    handleCurrentChange(current) {
      this.pagination.current = current;
      this.fetchProducts();
    },

    // 表格排序变化（当前为空实现）
    handleSortChange({ column, prop, order }) {
      // 实现排序逻辑
      console.log('排序:', prop, order);
    },

    // 对话框关闭时重置表单
    handleDialogClosed() {
      this.$refs.productForm.resetFields();
    },
    // 媒体预览对话框关闭时暂停视频
    handleMediaDialogClose(){
      if (this.$refs.videoPlayer) {
        this.$refs.videoPlayer.pause()
        this.$refs.videoPlayer.currentTime = 0
      }
    },

    // 判断 URL 是否为图片
    isImage(Url) {
      if (!Url) return false;
      const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.webp'];
      return imageExtensions.some(ext =>
          Url.toLowerCase().endsWith(ext)
      );
    },
    // 判断 URL 是否为视频
    isVideo(Url) {
      if (!Url) return false;
      const videoExtensions = ['.mp4', '.webm', '.ogg', '.mov'];
      return videoExtensions.some(ext =>
          Url.toLowerCase().endsWith(ext)
      );
    }
  },

};
</script>

<style scoped>
.product-management {
  padding: 20px;
  background-color: #fff;
  border-radius: 4px;
}

/* 页面头部 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.actions {
  display: flex;
  align-items: center;
}

/* 媒体资源上传区 */
.media-resource-container {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  padding: 20px;
  background-color: #fafafa;
}

.hide-upload :deep(.el-upload--picture-card) {
  display: none;
}

.el-divider {
  margin: 20px 0;
}

.el-table {
  margin-top: 15px;
}
</style>
