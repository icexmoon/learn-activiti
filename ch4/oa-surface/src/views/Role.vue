<template>
    <div class="role-container">
        <el-card class="list-card">
            <template #header>
                <div class="card-header">
                    <span>角色列表</span>
                    <el-button type="primary" @click="handleAdd">新增角色</el-button>
                </div>
            </template>

            <el-table :data="tableData" style="width: 100%">
                <el-table-column prop="id" label="ID" width="80" />
                <el-table-column prop="key" label="角色标识" />
                <el-table-column prop="name" label="角色名称" />
                <el-table-column label="操作" width="200">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
                        <el-button type="primary" link @click="handleSetPermission(row)">权限设置</el-button>
                        <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 添加/编辑角色对话框 -->
        <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增角色' : '编辑角色'" width="30%">
            <el-form :model="roleForm" label-width="100px">
                <el-form-item label="角色标识">
                    <el-input v-model="roleForm.key" placeholder="请输入角色标识" />
                </el-form-item>
                <el-form-item label="角色名称">
                    <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitForm">确定</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 设置菜单权限对话框 -->
        <el-dialog v-model="permissionDialogVisible" title="设置菜单权限" width="50%">
            <div class="permission-container">
                <el-tree ref="menuTree" :data="menuTreeData" :props="defaultProps" show-checkbox node-key="id"
                    @check="handleMenuCheck">
                    <template #default="{ node, data }">
                        <div class="custom-tree-node">
                            <span>{{ node.label }}</span>
                            <div class="permission-actions" v-if="data.id">
                                <el-checkbox v-model="data.permissions.view"
                                    @change="handlePermissionChange(data)">查看</el-checkbox>
                                <el-checkbox v-model="data.permissions.add"
                                    @change="handlePermissionChange(data)">新增</el-checkbox>
                                <el-checkbox v-model="data.permissions.edit"
                                    @change="handlePermissionChange(data)">编辑</el-checkbox>
                                <el-checkbox v-model="data.permissions.delete"
                                    @change="handlePermissionChange(data)">删除</el-checkbox>
                            </div>
                        </div>
                    </template>
                </el-tree>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="permissionDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitPermissions">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import request from '@/util/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogType = ref('add')
const roleForm = ref({
    id: null,
    key: '',
    name: ''
})

// 菜单权限相关
const permissionDialogVisible = ref(false)
const menuTreeData = ref([])
const checkedMenuIds = ref([])
const currentRole = ref(null)
const defaultProps = {
    children: 'children',
    label: 'name'
}
const menuTree = ref(null)

// 获取角色列表数据
const fetchRoleList = async () => {
    try {
        const response = await request.get('/api/role/list')
        if (response.success) {
            tableData.value = response.data
        }
    } catch (error) {
        console.error('获取角色列表失败', error)
        ElMessage.error('获取角色列表失败')
    }
}

// 添加角色
const handleAdd = () => {
    dialogType.value = 'add'
    roleForm.value = {
        id: null,
        key: '',
        name: ''
    }
    dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row) => {
    dialogType.value = 'edit'
    roleForm.value = {
        id: row.id,
        key: row.key,
        name: row.name
    }
    dialogVisible.value = true
}

// 删除角色
const handleDelete = (row) => {
    ElMessageBox.confirm(
        '确定要删除该角色吗？删除后不可恢复！',
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            const response = await request.delete(`/api/role/del/${row.id}`)
            if (response.success) {
                ElMessage.success('删除成功')
                fetchRoleList()
            } else {
                ElMessage.error(response.message || '删除失败')
            }
        } catch (error) {
            console.error('删除角色失败', error)
            ElMessage.error('删除失败')
        }
    }).catch(() => {
        // 用户取消删除操作
    })
}

// 提交表单
const submitForm = async () => {
    try {
        const url = dialogType.value === 'add' ? '/api/role/add' : '/api/role/edit'
        const response = await request.post(url, roleForm.value)
        if (response.success) {
            ElMessage.success(dialogType.value === 'add' ? '添加成功' : '更新成功')
            dialogVisible.value = false
            fetchRoleList()
        } else {
            ElMessage.error(response.message || (dialogType.value === 'add' ? '添加失败' : '更新失败'))
        }
    } catch (error) {
        console.error(dialogType.value === 'add' ? '添加角色失败' : '更新角色失败', error)
        ElMessage.error(dialogType.value === 'add' ? '添加失败' : '更新失败')
    }
}

// 获取菜单树数据
const fetchMenuTree = async () => {
    try {
        const response = await request.get('/api/menu/tree')
        if (response.success) {
            menuTreeData.value = [response.data]
            // 初始化权限数据
            initMenuPermissions(menuTreeData.value)
        }
    } catch (error) {
        console.error('获取菜单树失败', error)
        ElMessage.error('获取菜单树失败')
    }
}

