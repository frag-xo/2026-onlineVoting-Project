import service from '@/utils/request';

/**
 * 使用添加用户信息接口
 * @param data
 */
export function banners_page(params: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/banners_page',
        method: 'get',
        params: params,
        headers: {
            'Content-Type': 'application/json',
        },
    });

}

export function banners(params: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/banners_page',
        method: 'get',
        params: params,
        headers: {
            'Content-Type': 'application/json',
        },
    });

}

export function bannersTop4(params: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/bannersTop4',
        method: 'get',
        params: params,
        headers: {
            'Content-Type': 'application/json',
        },
    });

}

export function bannersByArea(id: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/bannersByArea/'+id,
        method: 'get',
        headers: {
            'Content-Type': 'application/json',
        },
    });

}





export function banner_id(id: any) {
    return service.request({
        url: 'http://localhost:5173/api/banner/'+id,
        method: 'get',
    });
}

export function bannerWithArea_id(id: any) {
    return service.request({
        url: 'http://localhost:5173/api/bannerWithArea/'+id,
        method: 'get',
    });
}

export function banner_add(data: any) {
    return service.request({
        url: 'http://localhost:5173/api/banner',
        method: 'post',
        data: data,
        headers: {
            'Content-Type': 'application/json',
        },
    });
}

export function banner_edit(data: any) {
    return service.request({
        url: 'http://localhost:5173/api/banner',
        method: 'patch',
        data: data,
        headers: {
            'Content-Type': 'application/json',
        },
    });
}

export function banner_delete(bannerId: any) {
    return service.request({
        url: `http://localhost:5173/api/banners_delete/${bannerId}`,
        method: 'delete',
    });
}
// export function banner_edit(data: any) {
//     return service.patch( 'http://localhost:5173/api/banner',
//        data, {
//             headers: {
//                 'Content-Type': 'application/json'
//             }
//         }
//
//     );
// }