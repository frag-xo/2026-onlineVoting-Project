import service from '@/utils/request';

/**
 * 药品管理 API
 * 后端基础路径: /api/drug
 */

/**
 * 分页查询药品（完整版，支持筛选条件）
 * POST /api/drug/page
 */
export function drugPage(params: any) {
    return service.request({
        url: '/drug/page',
        method: 'post',
        data: params,
    });
}

/**
 * 简单分页查询（无条件分页）
 * GET /api/drug/page/simple
 */
export function drugPageSimple(params: any) {
    return service.request({
        url: '/drug/page/simple',
        method: 'get',
        params: params,
    });
}

/**
 * 根据名称分页查询
 * GET /api/drug/page/name
 */
export function drugPageByName(params: any) {
    return service.request({
        url: '/drug/page/name',
        method: 'get',
        params: params,
    });
}

/**
 * 关键词搜索分页
 * GET /api/drug/page/search
 */
export function drugSearch(params: any) {
    return service.request({
        url: '/drug/page/search',
        method: 'get',
        params: params,
    });
}

/**
 * 根据ID查询药品详情
 * GET /api/drug/{id}
 */
export function drugById(id: number | string) {
    return service.request({
        url: `/drug/${id}`,
        method: 'get',
    });
}

/**
 * 批量根据ID查询
 * POST /api/drug/ids
 */
export function drugByIds(ids: number[]) {
    return service.request({
        url: '/drug/ids',
        method: 'post',
        data: ids,
    });
}

/**
 * 新增药品
 * POST /api/drug
 */
export function drugAdd(data: any) {
    return service.request({
        url: '/drug',
        method: 'post',
        data: data,
    });
}

/**
 * 批量新增药品
 * POST /api/drug/batch
 */
export function drugAddBatch(data: any[]) {
    return service.request({
        url: '/drug/batch',
        method: 'post',
        data: data,
    });
}

/**
 * 修改药品
 * PUT /api/drug
 */
export function drugUpdate(data: any) {
    return service.request({
        url: '/drug',
        method: 'put',
        data: data,
    });
}

/**
 * 批量修改药品
 * PUT /api/drug/batch
 */
export function drugUpdateBatch(data: any[]) {
    return service.request({
        url: '/drug/batch',
        method: 'put',
        data: data,
    });
}

/**
 * 更新药品图片
 * PUT /api/drug/{id}/img
 */
export function drugUpdateImg(id: number | string, imgUrl: string) {
    return service.request({
        url: `/drug/${id}/img`,
        method: 'put',
        params: { imgUrl },
    });
}

/**
 * 更新药品发布者
 * PUT /api/drug/{id}/publisher
 */
export function drugUpdatePublisher(id: number | string, publisher: string) {
    return service.request({
        url: `/drug/${id}/publisher`,
        method: 'put',
        params: { publisher },
    });
}

/**
 * 根据ID删除药品
 * DELETE /api/drug/{id}
 */
export function drugDeleteById(id: number | string) {
    return service.request({
        url: `/drug/${id}`,
        method: 'delete',
    });
}

/**
 * 批量删除药品
 * DELETE /api/drug/batch
 */
export function drugDeleteBatch(ids: number[]) {
    return service.request({
        url: '/drug/batch',
        method: 'delete',
        data: ids,
    });
}

/**
 * 获取所有药品列表
 * GET /api/drug/all
 */
export function drugAll() {
    return service.request({
        url: '/drug/all',
        method: 'get',
    });
}

/**
 * 检查药品是否存在
 * GET /api/drug/{id}/exists
 */
export function drugExists(id: number | string) {
    return service.request({
        url: `/drug/${id}/exists`,
        method: 'get',
    });
}

/**
 * 检查药品名称是否重复
 * GET /api/drug/check-name
 */
export function drugCheckName(params: { drugName: string; excludeId?: number }) {
    return service.request({
        url: '/drug/check-name',
        method: 'get',
        params: params,
    });
}

/**
 * 生成随机药品数据
 * POST /api/drug/init/random
 */
export function drugInitRandom(count: number = 50) {
    return service.request({
        url: '/drug/init/random',
        method: 'post',
        params: { count },
    });
}
