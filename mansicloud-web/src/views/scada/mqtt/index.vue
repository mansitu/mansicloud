<template>
  <div class="app-container">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>MQTT 消息发送</span>
      </div>
      <el-form ref="form" :model="form" size="small" label-width="100px">
        <el-form-item label="Topic">
          <el-input
            v-model="form.topic"
            placeholder="请输入MQTT主题，例如：msc/mst"
            clearable
            style="width: 500px;"
          />
        </el-form-item>
        <el-form-item label="消息内容">
          <el-input
            v-model="form.sendData"
            type="textarea"
            :rows="10"
            placeholder="请输入JSON格式的消息内容"
            style="width: 700px;"
            spellcheck="false"
            @input="validateJsonFormat"
          />
        </el-form-item>
        <el-form-item>
          <div v-if="jsonValidMessage" :class="jsonValidClass" style="margin-bottom: 10px;">
            {{ jsonValidMessage }}
          </div>
          <el-button
            type="primary"
            icon="el-icon-s-promotion"
            :loading="loading"
            :disabled="!isJsonValid"
            @click="handleSubmit"
          >
            发送
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { sendMqtt } from '@/api/scada/mqtt'

const defaultSendData = `{
  "db-01": {
    "v1": 123456.12,
    "v2": 231.1,
    "v3": 231.1
  },
  "db-011": {
    "v1": 123456.12,
    "v2": 231.1,
    "v3": 231.1
  }
}`

export default {
  name: 'MqttPublish',
  data() {
    return {
      loading: false,
      isJsonValid: true,
      jsonValidMessage: '',
      jsonValidClass: '',
      form: {
        topic: 'msc/mst',
        sendData: defaultSendData
      }
    }
  },
  created() {
    this.validateJsonFormat()
  },
  methods: {
    validateJsonFormat() {
      const value = this.form.sendData
      if (!value || value.trim() === '') {
        this.isJsonValid = false
        this.jsonValidMessage = ' 消息内容不能为空'
        this.jsonValidClass = 'text-danger'
        return
      }

      try {
        JSON.parse(value)
        this.isJsonValid = true
        this.jsonValidMessage = '✅ JSON格式正确'
        this.jsonValidClass = 'text-success'
      } catch (e) {
        this.isJsonValid = false
        this.jsonValidMessage = '❌ JSON格式错误：' + e.message
        this.jsonValidClass = 'text-danger'
      }
    },
    handleSubmit() {
      if (!this.isJsonValid) {
        this.$message.warning('请先修正JSON格式错误')
        return
      }

      if (!this.form.topic || this.form.topic.trim() === '') {
        this.$message.warning('Topic不能为空')
        return
      }

      // 格式化JSON，去除不必要的空格和空行
      try {
        const jsonObj = JSON.parse(this.form.sendData)
        this.form.sendData = JSON.stringify(jsonObj, null, 2)
      } catch (e) {
        this.$message.error('JSON格式化失败：' + e.message)
        return
      }

      this.loading = true
      sendMqtt(this.form).then(() => {
        this.$notify({
          title: '发送成功',
          message: 'MQTT消息已成功发送',
          type: 'success',
          duration: 3000
        })
        this.loading = false
      }).catch((error) => {
        // 显示后端返回的错误消息
        if (error.response && error.response.data) {
          const errorMsg = error.response.data
          this.$message.error(errorMsg || '发送失败，请稍后重试')
        } else {
          this.$message.error('发送失败，请稍后重试')
        }
        this.loading = false
      })
    },
    handleReset() {
      this.form.topic = 'msc/mst'
      this.form.sendData = defaultSendData
      this.validateJsonFormat()
    }
  }
}
</script>

<style scoped>
.box-card {
  max-width: 800px;
}
.text-success {
  color: #67C23A;
  font-size: 12px;
}
.text-danger {
  color: #F56C6C;
  font-size: 12px;
}
</style>

