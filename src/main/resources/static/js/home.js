/**
 * 温宿县农商平台 - 首页 JavaScript
 */

// API 基础路径
const API_BASE = '/api';

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    loadHomeData();
    loadCartCount(); // 加载购物车数量
});

/**
 * 加载首页数据
 */
async function loadHomeData() {
    try {
        const response = await axios.get(`${API_BASE}/home`);
        
        console.log('API 响应:', response);
        console.log('响应数据:', response.data);
        
        if (response.data.code === 200) {
            const data = response.data.data;
            
            console.log('首页数据:', data);
            console.log('轮播图:', data.banners);
            console.log('推荐商品:', data.recommendedProducts);
            console.log('苹果商品:', data.appleProducts);
            console.log('核桃商品:', data.walnutProducts);
            console.log('友情链接:', data.friendLinks);
            
            // 添加详细的字段检查
            if (data.recommendedProducts && data.recommendedProducts.length > 0) {
                console.log('=== 第一个推荐商品的原始数据 ===');
                console.log('完整对象:', JSON.stringify(data.recommendedProducts[0], null, 2));
                console.log('所有键名:', Object.keys(data.recommendedProducts[0]));
                console.log('所有键值:', Object.values(data.recommendedProducts[0]));
                
                // 尝试访问不同的字段名
                const firstProduct = data.recommendedProducts[0];
                console.log('尝试访问 pId:', firstProduct.pId);
                console.log('尝试访问 p_id:', firstProduct.p_id);
                console.log('尝试访问 id:', firstProduct.id);
                console.log('尝试访问 productId:', firstProduct.productId);
            }
            
            // 渲染各个模块
            renderBanners(data.banners);
            renderRecommendedProducts(data.recommendedProducts);
            renderAppleProducts(data.appleProducts);
            renderWalnutProducts(data.walnutProducts);
            renderFriendLinks(data.friendLinks);
        } else {
            showError('加载首页数据失败');
        }
    } catch (error) {
        console.error('加载首页数据错误:', error);
        showError('网络错误，请稍后重试');
    }
}

/**
 * 渲染轮播图
 */
function renderBanners(banners) {
    const wrapper = document.getElementById('banner-wrapper');
    
    if (!banners || banners.length === 0) {
        wrapper.innerHTML = `
            <div class="swiper-slide">
                <div class="empty-state">
                    <i class="fas fa-image"></i>
                    <p>暂无轮播广告</p>
                </div>
            </div>
        `;
        return;
    }
    
    wrapper.innerHTML = banners.map(banner => `
        <div class="swiper-slide">
            <img src="${banner.imageUrl || 'https://via.placeholder.com/1200x400?text=Banner'}" 
                 alt="${banner.title || '轮播图'}"
                 onclick="handleBannerClick('${banner.linkUrl || ''}')"
                 style="cursor: pointer;">
        </div>
    `).join('');
    
    // 初始化 Swiper
    new Swiper('.banner-swiper', {
        loop: true,
        autoplay: {
            delay: 3000,
            disableOnInteraction: false,
        },
        pagination: {
            el: '.swiper-pagination',
            clickable: true,
        },
        navigation: {
            nextEl: '.swiper-button-next',
            prevEl: '.swiper-button-prev',
        },
    });
}

/**
 * 处理轮播图点击
 */
function handleBannerClick(linkUrl) {
    if (linkUrl) {
        window.location.href = linkUrl;
    }
}

/**
 * 渲染推荐商品
 */
