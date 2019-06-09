package com.dpcsa.compon.base;

import android.Manifest;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ActivityResult;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.ItemSetValue;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.single.Injector;
import com.google.android.gms.common.api.GoogleApiClient;

import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.interfaces_classes.AnimatePanel;
import com.dpcsa.compon.interfaces_classes.EventComponent;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.OnResumePause;
import com.dpcsa.compon.interfaces_classes.ParentModel;
import com.dpcsa.compon.interfaces_classes.SetData;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.tools.Constants;

import java.util.ArrayList;
import java.util.List;

public class BaseFragment extends Fragment implements IBase {
    protected View parentLayout;
    private Object mObject;
    private int countProgressStart;
    private DialogFragment progressDialog;
    public List<BaseInternetProvider> listInternetProvider;
    public Screen mComponent;
    public List<EventComponent> listEvent;
    public List<ParentModel> parentModelList;
    private Bundle savedInstanceState;
    private GoogleApiClient googleApiClient;
    private List<AnimatePanel> animatePanelList;
    protected BaseActivity activity;
    private String nameMvp = null;
    public String TAG;
    public Field paramScreen;
    public List<OnResumePause> resumePauseList;
    private ComponGlob componGlob;
    private ComponPrefTool preferences;
    public WorkWithRecordsAndViews workWithRecordsAndViews;

    public BaseFragment() {
        mObject = null;
        listInternetProvider = new ArrayList<>();
        listEvent = new ArrayList<>();
        parentModelList = new ArrayList<>();
        componGlob = Injector.getComponGlob();
        TAG = componGlob.appParams.NAME_LOG_APP;
    }

    public BaseFragment getThis() {
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        activity = getBaseActivity();
        preferences = Injector.getPreferences();
        workWithRecordsAndViews = new WorkWithRecordsAndViews();
        Bundle bundle = getArguments();
        if (bundle != null) {
            nameMvp = bundle.getString(Constants.NAME_MVP);
            String paramJson = bundle.getString(Constants.NAME_PARAM_FOR_SCREEN);
            if (paramJson != null && paramJson.length() >0) {
                JsonSimple jsonSimple = new JsonSimple();
                try {
                    paramScreen = jsonSimple.jsonToModel(paramJson);
                } catch (JsonSyntaxException e) {
                    log(e.getMessage());
                    e.printStackTrace();
                }
            }
        } else if (savedInstanceState != null) {
            nameMvp = savedInstanceState.getString(Constants.NAME_MVP);
        }
        if (mComponent == null) {
            if (nameMvp != null && nameMvp.length() > 0) {
                mComponent = activity.getComponent(nameMvp);
            }
        }
        if (mComponent == null || mComponent.typeView == Screen.TYPE_VIEW.CUSTOM_FRAGMENT) {
            parentLayout = inflater.inflate(getLayoutId(), null, false);
        } else {
            parentLayout = inflater.inflate(mComponent.fragmentLayoutId, null, false);
        }
        if (mComponent != null) {
            TextView title = (TextView) componGlob.findViewByName(parentLayout, "title");
            if (title != null && mComponent.title != null) {
                if (mComponent.args != null && mComponent.args.length > 0) {
                    title.setText(String.format(mComponent.title, activity.setFormatParam(mComponent.args)));
                } else {
                    if (mComponent.title.length() > 0) {
                        title.setText(mComponent.title);
                    }
                }
            }
            if (mComponent.navigator != null) {
                for (ViewHandler vh : mComponent.navigator.viewHandlers) {
                    View v = parentLayout.findViewById(vh.viewId);
                    if (v != null) {
                        v.setOnClickListener(navigatorClick);
                    }
                }
            }
            if (mComponent.listSetData != null) {
                int ik = mComponent.listSetData.size();
                for (int i = 0; i < ik; i++) {
                    SetData sd = (SetData) mComponent.listSetData.get(i);
                    String value;
                    if (sd.source == 0) {
                        value = preferences.getNameString(sd.nameParam);
                    } else {
                        value = componGlob.getParamValue(sd.nameParam);
                    }
                    View v = parentLayout.findViewById(sd.viewId);
                    if (v != null) {
                        if (v instanceof TextView) {
                            ((TextView)v).setText(value);
                        }
                    }
                }
            }
            mComponent.initComponents(this);
            if (mComponent.moreWork != null) {
                mComponent.moreWork.startScreen();
            }
        }
        initView(savedInstanceState);
        setValue();
        animatePanelList = new ArrayList<>();
        return parentLayout;
    }

    public void initView(Bundle savedInstanceState) {

    }

