<!-- 定义表单 表单中有表单项 表单需要绑定对象， 表单项需要绑定对象的成员变量 提交表单需要使用ajax方法提交json对象 -->
<!-- el-cascader 级联下拉选择框 需要通过options指定选项，这里绑定了options变量所以需要自己在data定义options 变量 绑定了change事件（当用户选择性的选项时候激活 handleChange，将多级选择的地址值组合 赋值给指定对象的属性）并且选中的值绑定给
selectedOptions
-->

<!--  private String bid;-->
<!--  private String btitle;-->
<!--  private String bimg;-->
<!--  private String bdes-->
<!--  private Integer bnum;-->

<template>
  <div>
    <el-dialog title="修改用戶"  v-model="dialogVisble" :before-close="handleClose" :close-on-click-modal="false">
      <el-form   label-width="120px"  ref="editBannerUploadForm">
        <!-- 代码中的1表示第几个 和引入js的type变量对应 -->
        <!-- action 就是原来普通form action也就是提交地址 所以对应方法返回后端正确的action请求地址-->
        <el-form-item label="头像">
          <el-upload class="avatar-uploader" :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
            <img v-if="banner.bimg" :src="getUploadUrl(banner.bimg)" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <el-form :model="banner" ref="editBannerForm">
        
<!--        <el-form-item  label="球员编号：" label-width="100px" prop="bid">-->
<!--          <el-col :span="16">-->
<!--            <el-input v-model="banner.pid" placeholder="请填入学号"/>-->
<!--          </el-col>-->
<!--        </el-form-item>-->

        <el-form-item  label="标题：" label-width="100px" prop="btitle">
          <el-col :span="16">
            <el-input v-model="banner.btitle" placeholder="请填入标题"/>
          </el-col>
        </el-form-item>
        <el-form-item  label="描述：" label-width="100px" prop="bdes">
          <el-col :span="16">
            <el-input v-model="banner.bdes" placeholder="请填入描述"/>
          </el-col>
        </el-form-item>
        <el-form-item  label="优先级：" label-width="100px" prop="bnum">
          <el-col :span="16">
            <el-input v-model="banner.bnum" placeholder="请填入优先级"/>
          </el-col>
        </el-form-item>

<!--        <el-form-item  label="性别：" label-width="100px" prop="psex">-->
<!--          <el-col :span="8">-->
<!--            <el-radio v-model="banner.psex" label="男">男</el-radio>-->
<!--            <el-radio v-model="banner.psex" label="女">女</el-radio>-->
<!--          </el-col>-->
<!--        </el-form-item>-->


<!--        <el-form-item  label="出生日期：" label-width="100px" prop="pbirthday">-->
<!--          <el-col :span="8">-->
<!--            <el-date-picker-->
<!--                    v-model="banner.pbirthday"-->
<!--                    type="date"-->
<!--                    placeholder="选择日期">-->
<!--            </el-date-picker>-->
<!--          </el-col>-->
<!--        </el-form-item>-->


<!--        <el-form-item  label="身价：" label-width="100px" prop="costVal">-->
<!--          <el-col :span="16">-->
<!--            <el-input v-model="banner.costVal" placeholder="请填入身价"/>-->
<!--          </el-col>-->
<!--        </el-form-item>-->




        <!--<el-form-item label="籍贯">-->
        <!--<el-cascader-->
        <!--size="large"-->
        <!--:options="options"-->
        <!--v-model="selectedOptions"-->
        <!--@change="handleChange"-->
        <!--&gt;-->
        <!--</el-cascader>-->


        <!--</el-form-item>-->
<!--        <el-form-item label="位置：" label-width="100px" prop="position1">-->
<!--          <el-select-->
<!--                  v-model="banner.position1"-->
<!--                  placeholder="Select"-->
<!--                  size="small"-->
<!--                  style="width: 240px"-->
<!--          >-->
<!--            <el-option-->
<!--                    v-for="item in poptions"-->
<!--                    :key="item.value"-->
<!--                    :label="item.label"-->
<!--                    :value="item.value"-->
<!--            />-->
<!--          </el-select>-->
<!--        </el-form-item>-->
<!--        <el-form-item label="位置：" label-width="100px" prop="position2">-->
<!--          <el-select-->
<!--                  v-model="banner.position2"-->
<!--                  placeholder="Select"-->
<!--                  size="small"-->
<!--                  style="width: 240px"-->
<!--          >-->
<!--            <el-option-->
<!--                    v-for="item in poptions"-->
<!--                    :key="item.value"-->
<!--                    :label="item.label"-->
<!--                    :value="item.value"-->
<!--            />-->
<!--          </el-select>-->
<!--        </el-form-item>-->

        <el-row>
          <el-col :span="16">
            <span class="editSpan">&nbsp;</span>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" @click="editBanner">修改轮播图</el-button>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>
  </div>
</template>