// 初始化菜单权限数据
const initMenuPermissions = (nodes) => {
    nodes.forEach(node => {
        if (node.id) {
            node.permissions = {
                view: false,
                add: false,
                edit: false,
                delete: false
            }
        }
        if (node.children) {
            initMenuPermissions(node.children)
        }
    })
}

// 处理设置权限按钮点击
const handleSetPermission = async (row) => {
    currentRole.value = row
    checkedMenuIds.value = []

    // 重新初始化菜单权限
    initMenuPermissions(menuTreeData.value)

    // 如果有现有权限，设置选中状态
    //   console.log(row.menuPermissions)
    if (row.menuPermissions) {
        row.menuPermissions.forEach(permission => {
            checkedMenuIds.value.push(permission.menuId)
            updateMenuPermissions(menuTreeData.value, permission)
        })
    }

    permissionDialogVisible.value = true
    // 等待DOM更新后设置选中状态
    await nextTick()
    const menuIds = [...checkedMenuIds.value]
    menuTree.value?.setCheckedKeys(menuIds, false)
}

// 更新菜单权限
const updateMenuPermissions = (nodes, permission) => {
    nodes.forEach(node => {
        if (node.id === permission.menuId) {
            node.permissions = {
                view: permission.view,
                add: permission.add,
                edit: permission.edit,
                delete: permission.delete
            }
        }
        if (node.children) {
            updateMenuPermissions(node.children, permission)
        }
    })
}

// 处理菜单选中状态变化
const handleMenuCheck = (data, checked) => {
    // console.log('here')
    // console.log(data)
    // console.log(checked)
    if (checked.checkedKeys) {
        // 更新子节点的权限状态
        updateChildrenPermissions(data, checked.checkedKeys.includes(data.id))
    }
}

// 更新子节点权限状态
const updateChildrenPermissions = (node, checked) => {
    if (node.children) {
        node.children.forEach(child => {
            if (child.id) {
                child.permissions = {
                    view: checked,
                    add: checked,
                    edit: checked,
                    delete: checked
                }
            }
            updateChildrenPermissions(child, checked)
        })
    }
}

// 处理权限变化
const handlePermissionChange = (data) => {
    // 如果四种权限都设置为 false，取消当前菜单节点的选中
    if (!data.permissions.view && !data.permissions.add && !data.permissions.edit && !data.permissions.delete) {
        checkedMenuIds.value = checkedMenuIds.value.filter(id => id !== data.id)
    } else {
        // 如果当前节点不在选中列表中，添加它
        if (!checkedMenuIds.value.includes(data.id)) {
            checkedMenuIds.value = [...checkedMenuIds.value, data.id]
        }
    }
    menuTree.value?.setCheckedKeys(checkedMenuIds.value)
}

// 收集选中的菜单权限
const collectMenuPermissions = (nodes) => {
    const permissions = []
    const collect = (nodeList) => {
        nodeList.forEach(node => {
            if (node.id && node.permissions) {
                if (node.permissions.view
                    || node.permissions.add
                    || node.permissions.edit
                    || node.permissions.delete) {
                    permissions.push({
                        menuId: node.id,
                        view: node.permissions.view,
                        add: node.permissions.add,
                        edit: node.permissions.edit,
                        delete: node.permissions.delete
                    })
                }
            }
            if (node.children) {
                collect(node.children)
            }
        })
    }
    collect(nodes)
    return permissions
}

// 提交权限设置
const submitPermissions = async () => {
    try {
        const menuPermissions = collectMenuPermissions(menuTreeData.value)
        const response = await request.post('/api/role/menu/permit', {
            roleId: currentRole.value.id,
            menuPermissions
        })

        if (response.success) {
            ElMessage.success('权限设置成功')
            permissionDialogVisible.value = false
            // 刷新角色列表
            fetchRoleList()
        } else {
            ElMessage.error(response.message || '权限设置失败')
        }
    } catch (error) {
        console.error('设置权限失败', error)
        ElMessage.error('设置权限失败')
    }
}

// 组件挂载时获取数据
onMounted(() => {
    fetchRoleList()
    fetchMenuTree()
})
</script>

<style scoped>
.role-container {
    padding: 20px;
}

.list-card {
    width: 100%;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.permission-container {
    max-height: 500px;
    overflow-y: auto;
}

.custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
}

.permission-actions {
    display: flex;
    gap: 10px;
}

.permission-actions .el-checkbox {
    margin-right: 0;
}
</style>