import request from './index'

// ========== 积分商城 ==========

// 1. 获取商品列表
export const getShopItems = () => {
    return request.get('/shop/items')
}

// 2. 购买商品
export const buyShopItem = (itemId: number) => {
    return request.post(`/shop/buy/${itemId}`)
}

// 3. 获取我的物品
export const getMyItems = () => {
    return request.get('/shop/my-items')
}

// 4. 装备/卸下物品
export const equipItem = (itemId: number, equip: boolean) => {
    return request.put(`/shop/equip/${itemId}`, null, {
        params: { equip }
    })
}

// 5. 使用改名卡
export const useNameCard = (itemId: number, newName: string) => {
    return request.put(`/shop/use/name-card/${itemId}`, null, {
        params: { newName }
    })
}