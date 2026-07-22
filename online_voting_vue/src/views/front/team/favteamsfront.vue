<template>
<div class="leaguesContent">
    <div class="leaguesTabPane">
        <div class="leaguesTab" v-for="(league,index) in data.leagues" :key="index"  @click="tab(index)">
            <img :src="getUploadUrl(league.leImg)">
            <!-- 着重理解v-bind:class是接受了一个对象，'active'是类名，第二个参数可以是一个表达式或者一个数据属性为boolean类型的值，如果为true则绑定class -->
            <span class="tabSpan" :class="{active:num==index}">
        ●&nbsp;&nbsp;&nbsp;{{league.leShortname}}
      </span>
        </div>
    </div>
    <div id="teamInfo"><!-- 必须写div 否则 dl元素成为顶层元素 无法渲染 -->
        <dl class="leagues" v-for="(league,index) in data.leagues" :key="index" v-show="num==index">
            <dd v-for="(team,teamIndex) in league.teams" :key="teamIndex">
                <a :href="'/#/teamInfo?teamId='+team.teamId">
                    <div class="team">
                        <div>
                            <img :src="getUploadUrl(team.timg)">
                        </div>
                        <div>{{team.tname}}</div>
                    </div>
                </a>
            </dd>
        </dl>
    </div>
</div>
</template>

<script lang="ts">
    import { ref, onMounted,onUnmounted,reactive,watch} from "vue";
    import { leagues_teams } from "@/api/league";
    import { useRoute } from 'vue-router';
    import { formatToDate } from "@/utils/dateUtil"
    export default {

        setup(){
            const data = reactive({ leagues: []}); //测试请求方法
              const get_leagues_teams = ()=>{
                  leagues_teams()
                      .then((dto: any) => {
                          console.log(dto)
                          data.leagues = dto.tlist; // 将返回的公司数据赋值给 player
                          console.log(data)
                      })

              };
            const getUploadUrl=(imgsrc)=>{
                return      "http://localhost:5173/target/upload/league/"+imgsrc;
            }
            const num = ref(0)
            const tab = (index)=> {
                num.value = index;
            }
            onMounted(()=>{
                get_leagues_teams()
            }) ;
            return {get_leagues_teams,data,getUploadUrl,tab,num}
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
        margin-top 20px
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