<template>
    <div class="application-container">
        <el-card>
            <div slot="header">
                <span>申请管理</span>
            </div>

            <el-tabs v-model="activeTab">
                <el-tab-pane label="待审核" name="pending">
                    <el-table :data="pendingApplications" border>
                        <el-table-column prop="studentName" label="申请人" />
                        <el-table-column prop="clubName" label="申请社团" />
                        <el-table-column prop="reason" label="申请理由" />
                        <el-table-column prop="createTime" label="申请时间" />
                        <el-table-column label="操作">
                            <template #default="scope">
                                <el-button type="success" size="small" @click="handleApprove(scope.row)">通过</el-button>
                                <el-button type="danger" size="small" @click="handleReject(scope.row)">拒绝</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>

                <el-tab-pane label="已处理" name="handled">
                    <el-table :data="handledApplications" border>
                        <el-table-column prop="studentName" label="申请人" />
                        <el-table-column prop="clubName" label="申请社团" />
                        <el-table-column prop="status" label="状态" />
                        <el-table-column prop="approverName" label="审核人" />
                        <el-table-column prop="approveTime" label="审核时间" />
                        <el-table-column prop="remark" label="审核备注" />
                    </el-table>
                </el-tab-pane>
            </el-tabs>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const activeTab = reactive('pending')
const pendingApplications = reactive([])
const handledApplications = reactive([])

// 获取待审核申请
const getPendingApplications = () => {
    request.get('/application/pending').then(res => {
        if (res.code === '200') {
            pendingApplications.splice(0, pendingApplications.length, ...res.data)
        } else {
            ElMessage.error(res.msg)
        }
    })
}

// 处理申请
const handleApplication = (application, status, remark = '') => {
    const params = {
        id: application.id,
        status,
        remark
    }
    request.post('/application/handle', params).then(res => {
        if (res.code === '200') {
            ElMessage.success('操作成功')
            getPendingApplications()
            getHandledApplications()
        } else {
            ElMessage.error(res.msg)
        }
    })
}

const handleApprove = (row) => {
    handleApplication(row, 'APPROVED', '申请已通过')
}

const handleReject = (row) => {
    // 可以弹出输入框让管理员填写拒绝理由
    handleApplication(row, 'REJECTED', '申请未通过')
}

// 获取已处理申请
const getHandledApplications = () => {
    request.get('/application/handled').then(res => {
        if (res.code === '200') {
            handledApplications.splice(0, handledApplications.length, ...res.data)
        } else {
            ElMessage.error(res.msg)
        }
    })
}

onMounted(() => {
    getPendingApplications()
    getHandledApplications()
})
</script>
