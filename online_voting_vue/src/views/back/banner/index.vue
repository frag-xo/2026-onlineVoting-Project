<template>
<div>
<!--  private String bid;-->
<!--  private String btitle;-->
<!--  private String bimg;-->
<!--  private String bdes-->
<!--  private Integer bnum;-->
  <el-table :data="data.banners" stripe style="width: 100%">
    <el-table-column prop="bid" label="编号" width="100"/>
    <el-table-column  label="园艺师头像" width="80">
      <template v-slot="scope">
        <img :src="getUploadUrl(scope.row.bimg)" style="width:40px;height: 40px">
      </template>
    </el-table-column>
    <el-table-column prop="btitle" label="名称" width="160"/>
    <el-table-column prop="bdes" label="描述"  width="160"/>
    <el-table-column prop="bnum" label="优先" width="100"/>
    <!--<el-table-column prop="amBirthday" :formatter="myformatDate" label="生日" width="160"/>-->
    <!--<el-table-column prop="amJoinday" :formatter="myformatDate" label="參加" width="160"/>-->

    <el-table-column label="操作" width="160">
        <template v-slot="scope">
            <el-button class="editBtn" type="success" @click="showEdit(scope.row.bid)">修改</el-button>
            <el-button class="delBtn" type="danger" @click="handleDelete(scope.row.bid)">删除</el-button>
        </template>

    </el-table-column>
  </el-table>
  <el-row>
  <el-pagination
          background
          layout="prev, pager, next"
          @current-change="currPageChange"
          :current-page.sync="pageInfo.current"
          :size="data.pageInfo.size"
          :page-count="data.pageInfo.pages"
          :total="data.pageInfo.total"
  v-model:current-page="pageInfo.current">
  </el-pagination>
    <el-button @click="showAdd" class="addBtn" type="primary">新增园艺师</el-button>
  </el-row>
   <!--:current-page.sync="currPage" 当这个组件改变，将组件上的值同步给currPage-->
  <addBanner   v-model:visible="flagAddVisiable"/>
  <editBanner   v-model:visible="flagEditVisiable" :bid="currId"/>
  <edit :editDialogIsShow="editDialogIsShow" @closeEditDialog="closeEditDialog" ref="showEditTeacher" :clazzes="clazzes"/>


  </div>
</template>

<script lang="ts">
  import { ref, onMounted,reactive ,provide ,watch} from "vue";
  import { banners_page } from "@/api/banner" //数据
  import addBanner from '@/views/back/banner/addBanner.vue'
  import editBanner from '@/views/back/banner/editBanner.vue'
  // import edit from '@/views/back/banner/edit.vue'
  import { formatToDate } from "@/utils/dateUtil"



  // 声明一个 ref 来存放该元素的引用
  // 必须和模板里的 ref 同名
  // 为了获取 MyDialog 的类型，我们首先需要通过 typeof 得到其类型，再使用 TypeScript 内置的 InstanceType 工具类型来获取其实例类型
  var addDialogIsShow = false;
  var editDialogIsShow = false;
  export default {
    components:{addBanner,editBanner},
    setup() {//setup等价与before moute 组件加载前调用的方法
      const data = reactive({ banners: [],pageInfo:Object}); //测试请求方法
      const pageInfo = reactive({ total: String,size:String,pages:String,PageNo:String });
      var banners=[];

      const flagAddVisiable = ref(false)
      const flagEditVisiable = ref(false)

      const showAdd = () => {
        flagAddVisiable.value = true
      }
      const currId = ref()
      const showEdit = (id) => {
        flagEditVisiable.value = true
        currId.value = id
      };


      // watch(() => flagAddVisiable.value , (val) => {
      //   console.log("监听flagAddVisiable值得变化:", val)
      // })





      const   get_banners_page = (pageInfo) =>  {
        banners_page(pageInfo).then((dto: any) => {
            data.banners = dto.obj.records
            pageInfo = dto.obj
            data.pageInfo = pageInfo
          });
        };

      // const   get_banners = (pageInfo) =>  {
      //   banners(pageInfo).then((dto: any) => {
      //     console.log(dto);
      //     data.banners = dto.obj.records
      //     pageInfo = dto.obj
      //     data.pageInfo = pageInfo
      //   });
      // };



      const currPageChange = (currPage)=> {
        get_banners_page({"current":currPage,"size":3});//当用户选择页码，选中的页码自动同步给currPage，再调用重新分页查询当前页方法
        };
      const getUploadUrl=(bimg)=>{
        return      "http://localhost:5173/target/upload/banner/"+bimg;
      }

      const  myformatDate = (row, column, cellValue)=> {
        return formatToDate(cellValue);
      };
      onMounted(() => {
        //console.log(222);
        get_banners_page({"current": 1,"size":3})
      });
//return 返回的值是让其它组件能够发现这些值和值背后的函数
      return {
        currId,data,pageInfo,formatToDate,get_banners_page,currPageChange,banners,myformatDate,flagAddVisiable,flagEditVisiable,showAdd,showEdit,getUploadUrl
      }
    },

  };
</script>
<style lang="stylus">
.el-row
  height 81px
  line-height 81px
  button
    margin-top 20px


.editBtn
  float left
.delBtn
  float right

.addBtn
  position absolute
  right:30px


</style>