function renderRecommendedProducts(products) {
    const container = document.getElementById('recommended-products');
    
    if (!products || products.length === 0) {
        container.innerHTML = `
            <div class="col-12">
                <div class="empty-state">
                    <i class="fas fa-box-open"></i>
                    <p>暂无推荐商品</p>
                </div>
            </div>
        `;
        return;
    }
    
    console.log('=== 渲染推荐商品 ===');
    console.log('商品数量:', products.length);
    if (products.length > 0) {
        console.log('第一个商品:', products[0]);
        console.log('第一个商品的 pId:', products[0].pId);
        console.log('第一个商品的所有键:', Object.keys(products[0]));
        
        // 详细检查第一个商品的所有可能字段
        const first = products[0];
        console.log('=== 详细检查第一个商品 ===');
        for (let key in first) {
            console.log(key + ':', first[key]);
        }
    }
    
    container.innerHTML = products.map(product => createProductCard(product)).join('');
}

/**
 * 渲染苹果商品
 */
function renderAppleProducts(products) {
    const container = document.getElementById('apple-products');
    
    if (!products || products.length === 0) {
        container.innerHTML = `
            <div class="col-12">
                <div class="empty-state">
                    <i class="fas fa-apple-alt"></i>
                    <p>暂无苹果商品</p>
                </div>
            </div>
        `;
        return;
    }
    
    container.innerHTML = products.map(product => createProductCard(product)).join('');
}

/**
 * 渲染核桃商品
 */
function renderWalnutProducts(products) {
    const container = document.getElementById('walnut-products');
    
    if (!products || products.length === 0) {
        container.innerHTML = `
            <div class="col-12">
                <div class="empty-state">
                    <i class="fas fa-seedling"></i>
                    <p>暂无核桃商品</p>
                </div>
            </div>
        `;
        return;
    }
    
    container.innerHTML = products.map(product => createProductCard(product)).join('');
}

/**
 * 创建商品卡片 HTML
 */
