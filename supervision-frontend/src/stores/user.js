import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    currentRegion: null
  }),
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    setCurrentRegion(region) {
      this.currentRegion = region
    },
    logout() {
      this.token = ''
      this.userInfo = {}
      this.currentRegion = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
