import request from './request'

export function getDashboardMetrics(params) {
  return request.get('/api/v1/supervision/dashboard/metrics', { params })
}

export function getChargeDischargeTrend(params) {
  return request.get('/api/v1/supervision/dashboard/charge-discharge-trend', { params })
}

export function getDemandResponseEffect(params) {
  return request.get('/api/v1/supervision/dashboard/demand-response-effect', { params })
}

export function getAlarms(params) {
  return request.get('/api/v1/supervision/dashboard/alarms', { params })
}

export function getUnreadAlarmCount(params) {
  return request.get('/api/v1/supervision/dashboard/unread-alarm-count', { params })
}

export function getUnreadAlarms(params) {
  return request.get('/api/v1/supervision/dashboard/alarms', { params })
}

export function getAlarmList(params) {
  return request.get('/api/v1/supervision/dashboard/alarms', { params })
}
