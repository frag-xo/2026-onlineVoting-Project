<template>
    <el-container>
    <div class="main">
        <div class="top">1111111111111111111111</div>
        <div class="content">
            <div class="left_menu">
                <router-link :to="{ name: 'student', params: {} }">学生</router-link>
                <router-link :to="{ name: 'teacher', params: { } }">教师</router-link>
            </div>
            <div class="right_content">
                <router-view></router-view>
            </div>
        </div>
    </div>
    </el-container>
</template>
<script lang="ts">

</script>
<style>
    .main{marign:0 auto;width:1000px;border: solid 1px;}
    .main > .content {width:1000px;}
    .main > .content > div{float:left;}
    .left_menu{width:200px;height:300px;border: solid 1px;}
    .right_content{width:796px;height:300px;border: solid 1px;}
    .main:after{clear:both;content: '';display: block;}
</style>