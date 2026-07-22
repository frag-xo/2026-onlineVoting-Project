<!--assets：在项目编译的过程中会被webpack处理解析为模块依赖，只支持相对路径的形式，如< img src=”./logo.png”>和background:url(./logo.png),”./logo.png”是相对资源路径，将有webpack解析为模块依赖
　static：在这个目录下文件不会被webpack处理,简单就是说存放第三方文件的地方，不会被webpack解析。他会直接被复制到最终的打包目录(默认是dist/static)下。必须使用绝对路径引用这些文件，这是通过config.js文件中的build.assetsPublic和build.assertsSubDirectory链接来确定的。任何放在static/中文件需要以绝对路径的形式引用：/static[filename]
　根据webpack的特性，总的来说就是static放不会变动的，第三档的文件，asserts放可能会变动的文件-->
<template>
    <div id="teamInfo"><!-- 必须写div 否则 dl元素成为顶层元素 无法渲染 -->
        <dl class="leagues" v-for="(league,index) in data.leagues" :key="index">
            <dt>
                <div class="league">
                    <img :src="getUploadUrl(league.leImg)"><span>●&nbsp;&nbsp;&nbsp;{{league.lename}}</span>
                    <hr/>
                </div>
            </dt>
            <dd v-for="(team,teamIndex) in league.teams" :key="teamIndex">
                <div class="team">
                    <div>
                        <img :src="getUploadUrl(team.timg)">
                    </div>
                    <div>{{team.tname}}</div>
                </div>
            </dd>
        </dl>
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
            onMounted(()=>{
                get_leagues_teams()
            }) ;
            return {get_leagues_teams,data,getUploadUrl}
        }
    }
</script>

<style lang="stylus">
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
    .league
        width 1280px
        text-align left
        height 80px
        line-height 80px
        position relative


        span
            color slategray
            font-family "微软雅黑"
            font-size 24px
            font-weight 300;
            display inline-block;
            width 200px
            height 80px
            line-height 80px
            position absolute
            left 90px

        img
            width 80px
            height 80px
            position absolute

        hr
            margin 0
            position absolute
            width 1200px
            bottom 22px
            left 80px

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
