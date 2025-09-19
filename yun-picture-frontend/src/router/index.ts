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
import WebSocketTest from '../pages/test/WebSocketTest.vue'

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
      meta: { requiresAdmin: true }
    },
    {
      path: '/admin/pictureManage',
      name: '图片管理',
      component: PictureManage,
      meta: { requiresAdmin: true }
    },
    {
      path: '/admin/spaceManage',
      name: '空间管理',
      component: SpaceManage,
      meta: { requiresAdmin: true }
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
      meta: { requiresAdmin: true }
    },
    {
      path: '/test/websocket',
      name: 'WebSocket测试',
      component: WebSocketTest,
    },
  ],
})

// 路由守卫：检查管理员权限
router.beforeEach((to, from, next) => {
  // 检查路由是否需要管理员权限
  if (to.meta.requiresAdmin) {
    // 获取用户信息
    const userInfo = localStorage.getItem('userInfo')
    if (userInfo) {
      try {
        const user = JSON.parse(userInfo)
        // 检查用户角色是否为admin
        if (user.userRole === 'admin') {
          next() // 允许访问
        } else {
          // 非管理员用户重定向到首页
          next('/')
        }
      } catch (error) {
        console.error('解析用户信息失败:', error)
        next('/')
      }
    } else {
      // 未登录用户重定向到认证页面
      next('/user/UserAuth')
    }
  } else {
    next() // 不需要管理员权限的路由直接通过
  }
})

export default router
