<template>
    <div class="leaguesContent">
        <div class="leaguesTabPane">
            <div class="leaguesTab" v-for="(league,index) in leagues" :key="index"  @click="tab(index)">
                <img :src="getImgUrl(league.ico)">
                <!-- 着重理解v-bind:class是接受了一个对象，'active'是类名，第二个参数可以是一个表达式或者一个数据属性为boolean类型的值，如果为true则绑定class -->
                <span class="tabSpan" :class="{active:num==index}">
        ●&nbsp;&nbsp;&nbsp;{{league.name}}
      </span>
            </div>
        </div>
        <div id="teamInfo"><!-- 必须写div 否则 dl元素成为顶层元素 无法渲染 -->
            <dl class="leagues" v-for="(league,index) in leagues" :key="index" v-show="num==index">
                <dd v-for="(team,teamIndex) in league.teams" :key="teamIndex">
                    <a :href="'/#/teamInfo?teamId='+team.teamId">
                        <div class="team">
                            <div>
                                <img :src="getImgUrl(team.pic)" :style="postion">
                            </div>
                            <div>{{team.name}}</div>
                        </div>
                    </a>
                </dd>
            </dl>
        </div>
    </div>
</template>

<script>
    import api from '../../../axios/api.js'
    export default {
        name: 'team',
        data () {
            return {
                num:0,
                leagues:[]
            }
        },
        created() {//组件加载时候 调用
            this.setDataFromAxios();
        },
        methods:{
            getImgUrl:function(ico){
                return require("../../../static/images/league/"+ico);
            },
            setDataFromAxios: function() {
                api.setDataFromAxios('/team', null,'get')//自行封装的axios方法 post get参数需要与mock.js中 参数匹配
                    .then(res => {
                        console.log(res);
                        this.leagues = res.leagues;
                    });
            },
            tab:function(index) {
                this.num = index;
            }
        }
    }
</script>

<style lang="stylus">
    .tabSpan
        color #042122
        font-family "微软雅黑"
        font-size 20px
        font-weight 300;
        display block;
        width 80px
        height 80px
        line-height 80px
        position absolute
        right 20px
    .active
        color  coral
    .leaguesContent
        width 1280px
        margin 0 auto
    .leaguesTabPane
        width 1280px
        height 80px
        .leaguesTab
            width 180px
            height 80px
            float:left
            position relative
            padding 0 10px
            margin-left 10px
            img
                width 80px
                height 80px
                position absolute
                left 10px;
    .league
        width 1280px
        text-align left
        height 80px
        line-height 80px
        position relative


    #teamInfo
        margin 0 auto
        width 1280px

    .leagues
        width 1280px
        height auto
        &:after
            content ''
            display block
            clear both




    dd
        margin 0px
    .team
        float left
        width 213px
        height 180px

        img
            width 100px
            height 100px

        div
            font-size 20px
            padding-top 20px
</style>
