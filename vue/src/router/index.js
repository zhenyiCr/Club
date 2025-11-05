import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {   path: '/', redirect: '/manager/home'},
        {
            path: '/manager', component:() => import('../views/Manager.vue'),
            children: [
                {path: 'home',meta: {name: '主页'}, component:() => import('../views/Home.vue')},
                {path: 'student',meta: {name: '学生信息'}, component:() => import('../views/Student.vue')},
                {path: 'admin',meta: {name: '管理员信息'}, component:() => import('../views/Admin.vue')},
                {path: 'person',meta: {name: '个人中心'}, component:() => import('../views/Person.vue')},
                {path: 'upadatePassward',meta: {name: '修改密码'}, component:() => import('../views/UpadatePassward.vue')},
                {path: 'notice',meta: {name: '系统公告'}, component:() => import('../views/Notice.vue')},
                {path: 'introduction',meta: {name: '文本'}, component:() => import('../views/Introduction.vue')},
                {path: 'category',meta: {name: '分类'}, component:() => import('../views/Category.vue')},
                {path: 'club',meta: {name: '社团'}, component:() => import('../views/Club.vue')},
            ]
        },
        {path: '/login', component: import('../views/Login.vue')},
        {path: '/register', component: import('../views/Register.vue')},


    ],
})

export default router
