<!--assets：在项目编译的过程中会被webpack处理解析为模块依赖，只支持相对路径的形式，如< img src=”./logo.png”>和background:url(./logo.png),”./logo.png”是相对资源路径，将有webpack解析为模块依赖
　static：在这个目录下文件不会被webpack处理,简单就是说存放第三方文件的地方，不会被webpack解析。他会直接被复制到最终的打包目录(默认是dist/static)下。必须使用绝对路径引用这些文件，这是通过config.js文件中的build.assetsPublic和build.assertsSubDirectory链接来确定的。任何放在static/中文件需要以绝对路径的形式引用：/static[filename]
　根据webpack的特性，总的来说就是static放不会变动的，第三档的文件，asserts放可能会变动的文件-->
<template>
 <div>
   <plantlist></plantlist>
   <horticultirlist></horticultirlist>
 </div>
</template>

<script>
import plantlist from "@/views/front/plant/plantlist.vue";
import horticultirlist from "@/views/front/horticultirist/horticultiristlist.vue";

export default {
  data() {
    return {loginFormShow: false}
  },
  name: "myheader",
  components: {
    plantlist,horticultirlist
  }
}
</script>