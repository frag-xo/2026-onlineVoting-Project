<template>
  <div>
  <div id="headbar">
    <div id="logo" @click="comeback">
            <img src="../../../../assets/logo.png"/>
    </div>

    <div class="headtitle">Online_Voting</div>
    <div class="login">
      <!--与 v-if 比较
v-show 指令的功能与 v-if 指令相似。不过 v-if 指令会根据表达式重建或销毁元素或组件以及它们所绑定的事件。v-show 指令只是简单地设置 css 属性。-->
     <login id="login" v-show="getLoginFormShow(this)"></login>
      <div class="loginUIbtn" @click="showLoginUI"></div>
      <div class="loginoutUIbtn" @click="loginOut"></div>
    </div>

  </div>
<!--    <navi></navi>-->
  </div>
</template>

<script>
  import login from "@/views/front/public/login.vue"
  // import navi from "@/views/front/public/navi/navi.vue"
  export default {
    data(){
      return {loginFormShow:false}
    },
    name: "myheader",
    components: {
      login,
      // navi
    },
    methods:{
      showLoginUI : function(){
        if (this.loginFormShow){
          this.loginFormShow = false;
        } else{
          this.loginFormShow = true;
        }
      },
      getLoginFormShow:function(){
        return this.loginFormShow
      },
      loginOut:function(){

      },
        comeback:function(){
            this.$router.push({
                path:"/front/content",
                name:"content",
            })
        }
    }
  }
</script>

<style lang="stylus">
  #headbar
    margin-left auto
    margin-right auto
    width 1280px;
    height 80px;
    position relative;
    line-hight 120px;

    &:after
      clear both;
      content "";
      display block;

    #logo
      width 40px;
      height 40px;
      position absolute;
      left 40px;
      top 20px;

      img
        width 40px;
        height 40px;

  .headtitle
    position absolute;
    width 800px;
    height 80px;
    line-height 80px;
    font-size 30px;
    left 100px;
    top 0px;
    text-align center;
    color rgba(107, 113, 113, 0.71);

  .login
    width:440px;
    position absolute
    right 0;
    z-index 6
    .loginUIbtn
        background-color coral
        width  15px
        height 15px
        position absolute
        top 20px
        left 20px
        z-index  5
    #login .login-page
      margin-top 0;
</style>
