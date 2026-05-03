import request from './request'

export function getOperatorList(params) {
  return request.get('/api/v1/supervision/compliance/operators', { params })
}

export function getOperatorDetail(id) {
  return request.get(`/api/v1/supervision/compliance/operators/${id}`)
}

export function getQualificationList(params) {
  return request.get('/api/v1/supervision/compliance/qualifications', { params })
}

export function auditQualification(id, data) {
  return request.post(`/api/v1/supervision/compliance/qualifications/${id}/audit`, data)
}

export function getRatingList(params) {
  return request.get('/api/v1/supervision/compliance/ratings', { params })
}

export function getViolationList(params) {
  return request.get('/api/v1/supervision/compliance/violations', { params })
}

export function addViolation(data) {
  return request.post('/api/v1/supervision/compliance/violations', data)
}

export function getSubsidyApplications(params) {
  return request.get('/api/v1/supervision/subsidy/applications', { params })
}

export function auditSubsidy(id, data) {
  return request.post(`/api/v1/supervision/subsidy/applications/${id}/audit`, data)
}

export function getPolicyList(params) {
  return request.get('/api/v1/supervision/compliance/policies', { params })
}

export function publishPolicy(data) {
  return request.post('/api/v1/supervision/compliance/policies/publish', data)
}

export function offlinePolicy(id) {
  return request.post(`/api/v1/supervision/compliance/policies/${id}/offline`)
}

export function getPushRecords(policyId) {
  return request.get(`/api/v1/supervision/compliance/policies/${policyId}/push-records`)
}

export function pushPolicy(policyId, operatorIds) {
  return request.post(`/api/v1/supervision/compliance/policies/${policyId}/push`, { operatorIds })
}
