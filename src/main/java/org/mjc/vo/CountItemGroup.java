package org.mjc.vo;

import lombok.Data;

import java.util.List;

@Data
public class CountItemGroup {
    private  String ItemName;
    private  List<Integer> itemVals;

    public CountItemGroup() {
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public List<Integer> getItemVals() {
        return itemVals;
    }

    public void setItemVals(List<Integer> itemVals) {
        this.itemVals = itemVals;
    }
}
