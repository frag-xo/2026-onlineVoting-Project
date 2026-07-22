<template>
    <div class="index">
        <header>
            <myheader></myheader>
        </header>

        <section class="contentsection">
            <mybanner></mybanner>
            <router-view></router-view>
        </section>


        <footer></footer>
    </div>
</template>

<script lang="ts">
import { ref, onMounted, reactive } from "vue";
import myheader from '@/views/front/public/header/myheader.vue';
import mybanner from '@/views/front/public/banner/banner.vue';
import { useRoute,useRouter} from "vue-router";


export default {
    name: 'index',
    components: {
        myheader,
        mybanner,
    },
    setup(){
        var route = useRoute()
        //返回了路由对象
        var router = useRouter()

        onMounted(() => {
            router.push({
                path:"/front/content",
                name:"",
            })
        });
        return{onMounted}
    }

};
</script>

<style scoped lang="stylus">

.search-button {
    display: flex;
    justify-content: center;
    /*margin-top: 620px;*/
    background-color: #b3d7ff;
    padding: 20px;
    overflow: hidden;

    button {
        padding: 12px 24px;
        font-size: 16px;
        background-color: #007bff;
        color: white;
        border: 1px solid #b3d7ff;
        border-radius: 4px;
        transition: background-color 0.3s ease;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        height: 50px;
        text-align: center;
        max-width: 100%;
    }

    button:hover {
        background-color: #0056b3;
    }


}


.contentsection{
    position relative
    margin-top  50px
    width 100%
}

header{
    position relative
}

nav{
    position absolute
    z-index 5
}

</style>
