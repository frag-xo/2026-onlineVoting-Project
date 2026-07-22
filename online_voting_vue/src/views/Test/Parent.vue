<template>
    <div>
      <el-button type="primary" @click="myClick">Primary</el-button><el-button type="primary" @click="myClick">Primary</el-button>
<button onclick="myClick()">111</button>
        <Son  v-model:visible="isSonShow"></Son>
    </div>
</template>
<script lang="ts">
    import Son from './Son.vue'
    import { ref,watch } from 'vue'
export default {
    components:{Son},
setup(){
    //vue3 认为都是变量 方法名是返回值的句柄，方法其实是匿名函数
    const isSonShow = ref(false);//ref放入vue生命周期 默认为false

    const  myClick=()=>{
        isSonShow.value = true //点击修改为true
    };

    watch(() => isSonShow.value , (val) => {
        console.log("监听flag值得变化:", val)
    })
    return{myClick,isSonShow}
}



}



</script>
<style lang="stylus">

</style>