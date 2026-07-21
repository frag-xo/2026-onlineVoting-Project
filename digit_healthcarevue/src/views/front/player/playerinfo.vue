<template>
  <div class="playerInfo">
    <!-- 公司信息头部 -->
    <div class="playerInfohead">
      <div>{{ player.pname }}</div>
    </div>
    <!-- 公司详细信息 -->
    <div class="playerInfoDeatail">
      <div><span>所在区域：</span><span>{{ player.costVal }}</span><span>效力球队:</span><span>{{ player.team.tname }}</span></div>
      <div><span>出生日期：</span><span>{{ myformatDate("player.pbirthday") }}</span></div>
      <div><span>位置：</span><span>{{ player.position1 }}</span></div>
      <div><span>可选位置：</span><span>{{ player.position2 }}</span></div>

    </div>
    <!-- 公司Logo -->
    <div class="playerLogo">
      <img :src="getUploadUrl('player',player.pheadImg)" alt="Player Logo" style="width: 158px; height: 158px;">
    </div>


  </div>
  <div class="main_content">


  <!-- 热门职位信息 -->
  <div class="teamsContent all_content">
    <div class="teamInfoTitle all_title">
      <h3>效力过球队</h3>
      <hr>
    </div>
    <table class="teams_tab">
      <tr>
        <th>编号</th>
        <th>球队队徽</th>
        <th>球队名称</th>
        <th>国家</th>
        <th>等级</th>

      </tr>
      <tr v-for="(team, index) in player.teams" :key="index">
        <td>{{index}}</td>
        <td class="team_img"><img :src="getUploadUrl('team',team.timg)"></td>
        <td>{{team.tname}}</td>
        <td>{{team.tnational}}</td>
        <td>{{team.tlevel}}</td>
      </tr>
    </table>
  </div>
    <div class="employmentContent all_content">
      <div class="teamInfoTitle all_title">
        <h3>效力記錄</h3>
        <hr>
      </div>
      <table class="employment_tab">
        <tr>
          <th>编号</th>
          <th>球队名称</th>
          <th>球队队徽</th>
          <th>入队日期</th>
          <th>离队日期</th>
          <th>总进球数</th>
          <th>射门成功率</th>

        </tr>
        <tr v-for="(employmentrecord, index) in player.employmentrecords" :key="index">
          <td>{{index}}</td>
          <td>{{employmentrecord.team.tname}}</td>
          <td class="team_img"><img :src="getUploadUrl('team',employmentrecord.team.timg)"></td>
          <td>{{myformatDate(employmentrecord.erStartDate)}}</td>
          <td>{{myformatDate(employmentrecord.erEnDate)}}</td>
          <td>{{employmentrecord.totalGoals}}</td>
        </tr>
      </table>
    </div>
  </div>
</template>

<script lang="ts">
  import { ref, onMounted,onUnmounted,reactive,watch} from "vue";
  import { player_teams_er } from "@/api/player";
  import { useRoute } from 'vue-router';
  import { formatToDate } from "@/utils/dateUtil"



  export default {
    setup() {
      const player = ref({team:Object,teams:[],employmentrecords:[]}); // 使用 ref 定义 player

      const  route = useRoute(); // 获取路由实例
      const  myformatDate = (row, column, cellValue)=> {
        return formatToDate(cellValue);
      };
      // 获取指定公司详细信息的函数
      const get_playerDetail = (pid: string) => {
        player_teams_er(pid)
                .then((dto: any) => {
                  console.log(dto)
                  player.value = dto.t; // 将返回的公司数据赋值给 player
                })

      };

      const pid = ref()
      onMounted(() => {
        if (route.query.pid){
          pid.value = route.query.pid; // 获取路由参数中的球员编号
        }

        if (pid) {
          // 初始化时获取公司信息，使用动态的公司账号
         get_playerDetail(route.query.pid);
       } else {
          console.error('No player pid found in route parameters.');
       }
      });

      const getUploadUrl=(model,imgsrc)=>{
        return      "http://localhost:5173/target/upload/"+model+"/"+imgsrc;
      }

      // 当参数更改时获取用户信息
      watch(
              () => route.query.pid,
              async newId => {
                player.value = await get_playerDetail(newId)
              }
      );


      return {
        player,myformatDate,watch,getUploadUrl
      };
    },
  };
