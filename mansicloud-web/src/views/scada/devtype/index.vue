<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- 搜索 -->
        <label class="el-form-item-label">设备名称</label>
        <el-input v-model="query.typeName" clearable placeholder="设备名称" style="width: 185px;" class="filter-item" @keyup.enter.native="crud.toQuery" />
        <label class="el-form-item-label">状态</label>
        <el-input v-model="query.isDeleted" clearable placeholder="状态" style="width: 185px;" class="filter-item" @keyup.enter.native="crud.toQuery" />
        <rrOperation :crud="crud" />
      </div>
      <!--如果想在工具栏加入更多按钮，可以使用插槽方式， slot = 'left' or 'right'-->
      <crudOperation :permission="permission" />
      <!--表单组件-->
      <el-dialog :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0" :title="crud.status.title" width="500px">
        <el-form ref="form" :model="form" :rules="rules" size="small" label-width="80px">
          <el-form-item label="设备代码" prop="typeCode">
            <el-input v-model="form.typeCode" style="width: 370px;" maxlength="50" show-word-limit @input="validateTypeCode" />
          </el-form-item>
          <el-form-item label="设备名称" prop="typeName">
            <el-input v-model="form.typeName" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="版本号" prop="version">
            <el-input v-model="form.version" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="form.remark" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="状态" prop="isDeleted">
            <el-radio v-for="item in dict.dept_isDeleted" :key="item.id" v-model="form.isDeleted" :label="item.value">{{ item.label }}</el-radio>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="text" @click="crud.cancelCU">取消</el-button>
          <el-button :loading="crud.status.cu === 2" type="primary" @click="crud.submitCU">确认</el-button>
        </div>
      </el-dialog>
      <!--表格渲染-->
      <el-table ref="table" v-loading="crud.loading" :data="crud.data" size="small" style="width: 100%;" @selection-change="crud.selectionChangeHandler">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="typeCode" label="设备类型代码" />
        <el-table-column prop="typeName" label="类型名称" />
        <el-table-column prop="version" label="版本号" />
        <el-table-column prop="modelJson" label="设备模型">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="openJsonEditor(scope.row, 'modelJson')">编辑</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="configJson" label="组态配置">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="openJsonEditor(scope.row, 'configJson')">编辑</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="updateTime" label="更新时间" />
        <el-table-column prop="isDeleted" label="状态" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isDeleted === '1' || scope.row.isDeleted === 1 ? 'danger' : 'success'">
              {{ dict.label.dept_isDeleted[scope.row.isDeleted] }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="checkPer(['admin','deviceType:edit','deviceType:del'])" label="操作" width="150px" align="center">
          <template slot-scope="scope">
            <udOperation
              :data="scope.row"
              :permission="permission"
            />
          </template>
        </el-table-column>
      </el-table>
      <!--分页组件-->
      <pagination />
    </div>
    <!-- JSON编辑对话框 -->
    <el-dialog :visible.sync="jsonDialogVisible" :title="jsonDialogTitle" width="1200px" :close-on-click-modal="false" top="5vh">
      <div class="json-editor-container">
        <el-tabs v-model="activeTab" type="border-card">
          <!-- 属性配置 -->
          <el-tab-pane label="属性列表" name="properties">
            <div class="tab-content">
              <el-button size="small" type="primary" icon="el-icon-plus" @click="addProperty">添加属性</el-button>
              <el-table :data="tslData.properties" border style="margin-top: 10px" max-height="400">
                <el-table-column label="数据标识" width="120">
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.dataFlag" size="small" placeholder="选择标识" clearable>
                      <el-option v-for="i in 20" :key="i" :label="'v' + i" :value="'v' + i" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="ID" width="120">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.id" size="small" placeholder="如: temp1" />
                  </template>
                </el-table-column>
                <el-table-column label="名称" width="150">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.name" size="small" placeholder="如: 料筒温度" />
                  </template>
                </el-table-column>
                <el-table-column label="数据类型" width="120">
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.dataType" size="small" placeholder="选择类型">
                      <el-option label="整数(int)" value="int" />
                      <el-option label="浮点数(float)" value="float" />
                      <el-option label="字符串(string)" value="string" />
                      <el-option label="布尔(bool)" value="bool" />
                      <el-option label="枚举(enum)" value="enum" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="单位" width="100">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.unit" size="small" placeholder="如: ℃" />
                  </template>
                </el-table-column>
                <el-table-column label="读写类型" width="100">
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.rwFlag" size="small">
                      <el-option label="只读(r)" value="r" />
                      <el-option label="读写(rw)" value="rw" />
                      <el-option label="只写(w)" value="w" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80">
                  <template slot-scope="scope">
                    <el-button size="mini" type="danger" icon="el-icon-delete" circle @click="removeProperty(scope.$index)" />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>

          <!-- 服务配置 -->
          <el-tab-pane label="服务列表" name="services">
            <div class="tab-content">
              <el-button size="small" type="primary" icon="el-icon-plus" @click="addService">添加服务</el-button>
              <el-table :data="tslData.services" border style="margin-top: 10px" max-height="400">
                <el-table-column label="ID" width="150">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.id" size="small" placeholder="如: openLight" />
                  </template>
                </el-table-column>
                <el-table-column label="名称">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.name" size="small" placeholder="如: 开启运行灯" />
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80">
                  <template slot-scope="scope">
                    <el-button size="mini" type="danger" icon="el-icon-delete" circle @click="removeService(scope.$index)" />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>

          <!-- 事件配置 -->
          <el-tab-pane label="事件列表" name="events">
            <div class="tab-content">
              <el-button size="small" type="primary" icon="el-icon-plus" @click="addEvent">添加事件</el-button>
              <el-table :data="tslData.events" border style="margin-top: 10px" max-height="400">
                <el-table-column label="ID" width="150">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.id" size="small" placeholder="如: overHeat" />
                  </template>
                </el-table-column>
                <el-table-column label="名称" width="150">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.name" size="small" placeholder="如: 超温报警" />
                  </template>
                </el-table-column>
                <el-table-column label="等级">
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.level" size="small">
                      <el-option label="提示(info)" value="info" />
                      <el-option label="警告(warning)" value="warning" />
                      <el-option label="错误(error)" value="error" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80">
                  <template slot-scope="scope">
                    <el-button size="mini" type="danger" icon="el-icon-delete" circle @click="removeEvent(scope.$index)" />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </el-tab-pane>

          <!-- 基础配置 -->
          <el-tab-pane label="基础配置" name="basic">
            <div class="tab-content">
              <el-form label-width="120px" style="padding: 20px">
                <el-form-item label="TSL版本">
                  <el-input v-model="tslData.tslVersion" placeholder="如: 1.0" disabled />
                  <span class="form-tip">版本号自动生成，不可修改</span>
                </el-form-item>
                <el-form-item label="设备类型">
                  <el-input v-model="tslData.deviceType" placeholder="设备类型代码" disabled />
                  <span class="form-tip">设备类型自动生成，不可修改</span>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>

          <!-- JSON预览 -->
          <el-tab-pane label="JSON预览" name="preview">
            <div class="tab-content">
              <pre class="json-preview">{{ formatJsonPreview() }}</pre>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button type="text" @click="jsonDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveJsonEdit">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import crudDeviceType from '@/api/scada/deviceType'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'

const defaultForm = { id: null, typeCode: null, typeName: null, version: null, modelJson: null, configJson: null, icon: null, remark: null, createTime: null, updateTime: null, isDeleted: '0' }
export default {
  name: 'DeviceType',
  components: { pagination, crudOperation, rrOperation, udOperation },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  dicts: ['dept_isDeleted'],
  cruds() {
    return CRUD({
      title: '设备类型',
      url: 'api/deviceType',
      idField: 'id',
      sort: 'id,desc',
      crudMethod: { ...crudDeviceType },
      optShow: {
        add: true,
        edit: true,
        del: true,
        download: true
      }
    })
  },
  data() {
    return {
      permission: {
        add: ['admin', 'deviceType:add'],
        edit: ['admin', 'deviceType:edit'],
        del: ['admin', 'deviceType:del']
      },
      rules: {
        typeCode: [
          { required: true, message: '设备代码不能为空', trigger: 'blur' },
          {
            pattern: /^[a-zA-Z][a-zA-Z0-9]*$/,
            message: '设备代码必须以字母开头，只能包含英文字母和数字',
            trigger: 'blur'
          }
        ],
        typeName: [
          { required: true, message: '设备名称不能为空', trigger: 'blur' }
        ],
        version: [
          { required: true, message: '版本号不能为空', trigger: 'blur' }
        ],
        isDeleted: [
          { required: true, message: '状态不能为空', trigger: 'blur' }
        ]
      },
      queryTypeOptions: [
        { key: 'typeName', display_name: '设备名称' },
        { key: 'isDeleted', display_name: '状态' }
      ],
      jsonDialogVisible: false,
      jsonDialogTitle: '',
      editJsonContent: '',
      currentEditRow: null,
      currentEditField: '',
      activeTab: 'properties',
      tslData: {
        tslVersion: '1.0',
        deviceType: '',
        properties: [],
        services: [],
        events: []
      }
    }
  },
  methods: {
    // 钩子：在获取表格数据之前执行，false 则代表不获取数据
    [CRUD.HOOK.beforeRefresh]() {
      return true
    },
    // 验证设备代码必须以字母开头，只能包含字母和数字
    validateTypeCode(value) {
      const pattern = /^[a-zA-Z][a-zA-Z0-9]*$/
      if (value && !pattern.test(value)) {
        // 如果第一个字符不是字母，清空输入
        if (!/^[a-zA-Z]/.test(value)) {
          this.form.typeCode = value.replace(/^[^a-zA-Z]*/, '')
          this.$message({
            message: '设备代码必须以字母开头',
            type: 'warning'
          })
        } else {
          // 过滤掉非字母数字字符
          this.form.typeCode = value.replace(/[^a-zA-Z0-9]/g, '')
          this.$message({
            message: '设备代码只能包含英文字母和数字',
            type: 'warning'
          })
        }
      }
    },
    // 钩子：新增前设置默认值
    [CRUD.HOOK.beforeToAdd]() {
      this.form.isDeleted = '0'
    },
    // 钩子：编辑前处理数据
    [CRUD.HOOK.beforeToEdit](crud, record) {
      this.form.isDeleted = String(record.isDeleted)
    },
    // 打开JSON编辑器
    openJsonEditor(row, field) {
      this.currentEditRow = row
      this.currentEditField = field
      this.jsonDialogTitle = field === 'modelJson' ? '编辑设备模型' : '编辑组态配置'
      this.editJsonContent = row[field] || ''
      this.activeTab = 'properties'

      // 解析现有JSON
      if (this.editJsonContent && this.editJsonContent.trim()) {
        try {
          const parsed = JSON.parse(this.editJsonContent)
          this.tslData = {
            tslVersion: parsed.tslVersion || row.version || '1.0',
            deviceType: parsed.deviceType || row.typeCode || '',
            properties: parsed.properties || [],
            services: parsed.services || [],
            events: parsed.events || []
          }
        } catch (e) {
          this.$message({
            message: 'JSON格式错误，将使用默认值',
            type: 'warning'
          })
          this.resetTslData(row)
        }
      } else {
        this.resetTslData(row)
      }

      this.jsonDialogVisible = true
    },

    // 重置TSL数据
    resetTslData(row) {
      this.tslData = {
        tslVersion: row ? row.version : '1.0',
        deviceType: row ? row.typeCode : '',
        properties: [],
        services: [],
        events: []
      }
    },

    // 添加属性
    addProperty() {
      this.tslData.properties.push({
        id: '',
        name: '',
        dataType: 'float',
        dataFlag: '',
        unit: '',
        rwFlag: 'r'
      })
    },

    // 删除属性
    removeProperty(index) {
      this.tslData.properties.splice(index, 1)
    },

    // 添加服务
    addService() {
      this.tslData.services.push({
        id: '',
        name: ''
      })
    },

    // 删除服务
    removeService(index) {
      this.tslData.services.splice(index, 1)
    },

    // 添加事件
    addEvent() {
      this.tslData.events.push({
        id: '',
        name: '',
        level: 'info'
      })
    },

    // 删除事件
    removeEvent(index) {
      this.tslData.events.splice(index, 1)
    },

    // 格式化JSON预览
    formatJsonPreview() {
      const jsonStr = JSON.stringify(this.tslData, null, 2)
      return jsonStr
    },

    // 保存JSON编辑
    async saveJsonEdit() {
      // 验证必填字段
      const errors = []

      this.tslData.properties.forEach((prop, index) => {
        if (!prop.id) errors.push(`属性${index + 1}的ID不能为空`)
        if (!prop.name) errors.push(`属性${index + 1}的名称不能为空`)
      })

      if (errors.length > 0) {
        this.$message({
          message: errors.join('；'),
          type: 'error'
        })
        return
      }

      // 构建最终JSON
      const finalContent = JSON.stringify(this.tslData, null, 2)

      // 更新数据
      const updateData = {
        ...this.currentEditRow,
        [this.currentEditField]: finalContent
      }

      try {
        await crudDeviceType.edit(updateData)
        this.$message({
          message: '保存成功',
          type: 'success'
        })
        this.jsonDialogVisible = false
        // 刷新列表
        this.crud.refresh()
      } catch (error) {
        this.$message({
          message: '保存失败：' + (error.message || '未知错误'),
          type: 'error'
        })
      }
    },

    // 验证TSL ID只能包含英文、数字、下划线和连字符
    validateTslId(row, field) {
      const pattern = /^[a-zA-Z0-9_-]*$/
      if (!pattern.test(row[field])) {
        row[field] = row[field].replace(/[^a-zA-Z0-9_-]/g, '')
        this.$message({
          message: 'ID只能包含英文字母、数字、下划线和连字符',
          type: 'warning'
        })
      }
    }
  }
}

</script>

<style scoped>
.json-editor-container {
  max-height: 70vh;
  overflow-y: auto;
}

.tab-content {
  padding: 10px;
}

.json-preview {
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  font-size: 13px;
  line-height: 1.6;
  overflow-x: auto;
  max-height: 500px;
}

.form-tip {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}
</style>
