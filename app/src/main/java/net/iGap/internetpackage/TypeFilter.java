package net.iGap.internetpackage;

import java.util.List;

public class TypeFilter {

    private List<String> filterList;
    private boolean isDaily;
    private int threshold;

    public TypeFilter(List<String> filterList, boolean isDaily, int threshold) {
        this.filterList = filterList;
        this.isDaily = isDaily;
        this.threshold = threshold;
    }

    public List<String> getFilterList() {
        return filterList;
    }

    public boolean isDaily() {
        return isDaily;
    }

    public int getThreshold() {
        return threshold;
    }
}
