package com.dpcsa.compon.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ActivityResult;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.components.RecyclerComponent;
import com.dpcsa.compon.interfaces_classes.AnimatePanel;
import com.dpcsa.compon.interfaces_classes.ICustom;
import com.dpcsa.compon.interfaces_classes.IValidate;
import com.dpcsa.compon.interfaces_classes.Param;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.interfaces_classes.MoreWork;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.OnClickItemRecycler;
import com.dpcsa.compon.interfaces_classes.ParentModel;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.FieldBroadcaster;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.presenter.ListPresenter;
import com.dpcsa.compon.single.ComponTools;
import com.dpcsa.compon.single.Injector;
import com.dpcsa.compon.tools.Constants;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.tools.phone_picker.GetCountryCode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.dpcsa.compon.param.ParamModel.DEL_DB;
import static com.dpcsa.compon.param.ParamModel.GET_DB;
import static com.dpcsa.compon.param.ParamModel.POST_DB;

public abstract class BaseComponent {
    public abstract void initView();
    public abstract void changeData(Field field);
    public View parentLayout;
    public BaseProvider provider;
    public ListPresenter listPresenter;
    public ParamComponent paramMV;
    public BaseActivity activity;
    public Navigator navigator;
    public MoreWork moreWork;
    public ListRecords listData;
    public Record recordComponent;
    public IBase iBase;
    public ICustom iCustom;
    public ViewHandler selectViewHandler;
    public View viewComponent;
    public Field argument;
    public Screen multiComponent;
    public ComponGlob componGlob;
    public BaseDB baseDB;
    public ComponPrefTool preferences;
    private ComponTools componTools;
    public ListRecords listRecords;
    public Field responseSave;

    public WorkWithRecordsAndViews workWithRecordsAndViews = new WorkWithRecordsAndViews();

