<template>
  <el-dialog
    :title="title"
    v-bind="$attrs"
    :before-close="handleClose"
    :close-on-click-modal="false"
    width="600px"
  >
    <el-form :model="formData" ref="formRef" :rules="formRules" label-width="100px">
      <el-form-item label="药品名称" prop="drugName">
        <el-input v-model="formData.drugName" placeholder="请输入药品名称" />
      </el-form-item>

      <el-form-item label="药品成分" prop="drugInfo">
        <el-input
          v-model="formData.drugInfo"
          type="textarea"
          :rows="3"
          placeholder="请输入药品成分信息"
        />
      </el-form-item>

      <el-form-item label="功能主治" prop="drugEffect">
        <el-input
          v-model="formData.drugEffect"
          type="textarea"
          :rows="3"
          placeholder="请输入药品功能主治"
        />
      </el-form-item>

      <el-form-item label="药品图片" prop="drugImg">
        <el-upload
          class="drug-img-uploader"
          action="#"
          :show-file-list="false"
          :before-upload="beforeUpload"
          :http-request="uploadImg"
        >
          <img v-if="formData.drugImg" :src="getImageUrl(formData.drugImg)" class="drug-image" />
          <el-icon v-else class="drug-uploader-icon"><Plus /></el-icon>
        </el-upload>
        <div class="upload-tip">支持 jpg、png 格式，大小不超过 4MB</div>
      </el-form-item>

      <el-form-item label="发布者" prop="publisher">
        <el-input v-model="formData.publisher" placeholder="请输入发布者" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, defineEmits, defineProps, useAttrs, watch, computed } from 'vue';
import type { UploadRequestOptions } from 'element-plus';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { drugById, drugUpdate } from '@/api/drug';
import { reqUpload } from '@/api/utils';

const attrs = useAttrs();
const props = defineProps<{
  drugId: number | null;
  visible?: boolean;
}>();

const emit = defineEmits(['update:visible', 'update:modelValue', 'success']);

const dialogVisible = computed({
  get: () => props.visible ?? attrs['modelValue'] ?? false,
  set: (val: boolean) => {
    emit('update:visible', val);
    emit('update:modelValue', val);
  },
});

const formRef = ref();
const submitLoading = ref(false);

const formData = ref({
  drugId: 0,
  drugName: '',
  drugInfo: '',
  drugEffect: '',
  drugImg: '',
  publisher: '',
});

const formRules = {
  drugName: [
    { required: true, message: '请输入药品名称', trigger: 'blur' },
    { max: 255, message: '药品名称长度不能超过255个字符', trigger: 'blur' },
  ],
  publisher: [
    { required: true, message: '请输入发布者', trigger: 'blur' },
  ],
};

const title = '修改药品';

// 监听弹窗打开，获取详情
watch(
  () => dialogVisible.value,
  (val) => {
    if (val && props.drugId) {
      fetchDrugDetail(props.drugId);
    }
  }
);

// 获取药品详情
const fetchDrugDetail = async (id: number) => {
  try {
    const res: any = await drugById(id);
    const drug = res.t;
    formData.value = {
      drugId: drug.drugId,
      drugName: drug.drugName || '',
      drugInfo: drug.drugInfo || '',
      drugEffect: drug.drugEffect || '',
      drugImg: drug.drugImg || '',
      publisher: drug.publisher || '',
    };
  } catch (error: any) {
    ElMessage.error(error.message || '获取药品详情失败');
  }
};

const handleClose = () => {
  formRef.value?.resetFields();
  dialogVisible.value = false;
};

// 图片上传前校验
const beforeUpload = (file: File) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt4M = file.size / 1024 / 1024 < 4;

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的文件!');
  }
  if (!isLt4M) {
    ElMessage.error('图片大小不能超过 4MB!');
  }
  return isImage && isLt4M;
};

// 自定义上传逻辑
const uploadImg = async (params: UploadRequestOptions) => {
  const formDataUpload = new FormData();
  formDataUpload.append('file', params.file);
  formDataUpload.append('model', 'drug');

  try {
    const res: any = await reqUpload(formDataUpload);
    formData.value.drugImg = res.obj;
    console.log('图片上传成功，路径:', formData.value.drugImg);
    ElMessage.success('图片上传成功');
  } catch (error: any) {
    ElMessage.error(error.message || '图片上传失败');
  }
};

const handleSubmit = async () => {
  try {
    await formRef.value?.validate();
  } catch {
    ElMessage.warning('请填写必填项');
    return;
  }

  submitLoading.value = true;
  try {
    const res: any = await drugUpdate(formData.value);
    ElMessage.success('修改成功');
    handleClose();
    emit('success');
  } catch (error: any) {
    ElMessage.error(error.message || '修改失败');
  } finally {
    submitLoading.value = false;
  }
};

// 图片路径处理 - 添加代理前缀
const getImageUrl = (url: string) => {
  if (!url) return '';
  // 如果是完整URL直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  // 后端返回的路径已经是 /upload/drug/xxx.jpg，添加代理前缀
  return '/target' + url;
};
</script>

<style scoped lang="scss">
.drug-img-uploader {
  :deep(.el-upload) {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
  }

  :deep(.el-upload:hover) {
    border-color: var(--el-color-primary);
  }
}

.drug-image {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.drug-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #e4e6e9;
  border-radius: 6px;
}

.upload-tip {
  color: #999;
  font-size: 12px;
  margin-top: 8px;
}
</style>
