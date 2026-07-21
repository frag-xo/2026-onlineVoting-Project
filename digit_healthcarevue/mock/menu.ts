import Mock from 'mockjs'
import { SUCCESS_CODE } from '@/constants'

const timeout = 1000

export default [
    {
        url: '/mock/menu/list',
        method: 'get',
        timeout,
        response: () => {
            return {
                code: SUCCESS_CODE,
                data: {
                    menuData: [

                        {
                            "entity": {
                                "id": 0,
                                "name": "/back/doctor/view",
                                "icon": "el-icon-message",
                                "alias": "医生管理"
                            }
                        },
                        {
                            "entity": {
                                "id": 1,
                                "name": "/back/drug/view",
                                "icon": "el-icon-message",
                                "alias": "药品管理"
                            }
                        },
                        {
                            "entity": {
                                "id": 2,
                                "name": "/back/banner/view",
                                "icon": "el-icon-message",
                                "alias": "轮播广告"
                            }
                        },
                        {
                            "entity": {
                                "id": 3,
                                "name": "/back/house/view",
                                "icon": "el-icon-message",
                                "alias": "房屋管理"
                            }
                        },
                        {
                            "entity": {
                                "id": 4,
                                "name": "/back/inhabitants/view",
                                "icon": "el-icon-message",
                                "alias": "住户管理"
                            }
                        },
                        {
                            "entity": {
                                "id": 4,
                                "name": "/back/userAccessRecord/view",
                                "icon": "el-icon-message",
                                "alias": "进出记录"
                            }
                        },




                        {
                            "entity": {
                                "id": 10,
                                "name": "systemManage",
                                "icon": "el-icon-message",
                                "alias": "基本信息管理"
                            },
                            "childs": [
                                {
                                    "entity": {
                                        "id": 101,
                                        "name": "authManage",
                                        "icon": "el-icon-loading",
                                        "alias": "用户管理",
                                        "value": {
                                            "path": "/hello"
                                        }
                                    }
                                },
                                {
                                    "entity": {
                                        "id": 102,
                                        "name": "roleManage",
                                        "icon": "el-icon-bell",
                                        "alias": "教师管理",
                                        "value": "/back/teacher"
                                    }
                                },
                                {
                                    "entity": {
                                        "id": 103,
                                        "name": "menuManage",
                                        "icon": "el-icon-edit",
                                        "alias": "菜单管理",
                                        "value": "/system/menu"
                                    }
                                },
                                {
                                    "entity": {
                                        "id": 104,
                                        "name": "groupManage",
                                        "icon": "el-icon-mobile-phone\r\n",
                                        "alias": "分组管理",
                                        "value": "/system/group"
                                    }
                                }
                            ]
                        },

                        {
                            "entity": {
                                "id": 12,
                                "name": "userManage",
                                "icon": "el-icon-news",
                                "alias": "三级菜单"
                            },
                            "childs": [
                                {
                                    "entity": {
                                        "id": 121,
                                        "name": "accountManage",
                                        "icon": "el-icon-phone-outline\r\n",
                                        "alias": "帐号管理",
                                        "value": ""
                                    },
                                    "childs": [
                                        {
                                            "entity": {
                                                "id": 14,
                                                "name": "emailManage",
                                                "icon": "el-icon-sold-out\r\n",
                                                "alias": "邮箱管理",
                                                "value": "/content/email"
                                            }
                                        },
                                        {
                                            "entity": {
                                                "id": 13,
                                                "name": "passManage",
                                                "icon": "el-icon-service\r\n",
                                                "alias": "密码管理",
                                                "value": "/content/pass"
                                            }
                                        }
                                    ]
                                },

                            ]
                        },

                    ]
                }
            }
        }
    }
]