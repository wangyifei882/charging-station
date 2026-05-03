import request from './request'

export function getRegionTree() {
  return request.get('/system/regions')
}

export function getRegionChildren(params) {
  return request.get('/system/regions/children', { params })
}
