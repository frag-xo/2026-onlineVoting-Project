package org.mjc.dto;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;


/**
 *
 * @author:LG
 * @date:Created at 2019/03/09
 */
@Data
public class DTO<T> {

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 信息
     */
    private String msg;

    /**
     *
     */
    private T t;

    /**
     * 需要传输的列表
     */
    private List<T> tList;


    private Object obj;


    private String url;

    public DTO() {
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    /**
     * 自定义信息的构造器
     * @param code
     * @param msg
     */
    public DTO(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public DTO(Integer code, String msg, T t) {
        this.code = code;
        this.msg = msg;
        this.t = t;
    }


    public DTO(Integer code, String msg, List<T> ts) {
        this.code = code;
        this.msg = msg;
        this.tList = ts;
    }

    public DTO(Integer code, String msg, T t, Object obj) {
        this.code = code;
        this.msg = msg;
        this.t = t;
        this.obj = obj;
    }

    public DTO(Integer code, String msg, List<T> ts, Object obj) {
        this.code = code;
        this.msg = msg;
        this.tList = ts;
        this.obj = obj;
    }
    public DTO(Integer code, String msg, Page page) {
        this.code = code;
        this.msg = msg;
        this.obj = page;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public List<T> gettList() {
        return tList;
    }

    public void settList(List<T> tList) {
        this.tList = tList;
    }
}
