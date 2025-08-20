<template>
  <div class="home-page">
    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">发现世界的美好瞬间</h1>
        <p class="hero-subtitle">精选全球优质图片，让创意无限可能</p>
        
        <!-- 搜索区域 -->
        <div class="search-section">
          <div class="search-container">
            <input type="text" placeholder="搜索你想要的图片..." class="search-input" />
            <select class="category-select">
              <option value="">所有分类</option>
              <option value="nature">自然风景</option>
              <option value="city">城市建筑</option>
              <option value="people">人物肖像</option>
            </select>
            <button class="search-btn">搜索</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 推荐图片展示区 -->
    <section class="featured-section">
      <div class="section-header">
        <h2 class="section-title">精选推荐</h2>
        <p class="section-subtitle">发现最受欢迎的优质图片</p>
      </div>
      
      <div class="image-grid">
        <div class="image-card" v-for="(image, index) in featuredImages" :key="index">
          <div class="image-wrapper">
            <img :src="image.url" :alt="image.title" class="card-image" />
            <div class="image-overlay">
              <h3 class="image-title">{{ image.title }}</h3>
              <div class="image-meta">
                <span class="author-name">{{ image.author.name }}</span>
                <span class="likes">❤️ {{ image.likes }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 分类快速入口 -->
    <section class="categories-section">
      <div class="section-header">
        <h2 class="section-title" style="color: white;">探索分类</h2>
        <p class="section-subtitle" style="color: white;">按主题浏览精彩内容</p>
      </div>
      
      <div class="categories-container">
        <div class="category-card" v-for="(category, index) in categories" :key="index">
          <h3 class="category-name">{{ category.name }}</h3>
          <p class="category-count">{{ category.count }} 张图片</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// 推荐图片数据
const featuredImages = ref([
  {
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop',
    title: '雪山日出',
    author: { name: '摄影师小王' },
    likes: 128
  },
  {
    url: 'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=400&h=300&fit=crop',
    title: '森林小径',
    author: { name: '自然摄影' },
    likes: 89
  },
  {
    url: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=400&h=300&fit=crop',
    title: '城市夜景',
    author: { name: '城市探索者' },
    likes: 156
  }
])

// 分类数据
const categories = ref([
  { name: '自然风景', count: 1250 },
  { name: '城市建筑', count: 890 },
  { name: '人物肖像', count: 756 },
  { name: '美食佳肴', count: 634 },
  { name: '旅行摄影', count: 445 }
])
</script>

<script lang="ts">
export default {
  name: 'HomePage'
}
</script>

<style scoped>
.home-page {
  width: 100%;
  min-height: 100vh;
  opacity: 0.9;
}

/* 英雄区域样式 */
.hero-section {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  color: white;
  text-align: center;
  padding: 2rem;
}

.hero-title {
  font-size: 3rem;
  font-weight: 800;
  margin-bottom: 1rem;
  text-shadow: 0 0 20px rgba(255, 255, 255, 0.3);
}

.hero-subtitle {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.search-container {
  display: flex;
  gap: 1rem;
  max-width: 500px;
  margin: 0 auto;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 50px;
  padding: 0.5rem;
}

.search-input {
  flex: 1;
  border: none;
  background: white;
  border-radius: 25px;
  padding: 0.75rem 1rem;
  outline: none;
}

.category-select {
  background: white;
  border: none;
  border-radius: 25px;
  padding: 0.75rem 1rem;
  outline: none;
}

.search-btn {
  background: linear-gradient(45deg, #ff6b6b, #4ecdc4);
  border: none;
  border-radius: 25px;
  padding: 0.75rem 2rem;
  color: white;
  font-weight: 600;
  cursor: pointer;
}

/* 通用区域样式 */
.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 1rem;
}

.section-subtitle {
  font-size: 1.1rem;
  color: #64748b;
}

/* 推荐图片区域 */
.featured-section {
  padding: 5rem 2rem;
  background: transparent;
  width: 100%;
  max-width: 100%;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
}

.image-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  width: 100%;
}

.image-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.card-image {
  width: 100%;
  height: 250px;
  object-fit: cover;
}

.image-overlay {
  padding: 1.5rem;
}

.image-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #1e293b;
}

.image-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-name {
  color: #64748b;
  font-size: 0.9rem;
}

.likes {
  color: #ef4444;
  font-weight: 600;
}

/* 分类区域 */
.categories-section {
  padding: 5rem 2rem;
  background: transparent;
  width: 100%;
  max-width: 100%;
}

.categories-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  width: 100%;
  max-width: 100%;
  margin: 0;
  padding: 0;
}

.category-card {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  width: 100%;
}

.category-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
}

.category-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 0.5rem;
}

.category-count {
  color: #64748b;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .hero-section {
    padding: 2rem 1.5rem;
  }
  
  .featured-section,
  .categories-section {
    padding: 4rem 1.5rem;
  }
  
  .image-grid {
    gap: 1.5rem;
  }
  
  .categories-container {
    gap: 1.5rem;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-subtitle {
    font-size: 1.1rem;
  }
  
  .search-container {
    flex-direction: column;
    max-width: 100%;
    border-radius: 16px;
  }
  
  .search-input,
  .category-select,
  .search-btn {
    border-radius: 12px;
  }
  
  .image-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .categories-container {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 1rem;
  }
  
  .featured-section,
  .categories-section {
    padding: 3rem 1rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .section-subtitle {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .hero-section {
    padding: 1.5rem 1rem;
    min-height: 50vh;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .search-container {
    padding: 0.25rem;
  }
  
  .search-input,
  .category-select,
  .search-btn {
    padding: 0.5rem 0.75rem;
    font-size: 0.9rem;
  }
  
  .featured-section,
  .categories-section {
    padding: 2rem 0.75rem;
  }
  
  .image-grid {
    gap: 1rem;
  }
  
  .categories-container {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .image-card {
    border-radius: 12px;
  }
  
  .image-overlay {
    padding: 1rem;
  }
  
  .image-title {
    font-size: 1.1rem;
  }
  
  .category-card {
    padding: 1.5rem;
    border-radius: 12px;
  }
  
  .category-name {
    font-size: 1.1rem;
  }
}

/* 追加暗色主题覆盖 */
.image-card { background: var(--bg-surface-strong); box-shadow: 0 4px 20px rgba(0,0,0,.25); }
.image-title { color: var(--text-primary); }
.author-name { color: var(--text-secondary); }
.likes { color: #fda4af; }

.category-card { background: var(--bg-surface); box-shadow: 0 4px 20px rgba(0,0,0,.2); }
.category-name { color: var(--text-primary); }
.category-count { color: var(--text-secondary); }

/* 精选推荐标题与副标题使用纯白 */
.featured-section .section-title { color: #ffffff; }
.featured-section .section-subtitle { color: #ffffff; }
</style> 