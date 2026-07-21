<template>

        <el-table
                :data="data.players"
                style="width: 100%"
                :row-class-name="tableRowClassName"
        >
            <el-table-column prop="pid" label="Date" width="180" />
            <el-table-column prop="pname" label="Name" width="180" />
            <el-table-column prop="address" label="Address" />
        </el-table>


</template>

<script lang="ts">
    import { players_page } from "@/api/player" //数据
    import { reactive,onMounted } from 'vue'
    export default {
        name: "student",
        setup(){
            const data = reactive({ players: []})

            const get_data = (pageInfo) =>{
                players_page(pageInfo).then((dto:any)=>{//then 中间的小括号包含的内容是 参数，但是参数是由回调函数的返回值组成，所以需要执行回调的内容
                    data.players = dto.obj.records;
                })
            }
            onMounted (()=>{
                get_data({"current":"1","size":"3"})
            })
                return{get_data,data}
        }
    }
</script>

<style scoped>
    .el-table .warning-row {
        --el-table-tr-bg-color: var(--el-color-warning-light-9);
    }
    .el-table .success-row {
        --el-table-tr-bg-color: var(--el-color-success-light-9);
    }
</style>