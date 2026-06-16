import request from '@/utils/request'

export function sendMqtt(data) {
  return request({
    url: '/api/sendMqtt',
    method: 'post',
    params: data
  })
}

export default { sendMqtt }
