import service from '@/utils/request';

/**
 * 使用添加用户信息接口
 * @param data
 */
export function customer_page(params: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/customers_page',
        method: 'get',
        params: params,
        headers: {
            'Content-Type': 'application/json',
        },
    });

}

export function customer(params: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/customer_page',
        method: 'get',
        params: params,
        headers: {
            'Content-Type': 'application/json',
        },
    });

}
export function customerByArea(id: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/customerByArea/'+id,
        method: 'get',
        headers: {
            'Content-Type': 'application/json',
        },
    });

}





export function customer_id(id: any) {
    return service.request({
        url: 'http://localhost:5173/api/customer/'+id,
        method: 'get',
    });
}


export function customerWithCountItem_page(params: any) {
    //console.log(JSON.stringify(data))
    return service.request({
        url: 'http://localhost:5173/api/customerWithCountItem_page',
        method: 'get',
        params: params,
        headers: {
            'Content-Type': 'application/json',
        },
    });

}

export function customer_add(data: any) {
    return service.request({
        url: 'http://localhost:5173/api/customer',
        method: 'post',
        data: data,
        headers: {
            'Content-Type': 'application/json',
        },
    });
}

export function customer_edit(data: any) {
    return service.request({
        url: 'http://localhost:5173/api/customer',
        method: 'patch',
        data: data,
        headers: {
            'Content-Type': 'application/json',
        },
    });
}

export function customer_delete(customerId: any) {
    return service.request({
        url: `http://localhost:5173/api/customer_delete/${customerId}`,
        method: 'delete',
    });
}
// export function customer_edit(data: any) {
//     return service.patch( 'http://localhost:5173/api/customer',
//        data, {
//             headers: {
//                 'Content-Type': 'application/json'
//             }
//         }
//
//     );
// }