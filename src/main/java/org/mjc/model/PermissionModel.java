package org.mjc.model;

import org.mjc.domain.Permission;

/**
 * @description: 权限菜单业务模型
 * @author: liulindong
 * @create: 2021/9/2 15:43
 **/
public class PermissionModel extends Permission {
    private MetaModel meta;//
    public MetaModel getMeta() {
        return meta;
    }

    public void setMeta(MetaModel meta) {
        this.meta = meta;
    }
}
