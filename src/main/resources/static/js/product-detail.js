/**
 * 温宿县农商平台 - 商品详情页 JavaScript
 */

// API 基础路径
const API_BASE = '/api';

// 获取 URL 参数
function getUrlParam(name) {
    console.log('=== 尝试获取 URL 参数:', name, '===');
    console.log('当前完整 URL:', window.location.href);
    console.log('当前 search 部分:', window.location.search);
    
    const params = new URLSearchParams(window.location.search);
    const value = params.get(name);
    console.log(`参数 ${name} 的值:`, value);
    
    return value;
}

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    console.log('=== DOM 加载完成 ===');
    const pId = getUrlParam('pId');
    
    console.log('=== 获取到的 pId:', pId, '===');
    
    if (!pId) {
        console.error('=== pId 为空，显示错误提示 ===');
        showError('商品 ID 缺失');
        return;
    }
    
    loadProductDetail(pId);
    loadCartCount(); // 加载购物车数量
});

/**
 * 加载商品详情
 */
async function loadProductDetail(pId) {
    try {
        console.log('=== 开始加载商品详情，pId:', pId, '===');
        const response = await axios.get(`${API_BASE}/product/${pId}`);
        
        console.log('=== API 响应:', response, '===');
        console.log('=== 响应数据:', response.data, '===');
        
        if (response.data.code === 200) {
            const data = response.data.data;
            console.log('=== 商品详情数据:', data, '===');
            console.log('=== 商品对象:', data.product, '===');
            
            // 渲染商品信息
            renderProductInfo(data.product);
            
            // 渲染主图和缩略图
            renderProductImages(data.product, data.detailImageList);
            
            // 渲染详情图片
            renderDetailImages(data.detailImageList);
            
            // 渲染顺手买一单
            renderBuyAgainRecommendation(data.buyAgainRecommendation);
        } else {
            console.error('=== 响应错误:', response.data.message, '===');
            showError(response.data.message || '加载商品详情失败');
        }
    } catch (error) {
        console.error('=== 加载商品详情错误:', error, '===');
        showError('网络错误，请稍后重试');
    }
}

/**
 * 渲染商品信息
 */
function renderProductInfo(product) {
    console.log('=== 渲染商品信息，product:', product, '===');
    
    if (!product) {
        console.error('=== 商品信息不存在 ===');
        showError('商品信息不存在');
        return;
    }
    
    // 适配后端返回的字段名（小写蛇形命名）
    const pId = product.pid || '';
    const pName = product.pname || '';
    const pPrice = product.pprice || 0;
    const pDescription = product.pdescription || '';
    const originalPrice = product.originalPrice || 0;
    const category = product.category || '';
    const origin = product.origin || '';
    const specification = product.specification || '';
    const sales = product.sales || 0;
    const discount = product.discount || 0;
    
    console.log('解析后的数据:');
    console.log('商品 ID:', pId);
    console.log('商品名称:', pName);
    console.log('商品价格:', pPrice);
    
    // 设置页面标题
    document.title = `${pName || '商品详情'} - 温宿县农商平台`;
    
    // 商品名称
    const nameElement = document.getElementById('product-name');
    if (nameElement) {
        nameElement.textContent = pName || '商品名称';
    }
    
    // 分类
    const categoryText = category === 'apple' ? '苹果' : '核桃';
    const categoryClass = category === 'apple' ? 'category-apple' : 'category-walnut';
    const badge = document.getElementById('category-badge');
    if (badge) {
        badge.textContent = categoryText;
        badge.className = `badge me-2 ${categoryClass}`;
    }
    
    // 面包屑导航
    const categoryNameElement = document.getElementById('category-name');
    if (categoryNameElement) {
        categoryNameElement.textContent = `${categoryText} - 商品详情`;
    }
    
    // 产地
    const originElement = document.getElementById('product-origin');
    if (originElement) {
        originElement.textContent = origin || '未知产地';
    }
    
    // 描述
    const descElement = document.getElementById('product-description');
    if (descElement) {
        descElement.textContent = pDescription || '';
    }
    
    // 价格
    const priceElement = document.getElementById('product-price');
    if (priceElement) {
        priceElement.textContent = pPrice ? Number(pPrice).toFixed(2) : '0.00';
    }
    
    const originalPriceElement = document.getElementById('original-price');
    if (originalPriceElement) {
        originalPriceElement.textContent = originalPrice ? `¥${Number(originalPrice).toFixed(2)}` : '';
    }
    
    // 折扣
    const discountElement = document.getElementById('discount-badge');
    if (discountElement) {
        discountElement.textContent = discount ? `${discount}折` : '';
    }
    
    // 规格
    const specElement = document.getElementById('product-spec');
    if (specElement) {
        specElement.textContent = specification || '无规格';
    }
    
    // 销量
    const salesElement = document.getElementById('product-sales');
    if (salesElement) {
        salesElement.textContent = sales || 0;
    }
    
    // 绑定购买按钮事件 - 使用解析后的正确数据
    const addToCartBtn = document.getElementById('add-to-cart');
    if (addToCartBtn) {
        addToCartBtn.onclick = () => addToCart({
            pId: pId,
            pName: pName,
            pPrice: pPrice,
            pImage: product.pImage || product.pimage || ''
        });
    }
    
    const buyNowBtn = document.getElementById('buy-now');
    if (buyNowBtn) {
        buyNowBtn.onclick = () => buyNow(product);
    }
}

