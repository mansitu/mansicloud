<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <div v-if="crud.props.searchToggle">
        <!-- 搜索 -->
        <label class="el-form-item-label">设备名称</label>
        <el-select
          v-model="query.deviceName"
          filterable
          clearable
          placeholder="请选择设备"
          style="width: 185px;"
          class="filter-item"
        >
          <el-option
            v-for="(device, index) in deviceList"
            :key="'search-device-' + index + '-' + device.deviceCode"
            :label="device.deviceName"
            :value="device.deviceCode"
          />
        </el-select>
        <el-date-picker
          v-model="query.ts"
          type="datetimerange"
          range-separator=":"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          :default-time="['00:00:00', '23:59:59']"
          style="width: 380px;"
          class="filter-item"
        />
        <el-button
          class="filter-item"
          size="mini"
          type="primary"
          icon="el-icon-search"
          @click="handleQuery"
        >
          查询
        </el-button>
      </div>
      <!--如果想在工具栏加入更多按钮，可以使用插槽方式， slot = 'left' or 'right'-->
      <crudOperation :permission="permission" />
      <!--表格渲染-->
      <el-table ref="table" v-loading="crud.loading" :data="crud.data" size="small" style="width: 100%;" @selection-change="crud.selectionChangeHandler">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="ts" label="采集时间戳" width="180" />
        <el-table-column prop="deviceName" label="设备名称" width="120" />
        <el-table-column
          v-for="(label, key) in currentMeasurePointColumns"
          :key="key"
          :prop="key"
          :label="label"
          min-width="100"
        >
          <template slot-scope="scope">
            {{ scope.row[key] !== undefined ? (typeof scope.row[key] === 'number' ? scope.row[key].toFixed(2) : scope.row[key]) : '--' }}
          </template>
        </el-table-column>
      </el-table>
      <!--分页组件-->
      <pagination />
    </div>
  </div>
</template>

<script>
import { getAll as getAllDevices } from '@/api/scada/device'
import { getAll } from '@/api/scada/rawData'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import crudOperation from '@crud/CRUD.operation'
import pagination from '@crud/Pagination'

const defaultForm = { id: null, ts: null, deviceName: null, deviceId: null, v1: null, v2: null, v3: null, v4: null, v5: null, v6: null, v7: null, v8: null, v9: null, v10: null, v11: null, v12: null, v13: null, v14: null, v15: null, v16: null, v17: null, v18: null, v19: null, v20: null }
export default {
  name: 'RawData',
  components: { pagination, crudOperation },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  cruds() {
    return CRUD({
      title: 'rawdata',
      url: 'api/rawData',
      idField: 'id',
      sort: 'ts,desc',
      crudMethod: {
        getAll: getAll
      },
      optShow: {
        add: false,
        edit: false,
        del: false,
        download: true
      }
    })
  },
  data() {
    return {
      deviceList: [],
      hasQueried: false,
      permission: {
        add: ['admin', 'rawData:add'],
        edit: ['admin', 'rawData:edit'],
        del: ['admin', 'rawData:del']
      },
      rules: {
        ts: [
          { required: true, message: '采集时间戳不能为空', trigger: 'blur' }
        ],
        deviceName: [
          { required: true, message: '设备名称不能为空', trigger: 'blur' }
        ],
        deviceId: [
          { required: true, message: '设备类型不能为空', trigger: 'blur' }
        ]
      },
      queryTypeOptions: [
        { key: 'deviceName', display_name: '设备名称' }
      ],
      currentMeasurePointColumns: {}
    }
  },
  watch: {
    deviceList: {
      handler(newVal) {
        console.log('=== deviceList 发生变化 ===')
        console.log('新值:', newVal)
        console.log('新值长度:', newVal.length)
        if (newVal && newVal.length > 0) {
          console.log('第一个元素:', newVal[0])
        }
      },
      deep: true,
      immediate: false
    }
  },
  created() {
    console.log('=== RawData 组件已创建 ===')
    this.loadDeviceTypes()
    this.loadDeviceList()
    this.setDefaultTimeRange()
  },
  mounted() {
    console.log('=== RawData 组件已挂载 ===')
    console.log('mounted 时 deviceList:', this.deviceList)
    console.log('mounted 时 deviceList 长度:', this.deviceList.length)
  },
  methods: {
    async loadDeviceTypes() {
      try {
        const res = await getAllDevices({ page: 0, size: 1000 })
        let devices = []
        if (res && res.content) {
          devices = res.content
        } else if (res && res.data && res.data.content) {
          devices = res.data.content
        } else if (Array.isArray(res)) {
          devices = res
        } else if (res && Array.isArray(res.data)) {
          devices = res.data
        }
        this.$set(this, 'deviceList', devices)

        if (this.deviceList.length === 0) {
          this.$message.warning('暂无设备数据,请先创建设备')
        }
      } catch (error) {
        this.$message.error('加载设备列表失败: ' + (error.message || '未知错误'))
      }
    },
    setDefaultTimeRange() {
      const today = new Date()
      const year = today.getFullYear()
      const month = String(today.getMonth() + 1).padStart(2, '0')
      const day = String(today.getDate()).padStart(2, '0')

      const startTime = `${year}-${month}-${day} 00:00:00`
      const endTime = `${year}-${month}-${day} 23:59:59`

      this.$set(this.query, 'ts', [startTime, endTime])
    },
    handleQuery() {
      if (!this.query.deviceName) {
        this.$message.warning('请选择设备名称')
        return
      }

      if (!this.query.ts || this.query.ts.length !== 2) {
        this.$message.warning('请选择时间范围')
        return
      }

      this.hasQueried = true

      const params = {
        page: 0,
        size: this.crud.props.pageSize || 10,
        sort: 'ts,desc',
        deviceName: this.query.deviceName,
        ts: this.query.ts
      }

      this.crud.toQuery(params)
    },
    async loadDeviceList() {
      try {
        const res = await getAllDevices({ page: 0, size: 1000 })

        let devices = []
        if (res && res.content) {
          devices = res.content
        } else if (res && res.data && res.data.content) {
          devices = res.data.content
        } else if (Array.isArray(res)) {
          devices = res
        } else if (res && Array.isArray(res.data)) {
          devices = res.data
        }

        this.$set(this, 'deviceList', devices)

        if (this.deviceList.length === 0) {
          this.$message.warning('暂无设备数据,请先创建设备')
        }
      } catch (error) {
        this.$message.error('加载设备列表失败: ' + (error.message || '未知错误'))
      }
    },
    [CRUD.HOOK.afterRefresh]() {
      if (this.crud.data && this.crud.data.length > 0) {
        const firstRow = this.crud.data[0]
        // 提取所有测点列（排除基础字段）
        const baseFields = ['id', 'ts', 'deviceName', 'deviceId']
        const columns = {}

        Object.keys(firstRow).forEach(key => {
          if (!baseFields.includes(key)) {
            columns[key] = key
          }
        })

        this.currentMeasurePointColumns = columns
        console.log('=== 更新测点列定义 ===', columns)
      } else {
        this.currentMeasurePointColumns = {}
      }
      return true
    },
    [CRUD.HOOK.beforeRefresh]() {
      return this.hasQueried
    }
  }
}
</script>

<style scoped>

</style>
