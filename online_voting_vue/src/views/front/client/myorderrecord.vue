<!-- 定义表单 表单中有表单项 表单需要绑定对象， 表单项需要绑定对象的成员变量 提交表单需要使用ajax方法提交json对象 -->
<!-- el-cascader 级联下拉选择框 需要通过options指定选项，这里绑定了options变量所以需要自己在data定义options 变量 绑定了change事件（当用户选择性的选项时候激活 handleChange，将多级选择的地址值组合 赋值给指定对象的属性）并且选中的值绑定给
selectedOptions
-->


<template>
  <el-dialog title="修改用戶"  v-model="dialogVisble" :before-close="handleClose" :close-on-click-modal="false">
  <div>
    <!-- 商品列表 -->
    <div class="info-card">
      <div class="card-title"><i class="fas fa-box"></i> 商品清单</div>
      <div class="goods-list">
        <div class="goods-item" v-for="item in order.goodsLists" :key="item.gid">
          <img :src="getUploadUrl(item.plant.pimg)" class="goods-img" alt="商品图片">
          <div class="goods-info">
            <div class="goods-name">{{ item.plant.pname }}</div>
            <div class="goods-spec">{{ item.plant.pdescription }}</div>
          </div>
          <div class="goods-price">¥{{ item.plant.pprice }}</div>
          <div class="goods-count">x{{ item.plant.pstock}}</div>
          <div class="goods-total">¥{{ (item.plant.pprice * item.plant.pstock).toFixed(2) }}</div>
        </div>
      </div>
    </div>

  </div>
    </el-dialog>
</template>

<script lang="ts">
  import api from "../../../axios/api.js"
  //import rules from "@/../static/js/validator/rules.js"
  import { horticultirist_edit } from "@/api/horticultirist" //数据
  import { reqUpload } from "@/api/utils" //数据
  import { regionData, codeToText } from "element-china-area-data";
  //import utils from "@/../static/js/utils/upload.js"
  import { ref, watch ,defineComponent,reactive,defineEmits,watchEffect,defineProps} from 'vue'
  import { ElMessage } from 'element-plus';
  import { Plus } from "@element-plus/icons-vue";
  import  type { UploadProps } from 'element-plus'
  import { horticultirist_id } from "@/api/horticultirist" //数据
  defineProps({
    horticultirist: Object // 注意这里的String的S是大写的，不是String
  })
  export default {
    props: {
      visible: {
        type: Boolean,
        default: false
      },
      order:{
        type: Object
      }
    },

    setup(props, ctx) {

      const horticultirist = ref({
        hid: '',
        hname: '',
        himg: '',
        hphone: '',
        hemail: '',
        htype: '',
      });
      const order = ref({})
      const   get_order = (currOrder) =>  {
        order.value = currOrder
        console.log(order)
        console.log("_____order______")

      };


      const dialogVisble = ref(false)

      const close = () => {
        ctx.emit("update:visible", false);
      };



      const getUploadUrl=(imgsrc)=>{
        return      "http://localhost:5173/target/upload/plant/"+imgsrc;
      }

      watch(
              () => [props.visible, props.order],
              ([newVisible, newCurrOrder], [oldVisible, oldCurrOrder]) => {
                console.log(newCurrOrder)
                console.log(newVisible)
                console.log("______newCurrOrder______")
                //利用父组件传过来的值可以执行相应的操作
                dialogVisble.value = newVisible;
                order.value = newCurrOrder
                get_order(newCurrOrder)
              })




                return {
        dialogVisble,order,get_order,
        horticultirist,getUploadUrl
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
      getAllHorticultirists:function(){
        api.setDataFromAxios("/api/horticultirists_page",null,"get",null).then(
                dto=>{
                  this.horticultirists = dto.tList;
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
        this.teacher.horticultirist = loc+","+regCode;
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
/* 信息卡片 */
.info-card {
  background: white;
  border-radius: 20px;
  padding: 20px 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #edf2f7;
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-title i {
  color: #3b82f6;
  font-size: 18px;
}

/* 商品列表 */
.goods-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid #f1f5f9;
}

.goods-img {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  object-fit: cover;
  background: #f8fafc;
}

.goods-info {
  flex: 2;
}

.goods-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.goods-spec {
  font-size: 12px;
  color: #94a3b8;
}

.goods-price {
  width: 100px;
  text-align: right;
  color: #e11d48;
  font-weight: 500;
}

.goods-count {
  width: 60px;
  text-align: center;
  color: #475569;
}

.goods-total {
  width: 100px;
  text-align: right;
  font-weight: 700;
}

</style>
