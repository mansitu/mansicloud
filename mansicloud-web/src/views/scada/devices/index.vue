<template>
  <div class="app-container">
    <!--工具栏-->
    <div class="head-container">
      <!--如果想在工具栏加入更多按钮，可以使用插槽方式， slot = 'left' or 'right'-->
      <crudOperation :permission="permission" />
      <!--表单组件-->
      <el-dialog :close-on-click-modal="false" :before-close="crud.cancelCU" :visible.sync="crud.status.cu > 0" :title="crud.status.title" width="500px">
        <el-form ref="form" :model="form" :rules="rules" size="small" label-width="80px">
          <el-form-item label="设备类型" prop="typeCode">
            <el-input v-model="form.typeCode" disabled style="width: 370px;" />
          </el-form-item>
          <el-form-item label="网关编号">
            <el-input v-model="form.gatewaySn" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="设备名称" prop="deviceName">
            <el-input v-model="form.deviceName" style="width: 370px;" />
          </el-form-item>
          <el-form-item label="remark">
            <el-input v-model="form.remark" style="width: 370px;" />
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
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="deviceCode" label="设备编码" />
        <el-table-column prop="typeCode" label="类型代码" />
        <el-table-column prop="gatewaySn" label="网关编号" />
        <el-table-column prop="status" label="状态" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="online" label="在线状态" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.online === 1 ? 'success' : 'danger'">
              {{ scope.row.online === 1 ? '在线' : '离线' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column prop="updateTime" label="更新时间" width="160" />
        <el-table-column v-if="checkPer(['admin','device:edit'])" label="操作" width="150px" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-edit"
              @click="crud.toEdit(scope.row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!--分页组件-->
      <pagination />
    </div>
  </div>
</template>

<script>
import crudDevice from '@/api/scada/device'
import CRUD, { presenter, header, form, crud } from '@crud/crud'
import crudOperation from '@crud/CRUD.operation'
import pagination from '@crud/Pagination'

const defaultForm = { id: null, typeCode: null, gatewaySn: null, deviceName: null, deviceCode: null, status: null, online: null, remark: null, createTime: null, updateTime: null }
export default {
  name: 'Device',
  components: { pagination, crudOperation },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  cruds() {
    return CRUD({
      title: '设备管理',
      url: 'api/device',
      idField: 'id',
      sort: 'id,desc',
      crudMethod: { ...crudDevice },
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
        add: ['admin', 'device:add'],
        edit: ['admin', 'device:edit'],
        del: ['admin', 'device:del']
      },
      rules: {
        typeCode: [
          { required: true, message: '设备类型不能为空', trigger: 'blur' }
        ],
        deviceName: [
          { required: true, message: '设备名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    // 钩子：在获取表格数据之前执行，false 则代表不获取数据
    [CRUD.HOOK.beforeRefresh]() {
      return true
    }
  }
}
</script>

<style scoped>

</style>