</script>



<style lang="stylus">
  .playerInfo
    width: 1000px;
    margin: 0 auto;
    position: relative;

  .playerInfohead
    width: 1028px;
    height: 90px;

    div
      font-size: 32px;
      font-family: "微软雅黑";
      color: dodgerblue;

  .playerInfoDeatail
    position: relative;
    div > span:nth-child(1){
      display inline-block
      width 80px
    }
    div > span:nth-child(2){
      display inline-block
      width 100px
    }
    div > span:nth-child(3){
      display inline-block
      width 80px
    }



  .playerLogo
    width: 160px;
    height: 160px;
    position: absolute;
    border: solid 1px;
    right: 20px;
    top: 10px;
    bottom: 20px;
    margin: 5px;
    padding: 0;

  .playerLogo img
    width: 160px;
    height: 160px;
  .main_content
    width 1000px;
    margin 0 auto;
    .all_content
      margin-top:20px;
      position: relative;
      height: auto; /* 将高度设置为自动，以适应内容的变化 */
      .all_title
        position: relative;

        h3
          width: 180px;
          height: 30px;
          background-color: #4fa7ff;
          text-align: center;
          line-height: 30px;
          margin-bottom: 20px;
          color: white;
          border-radius 4px 4px 0 0

        hr
          border: 1px #4fa7ff solid;
          width: 100%;
          position: absolute;
          bottom: 0;
      .teams_tab
        tr .team_img img
          width 60px
          height 60px



      .employment_tab
        tr > td:nth-child(1){  width 80px
        }
        tr > td:nth-child(2){  width 100px
        }
        tr > td:nth-child(3){
          width 120px
          text-align center
        }
        tr > td:nth-child(4){  width 80px
        }
        tr > td:nth-child(5){
          width 80px
          text-align center
        }
        tr > td:nth-child(6){
          width 80px
          text-align center
        }
        tr .team_img img
          width 50px
          height 50px;

    .playerInfodesc
      width: 1280px;
      text-align: justify;
      text-indent: 2em;
      font-size: 20px;
      padding: 30px 0px;

    .employeeInfoDiv
      width: 180px;
      padding: 30px;
      float: left;
      height: auto;






      > .playerByPosition
        float: left;
        margin: 5px 0px 5px 5px;
        padding: 0;
        height: auto;
        width: 248px;

        &:after
          clear: both;
          content: '';

        .playerTitle
          text-align: center;
          background-color: cornflowerblue;
          color: white;
          border-radius: 5px;

        .playerContent
          margin: 0 0 0 20px;

    .employeeInfo
      margin: 0;
      padding: 0;

      > li
        list-style-type: none;
        position: relative;

        > img
          width: 180px;
          height: 280px;

        > span:nth-child(2)
          display: inline-block;
          width: 40px;
          height: 40px;
          line-height: 40px;
          opacity: 0.3;
          color: slategray;
          position: absolute;
          top: 2px;
          left: 2px;
          border-radius: 90px;

        > span:nth-child(3)
          display: inline-block;
          width: 40px;
          height: 40px;
          opacity: 0.3;
          position: absolute;
          top: 0px;
          left: 0px;
          border-radius: 90px;
          border: solid 2px slategray;

    .playerseekerInfo
      width: 1280px;
      height: 300px;
      margin-top: 30px;

      .playerseekerInfoTitle
        position: relative;

        h3
          width: 180px;
          height: 30px;
          background-color: #4fa7ff;
          text-align: center;
          line-height: 30px;
          margin-bottom: 20px;
          color: white;

        hr
          border: 2px #4fa7ff solid;
          width: 92%;
          position: absolute;
          bottom: 0;

      > .playerseekerByPosition
        float: left;
        margin: 5px 0px 5px 5px;
        padding: 0;
        height: auto;
        width: 248px;

        &:after
          clear: both;
          content: '';

        .playerseekerTitle
          text-align: center;
          background-color: cornflowerblue;
          color: white;
          border-radius: 5px;

        .playerseekerContent
          margin: 0;
</style>

