<template>
  <div class="home">
    <!-- 背景装饰 -->
    <div class="home__bg">
      <div class="home__bg-overlay"></div>
      <div class="home__bg-shapes">
        <div class="shape shape--1"></div>
        <div class="shape shape--2"></div>
        <div class="shape shape--3"></div>
        <div class="shape shape--4"></div>
        <div class="shape shape--5"></div>
      </div>
    </div>

    <!-- 顶部导航 -->
    <header class="home__header">
      <div class="home__logo">
        <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5S10.62 6.5 12 6.5s2.5 1.12 2.5 2.5S13.38 11.5 12 11.5z" fill="currentColor"/>
        </svg>
        <span>AI 旅行规划大师</span>
      </div>
    </header>

    <!-- 主要内容区 -->
    <main class="home__main">
      <div class="home__hero">
        <h1 class="home__title">
          <span class="home__title-line">探索世界，</span>
          <span class="home__title-line home__title-line--accent">从智能规划开始</span>
        </h1>
        <p class="home__subtitle">
          基于 AI 大模型的智能旅行规划助手，为您定制专属旅程
        </p>
      </div>

      <!-- 应用卡片 -->
      <div class="home__cards">
        <!-- AI Trip 卡片 -->
        <router-link to="/ai-trip" class="app-card app-card--trip">
          <div class="app-card__image">
            <img
              src="https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=600&h=400&fit=crop&auto=format"
              alt="旅行风光"
              loading="lazy"
            />
            <div class="app-card__image-overlay"></div>
            <div class="app-card__icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M17.8 19.2L12 14.4l-5.8 4.8L3 21l9-15 9 15-3.2-1.8zM12 2l9 15H3L12 2z" fill="currentColor" opacity="0.9"/>
                <circle cx="12" cy="9" r="2" fill="currentColor"/>
              </svg>
            </div>
          </div>
          <div class="app-card__body">
            <h2 class="app-card__title">AI Trip 旅行规划</h2>
            <p class="app-card__desc">
              智能旅行规划大师，为您量身定制行程路线、景点推荐、住宿安排，让每一次旅行都精彩纷呈
            </p>
            <div class="app-card__features">
              <span class="feature-tag">🗺️ 行程规划</span>
              <span class="feature-tag">🏨 酒店推荐</span>
              <span class="feature-tag">🍜 美食探索</span>
            </div>
            <div class="app-card__action">
              <span>开始规划</span>
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </router-link>

        <!-- Manus 卡片 -->
        <router-link to="/manus" class="app-card app-card--manus">
          <div class="app-card__image">
            <img
              src="https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&h=400&fit=crop&auto=format"
              alt="智能科技"
              loading="lazy"
            />
            <div class="app-card__image-overlay"></div>
            <div class="app-card__icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2a4 4 0 0 1 4 4c0 1.1-.44 2.1-1.17 2.83l1.41 1.42A5.98 5.98 0 0 1 18 6a6 6 0 0 0-12 0c0 1.62.64 3.09 1.76 4.18l1.41-1.42A3.98 3.98 0 0 1 8 6a4 4 0 0 1 4-4z" fill="currentColor" opacity="0.6"/>
                <path d="M12 8a4 4 0 0 0-4 4c0 1.1.44 2.1 1.17 2.83l-1.41 1.42A5.98 5.98 0 0 1 6 12a6 6 0 0 1 12 0c0 1.62-.64 3.09-1.76 4.18l-1.41-1.42A3.98 3.98 0 0 0 16 12a4 4 0 0 0-4-4z" fill="currentColor" opacity="0.8"/>
                <circle cx="12" cy="16" r="3" fill="currentColor"/>
              </svg>
            </div>
          </div>
          <div class="app-card__body">
            <h2 class="app-card__title">AI 超级智能体</h2>
            <p class="app-card__desc">
              汇集多种 AI 工具的超级智能代理，强大的推理与执行能力，帮您完成各类复杂任务
            </p>
            <div class="app-card__features">
              <span class="feature-tag">🧠 深度推理</span>
              <span class="feature-tag">🔧 工具调用</span>
              <span class="feature-tag">⚡ 高效执行</span>
            </div>
            <div class="app-card__action">
              <span>开始对话</span>
              <svg viewBox="0 0 24 24" fill="none">
                <path d="M5 12h14M12 5l7 7-7 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
          </div>
        </router-link>
      </div>
    </main>

    <!-- 底部信息 -->
    <footer class="home__footer">
      <p>Powered by Spring AI &amp; DeepSeek · 让 AI 成为您的专属旅行管家</p>
    </footer>
  </div>
</template>

<script setup>
</script>

<style scoped>
.home {
  min-height: 100vh;
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ===== 背景 ===== */
.home__bg {
  position: fixed;
  inset: 0;
  z-index: 0;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 40%, #0f2a1f 70%, #1a1a2e 100%);
}

.home__bg-overlay {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 20% 20%, rgba(56, 189, 248, 0.12) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 60%, rgba(34, 197, 94, 0.1) 0%, transparent 50%),
    radial-gradient(ellipse at 50% 80%, rgba(168, 85, 247, 0.08) 0%, transparent 50%);
}

