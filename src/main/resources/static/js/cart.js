// 用户 ID（临时使用固定值，后续改为从登录信息获取）
const USER_ID = 'user001';

// 页面加载时获取购物车数据
document.addEventListener('DOMContentLoaded', function() {
    loadCart();
});

/**
 * 加载购物车数据
 */
async function loadCart() {
    try {
        console.log('=== 开始加载购物车数据 ===');
        const response = await axios.get('/api/cart', {
            params: { userId: USER_ID }
        });
        
        console.log('=== 购物车响应数据 ===');
        console.log('响应状态:', response.status);
        console.log('响应数据:', response.data);
        
        if (response.data.code === 200 && response.data.data) {
            const cartData = response.data.data;
            console.log('购物车商品数量:', cartData.itemCount);
            console.log('购物车总金额:', cartData.total);
            console.log('购物车商品列表:', cartData.items);
            
            // 检查第一个商品的字段
            if (cartData.items && cartData.items.length > 0) {
                console.log('=== 第一个商品的字段 ===');
                console.log('所有键名:', Object.keys(cartData.items[0]));
                console.log('第一个商品详情:', cartData.items[0]);
                console.log('pImage:', cartData.items[0].pImage);
                console.log('p_image:', cartData.items[0].p_image);
            }
            
            renderCart(cartData);
        } else {
            console.error('购物车数据格式错误:', response.data);
            showEmptyCart();
        }
    } catch (error) {
        console.error('=== 加载购物车失败 ===');
        console.error('错误信息:', error.message);
        console.error('完整错误:', error);
        showEmptyCart();
    }
}

/**
 * 渲染购物车
 */
function renderCart(cartData) {
    const container = document.getElementById('cart-content');
    
    if (!cartData.items || cartData.items.length === 0) {
        showEmptyCart();
        return;
    }
    
    const itemsHtml = cartData.items.map(item => {
        // 兼容驼峰命名和蛇形命名
        const pId = item.pId || item.p_id || '';
        const pName = item.pName || item.p_name || '';
        const pImage = item.pImage || item.p_image || '';
        const pPrice = item.pPrice || item.p_price || 0;
        const quantity = item.quantity || 1;
        const subtotal = item.subtotal || 0;
        
        return `
        <div class="cart-item" data-pid="${pId}">
            <div class="row align-items-center">
                <div class="col-md-2 mb-3 mb-md-0">
                    <img src="${pImage || 'https://via.placeholder.com/120x120?text=商品图片'}" 
                         alt="${pName}" 
                         class="cart-item-image"
                         onerror="this.src='https://via.placeholder.com/120x120?text=商品图片加载失败'">
                </div>
                <div class="col-md-4 mb-3 mb-md-0">
                    <h6 class="fw-bold mb-1">${pName}</h6>
                    <p class="text-muted small mb-0">商品编号：${pId}</p>
                </div>
                <div class="col-md-2 mb-3 mb-md-0 text-center">
                    <span class="price-highlight">¥${Number(pPrice).toFixed(2)}</span>
                </div>
                <div class="col-md-2 mb-3 mb-md-0">
                    <div class="quantity-control justify-content-center">
                        <button class="quantity-btn" onclick="updateQuantity('${pId}', ${quantity - 1})">
                            <i class="fas fa-minus"></i>
                        </button>
                        <span class="quantity-display">${quantity}</span>
                        <button class="quantity-btn" onclick="updateQuantity('${pId}', ${quantity + 1})">
                            <i class="fas fa-plus"></i>
                        </button>
                    </div>
                </div>
                <div class="col-md-2 text-center">
                    <span class="price-highlight">¥${Number(subtotal).toFixed(2)}</span>
                    <div class="mt-2">
                        <button class="btn btn-sm btn-outline-danger" onclick="removeFromCart('${pId}')">
                            <i class="fas fa-trash-alt me-1"></i>删除
                        </button>
                    </div>
                </div>
            </div>
        </div>
        `;
    }).join('');
    
    container.innerHTML = `
        <div class="cart-header d-none d-md-block">
            <div class="row">
                <div class="col-md-2"><strong>商品图片</strong></div>
                <div class="col-md-4"><strong>商品信息</strong></div>
                <div class="col-md-2 text-center"><strong>单价</strong></div>
                <div class="col-md-2 text-center"><strong>数量</strong></div>
                <div class="col-md-2 text-center"><strong>小计</strong></div>
            </div>
        </div>
        ${itemsHtml}
        <div class="cart-summary mt-4">
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h5 class="mb-0">购物车汇总</h5>
                <button class="btn btn-outline-secondary btn-sm" onclick="clearCart()">
                    <i class="fas fa-trash-alt me-1"></i>清空购物车
                </button>
            </div>
            <div class="d-flex justify-content-between align-items-center mb-3">
                <span class="text-muted">商品数量：</span>
                <span class="fw-bold">${cartData.itemCount} 件</span>
            </div>
            <div class="d-flex justify-content-between align-items-center mb-4">
                <span class="text-muted">商品金额：</span>
                <span class="price-highlight" style="font-size: 1.5rem;">¥${Number(cartData.total).toFixed(2)}</span>
            </div>
            <div class="d-grid gap-2">
                <button class="btn btn-success btn-lg" onclick="checkout()">
                    <i class="fas fa-cash-register me-2"></i>结算
                </button>
                <button class="btn btn-outline-success" onclick="continueShopping()">
                    <i class="fas fa-arrow-left me-2"></i>继续购物
                </button>
            </div>
        </div>
    `;
}