/**
 * 渲染商品主图和缩略图
 */
function renderProductImages(product, detailImages) {
    const mainWrapper = document.getElementById('main-image-wrapper');
    const thumbWrapper = document.getElementById('thumb-image-wrapper');
    
    // 收集所有图片
    const allImages = [];
    
    // 添加主图
    if (product.pImage) {
        allImages.push(product.pImage);
    }
    
    // 添加详情图片
    if (detailImages && detailImages.length > 0) {
        allImages.push(...detailImages);
    }
    
    // 如果没有图片，使用占位图
    if (allImages.length === 0) {
        allImages.push('https://via.placeholder.com/600x600?text=' + encodeURIComponent(product.pName));
    }
    
    // 渲染主图
    mainWrapper.innerHTML = allImages.map((img, index) => `
        <div class="swiper-slide">
            <img src="${img}" 
                 alt="${product.pName} - 图片${index + 1}"
                 onerror="this.src='https://via.placeholder.com/600x600?text=图片加载失败'">
        </div>
    `).join('');
    
    // 渲染缩略图 (只显示前 5 张)
    const thumbImages = allImages.slice(0, 5);
    thumbWrapper.innerHTML = thumbImages.map(img => `
        <div class="swiper-slide">
            <img src="${img}" 
                 alt="缩略图"
                 onerror="this.src='https://via.placeholder.com/100x100?text=缩略图'">
        </div>
    `).join('');
    
    // 初始化主图 Swiper
    const mainSwiper = new Swiper('.product-image-swiper', {
        loop: true,
        pagination: {
            el: '.swiper-pagination',
            clickable: true,
        },
        thumbs: {
            swiper: null, // 稍后绑定
        },
    });
    
    // 初始化缩略图 Swiper
    const thumbSwiper = new Swiper('.product-thumb-swiper', {
        loop: false,
        spaceBetween: 10,
        slidesPerView: 4,
        watchSlidesProgress: true,
    });
    
    // 绑定两个 Swiper
    mainSwiper.params.thumbs.swiper = thumbSwiper;
    mainSwiper.init();
}

/**
 * 渲染详情图片
 */
function renderDetailImages(detailImages) {
    const container = document.getElementById('detail-images');
    
    if (!detailImages || detailImages.length === 0) {
        container.innerHTML = '<p class="text-center text-muted">暂无详情图片</p>';
        return;
    }
    
    container.innerHTML = detailImages.map(img => `
        <img src="${img}" 
             alt="详情图片"
             class="img-fluid"
             onerror="this.src='https://via.placeholder.com/800x600?text=图片加载失败'">
    `).join('');
}

/**
 * 渲染顺手买一单推荐
 */
function renderBuyAgainRecommendation(products) {
    const container = document.getElementById('recommend-products');
    
    if (!products || products.length === 0) {
        container.innerHTML = `
            <div class="col-12">
                <div class="empty-state">
                    <i class="fas fa-hand-holding-heart"></i>
                    <p>暂无推荐商品</p>
                </div>
            </div>
        `;
        return;
    }
    
    container.innerHTML = products.map(product => `
        <div class="col-md-6 col-lg-3">
            <div class="recommend-card" onclick="goToProductDetail('${product.pId}')">
                <img src="${product.pImage || 'https://via.placeholder.com/300x200?text=' + encodeURIComponent(product.pName)}" 
                     alt="${product.pName}"
                     onerror="this.src='https://via.placeholder.com/300x200?text=商品图片'">
                <div class="recommend-info">
                    <h4 class="recommend-name">${product.pName}</h4>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="recommend-price">¥${product.pPrice ? product.pPrice.toFixed(2) : '0.00'}</span>
                        <small class="text-muted">销量：${product.sales || 0}</small>
                    </div>
                </div>
            </div>
        </div>
    `).join('');
}

/**
 * 跳转到商品详情页
 */
function goToProductDetail(pId) {
    window.location.href = `/product-detail.html?pId=${pId}`;
}

/**
 * 加入购物车
 */
async function addToCart(product) {
    console.log('=== 准备添加商品到购物车 ===');
    console.log('商品信息:', product);
    
    const pId = product.pId;
    const pName = product.pName;
    
    if (!pId) {
        console.error('错误：商品 ID 为空');
        alert('商品 ID 为空，无法添加');
        return;
    }
    
    try {
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
            // 显示成功提示
            alert(`已将"${pName}"添加到购物车！`);
            
            // 更新购物车数量
            loadCartCount();
        } else {
            console.error('添加失败:', response.data);
            alert('添加失败：' + (response.data.msg || '未知错误'));
        }
    } catch (error) {
        console.error('加入购物车错误:', error);
        console.error('错误信息:', error.message);
        console.error('响应数据:', error.response?.data);
        
        // 提供更详细的错误信息
        let errorMsg = '添加失败，请重试';
        if (error.message === 'Network Error') {
            errorMsg = '网络连接失败，请检查后端服务是否启动（端口 8888）';
        } else if (error.response) {
            errorMsg = error.response.data?.msg || error.response.data?.message || '添加失败';
        }
        
        alert(errorMsg);
    }
}

/**
 * 立即购买
 */
function buyNow(product) {
    // TODO: 实现购买功能
    alert(`即将购买"${product.pName}"\n(购买功能待开发)`);
    console.log('Buy now:', product);
}

/**
 * 显示错误信息
 */
function showError(message) {
    alert(message);
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
