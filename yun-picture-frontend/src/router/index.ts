import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/HomePage.vue'
import UserLogin from '../pages/user/UserLogin.vue'
import UserRegister from '../pages/user/UserRegister.vue'
import UserAuth from '../pages/user/UserAuth.vue'
import UserManage from '../pages/admin/UserManage.vue'
import AddPicture from '../components/picture/AddPicture.vue'
import BackGround from '../pages/background/BackGround.vue'
import PictureManage from '../pages/admin/PictureManage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: '主页',
      component: HomePage,
    },
    {
      path: '/user/UserAuth',
      name: '用户选择',
      component: UserAuth,
    },
    {
      path: '/user/login',
      name: '用户登录',
      component: UserLogin,
    },
    {
      path: '/user/register',
      name: '用户注册',
      component: UserRegister,
    },
    {
      path: '/admin/userManage',
      name: '用户管理',
      component: UserManage,
    },
    {
      path: '/admin/pictureManage',
      name: '图片管理',
      component: PictureManage,
    },
    {
      path: '/picture/upload',
      name: '上传图片',
      component: AddPicture,
    },
    {
      path: '/background',
      name: '切换背景',
      component: BackGround,
    },
  ],
})

export default router