/**
 * 显示空购物车
 */
function showEmptyCart() {
    const container = document.getElementById('cart-content');
    container.innerHTML = `
        <div class="empty-cart">
            <i class="fas fa-shopping-cart"></i>
            <h4 class="text-muted mb-3">购物车空空如也</h4>
            <p class="text-muted mb-4">快去挑选心仪的商品吧！</p>
            <button class="btn btn-success btn-lg" onclick="continueShopping()">
                <i class="fas fa-shopping-bag me-2"></i>去购物
            </button>
        </div>
    `;
}

/**
 * 更新商品数量
 */
async function updateQuantity(pId, quantity) {
    if (quantity <= 0) {
        removeFromCart(pId);
        return;
    }
    
    try {
        console.log(`=== 更新商品数量：pId=${pId}, quantity=${quantity} ===`);
        const response = await axios.put('/api/cart/update', null, {
            params: {
                userId: USER_ID,
                pId: pId,
                quantity: quantity
            }
        });
        
        if (response.data.code === 200 && response.data.data) {
            console.log('数量更新成功');
            // 重新加载购物车
            loadCart();
        } else {
            console.error('更新数量失败:', response.data);
            alert('更新数量失败');
        }
    } catch (error) {
        console.error('=== 更新商品数量失败 ===');
        console.error('错误信息:', error.message);
        alert('更新数量失败，请重试');
    }
}

/**
 * 删除商品
 */
async function removeFromCart(pId) {
    if (!confirm('确定要删除该商品吗？')) {
        return;
    }
    
    try {
        console.log(`=== 删除商品：pId=${pId} ===`);
        const response = await axios.delete('/api/cart/remove', {
            params: {
                userId: USER_ID,
                pId: pId
            }
        });
        
        if (response.data.code === 200 && response.data.data) {
            console.log('删除成功');
            // 重新加载购物车
            loadCart();
        } else {
            console.error('删除失败:', response.data);
            alert('删除失败');
        }
    } catch (error) {
        console.error('=== 删除商品失败 ===');
        console.error('错误信息:', error.message);
        alert('删除失败，请重试');
    }
}

/**
 * 清空购物车
 */
async function clearCart() {
    if (!confirm('确定要清空购物车吗？')) {
        return;
    }
    
    try {
        console.log('=== 清空购物车 ===');
        const response = await axios.delete('/api/cart/clear', {
            params: {
                userId: USER_ID
            }
        });
        
        if (response.data.code === 200 && response.data.data) {
            console.log('清空成功');
            // 重新加载购物车
            loadCart();
        } else {
            console.error('清空失败:', response.data);
            alert('清空失败');
        }
    } catch (error) {
        console.error('=== 清空购物车失败 ===');
        console.error('错误信息:', error.message);
        alert('清空失败，请重试');
    }
}

/**
 * 结算
 */
function checkout() {
    alert('结算功能待开发\n(需要实现订单和支付功能)');
    console.log('Checkout clicked');
}

/**
 * 继续购物
 */
function continueShopping() {
    window.location.href = '/';
}
