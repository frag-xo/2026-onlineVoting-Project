package org.mjc.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CountItem implements Serializable {
    private String itemName; //分组统计的显示名称字段
    private Integer itemVal;//分组统计的值
    private String itemId;  //分组统计的主键字段
    private String convertVal;  //转换的值
    private Float itemVal2;//分组统计的值

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
        if (itemName.equals("1")){
            this.convertVal = "是";
        }else{
            this.convertVal = "否";
        }
    }

    public Integer getItemVal() {
        return itemVal;
    }

    public void setItemVal(Integer itemVal) {
        this.itemVal = itemVal;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getConvertVal() {
        return convertVal;
    }

    public void setConvertVal(String convertVal) {
        this.convertVal = convertVal;
    }

    public Float getItemVal2() {
        return itemVal2;
    }

    public void setItemVal2(Float itemVal2) {
        this.itemVal2 = itemVal2;
    }
}