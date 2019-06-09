package com.dpcsa.compon.components;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;

public class SearchComponent extends BaseComponent {

    public View viewSearch;
    RecyclerComponent recycler;
//    BaseProviderAdapter adapter;
    Handler handler = new Handler();
    public String nameSearch;
    private boolean isChangeText;
    private ParamModel modelNew;
    private String[] paramArray;

    public SearchComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        viewSearch = parentLayout.findViewById(paramMV.viewSearchId);
        nameSearch = activity.getResources().getResourceEntryName(paramMV.viewSearchId);
        isChangeText = true;
        if (viewSearch instanceof EditText){
            ((EditText) viewSearch).addTextChangedListener(new Watcher());
        } else {
            iBase.log("View для поиска должно быть IComponent или EditText в " + multiComponent.nameComponent);
            return;
        }
        if (paramMV.paramView != null || paramMV.paramView.viewId != 0) {
            recycler = (RecyclerComponent) multiComponent.getComponent(paramMV.paramView.viewId);
        }
        if (recycler == null) {
            iBase.log("Не найден RecyclerView в " + multiComponent.nameComponent);
            return;
        }
        modelNew = new ParamModel(paramMV.paramModel.method);
        if (paramMV.paramModel.param != null && paramMV.paramModel.param.length() > 0) {
            paramArray = paramMV.paramModel.param.split(",");
        }

//
//        if (paramMV.paramView == null || paramMV.paramView.viewId == 0) {
//            recycler = (RecyclerView) StaticVM.findViewByName(parentLayout, "recycler");
//        } else {
//            recycler = (RecyclerView) parentLayout.findViewById(paramMV.paramView.viewId);
//        }
//        if (recycler == null) {
//            iBase.log("Не найден RecyclerView в " + paramMV.nameParentComponent);
//            return;
//        }
//        if (navigator == null) {
//            navigator = new Navigator();
//        }
//        navigator.viewHandlers.add(0, new ViewHandler(0, ViewHandler.TYPE.SELECT));


//        listData = new ListRecords();
//        provider = new BaseProvider(listData);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
//        recycler.setLayoutManager(layoutManager);
//        adapter = new BaseProviderAdapter(this);
//        recycler.setAdapter(adapter);
    }

    @Override
    public void changeData(Field field) {
//        listData.clear();
//        listData.addAll((ListRecords) field.value);
//        adapter.notifyDataSetChanged();
//        int splash = paramMV.paramView.splashScreenViewId;
//        if (splash != 0) {
//            View v_splash = parentLayout.findViewById(splash);
//            if (v_splash != null) {
//                if (listData.size() > 0) {
//                    v_splash.setVisibility(GONE);
//                } else {
//                    v_splash.setVisibility(VISIBLE);
//                }
//            } else {
//                iBase.log("Не найден SplashView в " + paramMV.nameParentComponent);
//            }
//        }
//        iBase.sendEvent(paramMV.paramView.viewId);
    }
//
//    @Override
//    public void actual() {
//        listData.clear();
//        adapter.notifyDataSetChanged();
//        super.actual();
//    }
//
//    public void clickAdapter(RecyclerView.ViewHolder holder, View view, int position) {
//        Record record = provider.get(position);
//        String st = record.getString(nameSearch);
//        isChangeText = false;
//        ((EditText) viewSearch).setText(st);
//        isChangeText = true;
//        ComponGlob.getInstance().setParam(record);
//        if (navigator.viewHandlers.size() > 1) {
//            super.clickAdapter(holder, view, position, record);
//        }
//    }

    public class Watcher implements TextWatcher{

        private String searchString = "";
        String nameParam;

        private Runnable task = new Runnable() {
            @Override
            public void run() {
                String stringParam = " ";
                if (modelNew.method == ParamModel.GET) {
                    componGlob.addParamValue(nameParam, searchString);
                    actual();
                } else if (modelNew.method == ParamModel.GET_DB) {
                    if (paramArray != null) {
                        String sep = "";
                        String[] searchArray = searchString.split(" ");
                        for (String st : paramArray) {
                            for (String stSearch : searchArray) {
                                stringParam += sep + st + " LIKE '%" + stSearch + "%' ";
                                sep = " OR ";
                            }
                        }
                        modelNew.url = paramMV.paramModel.url + stringParam;
                        recycler.updateData(modelNew);
                    }
                }
            }
        };

        public Watcher() {
            String[] stAr = paramMV.paramModel.param.split(",");
            nameParam = stAr[0];
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isChangeText) {
                searchString = s.toString();
                handler.removeCallbacks(task);
                handler.postDelayed(task, 700);
            }
        }
    }

}
