import request from './request'

export function getV2gActivityList(params) {
  return request.get('/api/v1/supervision/v2g/activities', { params })
}

export function getV2gActivityDetail(id) {
  return request.get(`/api/v1/supervision/v2g/activities/${id}`)
}

export function getOrderedChargingList(params) {
  return request.get('/api/v1/supervision/v2g/ordered-charging', { params })
}

export function getDemandResponseList(params) {
  return request.get('/api/v1/supervision/v2g/demand-response', { params })
}

export function getDemandResponseStats() {
  return request.get('/api/v1/supervision/v2g/demand-response/stats')
}

export function getDemandResponseOperatorRanking() {
  return request.get('/api/v1/supervision/v2g/demand-response/operator-ranking')
}

export function getDemandResponseDetail(id) {
  return request.get(`/api/v1/supervision/v2g/demand-response/${id}`)
}

export function getDispatchOverview(params) {
  return request.get('/api/v1/supervision/v2g/dispatch/overview', { params })
}

export function getRegionPowerAllocation(params) {
  return request.get('/api/v1/supervision/v2g/dispatch/region-power-allocation', { params })
}

export function getPowerTrend(params) {
  return request.get('/api/v1/supervision/v2g/dispatch/power-trend', { params })
}

export function getDispatchTasks(params) {
  return request.get('/api/v1/supervision/v2g/dispatch/tasks', { params })
}

export function getDispatchStats(params) {
  return request.get('/api/v1/supervision/v2g/dispatch/stats', { params })
}

export function getV2gActivityStats() {
  return request.get('/api/v1/supervision/v2g/activities/stats')
}

export function createV2gActivity(data) {
  return request.post('/api/v1/supervision/v2g/activities', data)
}

export function createDemandResponse(data) {
  return request.post('/api/v1/supervision/v2g/demand-response', data)
}
