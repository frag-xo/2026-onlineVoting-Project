<template>
  <div>
    <!-- 依次设置背景颜色 字体颜色 选中颜色 默认激活的菜单项根据activeIndex值匹配-->
    <el-menu
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      :default-active="activeIndex"
      router
      @select="handleSelect">
      <navMenu :navMenus="menuData"></navMenu>
    </el-menu>
  </div>
</template>

<script>
  import api from "@/axios/api"
  import navMenu from '@/views/back/navMenu.vue'
  export default {
    props:["breadcrumbs"],//如果子组件要使用父组件的值
    components: {
      navMenu: navMenu
    },
    data() {
      return {
        activeIndex: '/back/index',
        menuData: [

        ],
        indexBreadcrumbs: [],
      };
    },
    methods:{
    getMenus:function(){
      //为什么要封装一下方法 1.每个vue组件有自己的一些参数处理，简化调用本来这个setDataFromAxios这个方法属于api 重新封装就属于当前页面


      api.setDataFromAxios("/mock/menu/list",null,"get")
        .then(res=>{this.menuData = res.data.menuData});//.then方法 就是当服务器有返回的时候执行回调函数，对比jquery的ajax success:function（data）
    },
      handChange () {
        this.$emit('listenToChild', this.breadcrumbList)
      },
      handleSelect (index, indexPath) {
        this.indexBreadcrumbs = indexPath
        console.log(this.indexBreadcrumbs);
      }
  },
  //vue init方法 当前页面初始化就自动执行的方法 一般用于页面初始化就读取数据
  created:function () {
    this.getMenus();//1.创建页面的时候 调用自定义的数据请求方法  而数据请求方法把得到数据赋值给了当前页面的变量
    this.handChange();
  },
    computed: {
      breadcrumbList () {
        let breadcrumbs = []
        let menuList = this.menuData
        console.log(this.menuData);
        this.indexBreadcrumbs.map(item => {

          for (let i = 0; i < menuList.length; i++) {
            // console.log(item);
            // console.log(menuList[i].entity.name);
            // console.log(menuList[i].name);
            if (item === menuList[i].entity.name) {
              breadcrumbs.push(menuList[i])
              if (menuList[i].child) {
                menuList = menuList[i].child
              }
              break;
            }
          }
        })
        console.log(breadcrumbs)
        return breadcrumbs
      }
    },
    watch: {
      $route () {
        this.handChange()
      }
    },
  };
</script>

<style>
</style>
