<template>
  <div class="notfound-page">
    <div class="notfound-inner">
      <div class="notfound-art">
        <div class="art-symbol">?</div>
        <div class="art-shadow"></div>
      </div>
      <div class="notfound-content">
        <h1 class="notfound-code">404</h1>
        <p class="notfound-title">找不到这个页面</p>
        <p class="notfound-desc">
          你想要找的页面可能已被删除，或者地址有误。
        </p>
        <div class="notfound-actions">
          <router-link to="/" class="btn-home">回到首页</router-link>
          <button class="btn-back" @click="$router.back()">← 返回上一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.notfound-page {
  min-height: calc(100vh - 72px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.notfound-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  gap: 0;
  animation: fadeUp 0.6s both;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.notfound-art {
  margin-bottom: 0;
  position: relative;
  width: 120px;
  height: 120px;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.art-symbol {
  font-size: 5rem;
  line-height: 1;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12px); }
}

.art-shadow {
  position: absolute;
  bottom: -8px;
  width: 60px;
  height: 10px;
  background: rgba(0,0,0,0.1);
  border-radius: 50%;
  animation: shadow 3s ease-in-out infinite;
}

@keyframes shadow {
  0%, 100% { transform: scaleX(1); opacity: 0.1; }
  50% { transform: scaleX(0.7); opacity: 0.2; }
}

.notfound-content {
  margin-top: 24px;
}

.notfound-code {
  font-size: 4rem;
  font-weight: 800;
  color: var(--text);
  line-height: 1;
  margin-bottom: 8px;
  letter-spacing: -2px;
}

.notfound-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 10px;
}

.notfound-desc {
  font-size: 14px;
  color: var(--text-lighter);
  max-width: 320px;
  line-height: 1.7;
  margin-bottom: 28px;
}

.notfound-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.btn-home {
  padding: 10px 24px;
  background: var(--text);
  color: #fff;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  text-decoration: none;
}

.btn-home:hover {
  background: var(--accent);
  color: #fff;
  transform: translateY(-1px);
}

.btn-back {
  padding: 10px 24px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(148, 226, 255, 0.3);
  color: rgba(226, 239, 255, 0.9);
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-back:hover {
  border-color: rgba(96, 165, 250, 0.6);
  color: #fff;
  background: rgba(96, 165, 250, 0.1);
}
</style>
