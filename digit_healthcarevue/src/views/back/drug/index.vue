<template>
  <div class="drug-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="药品名称">
          <el-input
            v-model="queryParams.drugName"
            placeholder="请输入药品名称"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="药品功能">
          <el-input
            v-model="queryParams.drugEffect"
            placeholder="请输入药品功能"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="发布者">
          <el-input
            v-model="queryParams.publisher"
            placeholder="请输入发布者"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon> 搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon> 重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="hover">
      <div class="table-header">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon> 新增药品
        </el-button>
        <el-button type="success" @click="handleGenerateData">
          <el-icon><Download /></el-icon> 生成随机数据
        </el-button>
      </div>

      <el-table
        :data="drugList"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="drugId" label="编号" width="80" align="center" />
        <el-table-column label="药品图片" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.drugImg"
              :src="getImageUrl(row.drugImg)"
              style="width: 50px; height: 50px"
              fit="cover"
              :preview-src-list="[getImageUrl(row.drugImg)]"
              preview-teleported
              class="table-image"
            />
            <span v-else class="no-image">暂无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="drugName" label="药品名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="shortName" label="药品简称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="drugInfo" label="药品成分" min-width="150" show-overflow-tooltip />
        <el-table-column prop="drugEffect" label="功能主治" min-width="150" show-overflow-tooltip />
        <el-table-column prop="publisher" label="发布者" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">修改</el-button>
            <el-button type="danger" link @click="handleDelete(row.drugId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        class="pagination"
      />
    </el-card>

    <!-- 新增弹窗 -->
    <add-drug v-model="addDialogVisible" @success="fetchDrugList" />

    <!-- 编辑弹窗 -->
    <edit-drug v-model="editDialogVisible" :visible="editDialogVisible" :drug-id="currentDrugId" @success="fetchDrugList" />
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Refresh, Plus, Download } from '@element-plus/icons-vue';
import { drugPage, drugDeleteById, drugInitRandom } from '@/api/drug';
import AddDrug from './addDrug.vue';
import EditDrug from './editDrug.vue';

const loading = ref(false);
const drugList = ref<any[]>([]);
const total = ref(0);

const queryParams = reactive({
  drugName: '',
  drugEffect: '',
  publisher: '',
  pageNum: 1,
  pageSize: 10,
});

const addDialogVisible = ref(false);
const editDialogVisible = ref(false);
const currentDrugId = ref<number | null>(null);

// 获取药品列表
const fetchDrugList = async () => {
  loading.value = true;
  try {
    const res: any = await drugPage(queryParams);
    drugList.value = res.t.records;
    total.value = Number(res.t.total);
  } catch (error: any) {
    ElMessage.error(error.message || '获取药品列表失败');
  } finally {
    loading.value = false;
  }
};

// 搜索
const handleSearch = () => {
  queryParams.pageNum = 1;
  fetchDrugList();
};

// 重置
const handleReset = () => {
  queryParams.drugName = '';
  queryParams.drugEffect = '';
  queryParams.publisher = '';
  queryParams.pageNum = 1;
  queryParams.pageSize = 10;
  fetchDrugList();
};

// 新增
const handleAdd = () => {
  addDialogVisible.value = true;
};

// 编辑
const handleEdit = (row: any) => {
  currentDrugId.value = row.drugId;
  editDialogVisible.value = true;
};

// 删除
const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定要删除该药品吗？此操作不可恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const res: any = await drugDeleteById(id);
      ElMessage.success(res.t || '删除成功');
      fetchDrugList();
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败');
    }
  }).catch(() => {
    // 用户取消
  });
};

// 生成随机数据
const handleGenerateData = () => {
  ElMessageBox.confirm('确定要生成50条随机药品数据吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      const res: any = await drugInitRandom(50);
      ElMessage.success(res.msg || '生成成功');
      fetchDrugList();
    } catch (error: any) {
      ElMessage.error(error.message || '生成失败');
    }
  }).catch(() => {
    // 用户取消
  });
};

// 监听分页参数变化
watch(
  () => [queryParams.pageNum, queryParams.pageSize],
  () => {
    fetchDrugList();
  }
);

// 图片路径处理 - 添加代理前缀
const getImageUrl = (url: string) => {
  if (!url) return '';
  // 如果是完整URL直接返回
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url;
  }
  // 后端返回的路径已经是 /upload/drug/xxx.jpg，添加代理前缀
  return '/target/' + url;
};

onMounted(() => {
  fetchDrugList();
});
</script>

<style scoped lang="scss">
.drug-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;

  .search-form {
    display: flex;
    flex-wrap: wrap;
  }
}

.table-card {
  .table-header {
    margin-bottom: 16px;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}

.no-image {
  color: #999;
  font-size: 12px;
}

.table-image {
  cursor: pointer;
  border-radius: 4px;
  transition: transform 0.2s;
  
  &:hover {
    transform: scale(1.1);
  }
}
</style>
