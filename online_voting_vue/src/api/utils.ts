//引入二次封装的axios
import request from "@/utils/request";


enum API {

    //上传图片
    UPLOAD_URL = '/uploadImg'

}
// 上传图片
export const reqUpload = (formData:any) => {
    // 创建了一个新的 FormData 对象，用于构建表单数据,并将file添加到FormData对象中


    return request.request(    {
        url: API.UPLOAD_URL,
        method: 'post',
        data: formData,
    });
};