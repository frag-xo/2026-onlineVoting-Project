import service from '@/utils/request';

/**
 * 使用添加用户信息接口
 * @param data
 */
export function farmer_page(params: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/farmers_page',
        method: 'get',
        params: params,
        headers: {
            'Content-Type': 'application/json',
        },
    });

}

export function farmer(params: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/farmer_page',
        method: 'get',
        params: params,
        headers: {
            'Content-Type': 'application/json',
        },
    });

}
export function farmerByArea(id: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/farmerByArea/'+id,
        method: 'get',
        headers: {
            'Content-Type': 'application/json',
        },
    });

}





export function farmer_id(id: any) {
    return service.request({
        url: 'http://localhost:5173/api/farmer/'+id,
        method: 'get',
    });
}


export function farmerWithCountItem_page(params: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/farmerWithCountItem_page',
        method: 'get',
        params: params,
        headers: {
            'Content-Type': 'application/json',
        },
    });

}

export function farmer_add(data: any) {
    return service.request({
        url: 'http://localhost:5173/api/farmer',
        method: 'post',
        data: data,
        headers: {
            'Content-Type': 'application/json',
        },
    });
}

export function farmer_edit(data: any) {
    return service.request({
        url: 'http://localhost:5173/api/farmer',
        method: 'patch',
        data: data,
        headers: {
            'Content-Type': 'application/json',
        },
    });
}

export function farmer_delete(farmerId: any) {
    return service.request({
        url: `http://localhost:5173/api/farmer_delete/${farmerId}`,
        method: 'delete',
    });
}
// export function farmer_edit(data: any) {
//     return service.patch( 'http://localhost:5173/api/farmer',
//        data, {
//             headers: {
//                 'Content-Type': 'application/json'
//             }
//         }
//
//     );
// }