    public void setValue() {
        if (mComponent.itemSetValues != null) {
            for (ItemSetValue sv : mComponent.itemSetValues) {
                View v = parentLayout.findViewById(sv.viewId);
                if (v != null && v instanceof TextView) {
                    switch (sv.type) {
                        case PARAM:
                            ((TextView) v).setText(componGlob.getParamValue(sv.name));
                            break;
                        case LOCALE:
                            ((TextView) v).setText(preferences.getLocale());
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void itemSetValue(int viewId, Object value) {
        if (mComponent.itemSetValues != null) {
            for (ItemSetValue sv : mComponent.itemSetValues) {
                if (sv.componId == viewId) {
                    switch (sv.type) {
                        case SIZE:
                            View v = parentLayout.findViewById(sv.viewId);
                            if (v != null) {
                                if (v instanceof IComponent) {
                                    ((IComponent) v).setData(value);
                                } else if (v instanceof TextView){
                                    ((TextView) v).setText((Integer) value);
                                }
                            }
                            break;
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.NAME_MVP, nameMvp);
    }

    @Override
    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    @Override
    public void startDrawerFragment(String screen, int containerFragmentId) {
    }

    public int getLayoutId() {
        return 0;
    }

    @Override
    public void log(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    public void setResumePause(OnResumePause resumePause) {
        if (resumePauseList == null) {
            resumePauseList = new ArrayList<>();
        }
        resumePauseList.add(resumePause);
    }

    View.OnClickListener navigatorClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            for (ViewHandler vh : mComponent.navigator.viewHandlers) {
                if (vh.viewId == id) {
                    switch (vh.type) {
                        case NAME_FRAGMENT:
                            int requestCode = -1;
                            if (vh.afterResponse != null) {
//                                requestCode = activity.addForResult(vh.afterResponse, activity.activityResult);
                                requestCode = activity.addForResult(vh.afterResponse, activityResult);
                            }
                            if (vh.blocked) {
                                view.setEnabled(false);
                            }
                            switch (vh.paramForScreen) {
                                case RECORD:
                                    startScreen(vh.screen, false, paramScreen, requestCode);
                                    break;
                                case RECORD_COMPONENT:
                                    BaseComponent bc = mComponent.getComponent(vh.componId);
                                    if (bc != null) {
                                        componGlob.setParam(bc.recordComponent);
                                        startScreen(vh.screen, false, bc.recordComponent, requestCode);
                                    }
                                    break;
                                default:
                                    if (vh.addFragment) {
                                        startScreen(vh.screen, false, null, requestCode, vh.addFragment);
                                    } else {
                                        startScreen(vh.screen, false, null, requestCode);
                                    }
                                    break;
                            }
                            break;
                        case SET_PARAM:
                            componGlob.setParamValue(vh.nameFieldWithValue, vh.pref_value_string);
                            break;
                        case CALL_UP:
                            if (view instanceof TextView) {
                                String st = ((TextView) view).getText().toString();
                                if (st != null && st.length() > 0) {
                                    activity.callUp(st);
                                }
                            }
                            break;
                        case DIAL_UP:
                            if (view instanceof TextView) {
                                String st = ((TextView) view).getText().toString();
                                if (st != null && st.length() > 0) {
                                    activity.startDialPhone(st);
                                }
                            }
                            break;
                        case SHOW:
                            if (vh.onActivity) {
                                activity.showSheetBottom(vh.showViewId, null, null, null);
                            } else {
                                View showView = parentLayout.findViewById(vh.showViewId);
                                if (showView instanceof AnimatePanel) {
                                    ((AnimatePanel) showView).show(BaseFragment.this);
                                } else {
                                    showView.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                        case SHOW_HIDE:
                            View vv = parentLayout.findViewById(vh.showViewId);
                            if (vv != null) {
                                TextView tv = (TextView) view;
                                if (vv instanceof AnimatePanel) {
                                    AnimatePanel ap = (AnimatePanel) vv;
                                    if (ap.isShow()) {
                                        ap.hide();
                                        tv.setText(activity.getString(vh.textHideId));
                                    } else {
                                        ap.show(getThis());
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
                        case SET_GLOBAL:
                            BaseComponent bc = mComponent.getComponent(vh.componId);
                            bc.setGlobalData(vh.nameFieldWithValue);
                            break;
                        case SET_MENU_DEF:
                            activity.setMenu();
                            break;
                        case SET_MENU:
                            activity.setMenu();
                            break;
                        case CLICK_VIEW:
                            if (mComponent.iCustom != null) {
                                mComponent.iCustom.clickView(view, parentLayout, null, null, -1);
                            } else if (mComponent.moreWork != null) {
                                mComponent.moreWork.clickView(view, parentLayout, null, null, -1);
                            }
                            break;
                        case BACK:
                            backPressed();
                            break;
                        case OPEN_DRAWER:
                            activity.openDrawer();
                            break;
                        case RECEIVER:
                            LocalBroadcastManager.getInstance(activity)
                                    .registerReceiver(broadcastReceiver, new IntentFilter(vh.nameFieldWithValue));
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
                                    log(e.getMessage());
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
                                    ((AnimatePanel) vv).show(getThis());
                                } else {
                                    vv.setVisibility(View.VISIBLE);
                                }
                                if (vh.nameFieldWithValue != null && vh.nameFieldWithValue.length() > 0) {
                                    workWithRecordsAndViews.RecordToView(componGlob.paramToRecord(vh.nameFieldWithValue), vv);
                                }
                            }
                            break;
                    }
                }
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mComponent.moreWork != null) {
                mComponent.moreWork.receiverWork(intent);
            }
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver);
        }
    };

    public void setModel(Screen mComponent) {
        this.mComponent = mComponent;
    }

    @Override
    public void onStop() {
        if (mComponent != null && mComponent.moreWork != null) {
            mComponent.moreWork.stopScreen();
        }
        if (listInternetProvider != null) {
            for (BaseInternetProvider provider : listInternetProvider) {
                provider.cancel();
            }
        }
        mObject = null;
        super.onStop();
    }

    @Override
    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onPause();
            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onDestroy();
            }
        }
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void setFragmentsContainerId(int id) {

    }

    @Override
    public boolean isHideAnimatePanel() {
        int pos = animatePanelList.size();
        if (pos > 0) {
            animatePanelList.get(pos - 1).hide();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void addEvent(int sender, BaseComponent receiver) {
        listEvent.add(new EventComponent(sender, receiver));
    }

    @Override
    public void addEvent(int[] senderList, BaseComponent receiver) {
        for (int sender : senderList) {
            listEvent.add(new EventComponent(sender, receiver));
        }
    }

    @Override
    public Field getProfile() {
        return componGlob.profile;
    }

    @Override
    public void backPressed() {
        ((BaseActivity) getActivity()).onBackPressed();
    }

    public boolean canBackPressed() {
        return isHideAnimatePanel();
    }

    @Override
    public BaseActivity getBaseActivity(){
        return (BaseActivity) getActivity();
    }

    @Override
    public BaseFragment getBaseFragment() {
        return this;
    }

    @Override
    public void addInternetProvider(BaseInternetProvider internetProvider) {
        listInternetProvider.add(internetProvider);
    }

    @Override
    public void sendEvent(int sender) {
        for (EventComponent ev : listEvent) {
            if (ev.eventSenderId == sender) {
                ev.eventReceiverComponent.actual();
            }
        }
    }

    @Override
    public void sendActualEvent(int sender, Object paramEvent) {
        for (EventComponent ev : listEvent) {
            if (ev.eventSenderId == sender) {
                ev.eventReceiverComponent.actualEvent(sender, paramEvent);
            }
        }
    }

    public ParentModel getParentModel(String name) {
        if (parentModelList.size() > 0) {
            for (ParentModel pm : parentModelList) {
                if (pm.nameParentModel != null && pm.nameParentModel.equals(name)) {
                    return pm;
                }
            }
        }
        ParentModel pm = new ParentModel(name);
        parentModelList.add(pm);
        return pm;
    }

    public String getName() {
        return "Base";
    }

    public void setObject(Object o) {
        mObject = o;
    }

    public Object getObject() {
        return mObject;
    }

    @Override
    public View getParentLayout() {
        return parentLayout;
    }

    @Override
    public void startScreen(String screen, boolean startFlag, Object object, int forResult) {
        activity.startScreen(screen, startFlag, object, forResult);
    }

    @Override
    public void startScreen(String screen, boolean startFlag, Object object, int forResult, boolean addFragment) {
        activity.startScreen(screen, startFlag, object, forResult, addFragment);
    }

    @Override
    public void startScreen(String screen, boolean startFlag, Object object) {
        activity.startScreen(screen, startFlag, object);
    }

    @Override
    public void startScreen(String screen, boolean startFlag) {
        activity.startScreen(screen, startFlag, -1);
    }

    public void startFragment(String nameMVP, boolean startFlag,Object object) {
        activity.startFragment(nameMVP, activity.mapFragment.get(nameMVP), startFlag, object, -1, false);
    }

    @Override
    public void progressStart() {
        activity.progressStart();
    }

    @Override
    public void progressStop() {
        activity.progressStop();
    }


    @Override
    public void showDialog(String title, String message, View.OnClickListener click) {
        activity.showDialog(title, message, click);
    }

    @Override
    public void showDialog(int statusCode, String message, View.OnClickListener click) {
        activity.showDialog(statusCode, message, click);
    }

    @Override
    public boolean isViewActive() {
        return activity.isViewActive();
    }

    @Override
    public void addAnimatePanel(AnimatePanel animatePanel) {
        animatePanelList.add(animatePanel);
    }

    @Override
    public void delAnimatePanel(AnimatePanel animatePanel) {
        animatePanelList.remove(animatePanel);
    }

    @Override
    public Field getParamScreen() {
        return paramScreen;
    }

    public boolean noKeyBack() {
        if (mComponent.keyBack != 0) {
            View v = parentLayout.findViewById(mComponent.keyBack);
            if (v == null) {
                log("Нет View для keyBack в " + mComponent.nameComponent);
            } else {
                navigatorClick.onClick(v);
            }
            return false;
        } else {
            return true;
        }
    }

}
