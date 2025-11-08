<template>
    <div>
        <div class="card">
            <div>
                <el-input clearable @clear="getData"
                          style="margin-right: 10px;width: 260px;margin-bottom: 7px;margin-top: 8px"
                          placeholder="请输入名称"
                          :prefix-icon="Search"
                          v-model="data.name"></el-input>
                <el-button type="primary" @click="getData">查询</el-button>
                <el-button @click="reset">重置</el-button>
            </div>

            <div style="margin-bottom: 5px;margin-top: 5px">
                <el-button @click="headleAdd" type="primary">新增</el-button>
                <el-button @click="deleteBatch" type="danger">批量删除</el-button>
                <el-button @click="exportDate" type="success">批量导出</el-button>
                <el-upload
                        style="display: inline-block;margin-left: 10px"
                        action="http://localhost:8080/admin/import"
                        :show-file-list="false"
                        :on-success="handleImportSuccess"
                >
                    <el-button type="primary">导入</el-button>
                </el-upload>

            </div>
        </div>


        <div>
            <el-table :data="data.tableData" style="width: 100%" @selection-change="handleSelectionChange"
                      :header-cell-style="{fontWeight:'bold',background:'#f5f5f5'}">
                <el-table-column type="selection" width="55"/>
                <el-table-column label="图片" width="120px" size="large">
                    <template #default="scope">
                        <el-image
                            v-if="scope.row.avatar"
                            :src="scope.row.avatar"
                            :preview-src-list="[scope.row.avatar]"
                            :preview-teleported="true"
                            fit="cover"
                            style="width: 40px; height: 40px; border-radius: 25%; display: block"
                        />
                        <!-- 没有头像时显示默认图标或图片 -->
                        <el-avatar v-else icon="User" style="width: 40px; height: 40px; border-radius: 25%"/>
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="姓名"/>
                <el-table-column prop="leaderName" label="姓名"/>
                <el-table-column prop="description" label="内容">
                    <template v-slot="scope">
                        <el-button type="primary" size="mini" @click="viewContent(scope.row.description)">查看</el-button>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="200">
                    <template #default="scope">
                        <el-button @click="handleApplication(scope.row)" type="primary"  v-if="data.user.role === 'STUDENT' ">申请</el-button>
                        <el-button @click="headleEdit(scope.row)" type="primary" icon="edit" v-if="data.user.role==='ADMIN' || data.user.id === scope.row.leaderId "></el-button>
                        <el-button @click="del(scope.row.id)" type="danger" icon="delete" v-if="data.user.role==='ADMIN'"></el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <div style=": margin-top: 10px">
            <el-pagination
                    v-model:current-page="data.pageNum"
                    v-model:page-size="data.pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :page-sizes="[5, 10]"
                    :total="data.total"
                    @current-change="getData"
                    @size-change="getData"
            />
        </div>
        <el-dialog title="社团信息" v-model="data.formVisible" width="60%" destroy-on-close>
            <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px"
                     style="padding:20px 30px 20px 0">
                <el-form-item prop="name" label="名称">
                    <el-input v-model="data.form.name" autocomplete="off"/>
                </el-form-item>
                <el-form-item prop="avatar" label="社团头像">
                    <el-upload
                        action="/api/upload/avatar"
                        :on-success="handleFileSuccess"
                        :show-file-list="false"
                        accept="image/*"
                    >
                        <el-button size="small" type="primary">上传头像</el-button>
                    </el-upload>
                    <el-image
                        v-if="data.form.avatar"
                        :src="data.form.avatar"
                        style="width: 100px; height: 100px; margin-top: 10px"
                    />
                </el-form-item>
                <el-form-item prop="content" label="内容">
                    <div style="border: 1px solid #ccc; width: 100%">
                        <Toolbar
                            style="border-bottom: 1px solid #ccc"
                            :editor="editorRef"
                            :mode="mode"
                        />
                        <Editor
                            style="height: 500px; overflow-y: hidden"
                            v-model="data.form.description"
                            :mode="mode"
                            :defaultConfig="editorConfig"
                            @onCreated="handleCreated"
                        />

                    </div>
                </el-form-item>
            </el-form>
            <template #footer>
                <div>
                    <el-button @click="data.formVisible = false">取 消</el-button>
                    <el-button type="primary" @click="save">提 交</el-button>
                </div>
            </template>
        </el-dialog>

        <!-- 查看详情弹窗 -->
        <el-dialog title="社团信息" v-model="data.viewVisible" width="60%" destroy-on-close>
            <div class="wang-table-view" v-html="data.form.description"></div>
        </el-dialog>

        <el-dialog title="申请信息" v-model="data.applicationVisible" width="40%" destroy-on-close>
            <el-form ref="formRef" :model="data.applicationForm" :rules="data.rules" label-width="80px"
                     style="padding:20px 30px 20px 0">
                <el-form-item prop="reason" label="申请原因">
                    <el-input type="textarea" rows="4" v-model="data.applicationForm.reason" autocomplete="off"/>
                </el-form-item>
            </el-form>
            <template #footer>
                <div>
                    <el-button @click="data.applicationVisible = false">取 消</el-button>
                    <el-button type="primary" @click="submitApplication">提 交</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {onBeforeUnmount, reactive, ref, shallowRef} from "vue";
