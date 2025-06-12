import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'
import Employee from '../views/Employee.vue'
import Department from '../views/Department.vue'
import Menu from '../views/Menu.vue'
import Welcome from '../views/Welcome.vue'
import Apply from '../views/Apply.vue'
import Todo from '../views/Todo.vue'
import Perm from '../views/Perm.vue'
import Developing from '../views/Developing.vue'
import Interface from '../views/Interface.vue'
import Role from '../views/Role.vue'
import ProcessManagement from '../views/ProcessManagement.vue'
import ApplyProcessManagement from '../views/ApplyProcessManagement.vue'
import ApplyList from '../views/ApplyList.vue'
import BusinessTravelApplyVue from '@/views/BusinessTravelApply.vue'
import ApplyFormVue from '@/views/ApplyForm.vue'
import FormManagementVue from '@/views/FormManagement.vue'
import MyApply from '../views/MyApply.vue'

const routes = [
  { path: '/login', component: Login },
  {
    path: '/',
    component: Layout,
    children: [
      { path: '', component: Welcome },
      { path: 'employee', component: Employee },
      { path: 'department', component: Department },
      { path: 'menu', component: Menu },
      { path: 'apply', component: Apply },
      { path: 'apply/list', component: ApplyList },  // 新增的申请列表页面路由
      { path: 'todo', component: Todo },
      { path: 'perm', component: Perm },
      { path: 'interface', component: Interface },
      { path: 'developing', component: Developing },
      { path: 'role', component: Role },
      { path: 'permission', component: Perm },
      { path: 'process', component: ProcessManagement },
      { path: 'applyProcessManagement', component: ApplyProcessManagement },
      { path: 'businessTravelApply', component: BusinessTravelApplyVue },
      { path: 'applyForm/:applyProcessId', component: ApplyFormVue },
      { path: 'formManagement', component: FormManagementVue },
      {
        path: 'myApply',
        component: MyApply
      },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router 