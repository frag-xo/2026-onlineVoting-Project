import { createRouter, createWebHistory } from 'vue-router'
import backIndex from '@/views/back/index'
import frontIndex from '@/views/front/index/index'
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  mode: 'history',
  routes: [

    {
      path: '/back/index',
      name: 'backIndex',
      component: () =>
          //路由懒加载
          import(/* webpackChunkName: "AdminView" */ "../views/back/index.vue"),

      children:[

     


        {
          path: '/back/banner/view',
          name: 'bannerIndex',
          component: () =>
              //路由懒加载
              import(/* BuildingName: "BannerView" */ "@/views/back/banner/index.vue"),
          meta: {title: '广告管理'}
        },
        {
          path: '/back/drug/view',
          name: 'drugIndex',
          component: () =>
              //路由懒加载
              import(/* BuildingName: "DrugView" */ "@/views/back/drug/index.vue"),
          meta: {title: '药品管理'}
        },

      ]
    },


    {
      path: '/front/index',
      name: 'frontIndex',
      component: () => import('@/views/front/index/index.vue'),
      children:[
        {
          path: '/front/content',
          name: 'frontcontent',
          component: () => import('@/views/front/index/content.vue')
        },
        // {
        //   path: '/front/player',
        //   name: 'frontplayer',
        //   component: () => import('@/views/front/player/playerinfo.vue')
        // },
        // {
        //   path: '/front/team/favteams',
        //   name: 'favteams',
        //   component: () =>
        //       //路由懒加载
        //       import(/* webpackChunkName: "AdminView" */ "@/views/front/team/favteams.vue"),
        //   meta: {title: '联系我们'}
        // },
        {
          path: '/front/info/abouts',
          name: 'aboutus',
          component: () =>
              //路由懒加载
              import(/* webpackChunkName: "AdminView" */ "@/views/front/info/about_us.vue"),
          meta: {title: '关于我们'}
        },
        {
          path: '/front/info/connect_us',
          name: 'connectus',
          component: () =>
              //路由懒加载
              import(/* webpackChunkName: "AdminView" */ "@/views/front/info/connect_us.vue"),
          meta: {title: '联系我们'}
        },
        {
          path: '/front/news/index',
          name: 'newsindex',
          component: () =>
              //路由懒加载
              import(/* webpackChunkName: "AdminView" */ "@/views/front/news/index.vue"),
          meta: {title: '重磅新闻'}
        },


      ]
    },
//     {
//       path: '/clientInfo',
//       name: 'backIndex',
//       component: () =>
//           //路由懒加载
//           import(/* webpackChunkName: "AdminView" */ "../views/front/client/clientInfo.vue"),
//     }
  ]
});



export default router
