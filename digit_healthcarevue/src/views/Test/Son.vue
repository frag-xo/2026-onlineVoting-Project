<template>
    <div>
        <el-dialog title="新增用戶"  v-model="dialogVisble" :before-close="handleClose" :close-on-click-modal="false">

            <el-form :model="teacher" ref="addTeacherForm">
                <el-form-item  label="教师编号：" label-width="100px" prop="sno">
                    <el-col :span="16">
                        <el-input  placeholder="请填入学号"/>
                    </el-col>
                </el-form-item>
                <el-form-item  label="姓名：" label-width="100px" prop="name">
                    <el-col :span="16">
                        <el-input placeholder="请填入姓名"/>
                    </el-col>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>

<script lang="ts">
    import { watch,ref } from 'vue'
 export default {
     props: {
         visible: {
             type: Boolean,
             default: false
         }
     },
     setup(props,ctx){
         const dialogVisble = ref(false)
         const handleClose = () => {
             console.log('close')
             ctx.emit("update:visible", false);
         };
         watch(() => props.visible, (val) => {
             console.log(props.visible, val);
             dialogVisble.value = val
         });
         return {
             dialogVisble,handleClose
         }
     },

 }
</script>

<style scoped>
</style>