<script lang="ts">
  import api from "../../../axios/api.js"
  //import rules from "@/../static/js/validator/rules.js"
  import { banner_edit } from "@/api/banner" //数据
  import { reqUpload } from "@/api/utils" //数据
  import { regionData, codeToText } from "element-china-area-data";
  //import utils from "@/../static/js/utils/upload.js"
  import { ref, watch ,defineComponent,reactive,defineEmits,watchEffect,defineProps} from 'vue'
  import { ElMessage } from 'element-plus';
  import { Plus } from "@element-plus/icons-vue";
  import  type { UploadProps } from 'element-plus'
  import { banner_id } from "@/api/banner" //数据
  defineProps({
    banner: Object // 注意这里的String的S是大写的，不是String
  })
  export default {
    props: {
      visible: {
        type: Boolean,
        default: false
      },
      bid:{
        type: String
      }
    },

    setup(props, ctx) {

      const banner = ref({
        bid: '',
        btitle: '',
        bimg: '',
        bdes: '',
        bnum: '',
      });
      const bid = ref()
      const   get_banner_id = (id) =>  {
        banner_id(id).then((dto: any) => {
          banner.value = dto.t
console.log(banner)
        });
      };

      const editBannerForm = ref()


      let that = this;
      const position = ref('')

      const poptions = [
        {
          value: '前锋',
          label: '前锋',
        },
        {
          value: '中卫',
          label: '中卫',
        },
        {
          value: '边卫',
          label: '边卫',
        },
      ]
      const dialogVisble = ref(false)

      const close = () => {
        ctx.emit("update:visible", false);
      };

      const confirm = () => {
        console.log('你点击了确定按钮')
        ctx.emit("update:visible", false);
      };
      const handleClose=()=>{
        //给父组件传参
        //this.$emit("closeAddDialog");
        ctx.emit("update:visible", false);
      };
      // 使用TypeScript接口定义表单数据结构



      const editBanner=() => {

        console.log(banner.bimg)
        console.log(banner.value)
        banner_edit(banner.value).then(
                dto => {
                  ElMessage({
                    message: dto.msg,
                    type: 'success'
                  });
                  handleClose();
                }
        )


      };

      const getUploadUrl=(imgsrc)=>{
        return      "http://localhost:5173/target/upload/banner/"+imgsrc;
      }
//图片上传成功的钩子
      const handleAvatarSuccess: UploadProps['onSuccess'] = () => {
        //editBannerForm.value.clearValidate('imageUrl')
        ElMessage.success('上传头像成功')
      };
//上传图片组件->上传图片之前触发的钩子函数
      const beforeAvatarUpload: UploadProps['beforeUpload'] = async (rawFile: any) => {

        const formData = new FormData();
        formData.append('file', rawFile);
        formData.append('model',"banner" );
        //请求上传文件的接口
        let res = await reqUpload(formData)
        //将接口的地址赋值给表单并呈现

        banner.value.bimg = res.obj
        console.log(banner.value)
        //上传图片格式和大小要求  png|jpg  4M
        if (rawFile.type !== 'image/png' || rawFile.type == 'image/jpg') {
          ElMessage.error('上传文件格式务必PNG|JPG')
          return false
        } else if (rawFile.size / 1024 / 1024 > 4) {
          ElMessage.error('上传文件大小小于4M')
          return false
        }
        return true
      };


      // watch(() => props.visible, (val) => {
      //   console.log(props.visible, val);
      //   dialogVisble.value = val
      //
      // });
      watch(
              () => [props.visible, props.bid],
              ([newVisible, newCurrId], [oldVisible, oldCurrId]) => {

                //利用父组件传过来的值可以执行相应的操作
                dialogVisble.value = newVisible;
                bid.value = newCurrId
                get_banner_id(newCurrId)
              })




                return {
        dialogVisble,bid,
        handleClose,poptions,position,editBanner,banner,beforeAvatarUpload,handleAvatarSuccess,getUploadUrl,get_banner_id
      }

    },


    name: "edit",
    data(){
      return {
        //行政区使用
        options: regionData,
        selectedOptions: [],
        editDialogIsShow :false
        //行政区使用结束
        ,
        //校验

        //校验结束
        //上传使用
        fileList: [[],[],[]], //缓存区文件
        uploadFile:[[],[],[]], //  上传用文件
        formData:{files:[]},
        imagesUp:null,
        //上传使用结束
      }
    },
    methods:{

      //查询全部班级提供给下拉列表选择
      getAllBanners:function(){
        api.setDataFromAxios("/api/banners_page",null,"get",null).then(
                dto=>{
                  this.banners = dto.tList;
                }
        )
      },
      //行政区使用
      handleChange() {
        var loc = "";
        var regCode = "";
        for (let i = 0; i < this.selectedOptions.length; i++) {
          loc += codeToText[this.selectedOptions[i]];
          regCode =this.selectedOptions[i]
        }
        console.log(loc);
        console.log(regCode);
        this.teacher.banner = loc+","+regCode;
      },
      //行政区使用结束
      //提供给change事件刷新输入
      updateView(e) {
        this.$forceUpdate();
      },



      //多文件上传结束
    },
    created() {

    },
    computed:{//执行上传得到url


    },
  }
</script>

<style scoped>

  .avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: bannerer;
    position: relative;
    overflow: bidden;
    transition: var(--el-transition-duration-fast);
    width:178px;height:178px;
    border:  #2c3e50 solid 1px;
  }

  .avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
  }

  .avatar-uploader img{
    width:178px;height:178px;
  }

  .el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
    border:  #e4e6e9 dashed 1px;
  }
</style>
