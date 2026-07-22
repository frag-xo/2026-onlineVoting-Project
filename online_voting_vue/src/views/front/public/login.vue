<template>
  <div class="login-container">
    <el-form :model="ruleForm2" :rules="rules2" status-icon ref="ruleForm2" label-position="left" label-width="0px" class="demo-ruleForm login-page">
      <h3 class="title">系统登录</h3>
      <el-form-item prop="username">
        <el-input type="text" v-model="ruleForm2.username" auto-complete="off" placeholder="用户名"></el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input type="password" v-model="ruleForm2.password" auto-complete="off" placeholder="密码"></el-input>
      </el-form-item>
      <el-checkbox v-model="checked" class="rememberme">记住密码</el-checkbox>
      <el-radio  v-model="ruleForm2.e_level"  label="2">专家</el-radio>
      <el-radio  v-model="ruleForm2.e_level"  label="1">会员</el-radio>
      <el-form-item style="width:100%;">
        <el-button type="primary" style="width:100%;" @click="handleSubmit" :loading="logining">登录</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import Qs from 'qs'
  export default {
    data(){
      return {
        logining: false,
        ruleForm2: {
          username: 'ligang',
          password: '123456',
          e_level: '1'
        },
        rules2: {
          username: [{required: true, message: '请填入账号', trigger: 'blur'}],
          password: [{required: true, message: '请填入密码', trigger: 'blur'}]
        },
        checked: false
      }
    },
    methods: {
      handleSubmit:function(event){
        this.$refs.ruleForm2.validate((valid) => {
          if(valid){
            this.logining = true;
            this.userLogin();
          }else{
            console.log('error submit!');
            return false;
          }
        })
      },
      userLogin:function() {
        let that = this;
       // this.$http.post('https://4f5e7910-d45a-4b80-a0f0-6c73e228e625.mock.pstmn.io/tologin,Qs.stringify({username: that.ruleForm2.username, password: that.ruleForm2.password,e_level:that.ruleForm2.e_level}))
        this.$http.get('https://4f5e7910-d45a-4b80-a0f0-6c73e228e625.mock.pstmn.io/tologin?'+Qs.stringify({username: that.ruleForm2.username, password: that.ruleForm2.password,e_level:that.ruleForm2.e_level}))
          .then(res => {
            that.resData = res.data; // 把返回数据赋值给resData
            // 根据返回数据调整判断表达式
            if (that.resData.code == 200) {
              // 登录成功后,将头像 用户等 存在本地
              localStorage.setItem('localLogin', JSON.stringify(true))
              that.$store.dispatch('modifyLoginState', that.resData.data);//和$store.commit相比 dispatch异步 commit 同步
              if (that.resData.data.e_level == 2) {
                // 跳转到专家
                that.$router.push('/expert')
              } else if (that.resData.data.e_level == 1) {
                // 跳转到会员
                that.$router.push('/member')
              }
            } else {
              alert("登录失败");
              that.dialogVisible = true
            }
          })
      }
    }
  };
</script>

<style scoped>
  .login-container {
    width: 100%;
    height: 100%;
  }
  .login-page {
    -webkit-border-radius: 5px;
    border-radius: 5px;
    margin: 180px auto;
    width: 350px;
    padding: 35px 35px 15px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
  }
  label.el-checkbox.rememberme {
    margin: 0px 0px 15px;
    text-align: left;
  }
</style>
