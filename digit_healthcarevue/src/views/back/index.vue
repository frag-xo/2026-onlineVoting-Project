<template>
  <div class="topMain">
    <el-container class="main">
      <el-aside :width="tabWidth+'px'">
        <div>
          <div class="isClossTab" @click="isClossTabFun">
            <i :class="isCollapse?'el-icon-d-arrow-right':'el-icon-d-arrow-left'" ></i>
          </div>
          <leftMenu v-on:listenToChild="getBreadcrumbListFromChild"></leftMenu>
        </div>
      </el-aside>
      <el-container>
        <el-row class="main-header">
          <el-dropdown>
                        <span class="el-dropdown-link">
                            <img src="" alt="">
                        </span>
            <el-dropdown-menu v-slot="dropdown">
              <el-dropdown-item>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </el-row>
        <el-main>
          <el-breadcrumb separator="/" class="crumbs">
            <el-breadcrumb-item :to="{ path: '/back' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="o in breadcrumbList" :to="o.entity.path" :key="o.entity.id">{{o.entity.alias}}
            </el-breadcrumb-item>
          </el-breadcrumb>
          <div style="width: 950px">
            <router-view></router-view>
          </div>
        </el-main>
        <el-footer class="main-footer" height="50px">
          <p>页脚</p>
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>
<script>
  import leftMenu from '@/views/back/leftMenu.vue'
  export default {
    name: "index",
    components: {
      leftMenu: leftMenu
    },
    data() {
      return {
        isCollapse: false,
        tabWidth: 200,
        test1: 1,
        intelval: null,
        breadcrumbList:[] // 提供给面包屑导航使用
      };
    },
    methods: {
      handleOpen(key, keyPath) {
        console.log(key, keyPath);
      },
      handleClose(key, keyPath) {
        console.log(key, keyPath);
      },


      isClossTabFun(){
        clearInterval(this.intelval);
        if(!this.isCollapse){
          this.intelval = setInterval(()=>{
            if(this.tabWidth<= 64)
              clearInterval(this.intelval);
            this.tabWidth -= 1;
          }, 1);
        }else{
          this.tabWidth = 200;
        }
        this.isCollapse = !this.isCollapse;
      },
      //提供给面包屑导航使用,
      //方式一 获取子组件处理后的值
      getBreadcrumbListFromChild(breadcrumbs){
         this.breadcrumbList = breadcrumbs;
      },
      //方式二 获取main.js中处理to 路由的值
      // getRoutePath() {
      //   this.breadcrumbList = this.$route.meta.routeList;
      // }
    },
    //使用beforeRouteEnter和下面watch效果一样区别没有this对象只能通过vm对象操作
    // beforeRouteEnter(to,from, next) {
    //   next((vm) => {
    //     vm.realList = to.meta.routeList;
    //   });
    // },
    // watch:{
    //   $route:function(newV,oldV) {
    //     this.breadcrumbList =newV.meta.routeList;
    //   }
    // },
    // created(){
    //   this.getRoutePath();
    // },
  }
</script>
<style>
  *{
    padding: 0;
    margin: 0;
  }

</style>
<style scoped lang="stylus">
  .topMain
    margin 0 auto
  .main
    margin-left : 0px;
    height: 100%
    min-width 1000px
    min-height 600px
    max-height 3000px
    overflow hidden
    aside
      overflow: visible
      height: 100%
      background-color: #545c64
      color:#FFF
      .isClossTab
        width: 100%
        height: 60px
        cursor: pointer
        font-size: 25px
        text-align: center
        line-height: 60px
        font-weight: bold
        border-right: 1px solid #807c7c
        box-sizing: border-box
      .menu
        width: 100%
        border-right:0
.main-header
  background-color: #545c64
  color:#FFF

  .el-dropdown
    cursor: pointer
    position: absolute
    right 20px
    top 10px
  .el-dropdown-link

    img
      display inline-block
      width:50px
      height: 50px
      border-radius: 25px
      background-color: #FFF
      margin-top: ((60px - 50)/2)
      float:right
.crumbs
  margin-bottom: 20px
.main-footer
  text-align center
  background-color: #545c64
  color:#FFF
  line-height 50px
</style>
