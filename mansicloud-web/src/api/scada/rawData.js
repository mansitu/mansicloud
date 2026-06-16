import request from '@/utils/request'

export function add(data) {
  return request({
    url: 'api/rawData',
    method: 'post',
    data
  })
}

export function del(ids) {
  return request({
    url: 'api/rawData/',
    method: 'delete',
    data: ids
  })
}

export function edit(data) {
  return request({
    url: 'api/rawData',
    method: 'put',
    data
  })
}

export function getAll(params) {
  return request({
    url: 'api/rawData',
    method: 'get',
    params
  })
}

export function getLatestDeviceData() {
  return request({
    url: 'api/rawData/latest',
    method: 'get'
  })
}

export function getHistoryCurveData(params) {
  return request({
    url: 'api/rawData/history',
    method: 'get',
    params
  })
}


export default { add, edit, del, getAll }
