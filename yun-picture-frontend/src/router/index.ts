import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../components/HomePage.vue'
import UserLogin from '../pages/user/UserLogin.vue'
import UserRegister from '../pages/user/UserRegister.vue'
import UserAuth from '../pages/user/UserAuth.vue'
import MySpace from '../components/space/MySpace.vue'
import AddSpace from '../components/space/AddSpace.vue'
import JoinedSpaces from '../components/space/JoinedSpaces.vue'
import SpaceMembers from '../components/space/SpaceMembers.vue'
import AddPicture from '../components/picture/AddPicture.vue'
import CrawlPictures from '../components/picture/CrawlPictures.vue'
import CrawlByUrl from '../components/picture/CrawlByUrl.vue'
import PictureManage from '../pages/admin/PictureManage.vue'
import SpaceManage from '../pages/admin/SpaceManage.vue'
import UserManage from '../pages/admin/UserManage.vue'
import SpaceAnalyzePage from '../pages/analyze/SpaceAnalyzePage.vue'
import BackGround from '../pages/background/BackGround.vue'

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
      path: '/admin/spaceManage',
      name: '空间管理',
      component: SpaceManage,
    },
    {
      path: '/space/add',
      name: '创建空间',
      component: AddSpace,
    },
    {
      path: '/space/my',
      name: '我的空间',
      component: MySpace,
    },
    {
      path: '/joined-spaces',
      name: '我加入的空间',
      component: JoinedSpaces,
    },
    {
      path: '/space/members',
      name: '空间成员',
      component: SpaceMembers,
    },
    {
      path: '/picture/upload',
      name: '上传图片',
      component: AddPicture,
    },
    {
      path: '/picture/crawl',
      name: '批量抓取',
      component: CrawlPictures,
    },
    {
      path: '/picture/search',
      name: '以图搜图',
      component: CrawlByUrl,
    },
    {
      path: '/background',
      name: '切换背景',
      component: BackGround,
    },
    {
      path: '/analyze/space',
      name: '空间分析',
      component: SpaceAnalyzePage,
    },
  ],
})

export default router
