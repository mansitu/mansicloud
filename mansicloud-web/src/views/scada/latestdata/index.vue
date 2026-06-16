<template>
  <div class="app-container">
    <!-- 顶部工具栏 -->
    <el-card shadow="hover" class="header-card">
      <div class="header-content">
        <div class="header-left">
          <i class="el-icon-monitor header-icon" />
          <span class="header-title">设备实时监控</span>
        </div>
        <div class="header-right">
          <el-tag :type="isRefreshing ? 'warning' : 'info'" size="medium">
            <i :class="isRefreshing ? 'el-icon-loading' : 'el-icon-time'" />
            {{ isRefreshing ? '更新中...' : '最后更新: ' + lastUpdateTime }}
          </el-tag>
          <el-button
            type="primary"
            icon="el-icon-refresh"
            size="small"
            style="margin-left: 15px;"
            :loading="isRefreshing"
            @click="refreshData"
          >
            刷新数据
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 设备类型标签页 -->
    <el-card shadow="never" class="content-card">
      <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">
        <el-tab-pane
          v-for="(devices, devtype) in groupedData"
          :key="devtype"
          :name="devtype"
        >
          <!-- 自定义标签内容 -->
          <span slot="label">
            <i class="el-icon-s-order" />
            {{ getDeviceTypeName(devtype) }}
            <el-badge :value="getDeviceCount(devtype)" :max="999" class="tab-badge" />
          </span>

          <!-- 统计信息卡片 -->
          <div class="stats-bar">
            <el-row :gutter="20">
              <el-col :xs="24" :sm="8" :md="8">
                <div class="stat-item stat-total">
                  <i class="el-icon-s-data" />
                  <div class="stat-content">
                    <div class="stat-label">设备总量</div>
                    <div class="stat-value">{{ getDeviceCount(devtype) }}</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="8" :md="8">
                <div class="stat-item stat-online">
                  <i class="el-icon-success" />
                  <div class="stat-content">
                    <div class="stat-label">在线设备</div>
                    <div class="stat-value">{{ getOnlineCount(devtype) }}</div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="8" :md="8">
                <div class="stat-item stat-offline">
                  <i class="el-icon-warning" />
                  <div class="stat-content">
                    <div class="stat-label">离线设备</div>
                    <div class="stat-value">{{ getOfflineCount(devtype) }}</div>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>

          <!-- 设备卡片网格 -->
          <transition-group name="fade" tag="div" class="device-grid">
            <div
              v-for="device in devices"
              :key="device.devname"
              class="device-card-wrapper"
              :class="{ 'card-online': isOnline(device), 'card-offline': !isOnline(device) }"
            >
              <!-- 卡片头部 -->
              <div class="device-card-header">
                <div class="device-info">
                  <i class="el-icon-s-order device-icon" />
                  <div class="device-text">
                    <div class="device-name">{{ device.devname }}</div>
                    <div class="device-type">{{ getDeviceTypeName(devtype) }}</div>
                  </div>
                </div>
                <el-tag
                  :type="isOnline(device) ? 'success' : 'info'"
                  size="small"
                  effect="dark"
                >
                  <i :class="isOnline(device) ? 'el-icon-success' : 'el-icon-warning'" />
                  {{ isOnline(device) ? '在线' : '离线' }}
                </el-tag>
              </div>

              <!-- 卡片内容 - 数据网格 -->
              <div class="device-card-body">
                <div class="data-grid">
                  <div
                    v-for="(propInfo, index) in getDisplayProperties(devtype)"
                    :key="index"
                    class="data-item"
                    :class="{ 'data-updating': updatingDevices[device.devname] }"
                  >
                    <div class="data-label">{{ getColumnLabel(propInfo) }}</div>
                    <div class="data-value">
                      <span class="value-number">
                        {{ formatValue(getMeasurePointValue(device, propInfo)) }}
                      </span>
                      <span v-if="propInfo.unit" class="value-unit">{{ propInfo.unit }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 卡片底部 -->
              <div class="device-card-footer">
                <i class="el-icon-time" />
                <span>{{ formatFullTimestamp(device.ts) }}</span>
                <el-button
                  type="text"
                  size="mini"
                  icon="el-icon-data-line"
                  style="margin-left: auto;"
                  @click="showHistoryCurve(device)"
                >
                  历史曲线
                </el-button>
              </div>
            </div>
          </transition-group>

          <!-- 无设备提示 -->
          <el-empty v-if="!devices || devices.length === 0" description="暂无设备数据" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 历史曲线对话框 -->
    <el-dialog
      :title="curveDialogTitle"
      :visible.sync="curveDialogVisible"
      width="75%"
      top="8vh"
      :close-on-click-modal="false"
    >
      <!-- 时间选择器和操作按钮 -->
      <div class="curve-controls">
        <el-form :inline="true" size="mini">
          <el-form-item label="结束时间">
            <el-date-picker
              v-model="curveEndTime"
              type="datetime"
              placeholder="选择结束时间"
              value-format="yyyy-MM-dd HH:mm:ss"
              style="width: 160px;"
            />
          </el-form-item>
          <el-form-item>
            <span class="time-range-tip">（自动获取前2小时数据）</span>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" size="mini" @click="loadCurveData">查询</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- ECharts 图表容器 -->
      <div ref="curveChart" class="curve-chart-container" />
    </el-dialog>
  </div>
</template>

<script>
import { getLatestDeviceData, getHistoryCurveData } from '@/api/scada/rawData'
import { getAll } from '@/api/scada/deviceType'
import * as echarts from 'echarts'

export default {
  name: 'DeviceRealtimeMonitor',
  data() {
    return {
      activeTab: '',
      groupedData: {},
      deviceTypeMap: {},
      lastUpdateTime: '-',
      isRefreshing: false,
      updatingDevices: {},
      refreshTimer: null,
      // 历史曲线相关
      curveDialogVisible: false,
      curveDialogTitle: '',
      currentDevice: null,
      curveEndTime: '',
      curveChart: null,
      curveData: []
    }
  },
  computed: {
    endTimePickerOptions() {
      return {
        disabledDate(time) {
          // 不能选择未来时间
          return time.getTime() > Date.now()
        }
      }
    }
  },
  watch: {
    groupedData: {
      handler(newVal) {
        // 如果 activeTab 为空、为 0、或不在当前的 groupedData 中，则设置为第一个
        if ((!this.activeTab || !newVal[this.activeTab]) && Object.keys(newVal).length > 0) {
          const firstKey = Object.keys(newVal)[0]
          this.$nextTick(() => {
            this.activeTab = firstKey
          })
        }
      }
    }
  },
  created() {
    this.loadDeviceTypes()
    this.refreshData()
    this.startAutoRefresh()
  },
  beforeDestroy() {
    this.stopAutoRefresh()
    if (this.curveChart) {
      this.curveChart.dispose()
    }
  },
  methods: {
    async loadDeviceTypes() {
      try {
        const res = await getAll({ page: 0, size: 1000 })
        const types = res.content || []
        types.forEach(type => {
          this.deviceTypeMap[type.typeCode] = type
        })
      } catch (error) {
        this.$message.error('加载设备类型失败')
      }
    },

    async refreshData() {
      if (this.isRefreshing) {
        return // 防止重复刷新
      }

      this.isRefreshing = true

      try {
        const res = await getLatestDeviceData()
        // 从响应中获取 groupedData
        const newGroupedData = res.groupedData || {}

        // 标记正在更新的设备（新数据结构已经是 devname 和 devtype，无需映射）
        Object.keys(newGroupedData).forEach(devtype => {
          const devices = newGroupedData[devtype] || []
          devices.forEach(device => {
            this.$set(this.updatingDevices, device.devname, true)
          })
        })

        // 延迟一小段时间后更新数据，让用户看到更新动画
        setTimeout(() => {
          this.groupedData = newGroupedData
          this.lastUpdateTime = this.formatTime(new Date())

          // 设置默认激活的标签页（只在首次加载时设置）
          if (!this.activeTab || !this.groupedData[this.activeTab]) {
            if (Object.keys(this.groupedData).length > 0) {
              const firstKey = Object.keys(this.groupedData)[0]
              this.activeTab = firstKey
            }
          }

          // 清除更新标记
          setTimeout(() => {
            this.updatingDevices = {}
          }, 300)
        }, 200)
      } catch (error) {
        this.$message.error('获取设备数据失败: ' + (error.message || '未知错误'))
      } finally {
        this.isRefreshing = false
      }
    },

    handleTabClick(tab) {
      // 标签页切换时的处理
    },

    getDeviceTypeName(devtype) {
      const typeInfo = this.deviceTypeMap[devtype]
      return typeInfo ? `${typeInfo.typeName} (${devtype})` : devtype
    },

    getDeviceCount(devtype) {
      const devices = this.groupedData[devtype] || []
      return devices.length
    },

    getOnlineCount(devtype) {
      const devices = this.groupedData[devtype] || []
      return devices.filter(device => this.isOnline(device)).length
    },

    getOfflineCount(devtype) {
      const devices = this.groupedData[devtype] || []
      return devices.filter(device => !this.isOnline(device)).length
    },

    getDisplayProperties(devtype) {
      const typeInfo = this.deviceTypeMap[devtype]
      if (!typeInfo || !typeInfo.modelJson) {
        return []
      }

      try {
        const model = JSON.parse(typeInfo.modelJson)
        const properties = model.properties || []
        return properties.map(prop => ({
          dataFlag: prop.dataFlag,
          name: prop.name,
          unit: prop.unit
        }))
      } catch (error) {
        this.$message.error(`解析设备类型 ${devtype} 的配置失败`)
        return []
      }
    },

    getColumnKey(propInfo) {
      // 返回列的 key，从 measurePoints 中取值
      return propInfo.unit ? `${propInfo.name}_${propInfo.unit}` : propInfo.name
    },

    getColumnLabel(propInfo) {
      // 返回列的显示标签
      return propInfo.name
    },

    getMeasurePointValue(device, propInfo) {
      // 直接从 device 对象中获取测点值（新数据结构已展开到顶层）
      const key = this.getColumnKey(propInfo)
      return device[key]
    },

    formatValue(value) {
      if (value === null || value === undefined) {
        return '--'
      }
      // 保留两位小数
      return typeof value === 'number' ? value.toFixed(2) : value
    },

    formatFullTimestamp(ts) {
      if (!ts) return '-'
      const date = new Date(ts)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      const second = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`
    },

    isOnline(device) {
      // 根据最新数据的时间戳判断是否在线（5分钟内认为在线）
      if (!device.ts) return false
      const deviceTime = new Date(device.ts).getTime()
      const now = Date.now()
      return (now - deviceTime) <= 5 * 60 * 1000
    },

    formatTime(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      const second = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`
    },

    startAutoRefresh() {
      // 每30秒自动刷新一次
      this.refreshTimer = setInterval(() => {
        this.refreshData()
      }, 30000)
    },

    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    },

    // 显示历史曲线
    showHistoryCurve(device) {
      this.currentDevice = device
      this.curveDialogTitle = `${device.devname} - 历史曲线`

      // 设置默认结束时间为当前时间
      const now = new Date()
      this.curveEndTime = this.formatDateTime(now)

      this.curveDialogVisible = true

      // 延迟加载数据，确保对话框已渲染
      this.$nextTick(() => {
        this.loadCurveData()
      })
    },

    // 加载曲线数据
    async loadCurveData() {
      if (!this.curveEndTime) {
        this.$message.warning('请选择结束时间')
        return
      }

      // 计算起始时间（结束时间前2小时）
      const endTime = new Date(this.curveEndTime).getTime()
      const startTime = endTime - 2 * 60 * 60 * 1000

      try {
        const params = {
          devname: this.currentDevice.devname,
          startTime: this.formatDateTime(new Date(startTime)),
          endTime: this.curveEndTime
        }

        const res = await getHistoryCurveData(params)
        this.curveData = res

        // 渲染图表
        this.renderCurveChart()
      } catch (error) {
        this.$message.error('加载历史数据失败: ' + (error.message || '未知错误'))
      }
    },

    // 渲染曲线图表
    renderCurveChart() {
      if (!this.$refs.curveChart) return

      // 初始化或获取图表实例
      if (!this.curveChart) {
        this.curveChart = echarts.init(this.$refs.curveChart)
      }

      // 获取当前设备类型的属性定义
      const devtype = this.currentDevice.devtype
      const properties = this.getDisplayProperties(devtype)

      if (properties.length === 0) {
        this.$message.warning('该设备类型没有定义数据属性')
        return
      }

      // 准备数据
      const times = this.curveData.map(item => item.ts)

      // 构建系列数据
      const series = properties.map(prop => {
        const columnKey = this.getColumnKey(prop)
        return {
          name: prop.name + (prop.unit ? ` (${prop.unit})` : ''),
          type: 'line',
          smooth: true,
          data: this.curveData.map(item => item[columnKey]),
          markPoint: {
            data: [
              { type: 'max', name: '最大值' },
              { type: 'min', name: '最小值' }
            ]
          },
          markLine: {
            data: [
              { type: 'average', name: '平均值' }
            ]
          }
        }
      })

      // 图表配置
      const option = {
        title: {
          text: `${this.currentDevice.devname} 历史数据曲线`,
          left: 'center',
          textStyle: {
            fontSize: 14
          }
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          }
        },
        legend: {
          data: series.map(s => s.name),
          top: 35,
          textStyle: {
            fontSize: 11
          },
          itemWidth: 15,
          itemHeight: 10
        },
        toolbox: {
          feature: {
            dataView: {
              show: true,
              readOnly: false,
              title: '数据视图',
              lang: ['数据视图', '关闭', '导出Excel'],
              optionToContent: function(opt) {
                const axisData = opt.xAxis[0].data
                const series = opt.series
                let tdHeads = '<td style="padding: 5px;">时间</td>'
                series.forEach(function(s) {
                  tdHeads += '<td style="padding: 5px;">' + s.name + '</td>'
                })
                let table = '<table border="1" style="margin-left:20px;border-collapse:collapse;font-size:12px;text-align:center"><tbody><tr>' + tdHeads + '</tr>'
                for (let i = 0; i < axisData.length; i++) {
                  let tdBodys = '<td>' + axisData[i] + '</td>'
                  series.forEach(function(s) {
                    tdBodys += '<td>' + (s.data[i] !== undefined ? s.data[i].toFixed(2) : '-') + '</td>'
                  })
                  table += '<tr>' + tdBodys + '</tr>'
                }
                table += '</tbody></table>'
                return table
              },
              contentToOption: function(tableDom, opt) {
                // 构建 CSV 内容
                let csvContent = ''
                const rows = tableDom.querySelectorAll('tr')
                rows.forEach(function(row) {
                  const cells = row.querySelectorAll('td')
                  const rowData = []
                  cells.forEach(function(cell) {
                    rowData.push(cell.textContent)
                  })
                  csvContent += rowData.join(',') + '\n'
                })

                // 创建下载链接
                const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
                const link = document.createElement('a')
                link.href = URL.createObjectURL(blob)
                link.download = '历史数据_' + new Date().getTime() + '.csv'
                link.click()
              }
            },
            saveAsImage: {
              show: true,
              title: '保存为图片'
            },
            restore: {
              show: true,
              title: '还原'
            }
          },
          right: 10,
          top: 5
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '18%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: times,
          axisLabel: {
            formatter: '{HH}:{mm}:{ss}',
            fontSize: 10
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            fontSize: 10
          }
        },
        series: series,
        dataZoom: [
          {
            type: 'inside',
            start: 0,
            end: 100
          },
          {
            start: 0,
            end: 100,
            height: 25,
            bottom: 5,
            handleSize: '80%',
            handleStyle: {
              color: '#fff',
              shadowBlur: 3,
              shadowColor: 'rgba(0, 0, 0, 0.6)',
              shadowOffsetX: 2,
              shadowOffsetY: 2
            }
          }
        ]
      }

      this.curveChart.setOption(option, true)
    },

    formatDateTime(date) {
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hour = String(date.getHours()).padStart(2, '0')
      const minute = String(date.getMinutes()).padStart(2, '0')
      const second = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hour}:${minute}:${second}`
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
}

/* 顶部卡片 */
.header-card {
  margin-bottom: 20px;
  border-radius: 4px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-icon {
  font-size: 24px;
  color: #409eff;
  margin-right: 12px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 内容卡片 */
.content-card {
  border-radius: 4px;
}

.content-card >>> .el-card__body {
  padding: 0;
}

/* 标签页徽章 */
.tab-badge {
  margin-left: 8px;
}

.tab-badge >>> .el-badge__content {
  background-color: #409eff;
  border: none;
  height: 18px;
  line-height: 18px;
  font-size: 12px;
  padding: 0 6px;
}

/* 统计信息栏 */
.stats-bar {
  padding: 12px 20px;
  background: linear-gradient(to right, #f5f7fa 0%, #ffffff 100%);
  border-bottom: 1px solid #ebeef5;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 10px 16px;
  border-radius: 6px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-online {
  background: linear-gradient(135deg, #84fab0 0%, #8fd3f4 100%);
  color: #fff;
}

.stat-offline {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: #666;
}

.stat-item i {
  font-size: 24px;
  margin-right: 12px;
  opacity: 0.9;
}

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 12px;
  margin-bottom: 4px;
  opacity: 0.9;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  line-height: 1;
}

/* 设备网格 */
.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
  padding: 16px;
}

/* 设备卡片包装器 */
.device-card-wrapper {
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 2px 8px 0 rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
  border: 2px solid transparent;
}

.device-card-wrapper:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.12);
}

.card-online {
  border-left: 3px solid #67c23a;
}

.card-offline {
  border-left: 3px solid #909399;
  opacity: 0.85;
}

/* 卡片头部 */
.device-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(to right, #f5f7fa 0%, #ffffff 100%);
  border-bottom: 1px solid #ebeef5;
}

.device-info {
  display: flex;
  align-items: center;
}

.device-icon {
  font-size: 24px;
  color: #303133;
  margin-right: 10px;
}

.device-text {
  display: flex;
  flex-direction: column;
}

.device-name {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  line-height: 1.3;
}

.device-type {
  font-size: 11px;
  color: #909399;
  line-height: 1.3;
}

/* 卡片内容 - 数据网格 */
.device-card-body {
  padding: 12px 16px;
}

.data-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.data-item {
  background: #fafafa;
  border-radius: 4px;
  padding: 10px 6px;
  text-align: center;
  transition: all 0.3s ease;
  border: 1px solid #ebeef5;
}

.data-item:hover {
  background: #f0f9ff;
  border-color: #b3d8ff;
  transform: scale(1.02);
}

.data-item.data-updating {
  animation: pulse 0.6s ease-in-out;
  background: #fff7e6;
  border-color: #ffd591;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.data-label {
  font-size: 11px;
  color: #909399;
  margin-bottom: 6px;
  font-weight: 500;
}

.data-value {
  display: flex;
  align-items: baseline;
  justify-content: center;
}

.value-number {
  font-size: 16px;
  font-weight: 600;
  color: #409eff;
  margin-right: 3px;
  transition: color 0.3s ease;
}

.data-updating .value-number {
  color: #faad14;
}

.value-unit {
  font-size: 11px;
  color: #909399;
}

/* 卡片底部按钮 */
.device-card-footer {
  padding: 8px 16px;
  background-color: #fafafa;
  border-top: 1px solid #ebeef5;
  font-size: 11px;
  color: #909399;
  display: flex;
  align-items: center;
}

.device-card-footer i {
  margin-right: 5px;
}

/* 历史曲线对话框样式 */
.curve-controls {
  padding: 6px 12px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 12px;
}

.time-range-tip {
  color: #909399;
  font-size: 11px;
}

.curve-chart-container {
  width: 100%;
  height: 350px;
}

/* 淡入淡出动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .device-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 14px;
    padding: 14px;
  }

  .data-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .device-grid {
    grid-template-columns: 1fr;
    padding: 10px;
    gap: 12px;
  }

  .data-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
  }

  .stats-bar {
    padding: 8px 10px;
  }

  .stat-item {
    margin-bottom: 8px;
    padding: 8px 12px;
  }

  .stat-item i {
    font-size: 20px;
    margin-right: 10px;
  }

  .stat-label {
    font-size: 11px;
    margin-bottom: 3px;
  }

  .stat-value {
    font-size: 18px;
  }

  .header-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-right {
    margin-top: 12px;
    width: 100%;
    justify-content: space-between;
  }

  .curve-chart-container {
    height: 280px;
  }
}

@media (max-width: 480px) {
  .data-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 6px;
  }

  .data-item {
    padding: 8px 4px;
  }

  .value-number {
    font-size: 15px;
  }

  .stat-item i {
    font-size: 18px;
    margin-right: 8px;
  }

  .stat-label {
    font-size: 10px;
  }

  .stat-value {
    font-size: 16px;
  }

  .curve-chart-container {
    height: 300px;
  }
}
</style>