function createProductCard(product) {
    console.log('商品数据:', product);
    
    // 适配后端返回的字段名（小写蛇形命名）
    const pId = product.pid || '';
    const pName = product.pname || '';
    const pPrice = product.pprice || 0;
    const pImage = product.pimage || '';
    const pDescription = product.pdescription || '';
    const originalPrice = product.originalPrice || 0;
    const category = product.category || '';
    const origin = product.origin || '';
    const sales = product.sales || 0;
    const discount = product.discount || 0;
    
    console.log('解析后的数据:');
    console.log('商品 ID:', pId);
    console.log('商品名称:', pName);
    console.log('商品价格:', pPrice);
    console.log('商品图片:', pImage);
    
    const categoryText = category === 'apple' ? '苹果' : '核桃';
    const categoryClass = category === 'apple' ? 'category-apple' : 'category-walnut';
    const discountText = discount ? `${discount}折` : '';
    
    return `
        <div class="col-md-6 col-lg-4">
            <div class="product-card" onclick="goToProductDetail('${pId}')">
                <img src="${pImage || 'https://via.placeholder.com/400x300?text=' + encodeURIComponent(pName || '商品')}" 
                     alt="${pName || '商品'}"
                     onerror="this.src='https://via.placeholder.com/400x300?text=商品图片加载失败'">
                <div class="product-card-body">
                    <h3 class="product-name">${pName || '商品名称'}</h3>
                    <p class="product-desc">${pDescription || ''}</p>
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span class="product-price">¥${pPrice ? Number(pPrice).toFixed(2) : '0.00'}</span>
                        ${discountText ? `<span class="discount-badge">${discountText}</span>` : ''}
                    </div>
                    ${originalPrice ? `<small class="product-original-price">原价：¥${Number(originalPrice).toFixed(2)}</small>` : ''}
                    <div class="d-flex justify-content-between align-items-center mt-2">
                        <small class="product-sales">销量：${sales || 0}</small>
                        <span class="badge ${categoryClass}">${categoryText}</span>
                    </div>
                    ${origin ? `
                        <div class="product-origin">
                            <i class="fas fa-map-marker-alt"></i>
                            <span>${origin}</span>
                        </div>
                    ` : ''}
                    <div class="mt-3 d-grid gap-2">
                        <button class="btn btn-outline-success btn-sm" onclick="event.stopPropagation(); addToCartByProductId('${pId}')">
                            <i class="fas fa-shopping-cart me-1"></i>加入购物车
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
}

/**
 * 渲染友情链接
 */
function renderFriendLinks(links) {
    const container = document.getElementById('friend-links');
    
    if (!links || links.length === 0) {
        container.innerHTML = '<p class="text-center text-muted">暂无友情链接</p>';
        return;
    }
    
    container.innerHTML = links.map(link => `
        <div class="col-auto">
            <a href="${link.linkUrl}" target="_blank" class="friend-link-card">
                ${link.logo ? `<img src="${link.logo}" alt="${link.linkName}" style="width: 40px; height: 40px; margin-bottom: 0.5rem;">` : '<i class="fas fa-link"></i>'}
                <div class="friend-link-name">${link.linkName}</div>
            </a>
        </div>
    `).join('');
}

/**
 * 跳转到商品详情页
 */
function goToProductDetail(pId) {
    console.log('=== 准备跳转到商品详情 ===');
    console.log('商品 pId:', pId);
    console.log('pId 类型:', typeof pId);
    console.log('pId 是否为空:', !pId);
    
    if (!pId) {
        console.error('错误：pId 为空！');
        alert('商品 ID 为空，无法跳转');
        return;
    }
    
    const url = `/product-detail.html?pId=${pId}`;
    console.log('跳转 URL:', url);
    window.location.href = url;
}

/**
 * 显示错误信息
 */
function showError(message) {
    alert(message);
}

/**
 * 根据商品 ID 加入购物车（用于首页）
 */
async function addToCartByProductId(pId) {
    console.log('=== 准备添加商品到购物车，pId:', pId, '===');
    
    if (!pId) {
        console.error('错误：商品 ID 为空');
        alert('商品 ID 为空，无法添加');
        return;
    }
    
    try {
        // 显示加载提示
        const btn = event.target.closest('button');
        const originalText = btn.innerHTML;
        btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> 添加中...';
        btn.disabled = true;
        
        // 调用加入购物车 API
        const response = await axios.post('/api/cart/add', null, {
            params: {
                userId: 'user001', // 临时使用固定用户 ID
                pId: pId,
                quantity: 1
            }
        });
        
        console.log('加入购物车响应:', response);
        
        if (response.data.code === 200 && response.data.data) {
            console.log('添加成功');
            // 恢复按钮状态
            btn.innerHTML = '<i class="fas fa-check"></i> 已添加';
            setTimeout(() => {
                btn.innerHTML = originalText;
                btn.disabled = false;
            }, 1500);
            
            // 显示成功提示
            alert('已成功添加到购物车！');
            
            // 更新购物车数量
            loadCartCount();
        } else {
            console.error('添加失败:', response.data);
            btn.innerHTML = originalText;
            btn.disabled = false;
            alert('添加失败：' + (response.data.msg || '未知错误'));
        }
    } catch (error) {
        console.error('加入购物车错误:', error);
        console.error('错误信息:', error.message);
        console.error('响应数据:', error.response?.data);
        
        // 恢复按钮状态
        const btn = event.target.closest('button');
        btn.innerHTML = '<i class="fas fa-shopping-cart me-1"></i>加入购物车';
        btn.disabled = false;
        
        alert('添加失败，请重试');
    }
}

/**
 * 加载购物车数量
 */
async function loadCartCount() {
    try {
        const response = await axios.get('/api/cart', {
            params: { userId: 'user001' }
        });
        
        if (response.data.code === 200 && response.data.data) {
            const itemCount = response.data.data.itemCount || 0;
            const badge = document.getElementById('cart-count-badge');
            
            if (badge) {
                if (itemCount > 0) {
                    badge.textContent = itemCount > 99 ? '99+' : itemCount;
                    badge.style.display = 'flex';
                } else {
                    badge.style.display = 'none';
                }
            }
        }
    } catch (error) {
        console.error('加载购物车数量失败:', error);
        // 静默失败，不影响用户体验
    }
}
