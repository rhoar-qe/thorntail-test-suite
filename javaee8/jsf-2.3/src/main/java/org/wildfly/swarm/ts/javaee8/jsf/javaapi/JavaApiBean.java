package org.wildfly.swarm.ts.javaee8.jsf.javaapi;

import javax.enterprise.inject.Model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Model
public class JavaApiBean {
    private Map<String, String> map = new HashMap<>();

    private Iterable<Integer> iterable;

    private MyCollectionModel<String> customDataModel = new MyCollectionModel<>();

    public JavaApiBean() {
        map.put("key1", "value1");
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        setIterable(list);
        List<String> stringList = new ArrayList<>();
        stringList.add("a");
        stringList.add("b");
        customDataModel.setWrappedData(stringList);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public Iterable<Integer> getIterable() {
        return iterable;
    }

    public void setIterable(Iterable<Integer> iterable) {
        this.iterable = iterable;
    }

    public MyCollectionModel<String> getCustomDataModel() {
        return customDataModel;
    }

    public void setCustomDataModel(MyCollectionModel<String> customDataModel) {
        this.customDataModel = customDataModel;
    }
}
