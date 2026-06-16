<template>
  <div class="app-container">
    <!-- 顶部工具栏 -->
    <el-card shadow="hover" class="header-card">
      <div class="header-content">
        <div class="header-left">
          <i class="el-icon-s-data header-icon" />
          <span class="header-title">设备统计概览</span>
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

    <!-- 全局统计信息卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :xs="24" :sm="8" :md="8">
        <el-card shadow="hover" class="stat-card stat-total">
          <div class="stat-card-content">
            <div class="stat-icon">
              <i class="el-icon-s-data" />
            </div>
            <div class="stat-info">
              <div class="stat-label">设备总量</div>
              <div class="stat-value">{{ totalDevices }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8" :md="8">
        <el-card shadow="hover" class="stat-card stat-online">
          <div class="stat-card-content">
            <div class="stat-icon">
              <i class="el-icon-success" />
            </div>
            <div class="stat-info">
              <div class="stat-label">在线设备</div>
              <div class="stat-value">{{ onlineDevices }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8" :md="8">
        <el-card shadow="hover" class="stat-card stat-offline">
          <div class="stat-card-content">
            <div class="stat-icon">
              <i class="el-icon-warning" />
            </div>
            <div class="stat-info">
              <div class="stat-label">离线设备</div>
              <div class="stat-value">{{ offlineDevices }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 设备类型列表 -->
    <el-card shadow="never" class="content-card">
      <div slot="header" class="card-header">
        <i class="el-icon-s-order" />
        <span>设备类型统计</span>
      </div>

      <div v-show="!loading" class="device-type-list">
        <div
          v-for="(devices, devtype) in groupedData"
          :key="devtype"
          class="device-type-card"
        >
          <!-- 设备类型标题 -->
          <div class="type-header">
            <div class="type-info">
              <i class="el-icon-s-order type-icon" />
              <span class="type-name">{{ getDeviceTypeName(devtype) }}</span>
            </div>
            <el-badge :value="getDeviceCount(devtype)" :max="999" class="type-badge" />
          </div>

          <!-- 设备类型统计信息 -->
          <div class="type-stats">
            <div class="stats-row">
              <div class="type-stat-item type-stat-total">
                <i class="el-icon-s-data" />
                <div class="type-stat-content">
                  <div class="type-stat-label">设备总量</div>
                  <div class="type-stat-value">{{ getDeviceCount(devtype) }}</div>
                </div>
              </div>
              <div class="type-stat-item type-stat-online">
                <i class="el-icon-success" />
                <div class="type-stat-content">
                  <div class="type-stat-label">在线设备</div>
                  <div class="type-stat-value">{{ getOnlineCount(devtype) }}</div>
                </div>
              </div>
              <div class="type-stat-item type-stat-offline">
                <i class="el-icon-warning" />
                <div class="type-stat-content">
                  <div class="type-stat-label">离线设备</div>
                  <div class="type-stat-value">{{ getOfflineCount(devtype) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 无设备提示 -->
        <el-empty v-if="!groupedData || Object.keys(groupedData).length === 0" description="暂无设备数据" />
      </div>
    </el-card>
  </div>
</template>

<script>
import { getLatestDeviceData } from '@/api/scada/rawData'
import { getAll } from '@/api/scada/deviceType'

export default {
  name: 'DeviceStatistics',
  data() {
    return {
      groupedData: {},
      deviceTypeMap: {},
      lastUpdateTime: '-',
      isRefreshing: false,
      loading: true,
      refreshTimer: null
    }
  },
  computed: {
    // 设备总量
    totalDevices() {
      let count = 0
      Object.keys(this.groupedData).forEach(devtype => {
        count += this.getDeviceCount(devtype)
      })
      return count
    },
    // 在线设备数量
    onlineDevices() {
      let count = 0
      Object.keys(this.groupedData).forEach(devtype => {
        count += this.getOnlineCount(devtype)
      })
      return count
    },
    // 离线设备数量
    offlineDevices() {
      let count = 0
      Object.keys(this.groupedData).forEach(devtype => {
        count += this.getOfflineCount(devtype)
      })
      return count
    }
  },
  created() {
    this.loadDeviceTypes()
    this.refreshData()
    this.startAutoRefresh()
  },
  beforeDestroy() {
    this.stopAutoRefresh()
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
        return
      }

      this.isRefreshing = true

      try {
        const res = await getLatestDeviceData()
        const newGroupedData = res.groupedData || {}

        // 直接更新数据，无闪烁
        this.groupedData = newGroupedData
        this.lastUpdateTime = this.formatTime(new Date())

        // 首次加载完成后隐藏 loading
        if (this.loading) {
          this.$nextTick(() => {
            this.loading = false
          })
        }
      } catch (error) {
        this.$message.error('获取设备数据失败: ' + (error.message || '未知错误'))
      } finally {
        this.isRefreshing = false
      }
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
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 10px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
}

/* 顶部卡片 */
.header-card {
  margin-bottom: 10px;
  border-radius: 4px;
  /*background: linear-gradient(135deg, #f0e6ff 0%, #e8d8f8 100%);*/
  background: linear-gradient(135deg, #d6f0ff 0%, #c4e5f8 100%);
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
  color: #7a4ab5;
  margin-right: 12px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #5a2a8a;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 统计卡片 */
.stats-cards {
  margin-bottom: 10px;
}

.stat-card {
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 10px rgba(0, 0, 0, 0.12);
}

.stat-card >>> .el-card__body {
  padding: 10px;
}

.stat-card-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon i {
  font-size: 32px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 8px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #fff;
  line-height: 1;
}

/* 不同状态的卡片颜色 */
.stat-total {
  background: linear-gradient(135deg, #e8ecff 0%, #d8def8 100%);
}

.stat-total .stat-icon {
  color: #4a5bb5;
  background: rgba(74, 91, 181, 0.1);
}

.stat-total .stat-icon i {
  color: #4a5bb5;
}

.stat-total .stat-label,
.stat-total .stat-value {
  color: #2d3a7a;
}

.stat-online {
  background: linear-gradient(135deg, #d6f0ff 0%, #c4e5f8 100%);
}

.stat-online .stat-icon {
  color: #2b8a5a;
  background: rgba(43, 138, 90, 0.1);
}

.stat-online .stat-icon i {
  color: #2b8a5a;
}

.stat-online .stat-label,
.stat-online .stat-value {
  color: #1a6a42;
}

.stat-offline {
  /*background: linear-gradient(135deg, #fff4e6 0%, #f8ece0 100%);*/
  background: linear-gradient(135deg, #e6f9ff 0%, #d8f0f8 100%);
}

.stat-offline .stat-icon {
  color: #be2121;
  background: rgba(201, 74, 74, 0.1);
}

.stat-offline .stat-icon i {
  color: #be2121;
}

.stat-offline .stat-label,
.stat-offline .stat-value {
  /*color: #9a2a2a;*/
  color: #be2121;

}

/* 内容卡片 */
.content-card {
  border-radius: 4px;
}

.card-header {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  padding: 4px 16px;
  background: linear-gradient(135deg, #d6f0ff 0%, #c4e5f8 100%);
  border-bottom: 1px solid #ebeef5;
}

.card-header i {
  margin-right: 8px;
  font-size: 18px;
  color: #4ab5c9;
}

/* 设备类型列表 */
.device-type-list {
  padding: 2px 2px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
}

.device-type-card {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.device-type-card:hover {
  border-color: #b3d8ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

/* 设备类型标题 */
.type-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 2px;
  background: linear-gradient(to right, #f5f7fa 0%, #ffffff 100%);
  border-bottom: 1px solid #ebeef5;
}

.type-info {
  display: flex;
  align-items: center;
}

.type-icon {
  font-size: 20px;
  color: #409eff;
  margin-right: 8px;
}

.type-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.type-badge >>> .el-badge__content {
  background-color: #409eff;
  border: none;
  height: 18px;
  line-height: 18px;
  font-size: 12px;
  padding: 0 6px;
}

/* 设备类型统计信息 */
.type-stats {
  padding: 12px 12px;
  background-color: #fff;
}

.stats-row {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.type-stat-item {
  display: flex;
  align-items: center;
  padding: 8px 10px;
  border-radius: 4px;
  transition: all 0.3s ease;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.type-stat-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.type-stat-total {
  background: linear-gradient(135deg, #e8ecff 0%, #d8def8 100%);
  color: #2d3a7a;
}

.type-stat-online {
  background: linear-gradient(135deg, #d6f0ff 0%, #c4e5f8 100%);
  color: #1a6a42;
}

.type-stat-offline {
  /*background: linear-gradient(135deg, #fff4e6 0%, #f8ece0 100%);*/
  background: linear-gradient(135deg, #e6f9ff 0%, #d8f0f8 100%);
  color: #be2121;
}

.type-stat-item i {
  font-size: 18px;
  margin-right: 8px;
  opacity: 0.9;
}

.type-stat-content {
  flex: 1;
}

.type-stat-label {
  font-size: 10px;
  margin-bottom: 3px;
  opacity: 0.9;
}

.type-stat-value {
  font-size: 18px;
  font-weight: 700;
  line-height: 1;
}

/* 响应式布局 */
@media (max-width: 1400px) {
  .device-type-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1024px) {
  .device-type-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-cards {
    margin-bottom: 12px;
  }

  .stat-card {
    margin-bottom: 12px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
  }

  .stat-icon i {
    font-size: 24px;
  }

  .stat-label {
    font-size: 12px;
  }

  .stat-value {
    font-size: 24px;
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

  .device-type-list {
    grid-template-columns: 1fr;
    padding: 12px;
    gap: 12px;
  }

  .type-header {
    padding: 8px 12px;
  }

  .type-icon {
    font-size: 20px;
    margin-right: 8px;
  }

  .type-name {
    font-size: 14px;
  }

  .type-stats {
    padding: 12px 12px;
  }

  .type-stat-item {
    padding: 8px 10px;
  }

  .type-stat-item i {
    font-size: 18px;
    margin-right: 8px;
  }

  .type-stat-label {
    font-size: 10px;
  }

  .type-stat-value {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .stat-card {
    margin-bottom: 15px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
  }

  .stat-icon i {
    font-size: 24px;
  }

  .stat-label {
    font-size: 12px;
  }

  .stat-value {
    font-size: 24px;
  }

  .type-header {
    padding: 8px 10px;
  }

  .type-icon {
    font-size: 18px;
    margin-right: 6px;
  }

  .type-name {
    font-size: 13px;
  }

  .type-stats {
    padding: 8px 10px;
  }

  .type-stat-item {
    padding: 6px 8px;
  }

  .type-stat-item i {
    font-size: 16px;
    margin-right: 6px;
  }

  .type-stat-label {
    font-size: 9px;
  }

  .type-stat-value {
    font-size: 16px;
  }
}
</style>
