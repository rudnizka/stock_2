package com.dpcsa.compon.components;

import android.util.Log;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;

public class PanelComponent extends BaseComponent {
    @Override
    public void initView() {
        viewComponent = parentLayout.findViewById(paramMV.paramView.viewId);
        if (viewComponent == null) {
            iBase.log("Не найдена панель в " + multiComponent.nameComponent);
        }
    }

    @Override
    public void changeData(Field field) {
        if (field == null) return;
        if (field.value instanceof ListRecords) {
            ListRecords lr = (ListRecords) field.value;
            if (lr. size() > 0) {
                recordComponent = lr.get(0);
                workWithRecordsAndViews.RecordToView(recordComponent, viewComponent, this, clickView);
            } else {
                iBase.log("Тип данных не Record в " + multiComponent.nameComponent);
            }
        } else {
            if (field.value instanceof Record) {
                recordComponent = (Record) field.value;
                workWithRecordsAndViews.RecordToView(recordComponent, viewComponent, this, clickView);
            } else {
                iBase.log("Тип данных не Record в " + multiComponent.nameComponent);
            }
        }
    }

    public PanelComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }
}
