import { extend, ValidationObserver, ValidationProvider, localize } from 'vee-validate'
import { required, email, min, between, numeric } from 'vee-validate/dist/rules'
import zh_CN from 'vee-validate/dist/locale/zh_CN.json';
localize('zh_cn',zh_CN) // 配置中文
export default {
    install (Vue) {
        Vue.component('ValidationObserver', ValidationObserver)
        Vue.component('ValidationProvider', ValidationProvider)
        extend('required', required)
        extend('email', email)
        extend('min', min)
        extend('between', between)
        extend('numeric', numeric)
        extend('phone', {
            message: '请输入11位的手机号码',
            validate: value => value.length === 11 && /^1[3456789]\d{9}$/.test(value)
        })
    }
}