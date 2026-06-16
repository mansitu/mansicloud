import request from '@/utils/request'

// 获取所有缓存名称
export function getCacheNames() {
  return request({
    url: '/api/cache/names',
    method: 'get'
  })
}

// 获取所有缓存数据
export function getAllCacheData() {
  return request({
    url: '/api/cache/all',
    method: 'get'
  })
}

// 获取指定缓存的数据
export function getCacheByName(cacheName) {
  return request({
    url: `/api/cache/${cacheName}`,
    method: 'get'
  })
}
