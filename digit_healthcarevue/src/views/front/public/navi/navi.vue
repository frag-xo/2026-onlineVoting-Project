<template>
  <div class="navwrap">
    <div class="mynav">
      <dl class="navUl">
        <dd @mouseenter="showSub(title)" @mouseleave="closeSub(title)" class="maindd" v-for="(title,mainindex) in titles"
            :key="mainindex">
          <!--        title=titles-->
          <span v-for="(titleAllNames,index) in title" :key="index">
<!--          <span class='titlespan' v-for="(titlenames,titleindex) in titleAllNames" :key="titleindex">{{titleindex}}-->
          <span class='titlespan' v-for="(titlenames,titleindex) in titleAllNames" :key="titleindex">
            <router-link :to="{ name : titlenames}">{{titleindex}}</router-link>
            <dl class="subdl" v-if="title.show">
          <!-- 巨坑啊-->
          <dd class="subdd" v-for="(titlename,subIndex) in titlenames" :key="subIndex">
            <span class="subtitlespan">{{titlename}}</span>
          </dd>
        </dl>
          </span>
         </span>
        </dd>
      </dl>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'navi',//和别处引用的名字不匹配不影响使用，但浏览器报错
    data() {
      return {
        titles: [
          {'1': {'热门球队' :'favteams'}, show: false},
          // {'eng': '英超联赛', 'ger': '德甲联赛', 'cnh': '中超联赛'}
          {'2': {'热门球员':'favteams'}, show: false},
          // {'change': '重磅转会', 'clubnew': '俱乐部要闻'}
          {'3': {'重磅新闻': 'newsindex'}, show: false},
          // {'shoot': '射手榜', 'assert': '助攻榜'}
          {'4': {'联系我们': 'aboutus'}, show: false},
          {'5': {'关于我们': 'connectus'}, show: false}
        ],
      }
    },
    methods: {

      showSub: function (title) {
        title.show = true;
      },
      closeSub: function (title) {
        title.show = false;
      },
    }
  }
</script>

<!--<style>-->
<!--.mynav >.navUl{padding: 0px;margin: 0px}-->
<!--.mynav >.navUl >li{float: left;}-->
<!--</style>-->

<style lang="stylus">

  .navwrap
    margin-left auto
    margin-right auto
    width 1280px;
    overflow hidden;
  .mynav
    width 1280px;


    .navUl
      padding 0
      margin 0 0 0 -20px;

  .maindd
    list-style-type none
    float left
    width 240px
    height 40px
    margin-left 20px

    color aliceblue
    text-align center

  .titlespan
    width 240px
    height 40px
    line-height  40px
    display block
    background-color #3fa8f4
    .router-link-active {
      text-decoration: none;
      color: white;
    }
  a{
    text-decoration: none;
    color: white;
  }

  .subdl
    margin-top: 8px;

  .subdd
    width 240px
    margin-left 0
    margin-top 2px;

    .subtitlespan
      width 240px
      height 40px
      display block
      background-color #5699a4;
</style>
