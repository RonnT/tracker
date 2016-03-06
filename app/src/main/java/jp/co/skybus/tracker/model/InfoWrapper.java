package jp.co.skybus.tracker.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roman T. on 06.03.2016.
 */
public class InfoWrapper {

    private List<Info> data = new ArrayList<>();

    public List<Info> getData() {
        return data;
    }

    public void setData(List<Info> data) {
        this.data = data;
    }

    public void addList(List<Info> pItemList){
        this.data.addAll(pItemList);
    }
}
