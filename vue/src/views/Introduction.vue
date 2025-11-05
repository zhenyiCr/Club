<template>
    <div>
        <div class="card">
            <div>
                <el-input clearable @clear="getData"
                          style="margin-right: 10px;width: 260px;margin-bottom: 7px;margin-top: 8px"
                          placeholder="请输入标题"
                          :prefix-icon="Search"
                          v-model="data.title"></el-input>
                <el-button type="primary" @click="getData">查询</el-button>
                <el-button @click="reset">重置</el-button>
            </div>


            <div style="margin-bottom: 5px;margin-top: 5px">
                <el-button @click="headleAdd" type="primary">新增</el-button>
            </div>
        </div>
        <div>
            <el-table :data="data.tableData" style="width: 100%"
                      :header-cell-style="{fontWeight:'bold',background:'#f5f5f5'}">
                <el-table-column prop="title" label="标题" width="150"/>
                <el-table-column label="图片">
                    <template #default="scope">
                        <el-image
                                v-if="scope.row.img"
                                :src="scope.row.img"
                                :preview-src-list="[scope.row.img]"
                                :preview-teleported="true"
                                fit="cover"
                                style="width: 50px; height: 50px;border-radius: 5px; display: block"
                        />
                        <!-- 没有头像时显示默认图标或图片 -->
                        <el-image v-else icon="User"
                                  style="width: 50px; height: 50px;border-radius: 5px; display: block"/>
                    </template>
                </el-table-column>
                <el-table-column prop="categoryTitle" label="类别"/>
                <el-table-column prop="userName" label="发布用户"/>
                <el-table-column prop="content" label="内容">
                    <template v-slot="scope">
                        <el-button type="primary" size="mini" @click="viewContent(scope.row.content)">查看</el-button>
                    </template>
                </el-table-column>
                <el-table-column prop="time" label="发布时间"/>
                <el-table-column label="操作" width="150">
                    <template #default="scope">
                        <el-button @click="headleEdit(scope.row)" type="primary" icon="edit"></el-button>
                        <el-button @click="del(scope.row.id)" type="danger" icon="delete"></el-button>
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
        <el-dialog title="公告信息" v-model="data.formVisible" width="60%" destroy-on-close>
            <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="80px"
                     style="padding:20px 30px 20px 0">
                <el-form-item prop="img" label="主题">
                    <el-upload action="http://127.0.0.1:8080/file/upload"
                               :headers="{token: data.user.token}"
                               :on-success="handleFileSuccess"
                               list-type="picture"
                    >
                        <el-button>上传图片</el-button>
                    </el-upload>
                </el-form-item>
                <el-form-item prop="title" label="标题">
                    <el-input v-model="data.form.title" autocomplete="off"/>
                </el-form-item>
                <el-form-item prop="category" label="分类">
                    <el-select v-model="data.form.categoryId"
                               placeholder="请选择分类">
                        <el-option
                            v-for="item in data.categoryData"
                            :key="item.id"
                            :label="item.title"
                            :value="item.id"
                        />
                    </el-select>
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
                                v-model="data.form.content"
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
        <el-dialog title="公告信息" v-model="data.viewVisible" width="60%" destroy-on-close>
            <div class="wang-table-view" v-html="data.form.content"></div>
        </el-dialog>
    </div>
</template>
<script setup>
import {onBeforeUnmount, reactive, ref, shallowRef} from "vue";
import {Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css' // 引入css

const data = reactive({
    user: JSON.parse(localStorage.getItem('user') || '{}'),
    title: null,
    pageNum: 1,
    pageSize: 10,
    total: 0,
    tableData: [],
    formVisible: false,
    form: {},
    viewVisible: false,
    categoryData:[],
    rules: {
        title: [
            {required: true, message: '请输入账号', trigger: 'blur'},
        ],
        content: [
            {required: true, message: '请输入名称', trigger: 'blur'},
        ],
        categoryId: [],
    },
})

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


const formRef = ref()

const getData = () => {

    request.get('/introduction/selectPage', {
            params: {
                pageNum: data.pageNum,
                pageSize: data.pageSize,
                title: data.title
            }
        }
    ).then(res => {
        if (res.code === '200') {
            data.tableData = res.data?.list
            data.total = res.data?.total
        } else {
            ElMessage.error(res.msg)
        }
    })
}
getData()

const viewContent = (content) => {
    data.viewVisible = true
    data.form.content = content
}
const reset = () => {
    data.title = null
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
            request.post('/introduction/add', data.form).then(res => {
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
            request.put('/introduction/update', data.form).then(res => {
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
        request.delete('/introduction/delete/' + id).then(res => {
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
const handleFileSuccess = (res) => {
    data.form.img = res.data
}

const loadCategory = () => {
    request.get('/category/selectAll').then(res => {
        if (res.code === '200') {
            data.categoryData=res.data
        } else {
            ElMessage.error(res.msg)
        }
    })
}
loadCategory()
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
</style>