/* 装饰形状 */
.home__bg-shapes {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.06;
  filter: blur(60px);
}

.shape--1 {
  width: 500px;
  height: 500px;
  background: #38bdf8;
  top: -10%;
  left: -5%;
  animation: floatShape 20s ease-in-out infinite;
}

.shape--2 {
  width: 400px;
  height: 400px;
  background: #34d399;
  top: 50%;
  right: -8%;
  animation: floatShape 25s ease-in-out infinite reverse;
}

.shape--3 {
  width: 350px;
  height: 350px;
  background: #a78bfa;
  bottom: -10%;
  left: 30%;
  animation: floatShape 18s ease-in-out infinite 3s;
}

.shape--4 {
  width: 250px;
  height: 250px;
  background: #f59e0b;
  top: 30%;
  left: 60%;
  animation: floatShape 22s ease-in-out infinite 5s;
}

.shape--5 {
  width: 300px;
  height: 300px;
  background: #f472b6;
  bottom: 20%;
  right: 25%;
  animation: floatShape 19s ease-in-out infinite 7s;
}

@keyframes floatShape {
  0%, 100% { transform: translate(0, 0) scale(1); }
  25% { transform: translate(30px, -40px) scale(1.08); }
  50% { transform: translate(-20px, 20px) scale(0.95); }
  75% { transform: translate(15px, -10px) scale(1.03); }
}

/* ===== 头部导航 ===== */
.home__header {
  position: relative;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 40px;
}

.home__logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #f1f5f9;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: -0.02em;
}

.home__logo svg {
  width: 28px;
  height: 28px;
  color: #38bdf8;
}

/* ===== 主题内容 ===== */
.home__main {
  position: relative;
  z-index: 10;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px 60px;
}

.home__hero {
  text-align: center;
  margin-bottom: 56px;
}

.home__title {
  font-size: 56px;
  font-weight: 700;
  letter-spacing: -0.03em;
  line-height: 1.15;
  margin: 0 0 20px 0;
}

.home__title-line {
  display: block;
  color: #f1f5f9;
}

.home__title-line--accent {
  background: linear-gradient(135deg, #38bdf8 0%, #a78bfa 50%, #f472b6 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-size: 200% 200%;
  animation: gradientShift 4s ease-in-out infinite;
}

@keyframes gradientShift {
  0%, 100% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
}

.home__subtitle {
  font-size: 18px;
  color: #94a3b8;
  max-width: 500px;
  margin: 0 auto;
  line-height: 1.6;
  font-weight: 300;
}

/* ===== 应用卡片 ===== */
.home__cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 28px;
  max-width: 960px;
  width: 100%;
}

.app-card {
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  overflow: hidden;
  text-decoration: none;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
}

.app-card:hover {
  transform: translateY(-6px);
  border-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.app-card--trip:hover {
  background: rgba(56, 189, 248, 0.08);
}

.app-card--manus:hover {
  background: rgba(168, 85, 247, 0.08);
}

.app-card__image {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.app-card__image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.app-card:hover .app-card__image img {
  transform: scale(1.08);
}

.app-card__image-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to bottom,
    transparent 40%,
    rgba(15, 23, 42, 0.6) 100%
  );
}

.app-card__icon {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.app-card__icon svg {
  width: 24px;
  height: 24px;
}

.app-card__body {
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.app-card__title {
  font-size: 22px;
  font-weight: 600;
  color: #f1f5f9;
  margin: 0 0 10px 0;
  letter-spacing: -0.01em;
}

.app-card__desc {
  font-size: 14px;
  color: #94a3b8;
  line-height: 1.6;
  margin: 0 0 16px 0;
  flex: 1;
}

.app-card__features {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.feature-tag {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.08);
  color: #cbd5e1;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.app-card__action {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #38bdf8;
  transition: gap 0.3s ease;
}

.app-card:hover .app-card__action {
  gap: 14px;
}

.app-card__action svg {
  width: 18px;
  height: 18px;
}

.app-card--manus .app-card__action {
  color: #a78bfa;
}

/* ===== 底部 ===== */
.home__footer {
  position: relative;
  z-index: 10;
  text-align: center;
  padding: 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.home__footer p {
  font-size: 13px;
  color: #64748b;
  margin: 0;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .home__header {
    padding: 20px 24px;
  }

  .home__title {
    font-size: 36px;
  }

  .home__subtitle {
    font-size: 15px;
    padding: 0 20px;
  }

  .home__cards {
    grid-template-columns: 1fr;
    max-width: 440px;
    gap: 20px;
  }

  .app-card__image {
    height: 140px;
  }

  .home__main {
    padding: 30px 16px 50px;
  }
}
</style>
