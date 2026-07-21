package org.mjc.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * @description:  * @author: liulindong
 * @create: 2021/9/2 0002 15:24
 **/
public class Permission  {
    @JsonIgnore
    private Integer id;//菜单id
    @JsonIgnore
    private Integer pid;//菜单父id
    private String path;//菜单路径
    private String name;//菜单name
    private String component;//菜单组件
    @JsonIgnore
    private Integer level;//菜单级别
    private List<Permission> children;//
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<Permission> getChildren() {
        return children;
    }

    public void setChildren(List<Permission> children) {
        this.children = children;
    }
}
