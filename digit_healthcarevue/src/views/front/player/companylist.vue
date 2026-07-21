<template>
    <!--&lt;!&ndash; 顶层容器，用于显示公司及其职位信息 &ndash;&gt;-->
    <!--<div id="jobInfo">-->
        <!--&lt;!&ndash; 使用 v-for 渲染公司列表 &ndash;&gt;-->
        <!--<dl class="companys" v-for="(company, index) in data.companys" :key="index">-->
            <!--<dt>-->
                <!--&lt;!&ndash; 公司名称和分隔线 &ndash;&gt;-->
                <!--<div class="company">-->
                    <!--<span>●&nbsp;&nbsp;&nbsp;{{ company.companyName }}</span>-->
                    <!--<span class="jobmore"> <router-link :to="{ name: 'companyInfo', params: { companyAccount: company.companyAccount } }" tag="div">查看职位详情</router-link></span>-->
                    <!--<hr/>-->
                <!--</div>-->
            <!--</dt>-->
            <!--<dd v-for="(job, jobIndex) in company.jobs" :key="jobIndex">-->
                <!--&lt;!&ndash; 每个职位的信息显示 &ndash;&gt;-->
                <!--<div class="job">-->
                    <!--<ul>-->
                        <!--<li>{{ job.career }}</li>-->
                        <!--<li>{{ job.salary }}</li>-->
                        <!--<li>{{ job.employnumber }}</li>-->
                    <!--</ul>-->
                <!--</div>-->
            <!--</dd>-->
        <!--</dl>-->
    <!--</div>-->
</template>

<script lang="ts">
    // import { ref, onMounted, reactive } from "vue";
    // import { companyswithjob_page } from "@/api/company";
    //
    // export default {
    //     setup() {
    //         // 响应式数据，用于存储从后端获取的公司及职位信息
    //         const data = reactive({ companys: [] });
    //
    //         // 获取公司及职位信息的方法
    //         const get_companyswithjobs = (pageInfo: any) => {
    //             companyswithjob_page(pageInfo).then((dto: any) => {
    //                 // 将后端返回的公司及职位信息存储到 data.companys 中
    //                 data.companys = dto.tList;
    //             });
    //         };
    //
    //         // 组件挂载时触发获取数据的方法
    //         onMounted(() => {
    //             get_companyswithjobs({ pageNo: 1, pageSize: 3 });
    //         });
    //
    //         return {
    //             // 将 data 对象暴露给模板，用于渲染页面
    //             data,
    //         };
    //     },
    // };
</script>

<style lang="stylus" scoped>
#jobInfo {
  margin: 0 auto;
  width: 1280px;
  padding: 20px;
  background-color: #f9f9f9;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.companys {
  margin-bottom: 20px;
  &:after {
    content: '';
    display: block;
    clear: both;
  }
}

.company {
  text-align: left;
  padding: 20px;
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
  span {
    color: #333;
    font-family: "微软雅黑";
    font-size: 24px;
    font-weight: 300;
  a{
      color: #4fa7ff
  }
  }
  hr {
    margin: 10px 0;
  }
}

.job {
  float: left;
  width: 30%;
  padding: 10px;
  margin: 10px;
  background-color: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
  ul {
    margin: 0;
    padding: 0;
    list-style: none;
  }
  li {
    margin-bottom: 5px;
    color: #555;
  }
}

router-link {
  display: block;
  padding: 10px;
  text-align: center;
  color: #58aeff;
  background-color: #f0f0f0;
  border-radius: 4px;
  transition: background-color 0.3s ease;
  &:hover {
    background-color: #e0e0e0;
  }
}
</style>
