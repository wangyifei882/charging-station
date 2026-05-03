import request from './request'

export function getStationList(params) {
  return request.get('/api/v1/supervision/facility/ledger', { params })
}

export function getRegionsByOperator(operatorId) {
  return request.get('/api/v1/supervision/facility/regions-by-operator', {
    params: operatorId ? { operatorId } : {}
  })
}

export function getStationDetail(id) {
  return request.get(`/api/v1/supervision/facility/stations/${id}`)
}

export function getStatusMonitor(params) {
  return request.get('/api/v1/supervision/facility/status-monitor', { params })
}

export function getAccessApplicationList(params) {
  return request.get('/api/v1/supervision/facility/access-applications', { params })
}

export function auditAccessApplication(id, data) {
  return request.post(`/api/v1/supervision/facility/access-applications/${id}/audit`, data)
}
