/**
 * 前端路由配置模块
 * 定义应用所有路由、路由元信息（meta）及全局导航守卫，实现权限控制与角色校验。
 */
import { createRouter, createWebHistory } from 'vue-router'
import {getUserInfo} from "../api/loginAndUser";

// 公共路由配置
const constantRoutes = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/loginAndUser',
            name: 'login',
            component: () => import('../views/login.vue'),
            meta: { public: true } // 无需登录即可访问
        },
        {
            path: '/register',
            name: 'register',
            component: () => import('../views/register.vue'),
            meta: { public: true } // 无需登录即可访问
        },
        {
            path: '/',
            name: '',
            component: () => import('../views/Home.vue'),
            children: [
                {
                    path:'/',
                    name:'storeHome',
                    component: () => import('../components/Store/StoreHome.vue'),
                    meta: { public: true } // 商城首页，公开访问
                },
                {
                    path:'/memberManage',
                    name:'memberManage',
                    component: () => import('../components/admin/MemberManagement.vue'),
                    meta: { requiresAuth: true, role: "2" } // 管理员角色（2）可访问
                },
                {
                    path:'/productManage',
                    name:'productManage',
                    component: () => import('../components/admin/ProductManagement.vue'),
                    meta: { requiresAuth: true, role: "2" } // 管理员角色（2）可访问
                },
                {
                    path:'/shopManage',
                    name:'shopManage',
                    component: () => import('../components/admin/ShopManagement.vue'),
                    meta: { requiresAuth: true, role: "2" } // 管理员角色（2）可访问
                },
                {
                    path:'/personalCenter',
                    name:'personalCenter',
                    component: () => import('../components/PersonalCenter/PersonalCenter.vue'),
                    meta: { requiresAuth: true} // 登录后可访问
                },
                {
                    path:'/AddressList',
                    name:'AddressList',
                    component: () => import('../components/PersonalCenter/AddressList.vue'),
                    meta: { requiresAuth: true, role: "0" } // 普通用户角色（0）可访问
                },
                {
                    path: '/merchant',
                    name: 'MerchantDashboard',
                    component: () => import('../components/shopManagement/MerchantDashboard.vue'),
                    meta: { requiresAuth: true, role: "1" } // 商家角色（1）可访问
                },
                {
                    path: '/product',
                    name: 'Product',
                    component: () => import('../components/Store/Product.vue'),
                    meta: { public: true } // 商品详情页，公开访问
                },
                {
                    path: '/cart',
                    name: 'Cart',
                    component: () => import('../components/Store/shopCart.vue'),
                    meta: { requiresAuth: true, role: "0" } // 普通用户角色（0）可访问
                },
                {
                    path: '/customerOrder',
                    name: 'customerOrder',
                    component: () => import('../components/order/customerOrder.vue'),
                    meta: { requiresAuth: true, role: "0" } // 普通用户角色（0）可访问
                },
                {
                    path: '/ai',
                    name: 'ai',
                    component: () => import('../components/ai/ai.vue'),
                    meta: { requiresAuth: true,} // 登录后可访问
                },
                {
                    path: '/petShopAddress',
                    name: 'petShopAddress',
                    component: () => import('../components/location/pet.vue'),
                    meta: { public: true } // 宠物店地址页，公开访问
                },
                {
                    path: '/shop',
                    name: 'shop',
                    component: () => import('../components/Store/Shop.vue'),
                    meta: { public: true } // 店铺页，公开访问
                }
            ]
        },
        {
            path: '/404',
            name: '404',
            component: () => import('../components/404.vue'),
            meta: { public: true } // 404 页面，公开访问
        }
    ]
})

/**
 * 全局前置导航守卫
 * 根据路由 meta 中的 public、requiresAuth、role 字段进行权限与角色校验。
 *
 * @param {import('vue-router').RouteLocationNormalized} to - 目标路由对象
 * @param {import('vue-router').RouteLocationNormalized} from - 来源路由对象
 * @param {import('vue-router').NavigationGuardNext} next - 控制导航行为的回调函数
 * @returns {Promise<void>}
 */
 constantRoutes.beforeEach(async (to, from, next) => {
    // 获取 session 中保存的 token，用于判断用户登录状态
    const token = sessionStorage.getItem('token')

    // 如果路由标记为公开，直接放行
    if (to.meta.public) {
        return next()
    }

    // 如果路由指定了角色，需要校验用户角色是否匹配
    if (to.meta.role !== undefined) {
        if (!token) {
            // 没有 token 时重定向到商城首页
            return next({ name: 'storeHome' })
        }
        try {
            const user = (await getUserInfo()).data;
            if (user.userType === to.meta.role) {
                next(); // 角色匹配，允许进入
            } else {
                next({ name: 'storeHome' }); // 角色不符，重定向到首页
            }
        } catch (error) {
            console.error("获取用户信息失败:", error);
            next({ name: 'storeHome' }); // 获取用户信息失败，重定向到首页
        }
    }


    // 对于其他需要认证但未指定角色的路由
    if (!token) {
        // 没有 token 且不是公开路由时，仅允许访问商品详情页
        if (to.name === 'Product') {
            return next()
        }
        // 否则重定向到登录页
        return next({ name: 'login' })
    }

    // 所有校验通过，放行
    next()
})

export default constantRoutes
