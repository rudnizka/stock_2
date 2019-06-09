package com.dpcsa.compon.components;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.dpcsa.compon.base.BaseActivity;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.BaseProvider;
import com.dpcsa.compon.base.BaseProviderAdapter;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.presenter.ListPresenter;

public class MenuComponent extends BaseComponent {
    RecyclerView recycler;
    ListRecords listData;
    BaseProviderAdapter adapter;
    private String componentTag = "MENU_";
    private String fieldType = "select";

    public MenuComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView == null || paramMV.paramView.viewId == 0) {
            recycler = (RecyclerView) componGlob.findViewByName(parentLayout, "recycler");
        } else {
            recycler = (RecyclerView) parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (recycler == null) {
            iBase.log("Не найден RecyclerView для Menu в " + multiComponent.nameComponent);
        }
        if (navigator != null) {
            for (ViewHandler vh : navigator.viewHandlers) {
                if (vh.viewId == 0 && vh.type == ViewHandler.TYPE.FIELD_WITH_NAME_FRAGMENT) {
                    selectViewHandler = vh;
                    break;
                }
            }
        } else {
            iBase.log("Нет навигатора для Menu в " + multiComponent.nameComponent);
        }
        paramMV.paramView.fieldType = fieldType;
//        ComponPrefTool.setNameInt(componentTag + multiComponent.nameComponent, -1);
        listData = new ListRecords();
        listPresenter = new ListPresenter(this);
        provider = new BaseProvider(listData);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recycler.setLayoutManager(layoutManager);
        adapter = new BaseProviderAdapter(this);
        recycler.setAdapter(adapter);
    }

    @Override
    public void changeData(Field field) {
        listData.clear();
        listData.addAll((ListRecords) field.value);
        provider.setData(listData);
        int selectStart = preferences.getNameInt(componentTag + multiComponent.nameComponent, -1);
        int ik = listData.size();
        if (selectStart == -1) {
            for (int i = 0; i < ik; i++) {
                Record r = listData.get(i);
                int j = (Integer) r.getValue(fieldType);
                if (j == 1) {
                    selectStart = i;
                    break;
                }
            }
        } else {
            for (int i = 0; i < ik; i++) {
                Record r = listData.get(i);
                Field f = r.getField(fieldType);
                if (i == selectStart) {
                    f.value = 1;
                } else {
                    if (((Integer) f.value) == 1 ) {
                        f.value = 0;
                    }
                }
            }
        }
        listPresenter.changeData(listData, selectStart);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void changeDataPosition(int position, boolean select) {
        adapter.notifyItemChanged(position);
        ((BaseActivity) activity).closeDrawer();
        preferences.setNameInt(componentTag + multiComponent.nameComponent, position);
        if (select && selectViewHandler != null) {
            Record record = listData.get(position);
            componGlob.setParam(record);
            String screen = (String) record.getField(selectViewHandler.nameFieldScreen).value;
            if (screen != null && screen.length() > 0) {
                iBase.startScreen(screen, true);
            }
        }
    }

}
