// 定义一个函数，将嵌套对象转换为GET请求参数的格式
export  function objectToQueryString(obj, parentKey = '') {
    const parts = [];
    for (let key in obj) {
        if (!obj.hasOwnProperty(key)) continue; // 确保key是对象自身的属性，而不是继承来的
        let value = obj[key];
        let newKey = parentKey ? `${parentKey}[${key}]` : key; // 如果有父级key，则使用带有方括号的形式
        if (typeof value === 'object') {
            parts.push(objectToQueryString(value, newKey)); // 递归处理嵌套对象
        } else {
            parts.push(encodeURIComponent(newKey) + '=' + encodeURIComponent(value)); // 处理普通的键值对
        }
    }
    return parts.join('&'); // 将处理后的键值对用&连接起来
}
