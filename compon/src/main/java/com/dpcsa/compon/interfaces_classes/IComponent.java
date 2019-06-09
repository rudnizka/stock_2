package com.dpcsa.compon.interfaces_classes;

public interface IComponent {
    void setData(Object data);
    String getAlias();
    Object getData();
    void setOnChangeStatusListener(OnChangeStatusListener statusListener);
    String getString();
}
