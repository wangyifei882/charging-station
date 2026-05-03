<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
    'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial,
    sans-serif;
  background-color: var(--color-bg-page);
  color: var(--color-text-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  width: 100%;
  min-height: 100vh;
}

/* Global scrollbar */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: transparent;
}
::-webkit-scrollbar-thumb {
  background: var(--color-text-placeholder);
  border-radius: 3px;
}
::-webkit-scrollbar-thumb:hover {
  background: var(--color-text-secondary);
}

/* Element Plus overrides */
.el-card {
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
}

.el-button--primary {
  --el-button-bg-color: var(--color-primary);
  --el-button-border-color: var(--color-primary);
  --el-button-hover-bg-color: var(--color-primary-light);
  --el-button-hover-border-color: var(--color-primary-light);
}

.el-input__wrapper {
  --el-input-border-color: var(--color-border);
  --el-input-hover-border-color: var(--color-primary-lighter);
  --el-input-focus-border-color: var(--color-primary);
}

.el-table {
  --el-table-border-color: var(--color-border-light);
}
.el-table th.el-table__cell {
  background-color: var(--color-bg-subtle);
  color: var(--color-text-regular);
  font-weight: 600;
}
.el-table .el-table__row:hover > td {
  background-color: var(--color-bg-subtle);
}

.el-menu {
  border-right: none;
}

.el-tag {
  border-radius: var(--radius-sm);
  font-weight: 500;
}

.el-dialog {
  border-radius: var(--radius-lg);
}

.el-select-dropdown__item.is-selected {
  color: var(--color-primary);
  font-weight: 600;
}

.el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background-color: var(--color-primary);
  border-color: var(--color-primary);
}

/* Autosize form controls inside dialogs */
.el-dialog .el-select,
.el-dialog .el-date-picker {
  width: 100%;
}

/* Shared utility classes */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 15px;
  color: var(--color-text-primary);
}

.query-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  justify-content: flex-end;
}

.trend-up {
  color: var(--color-success);
  font-weight: 600;
}

.trend-down {
  color: var(--color-danger);
  font-weight: 600;
}

.text-success {
  color: var(--color-success);
}

.text-warning {
  color: var(--color-warning);
}

.summary-row {
  margin-bottom: 16px;
}
</style>
