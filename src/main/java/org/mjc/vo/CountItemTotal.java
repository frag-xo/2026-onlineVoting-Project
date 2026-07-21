package org.mjc.vo;

import lombok.Data;

import java.util.List;

@Data
public class CountItemTotal {
    private List<String> titles;
    private List<CountItemGroup> countItemGroups;

    public CountItemTotal() {
    }

    public List<String> getTitles() {
        return titles;
    }

    public void setTitles(List<String> titles) {
        this.titles = titles;
    }

    public List<CountItemGroup> getCountItemGroups() {
        return countItemGroups;
    }

    public void setCountItemGroups(List<CountItemGroup> countItemGroups) {
        this.countItemGroups = countItemGroups;
    }
}
