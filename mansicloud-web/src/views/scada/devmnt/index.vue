<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- 搜索 -->
        <label class="el-form-item-label">设备名称</label>
        <el-select v-model="query.deviceName" clearable placeholder="请选择设备" filterable style="width: 200px;" class="filter-item">
          <el-option
            v-for="device in deviceList"
            :key="device.id"
            :label="device.deviceName"
            :value="device.deviceName"
          />
        </el-select>
        <date-range-picker
          v-model="query.createTime"
          start-placeholder="createTimeStart"
          end-placeholder="createTimeStart"
          class="date-item"
        />
        <rrOperation :crud="crud" />
      </div>
      <!--如果想在工具栏加入更多按钮，可以使用插槽方式， slot = 'left' or 'right'-->
      <crudOperation :permission="permission" />
      <!--表单组件-->
      <el-dialog :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0" :title="crud.status.title" width="550px">
        <el-form ref="form" :model="form" :rules="rules" size="small" label-width="120px">

          <el-form-item label="设备名称" prop="deviceName">
            <el-select v-model="form.deviceName" placeholder="请选择设备" filterable style="width: 370px;" @change="handleDeviceChange">
              <el-option
                v-for="device in deviceList"
                :key="device.id"
                :label="device.deviceName"
                :value="device.deviceName"
              >
                <span style="float: left">{{ device.deviceName }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">{{ device.deviceCode }}</span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="设备编号">
            <el-input v-model="form.deviceCode" style="width: 370px;" disabled placeholder="选择设备后自动填充" />
          </el-form-item>
          <el-form-item label="维保周期（天）" prop="cycleDay">
            <el-input v-model="form.cycleDay" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="预警天数" prop="remindDays">
            <el-input v-model="form.remindDays" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="维保时间">
            <el-date-picker v-model="form.lastMaintainTime" type="date" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="下次维保时间" prop="nextMaintainTime">
            <el-date-picker v-model="form.nextMaintainTime" type="date" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="维保备注" prop="remark">
            <el-input v-model="form.remark" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">停用</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="维保内容" prop="content">
            <el-input v-model="form.content" type="textarea" :rows="3" style="width: 370px;" placeholder="请输入维保内容" maxlength="512" show-word-limit />
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
        <el-table-column prop="deviceName" label="设备名" />
        <el-table-column prop="cycleDay" label="维保周期（天）" />
        <el-table-column prop="remindDays" label="预警天数" />
        <el-table-column prop="lastMaintainTime" label="最新维保时间">
          <template slot-scope="scope">
            {{ scope.row.lastMaintainTime ? scope.row.lastMaintainTime.substring(0, 10) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="nextMaintainTime" label="计划维保时间">
          <template slot-scope="scope">
            {{ scope.row.nextMaintainTime ? scope.row.nextMaintainTime.substring(0, 10) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="维保备注" />
        <el-table-column label="维保内容" width="100" align="center">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="showContentDetail(scope.row.content)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="提醒" width="120" align="center">
          <template slot-scope="scope">
            <el-tag v-if="getRemindStatus(scope.row)" :type="getRemindType(scope.row)" size="small">
              {{ getRemindText(scope.row) }}
            </el-tag>
            <span v-else style="color: #909399;">正常</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="创建人" />
        <el-table-column prop="createTime" label="创建时间">
          <template slot-scope="scope">
            {{ scope.row.createTime ? scope.row.createTime.substring(0, 10) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="updateBy" label="更新人" />
        <el-table-column prop="updateTime" label="更新时间">
          <template slot-scope="scope">
            {{ scope.row.updateTime ? scope.row.updateTime.substring(0, 10) : '-' }}
          </template>
        </el-table-column>
        <el-table-column v-if="checkPer(['admin','sysDeviceMaintain:edit','sysDeviceMaintain:del'])" label="操作" width="150px" align="center">
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
    <!-- 维保内容详情对话框 -->
    <el-dialog title="维保内容详情" :visible.sync="contentDialogVisible" width="600px">
      <div style="white-space: pre-wrap; word-break: break-all; line-height: 1.8;">
        {{ currentContent || '暂无内容' }}
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="contentDialogVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import crudSysDeviceMaintain from '@/api/scada/sysDeviceMaintain'
import { getDeviceList } from '@/api/scada/device'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import rrOperation from '@crud/RR.operation'
import crudOperation from '@crud/CRUD.operation'
import udOperation from '@crud/UD.operation'
import pagination from '@crud/Pagination'

const defaultForm = { id: null, companyCode: null, cycleDay: null, remindDays: null, lastMaintainTime: null, nextMaintainTime: null, remark: null, status: 1, delFlag: 0, createBy: null, createTime: null, updateBy: null, updateTime: null, deviceCode: null, deviceName: null, content: null }
export default {
  name: 'SysDeviceMaintain',
  components: { pagination, crudOperation, rrOperation, udOperation },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  cruds() {
    return CRUD({ title: '新增维保记录', url: 'api/sysDeviceMaintain', idField: 'id', sort: 'id,desc', crudMethod: { ...crudSysDeviceMaintain }})
  },
  data() {
    return {
      deviceList: [],
      contentDialogVisible: false,
      currentContent: '',
      permission: {
        add: ['admin', 'sysDeviceMaintain:add'],
        edit: ['admin', 'sysDeviceMaintain:edit'],
        del: ['admin', 'sysDeviceMaintain:del']
      },
      rules: {
        cycleDay: [
          { required: true, message: '维保周期（天）不能为空', trigger: 'blur' }
        ],
        remindDays: [
          { required: true, message: '提前N天预警不能为空', trigger: 'blur' }
        ],
        nextMaintainTime: [
          { required: true, message: '计划下次维保时间不能为空', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '维保备注不能为空', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '状态不能为空', trigger: 'blur' }
        ]
      },
      queryTypeOptions: [
        { key: 'deviceName', display_name: 'deviceName' }
      ]
    }
  },
  created() {
    this.loadDeviceList()
  },
  methods: {
    // 钩子：在获取表格数据之前执行，false 则代表不获取数据
    [CRUD.HOOK.beforeRefresh]() {
      return true
    },
    // 加载设备列表
    loadDeviceList() {
      getDeviceList().then(response => {
        this.deviceList = response || []
      }).catch(() => {
        this.$message.error('加载设备列表失败')
      })
    },
    // 设备选择变化时，同步更新deviceCode
    handleDeviceChange(deviceName) {
      const selectedDevice = this.deviceList.find(d => d.deviceName === deviceName)
      if (selectedDevice) {
        this.form.deviceCode = selectedDevice.deviceCode
      } else {
        this.form.deviceCode = null
      }
    },
    // 显示维保内容详情
    showContentDetail(content) {
      this.currentContent = content
      this.contentDialogVisible = true
    },
    // 计算提醒状态
    getRemindStatus(row) {
      if (!row.nextMaintainTime || row.status === 0) {
        return false
      }

      const now = new Date()
      const nextMaintain = new Date(row.nextMaintainTime)
      const remindDays = row.remindDays || 0

      // 计算距离下次维保的天数
      const diffTime = nextMaintain.getTime() - now.getTime()
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

      // 如果已过期或即将到期（在提醒天数范围内）
      return diffDays <= remindDays
    },
    // 获取提醒类型（用于标签颜色）
    getRemindType(row) {
      if (!row.nextMaintainTime) return 'info'

      const now = new Date()
      const nextMaintain = new Date(row.nextMaintainTime)
      const remindDays = row.remindDays || 0

      const diffTime = nextMaintain.getTime() - now.getTime()
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

      if (diffDays < 0) {
        return 'danger' // 已过期，红色
      } else if (diffDays <= remindDays / 2) {
        return 'warning' // 紧急提醒，橙色
      } else {
        return 'warning' // 一般提醒，橙色
      }
    },
    // 获取提醒文本
    getRemindText(row) {
      if (!row.nextMaintainTime) return ''

      const now = new Date()
      const nextMaintain = new Date(row.nextMaintainTime)

      const diffTime = nextMaintain.getTime() - now.getTime()
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))

      if (diffDays < 0) {
        return `已过期${Math.abs(diffDays)}天`
      } else if (diffDays === 0) {
        return '今天维保'
      } else {
        return `${diffDays}天后维保`
      }
    }
  }
}

</script>

<style scoped>

</style>