    public BaseComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent){
        this.paramMV = paramMV;
        this.multiComponent = multiComponent;
        componGlob = Injector.getComponGlob();
        baseDB = Injector.getBaseDB();
        preferences = Injector.getPreferences();
        componTools = Injector.getComponTools();
        navigator = paramMV.navigator;
        paramMV.baseComponent = this;
        this.iBase = iBase;
        activity = iBase.getBaseActivity();
        this.parentLayout = iBase.getParentLayout();
//        moreWork = null;
        moreWork = paramMV.moreWork;
        if (paramMV.additionalWork != null) {
            try {
                moreWork = (MoreWork) paramMV.additionalWork.newInstance();
                moreWork.setParam(iBase, multiComponent);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void init() {
        initView();
        if (paramMV.nameReceiver != null) {
            LocalBroadcastManager.getInstance(iBase.getBaseActivity())
                    .registerReceiver(startActual, new IntentFilter(paramMV.nameReceiver));
        }
        if (paramMV.paramModel != null
                && paramMV.paramModel.method == ParamModel.FIELD) {
            if (paramMV.paramModel.field instanceof FieldBroadcaster) {
                LocalBroadcastManager.getInstance(iBase.getBaseActivity())
                        .registerReceiver(changeFieldValue, new IntentFilter(paramMV.paramModel.field.name));
            }
        }
        if (paramMV.mustValid != null) {
            iBase.addEvent(paramMV.mustValid, this);
        }
        if (paramMV.eventComponent == 0) {
            if (paramMV.startActual) {
                actual();
            }
        } else {
            iBase.addEvent(paramMV.eventComponent, this);
        }
    }

    public void updateData(ParamModel paramModel) {
        actualModel(paramModel);
    }

    private BroadcastReceiver startActual = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (iBase.isViewActive()) {
                        actual();
                    } else {
                        handler.postDelayed(this, 5);
                    }
                }
            }, 5);
        }
    };

    public void actualEvent(int sender, Object paramEvent) {
        actual();
    }

    public void actual() {
        actualModel(paramMV.paramModel);
    }

    private void actualModel(ParamModel paramModel) {
        if (paramModel != null) {
            switch (paramModel.method) {
                case ParamModel.PARENT :
                    ParentModel pm = iBase.getParentModel(paramModel.url);
                    if (pm.field == null) {
                        for (BaseComponent bc : pm.componentList) {
                            if (bc == this) {
                                return;
                            }
                        }
                        pm.componentList.add(this);
                    } else {
                        setParentData(pm.field);
                    }
                    break;
                case ParamModel.FIELD:
                    changeDataBase(paramModel.field);
                    break;
                case ParamModel.JSON:
                    Field ffJson = null;
                    try {
                        ffJson = componGlob.jsonSimple.jsonToModel(paramModel.url);
                    } catch (JsonSyntaxException e) {
                        iBase.log(e.getMessage());
                        e.printStackTrace();
                    }
                    if (ffJson != null) {
//                        ListRecords lr = (ListRecords)ffJson.value;
//                        for (Record rec : lr) {
//                            Log.d("QWERT","RRR="+rec);
//                        }
                        changeDataBase(ffJson);
                    }
                    break;
                case ParamModel.COUNTRY_CODE:
                    GetCountryCode gcc = new GetCountryCode();
                    gcc.getCountryCode(iBase, listener, paramMV.paramModel.url);
                    break;
                case ParamModel.GLOBAL:
//                    changeDataBase(componGlob.globalData.getField(paramModel.url));
                    listener.onResponse(componGlob.globalData.getField(paramModel.url));
                    break;
                case ParamModel.ARGUMENTS :
                    if (iBase.getBaseFragment() != null) {
                        argument = iBase.getBaseFragment().paramScreen;
                        changeDataBase(argument);
                    } else {
                        Intent intent = activity.getIntent();
                        String st = intent.getStringExtra(Constants.NAME_PARAM_FOR_SCREEN);
                        JsonSimple jsonSimple = new JsonSimple();
                        try {
                            argument = jsonSimple.jsonToModel(st);
                        } catch (JsonSyntaxException e) {
                            iBase.log(e.getMessage());
                            e.printStackTrace();
                        }
                        changeDataBase(argument);
                    }
                    break;
                case ParamModel.DATAFIELD :
                    if (paramModel.dataFieldGet != null) {
                        changeDataBase(paramModel.dataFieldGet.getField(this));
                    }
                    break;
                case GET_DB :
                    Record paramScreen = null; // ????? Параметри які передаються в Screen формує номер urlArrayIndex через параметри
                    if (paramModel.urlArray != null) {
                        Field f = iBase.getParamScreen();
                        if (f != null && f.type == Field.TYPE_CLASS) {
                            paramScreen = ((Record) f.value);
                            paramModel.urlArrayIndex = paramScreen.getInt(paramModel.urlArray[0]);
                            if (paramModel.urlArrayIndex < 0) {
                                paramModel.urlArrayIndex = 0;
                            }
                            int len = paramModel.urlArray.length - 1;
                            if (paramModel.urlArrayIndex > len) {
                                paramModel.urlArrayIndex = len;
                            }
                        }
                    }
                    baseDB.get(iBase, paramModel, setParam(paramModel.param, paramScreen), listener);
                    break;
                default: {
                    new BasePresenter(iBase, paramModel, null, null, listener);
                }
            }
        } else {
            changeDataBase(null);
        }
    }

    public BaseComponent getComponent(int id) {
        return multiComponent.getComponent(id);
    }

    public String[] setParam(String paramSt, Record rec) {
        if (paramSt == null) return null;
        String[] param = paramSt.split(",");
        int ik = param.length;
        for (int i = 0; i < ik; i++) {
            String par = param[i];
            String parValue = null;
            if (rec != null) {
                parValue = rec.getString(par);
            }
            if (parValue == null) {
                parValue = getGlobalParam(par);
            }
            if (parValue != null) {
                param[i] = parValue;
            } else {
                return null;
            }
        }
        return param;
    }

    private String getGlobalParam(String name) {
        String st = null;
        List<Param> paramV = componGlob.paramValues;
        for (Param par : paramV) {
            if (par.name.equals(name)) {
                st = par.value;
                break;
            }
        }
        return st;
    }

    public void setParentData(Field field) {
        if (field != null) {
            if (paramMV.paramModel.param.length() > 0) {
                Field f = ((Record) field.value).getField(paramMV.paramModel.param);
                if (f != null) {
                    changeDataBase(f);
                }
            } else {
                changeDataBase(field);
            }
        }
    }

    public void changeDataPosition(int position, boolean select) {
    }

    private BaseComponent getThis() {
        return this;
    }

    public void setGlobalData(String name) {
// У кожного типу компонету по своєму. Перевизначається
    }

    IPresenterListener listener = new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            if (response == null) return;
            responseSave = response;
            if (response.type == Field.TYPE_LIST_RECORD) {
                listRecords = (ListRecords) response.value;
            }
            if (iCustom != null) {
                iCustom.beforeProcessingResponse(response, getThis());
            } else if (moreWork != null) {
                moreWork.beforeProcessingResponse(response, getThis());
            }
            String fName = paramMV.paramModel.nameField;
            String addFieldName = paramMV.paramModel.nameAddField;
            if (fName != null || addFieldName != null) {
                String[] addField = null;
                if (addFieldName != null && addFieldName.length() > 0) {
                    addField = addFieldName.split(",");
                }
                String fNameTo = paramMV.paramModel.nameFieldTo;
                ListRecords listR = null;
                if (response.type == Field.TYPE_CLASS) {
                    Field ff = ((Record) response.value).get(0);
                    if (ff.type == Field.TYPE_CLASS) {
                        Field f = ((Record) ff.value).get(0);
                        listR = (ListRecords) f.value;
                    } else {
                        listR = (ListRecords) ff.value;
                    }
                } else {
                    if (response.type == Field.TYPE_LIST_RECORD) {
                        listR = (ListRecords) response.value;
                    }
                }
                int addFieldType = paramMV.paramModel.typeAddField;
                int addFieldIntValue = paramMV.paramModel.valueIntAddField;
                if (listR != null) {
                    for (Record record : listR) {
                        if (fName != null) {
                            Field f = record.getField(fName);
                            if (f != null) {
                                f.name = fNameTo;
                            }
                        }
                        if (addFieldName != null && addFieldName.length() > 0) {
                            for (String addSt : addField) {
                                Field ff = new Field(addSt, addFieldType, addFieldIntValue);
                                record.add(ff);
                            }
                        }
                    }
                }
            }

            setFilterData();
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            onErrorModel(statusCode, message, click);
        }
    };

    public void setFilterData() {
        ListRecords lr = null;
        Field resp = new Field(responseSave.name, responseSave.type, responseSave.value);
        if (paramMV.paramModel.filters != null) {
            lr = new ListRecords();
            ListRecords lrResp = (ListRecords) responseSave.value;
            for (Record rec : lrResp) {
                if (paramMV.paramModel.filters.isConditions(rec)) {
                    lr.add(rec);
                    if (lr.size() >= paramMV.paramModel.filters.maxSize) {
                        break;
                    }
                }
            }
            resp.value = lr;
        }
        // Реализация ParamModel обеспечивает получение данных из заданных источников в форме Field.
        // Если данные являются Record и задано nameTakeField, то результатом реализации ParamModel
        // будет Field с именем nameTakeField из этого Record
        if (paramMV.paramModel.nameTakeField == null) {
            changeDataBase(resp);
        } else {
            Record r = (Record) resp.value;
            changeDataBase(r.getField(paramMV.paramModel.nameTakeField));
        }
    }

    private void changeDataBase(Field field) {
        if (paramMV.paramModel != null
                && paramMV.paramModel.sortParam != null
                && paramMV.paramModel.sortParam.length() > 0) {
            sort(field);
        }
        if (paramMV.paramModel != null && paramMV.paramModel.addRecordBegining != null
                && ((ListRecords) field.value).size() > 0) {
            ((ListRecords) field.value).addAll(0, paramMV.paramModel.addRecordBegining);
        }
        if (iBase instanceof ICustom) {
            ((ICustom) iBase).changeValue(paramMV.paramView.viewId, field);
        }
        changeData(field);
        if (iCustom != null) {
            iCustom.afterChangeData(this);
        } else if (moreWork != null) {
            moreWork.afterChangeData(this);
        }
    }

    private void sort(Field field) {
        if (field.value instanceof ListRecords) {
            String[] param = paramMV.paramModel.sortParam.split(",");
            ListRecords listR = (ListRecords) field.value;
            Collections.sort(listR, new Comparator<Record>() {
                public int compare(Record o1, Record o2) {
                    int result = 0;
                    for (String par : param) {
                        Field f1 = o1.getField(par);
                        Field f2 = o2.getField(par);
                        switch (f1.type) {
                            case Field.TYPE_INTEGER:
                                int i1 = (Integer) f1.value;
                                int i2 = (Integer) f2.value;
                                if (i1 < i2) {
                                    return -1;
                                } else if (i1 > i2) {
                                    return 1;
                                }
                                break;
                            case Field.TYPE_LONG:
                                long l1 = (Long) f1.value;
                                long l2 = (Long) f2.value;
                                if (l1 < l2) {
                                    return -1;
                                } else if (l1 > l2) {
                                    return 1;
                                }
                                break;
                            case Field.TYPE_STRING:
                                String s1 = (String) f1.value;
                                String s2 = (String) f2.value;
                                int res = s1.compareTo(s2);
                                if (res != 0) return res;
                                break;
                        }
                    }
                    return result;
                }
            });
        }
    }

    private BroadcastReceiver changeFieldValue = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            changeDataBase(paramMV.paramModel.field);
        }
    };

    public View.OnClickListener clickView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int vId = v.getId();
            List<ViewHandler> viewHandlers = paramMV.navigator.viewHandlers;
            View vv;
            for (ViewHandler vh : viewHandlers) {
                if (vId == vh.viewId) {
                    switch (vh.type) {
                        case SEND_CHANGE_BACK :
                            Record param = workWithRecordsAndViews.ViewToRecord(viewComponent, vh.paramModel.param);
                            new BasePresenter(iBase, vh.paramModel, null, setRecord(param), listener_send_change);
                            break;
                        case EXEC:
                            if (vh.execMethod != null) {
                                vh.execMethod.run(getThis());
                            }
                            break;
                        case NAME_FRAGMENT:
                            if (recordComponent != null) {
                                componGlob.setParam(recordComponent);
                            }
                            int requestCode = -1;
                            if (vh.afterResponse != null) {
                                requestCode = activity.addForResult(vh.afterResponse, activityResult);
                            }
                            switch (vh.paramForScreen) {
                                case RECORD:
                                    if (recordComponent != null) {
                                        iBase.startScreen(vh.screen, false, recordComponent, requestCode);
                                    } else {
                                        if (vh.paramForSend != null && vh.paramForSend.length() > 0) {
                                            Record rec = workWithRecordsAndViews.ViewToRecord(viewComponent, vh.paramForSend);
                                            iBase.startScreen(vh.screen, false, rec, requestCode);
                                        }
                                    }
                                    break;
                                case RECORD_COMPONENT:
                                    BaseComponent bc = getComponent(vh.componId);
                                    if (bc != null) {
                                        componGlob.setParam(bc.recordComponent);
                                        iBase.startScreen(vh.screen, false, bc.recordComponent, requestCode);
                                    }
                                    break;
                                default:
                                    iBase.startScreen(vh.screen, false, null, requestCode);
                                    break;
                            }
                            break;
                        case SET_PARAM:
                            componGlob.setParam(recordComponent);
                            break;
                        case BACK:
                            activity.onBackPressed();
                            break;
                        case CALL_UP:
                            if (v instanceof TextView) {
                                String st = ((TextView) v).getText().toString();
                                if (st != null && st.length() > 0) {
                                    activity.callUp(st);
                                }
                            }
                            break;
                        case DIAL_UP:
                            if (v instanceof TextView) {
                                String st = ((TextView) v).getText().toString();
                                if (st != null && st.length() > 0) {
                                    activity.startDialPhone(st);
                                }
                            }
                            break;
                        case SHOW_HIDE:
                            vv = parentLayout.findViewById(vh.showViewId);
                            if (vv != null) {
                                TextView tv = (TextView) v;
                                if (vv instanceof AnimatePanel) {
                                    AnimatePanel ap = (AnimatePanel) vv;
                                    if (ap.isShow()) {
                                        ap.hide();
                                        tv.setText(activity.getString(vh.textHideId));
                                    } else {
                                        ap.show(iBase);
                                        tv.setText(activity.getString(vh.textShowId));
                                    }
                                } else {
                                    if (vv.getVisibility() == View.VISIBLE) {
                                        vv.setVisibility(View.GONE);
                                        tv.setText(activity.getString(vh.textHideId));
                                    } else {
                                        vv.setVisibility(View.VISIBLE);
                                        tv.setText(activity.getString(vh.textShowId));
                                    }
                                }
                            }
                            break;
                        case CLICK_SEND :
                            boolean valid = true;
                            if (vh.mustValid != null) {
                                for (int i : vh.mustValid) {
                                    vv = viewComponent.findViewById(i);
                                    if (vv instanceof IValidate) {
                                        boolean validI = ((IValidate) vv).isValid();
                                        if (!validI) {
                                            valid = false;
                                        }
                                    }
                                }
                            }
                            if (valid) {
                                selectViewHandler = vh;
                                param = workWithRecordsAndViews.ViewToRecord(viewComponent, vh.paramModel.param);
                                Record rec = setRecord(param);
                                if (moreWork != null) {
                                    moreWork.setPostParam(vh.viewId, rec);
                                }
                                componGlob.setParam(rec);
                                if (vh.paramModel.method == POST_DB) {
                                    baseDB.insertRecord(vh.paramModel.url, rec);
                                    listener_send_back_screen.onResponse(null);
                                } else {
                                    new BasePresenter(iBase, vh.paramModel, null, rec, listener_send_back_screen);
                                }
                            }
                            break;
                        case GET_DATA:
                            selectViewHandler = vh;
                            param = workWithRecordsAndViews.ViewToRecord(viewComponent, vh.paramModel.param);
                            Record rec = setRecord(param);
                            componGlob.setParam(rec);
                            new BasePresenter(iBase, vh.paramModel, null, rec, listener_get_data);
                            break;
                        default:
                            specificComponentClick(vh);
                            break;
                    }
                }
            }
        }
    };

    ActivityResult activityResult  = new ActivityResult() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data, ActionsAfterResponse afterResponse) {
            if (resultCode == activity.RESULT_OK) {
                View vv;
                for (ViewHandler vh : afterResponse.viewHandlers) {
                    switch (vh.type) {
                        case ASSIGN_VALUE:
                            vv = parentLayout.findViewById(vh.viewId);
                            if (vv != null) {
                                String json = data.getStringExtra(Constants.RECORD);
                                JsonSimple jsonSimple = new JsonSimple();
                                Field ff = null;
                                try {
                                    ff = jsonSimple.jsonToModel(json);
                                } catch (JsonSyntaxException e) {
                                    iBase.log(e.getMessage());
                                    e.printStackTrace();
                                }
                                if (ff != null) {
                                    workWithRecordsAndViews.RecordToView((Record) ff.value, vv);
                                }
                            }
                            break;
                        case SHOW:
                            vv = parentLayout.findViewById(vh.showViewId);
                            if (vv != null) {
                                if (vv instanceof AnimatePanel) {
                                    ((AnimatePanel) vv).show(iBase);
                                } else {
                                    vv.setVisibility(View.VISIBLE);
                                }
                                if (vh.nameFieldWithValue != null && vh.nameFieldWithValue.length() > 0) {
                                    workWithRecordsAndViews.RecordToView(paramToRecord(vh.nameFieldWithValue), vv);
                                }
                            }
                            break;
                    }
                }
            }
        }
    };

    public void specificComponentClick(ViewHandler viewHandler) {

    }

    public void clickAdapter(RecyclerView.ViewHolder holder, View view, int position, Record record) {
//        Record record = provider.get(position);
        if (navigator != null) {
            int id = view == null ? 0 : view.getId();
            for (ViewHandler vh : navigator.viewHandlers) {
                if (vh.viewId == id) {
                    switch (vh.type) {
                        case SELECT:
                            if (listPresenter != null) {
                                listPresenter.ranCommand(ListPresenter.Command.SELECT,
                                        position, null);
                                componGlob.setParam(record);
                            }
                            break;
                        case SET_PARAM:
                            componGlob.setParam(record);
                            break;
                        case FIELD_WITH_NAME_FRAGMENT:
                            if (listPresenter != null) {
                                listPresenter.ranCommand(ListPresenter.Command.SELECT,
                                        position, null);
                            } else {
                                componGlob.setParam(record);
                                iBase.startScreen((String) record.getValue(vh.nameFieldScreen), false);
                            }
                            break;
                        case RESULT_RECORD :
                            Intent intent = new Intent();
                            intent.putExtra(Constants.RECORD, record.toString());
                            activity.setResult(Activity.RESULT_OK, intent);
                            activity.finishActivity();
                            break;
                        case RESULT_PARAM :
                            componGlob.setParam(record);
                            activity.setResult(Activity.RESULT_OK);
                            activity.finishActivity();
                            break;
                        case MODEL_PARAM:
                            ParamModel pm = vh.paramModel;
                            if (pm.method == DEL_DB) {
                                baseDB.deleteRecord(iBase, pm, setParam(pm.param, record));
                            }
                            break;
                        case ACTUAL:
                            actual();
                            break;
                        case DEL_RECYCLER:
                            listData.remove(position);
                            if (this instanceof RecyclerComponent) {
                                ((RecyclerComponent) this).adapter.notifyItemRemoved(position);
                            }
                            break;
                        case NAME_FRAGMENT:
//                            Log.d("QWERT","clickAdapter record="+record.toString());
                            componGlob.setParam(record);
                            if (vh.paramForScreen == ViewHandler.TYPE_PARAM_FOR_SCREEN.RECORD) {
                                iBase.startScreen(vh.screen, false, record);
                            } else {
                                iBase.startScreen(vh.screen, false);
                            }
                            break;
                        case CLICK_VIEW:
                            if (iCustom != null) {
                                iCustom.clickView(view, holder.itemView, this, record, position);
                            } else if (moreWork != null) {
                                moreWork.clickView(view, holder.itemView, this, record, position);
                            }
                            break;
                        case BACK:
                            iBase.backPressed();
                            break;
                        case CLICK_CUSTOM:
                            if (iCustom != null) {
                                iCustom.customClick(paramMV.paramView.viewId, position, record);
                            }
                            break;
                        case BROADCAST:
                            Intent intentBroad = new Intent(vh.nameFieldWithValue);
                            intentBroad.putExtra(Constants.RECORD, record.toString());
                            LocalBroadcastManager.getInstance(activity).sendBroadcast(intentBroad);
                            break;
                    }
                }
            }
        }
    }

    public OnClickItemRecycler clickItem = new OnClickItemRecycler() {
        @Override
        public void onClick(RecyclerView.ViewHolder holder, View view, int position, Record record) {
            clickAdapter(holder, view, position, record);
        }
    };

    public Record setRecord(Record paramRecord) {
        Record rec = new Record();
        for (Field f : paramRecord) {
            if (f.value == null) {
                String st = componGlob.getParamValue(f.name);
                if (st.length() > 0) {
                    rec.add(new Field(f.name, Field.TYPE_STRING, st));
                }
            } else {
                rec.add(new Field(f.name, Field.TYPE_STRING, f.value));
            }
        }
        return rec;
    }

    IPresenterListener listener_get_data = new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            if (selectViewHandler.afterResponse != null) {
                ParamModel parModel = selectViewHandler.paramModel;

                String fName = parModel.nameField;
                String addFieldName = parModel.nameAddField;
                ListRecords listR = null;
                if (response.type == Field.TYPE_CLASS) {
                    Field ff = ((Record) response.value).get(0);
                    if (ff.type == Field.TYPE_CLASS) {
                        Field f = ((Record) ff.value).get(0);
                        listR = (ListRecords) f.value;
                    } else {
                        listR = (ListRecords) ff.value;
                    }
                } else {
                    if (response.type == Field.TYPE_LIST_RECORD) {
                        listR = (ListRecords) response.value;
                    }
                }
                if (fName != null || addFieldName != null) {
                    String[] addField = null;
                    if (addFieldName != null && addFieldName.length() > 0) {
                        addField = addFieldName.split(",");
                    }
                    String fNameTo = parModel.nameFieldTo;
                    int addFieldType = parModel.typeAddField;
                    int addFieldIntValue = parModel.valueIntAddField;
                    if (listR != null) {
                        for (Record record : listR) {
                            if (fName != null) {
                                Field f = record.getField(fName);
                                if (f != null) {
                                    f.name = fNameTo;
                                }
                            }
                            if (addFieldName != null && addFieldName.length() > 0) {
                                for (String addSt : addField) {
                                    Field ff = new Field(addSt, addFieldType, addFieldIntValue);
                                    record.add(ff);
                                }
                            }
                        }
                    }
                }
                if (selectViewHandler.afterResponse != null) {
                    for (ViewHandler vh : selectViewHandler.afterResponse.viewHandlers) {
                        switch (vh.type) {
                            case NAME_FRAGMENT:
                                iBase.startScreen(vh.screen, false);
                                break;
                            case SET_GLOBAL:
                                activity.setGlobalData(vh.nameFieldWithValue, Field.TYPE_LIST_RECORD, listR);
                                break;
                        }
                    }
                }
            }
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            onErrorModel(statusCode, message, click);
        }
    };

    private void onErrorModel(int statusCode, String message, View.OnClickListener click) {
        Record rec = componGlob.formErrorRecord(iBase, statusCode, message);
        if (paramMV.paramModel.errorShowView != 0) {
            View v = parentLayout.findViewById(paramMV.paramModel.errorShowView);
            if (v != null) {
                if (v instanceof AnimatePanel) {
                    ((AnimatePanel) v).show(iBase);
                } else {
                    v.setVisibility(View.VISIBLE);
                }
                workWithRecordsAndViews.RecordToView(rec, v, this, click);
            }
        }
    }

    IPresenterListener listener_send_back_screen = new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            if (selectViewHandler.afterResponse != null) {
                for (ViewHandler vh : selectViewHandler.afterResponse.viewHandlers) {
                    switch (vh.type) {
                        case NAME_FRAGMENT:
                            iBase.startScreen(vh.screen, false);
                            break;
                        case PREFERENCE_SET_TOKEN:
                            Record rec = ((Record) response.value);
                            String st = rec.getString(vh.nameFieldWithValue);
                            if (st != null) {
                                preferences.setSessionToken(st);
                            }
                            break;
                        case PREFERENCE_SET_NAME:
                            rec = ((Record) response.value);
                            st = rec.getString(vh.nameFieldWithValue);
                            if (st != null) {
                                preferences.setNameString(vh.nameFieldWithValue, st);
                            }
                            break;
                        case ASSIGN_VALUE :

                            break;
                        case SHOW:
                            View vv = parentLayout.findViewById(vh.viewId);
                            if (vv != null) {
                                if (vv instanceof AnimatePanel) {
                                    ((AnimatePanel) vv).show(iBase);
                                } else {
                                    vv.setVisibility(View.VISIBLE);
                                }
                                if (vh.nameFieldWithValue.length() > 0) {
                                    workWithRecordsAndViews.RecordToView(paramToRecord(vh.nameFieldWithValue), vv);
                                }
                            }
                            break;
                        case BACK:
                            iBase.backPressed();
                            break;
                    }
                }
            }
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            onErrorModel(statusCode, message, click);
        }
    };

    private Record paramToRecord(String param) {
        Record rec = new Record();
        String[] par = param.split(",");
        if (par.length > 0) {
            for (String nameField : par) {
                String value = componGlob.getParamValue(nameField);
                if (value.length() > 0) {
                    rec.add(new Field(nameField, Field.TYPE_STRING, value));
                }
            }
        }
        return rec;
    }

    IPresenterListener listener_send_change =new IPresenterListener() {
        @Override
        public void onResponse(Field response) {
            if (paramMV.paramModel.nameTakeField == null) {
                paramMV.paramModel.field.value = response.value;
            } else {
                if (response.type == Field.TYPE_CLASS) {
                    paramMV.paramModel.field.setValue(
                            ((Record) response.value).getField(paramMV.paramModel.nameTakeField).value,
                            paramMV.paramView.viewId, iBase);
                } else {
                    paramMV.paramModel.field.setValue(response.value, paramMV.paramView.viewId, iBase);
                }
            }
            iBase.backPressed();
        }

        @Override
        public void onError(int statusCode, String message, View.OnClickListener click) {
            onErrorModel(statusCode, message, click);
        }
    };

    public ComponTools getComponTools() {
        return componTools;
    }
}
