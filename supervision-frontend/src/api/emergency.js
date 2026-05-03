import request from './request'

export function getAlarmList(params) {
  const apiParams = { ...params }
  if (apiParams.level !== undefined) {
    apiParams.alarmLevel = apiParams.level
    delete apiParams.level
  }
  return request.get('/api/v1/supervision/emergency/alarms', { params: apiParams })
}

export function handleAlarm(id, data) {
  return request.post(`/api/v1/supervision/emergency/alarms/${id}/handle`, data)
}

export function getCommandList(params) {
  return request.get('/api/v1/supervision/emergency/commands', { params })
}

export function sendCommand(data) {
  return request.post('/api/v1/supervision/emergency/commands', data)
}

export function cancelCommand(id, cancelReason = '用户撤销') {
  return request.put(`/api/v1/supervision/emergency/commands/${id}/cancel`, {
    cancelReason
  })
}

export function getEscalationRules(params) {
  return request.get('/api/v1/supervision/emergency/alarms/escalation-rules', { params })
}

export function updateEscalationRule(id, data) {
  return request.put(`/api/v1/supervision/emergency/alarms/escalation-rules/${id}`, data)
}
