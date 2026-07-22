<template>
  <div  v-on:mouseover="stop()" v-on:mouseout="move()" class="focus"  v-cloak><!-- v-cloak定义包含全部切换内容的标签放置闪烁 -->
    <ul class="focusUl">
      <li v-for="(banner,index) in data.banners" v-show="index===num" :key="banner.bid">
        <!-- tag属性可以将router-link指定为其他标签,如图：改为div标签 -->
        <!-- <router-link to="" tag="div">-->
        <img :src="getUploadUrl(banner.bimg)" :alt="banner.btitle">
      </li>
    </ul>
    <div class="focusSpans">
      <!-- stylet 编写v-cloak 解决谷歌等浏览器 切换闪烁，原因JavaScript去操作DOM，都会等待DOM加载完成（DOM ready）。对于vuejs、angularjs这些会在DOM ready完会才回去解析html view Template 如果内容只是文本 或者使用v-text 代替{{}}} 因为渲染文本和添加节点2个操作顺序
       v-cloak指令和css规则如[v-cloak]{display:none}一起用时，这个指令可以隐藏未编译的Mustache标签直到实例准备完毕。
    v-cloak 指令可以像css选择器一样绑定一套css样式然后这套css会一直生效到实例编译结束。-->

      <span v-for="(span,index) in spans" class="navspan" @mouseenter="change(index)" :class="{'active':index==num}" v-text="span"></span>
    </div>
  </div>
</template>

<script lang="ts">

  import { ref, onMounted,reactive ,provide ,watch} from "vue";
  import { bannersTop4 } from "@/api/banner" //数据

    export default {
    setup(){
      const data = reactive({ banners: []}); //测试请求方法
      const   get_bannersTop4 = (pageInfo) =>  {
        bannersTop4(pageInfo).then((dto: any) => {
          console.log(dto);
          data.banners = dto.tList
        });
      };
      const spans = ['1','2','3','4'];
      var  time = '';
      const  num = ref(0);
      const play = () =>{
        time=setInterval(()=>{
          num.value = num.value + 1;
          if(num.value==3){
            num.value=0
          }

        },2000)
      };

      const change = (i)  =>{
        num.value=i
      };
      const stop = () => {
        clearInterval(time)
      };
      const move = () =>{
        play();
      };
      const getUploadUrl=(imgsrc)=>{
        return      "http://localhost:5173/target/upload/banner/"+imgsrc;
      }

      onMounted(() => {
        get_bannersTop4()
        //console.log(222);
        play()
      });

      return {
        data,spans,time,num,play,change,stop,move,onMounted,getUploadUrl,get_bannersTop4
      }
    },

    }
</script>

<style lang="stylus">
  [v-cloak] {
    display: none!important;
  }
  .focus
    margin 0 auto;

    width 1280px;
    height 420px
    position relative
    &:after
      display block
      content ''
      clear both
    .focusUl
      margin 0
      padding 0
      position relative
      z-index 0
      li
       list-style-type none
       width 1280px
       height 420px
       position absolute
       top 0
       left 0
       img
        width 1280px
        height 420px
    .focusSpans
          position absolute
          height 20px
          bottom 10px
          right 10px
          z-index 3
      .navspan
        display inline-block
        width 20px;
        height 20px;
        background-color white
        opacity 0.6
        border-radius 10px;
        color white
        margin-right 10px;
</style>