import {Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";
import '@wangeditor/editor/dist/css/style.css' // 引入css


const data = reactive({
    user: JSON.parse(localStorage.getItem('user') || '{}'),
    username: null,
    name: null,
    pageNum: 1,
    pageSize: 10,
    total: 0,
    tableData: [],
    formVisible: false,
    form: {},
    viewVisible: false,
    applicationVisible: false,
    applicationForm:{},
    rules: {
        name: [
            {required: true, message: '请输入名称', trigger: 'blur'},
        ],
        description: [
                {required: true, message: '请输入账号', trigger: 'blur'},
        ]
    },
    rows: [],
    ids: [],
})

const formRef = ref()

// wangEditor5 初始化开始
const editorRef = shallowRef() //编辑器实例 必须用shallowRef
const mode = 'default'
const editorConfig = {MENU_CONF: {}}
// 图片上传配置
editorConfig.MENU_CONF['uploadImage'] = {
    headers: {
        token: data.user.token,
    },
    server: 'http://127.0.0.1:8080/file/wang/upload', // 服务端上传地址
    fieldName: 'file', // 服务端接收图片的参数名
    // 新增：图片插入后的默认样式（限制最大宽度）
    image: {
        // 所有插入的图片，最大宽度为容器的 100%
        style: {
            maxWidth: '50%',
            height: 'auto' // 高度自动适配，保持比例
        }
    }
}
// 组件销毁时，及时销毁编辑器 ，防止内存泄漏
onBeforeUnmount(() => {
    const editor = editorRef.value
    if (editor == null) return
    editor.destroy() // 销毁编辑器
})
// 记录 editor 实例，用于后续操作
const handleCreated = (editor) => {
    editorRef.value = editor // 记录 editor 实例，用于后续操作
}
// wangEditor5 初始化结束
const getData = () => {
    request.get('/club/selectPage', {
            params: {
                pageNum: data.pageNum,
                pageSize: data.pageSize,
                name: data.name,
                username: data.username
            }
        }
    ).then(res => {
        if (res.code === '200') {
            data.tableData = res.data.list
            data.total = res.data.total
        } else {
            ElMessage.error(res.msg)
        }
    })
}
getData()
const reset = () => {
    data.username = null
    data.name = null
    getData()
}
const headleAdd = () => {
    data.formVisible = true
    data.form = {}
}
const add = () => {
    // formRef 表单的验证
    formRef.value.validate((valid) => {
        if (valid) { // 表单验证成功
            request.post('/club/add', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success("新增成功")
                    data.formVisible = false
                    getData()
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}
const headleEdit = (row) => {
    data.formVisible = true
    data.form = JSON.parse(JSON.stringify(row))
}
const edit = () => {
    // formRef 表单的验证
    formRef.value.validate((valid) => {
        if (valid) { // 表单验证成功
            request.put('/club/update', data.form).then(res => {
                if (res.code === '200') {
                    ElMessage.success("修改成功")
                    data.formVisible = false
                    getData()
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}
const save = () => {
    data.form.id ? edit() : add()
}

const del = (id) => {
    ElMessageBox.confirm(' 你确定删除信息吗', 'Warning', {type: 'warning'}).then(() => {
        request.delete('/club/delete/' + id).then(res => {
            if (res.code === '200') {
                ElMessage.success("删除成功")
                getData()
            } else {
                ElMessage.error(res.msg)
            }
        }).catch(() => {
        })
    })
}

const handleSelectionChange = (rows) => { // rows 实际选择的数组
    data.rows = rows
    data.ids = data.rows.map(v => v.id) // map可以把对象的数组 转换成纯数字的数组 [1,2,3]
}

const deleteBatch = () => {
    if (data.rows.length === 0) {
        ElMessage.warning("请选择要删除的行")
        return
    }
    ElMessageBox.confirm(' 你确定删除信息吗', 'Warning', {type: 'warning'}).then(() => {
        request.delete('/club/deleteBatch', {data: data.rows}).then(res => {
            if (res.code === '200') {
                ElMessage.success("删除成功")
                getData()
            } else {
                ElMessage.error(res.msg)
            }
        })
    })
}

const exportDate = () => {
    let idsStr = data.ids.join(",") // 把数组转换成 字符串  [1,2,3] -> "1,2,3"
    let url = `http://localhost:8080//export?username=${data.username === null ? '' : data.username}`
        + `&name=${data.name === null ? '' : data.name}`
        + `&ids=${idsStr}`
        + `&token=${data.user.token}`
    window.open(url)
}

const handleImportSuccess = (res) => {
    if (res.code === '200') {
        ElMessage.success("导入成功")
        getData()
    } else {
        ElMessage.error(res.msg)
    }
}
const handleFileSuccess = (res) => {
    data.form.avatar = res.data
}

const viewContent = (description) => {
    data.viewVisible = true
    data.form.description = description
}
const handleApplication = (row) => {
    data.applicationVisible = true
    data.applicationForm.studentId = data.user.id
    data.applicationForm.clubId = row.id
}

const submitApplication = () => {
    // formRef 表单的验证
    formRef.value.validate((valid) => {
        if (valid) { // 表单验证成功
            request.post('application/add', data.applicationForm).then(res => {
                if (res.code === '200') {
                    ElMessage.success("申请成功")
                    data.applicationVisible = false
                } else {
                    ElMessage.error(res.msg)
                }
            })
        }
    })
}
</script>

<style scoped>
:deep(.wang-table-view table) {
    border-collapse: collapse;
    width: 100%;
    margin: 10px 0;
}
:deep(.wang-table-view table tr td),
:deep(.wang-table-view table tr th) {
    border: 1px solid #ccc;
    padding: 8px 12px;
}
:deep(.wang-table-view table th) {
    background-color: #f5f5f5;
    font-weight: bold;
}
:deep(.wang-table-view img) {
    max-width: 100%;
    height: auto; /* 保持图片比例 */
    display: block; /* 可选：让图片独占一行，避免和文字拥挤 */
    margin: 0 auto; /* 可选：让图片居中显示 */
}
</style>
