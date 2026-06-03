<template>
  <aside class="sidebar">
    <div class="sidebar-section">
      <h3>导航</h3>
      <ul>
        <li><router-link to="/">首页</router-link></li>
        <li>问答</li>
        <li>文章</li>
        <li>标签</li>
      </ul>
    </div>
    <div class="sidebar-section">
      <h3>热门话题</h3>
      <div class="placeholder">即将上线 ✨</div>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 240px;
  padding: 20px 16px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  min-height: calc(100vh - 60px);
  flex-shrink: 0;
}

.sidebar-section {
  margin-bottom: 24px;
}

.sidebar-section h3 {
  font-size: 14px;
  color: #303133;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
}

.sidebar-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-section li {
  padding: 8px 12px;
  color: #606266;
  cursor: pointer;
  border-radius: 6px;
  font-size: 14px;
}

.sidebar-section li:hover {
  background: #ecf5ff;
  color: #409eff;
}

.sidebar-section li a {
  color: inherit;
  text-decoration: none;
}

.placeholder {
  color: #c0c4cc;
  font-size: 13px;
  text-align: center;
  padding: 20px 0;
}
</style>
