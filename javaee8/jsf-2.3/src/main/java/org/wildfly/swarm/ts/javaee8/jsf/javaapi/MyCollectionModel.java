package org.wildfly.swarm.ts.javaee8.jsf.javaapi;

import javax.faces.model.DataModel;
import javax.faces.model.FacesDataModel;
import java.util.List;

@FacesDataModel(forClass = List.class)
public class MyCollectionModel<E> extends DataModel<E> {
    private List<E> list;

    private int rowIndex;

    @Override
    public boolean isRowAvailable() {
        return rowIndex < list.size();
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    @Override
    public E getRowData() {
        return list.get(rowIndex);
    }

    @Override
    public int getRowIndex() {
        return rowIndex;
    }

    @Override
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    @Override
    public Object getWrappedData() {
        return list;
    }

    @Override
    public void setWrappedData(Object data) {
        list = (List<E>) data;
    }
}
