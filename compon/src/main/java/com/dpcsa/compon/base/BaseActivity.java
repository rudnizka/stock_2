package com.dpcsa.compon.base;

import android.Manifest;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dpcsa.compon.components.ButtonComponent;
import com.dpcsa.compon.components.MapComponent;
import com.dpcsa.compon.components.MenuBottomComponent;
import com.dpcsa.compon.dialogs.ProgressDialog;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.ItemSetValue;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.single.Injector;
import com.google.android.gms.common.api.GoogleApiClient;

import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.R;
import com.dpcsa.compon.dialogs.DialogTools;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ActivityResult;
import com.dpcsa.compon.interfaces_classes.AnimatePanel;
import com.dpcsa.compon.interfaces_classes.EventComponent;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.ICustom;
import com.dpcsa.compon.interfaces_classes.OnResumePause;
import com.dpcsa.compon.interfaces_classes.Param;
import com.dpcsa.compon.interfaces_classes.ParentModel;
import com.dpcsa.compon.interfaces_classes.PermissionsResult;
import com.dpcsa.compon.interfaces_classes.RequestActivityResult;
import com.dpcsa.compon.interfaces_classes.RequestPermissionsResult;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.json_simple.SimpleRecordToJson;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.tools.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.View.inflate;

public abstract class BaseActivity extends FragmentActivity implements IBase {

    public Map<String, Screen> mapFragment;
    private DialogFragment progressDialog;
    private int countProgressStart;
    public List<BaseInternetProvider> listInternetProvider;
    public List<EventComponent> listEvent;
    public View parentLayout;
    public Screen mComponent;
    public int containerFragmentId;
    private boolean isActive;
    public List<ParentModel> parentModelList;
    private Bundle savedInstanceState;
    private GoogleApiClient googleApiClient;
    private List<AnimatePanel> animatePanelList;
    public DrawerLayout drawer;
    public ComponGlob componGlob;
    public String TAG;
    public List<RequestActivityResult> activityResultList;
    public List<RequestPermissionsResult> permissionsResultList;
    public Field paramScreen;
    public WorkWithRecordsAndViews workWithRecordsAndViews;
    public Record paramScreenRecord;
    public List<OnResumePause> resumePauseList;
    private ComponPrefTool preferences;
    private JsonSimple jsonSimple = new JsonSimple();
    private List<String> nameGlobalData;
    private String phoneDial;
    private final int CALL_PHONE_REQUEST = 10101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();

        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;
        parentModelList = new ArrayList<>();
        preferences = Injector.getPreferences();
        componGlob = Injector.getComponGlob();
        TAG = componGlob.appParams.NAME_LOG_APP;
        mapFragment = componGlob.MapScreen;
        nameGlobalData = new ArrayList<>();
        animatePanelList = new ArrayList<>();
        activityResultList = null;
        permissionsResultList = null;
        countProgressStart = 0;
        listInternetProvider = new ArrayList<>();
        listEvent = new ArrayList<>();
        String st = componGlob.appParams.nameLanguageInHeader;
//        Intent intent = getIntent();
        workWithRecordsAndViews = new WorkWithRecordsAndViews();
        String paramJson = intent.getStringExtra(Constants.NAME_PARAM_FOR_SCREEN);
        if (paramJson != null && paramJson.length() >0) {
//            Log.d("QWERT","BaseActivity paramJson="+paramJson);
            try {
                paramScreen = jsonSimple.jsonToModel(paramJson);
                paramScreenRecord = (Record) paramScreen.value;
            } catch (JsonSyntaxException e) {
                log(e.getMessage());
                e.printStackTrace();
            }
        }
        if ((st != null && st.length() > 0) || componGlob.appParams.nameLanguageInURL) {
            setLocale();
        }
        String nameScreen = getNameScreen();
        if (nameScreen == null) {
            nameScreen = intent.getStringExtra(Constants.NAME_MVP);
        }

        if (nameScreen != null && nameScreen.length() > 0) {
            mComponent = getComponent(nameScreen);
            if (mComponent.typeView == Screen.TYPE_VIEW.CUSTOM_ACTIVITY) {
                parentLayout = inflate(this, getLayoutId(), null);
            } else {
                parentLayout = inflate(this, mComponent.fragmentLayoutId, null);
            }
        } else {
            parentLayout = inflate(this, getLayoutId(), null);
        }
        if (nameScreen != null) {
            setContentView(parentLayout);
            if (mComponent.navigator != null) {
                for (ViewHandler vh : mComponent.navigator.viewHandlers) {
                    View v = findViewById(vh.viewId);
                    if (v != null) {
                        v.setOnClickListener(navigatorClick);
                    }
                }
            }
            mComponent.initComponents(this);
            if (mComponent.moreWork != null) {
                mComponent.moreWork.startScreen();
            }
        }

        if (this instanceof ICustom) {
            mComponent.setCustom((ICustom) this);
        }
        TextView title = (TextView) componGlob.findViewByName(parentLayout, "title");
        if (title != null && mComponent.title != null) {
            if (mComponent.args != null && mComponent.args.length > 0) {
                title.setText(String.format(mComponent.title, setFormatParam(mComponent.args)));
            } else {
                if (mComponent.title.length() > 0) {
                    title.setText(mComponent.title);
                }
            }
        }
        initView();
        setValue();
    }

    public void setValue() {
        if (mComponent.itemSetValues != null) {
            for (ItemSetValue sv : mComponent.itemSetValues) {
                View v = parentLayout.findViewById(sv.viewId);
                if (v != null) {
                    switch (sv.type) {
                        case PARAM:
                            if (v instanceof TextView) {
                                ((TextView) v).setText(componGlob.getParamValue(sv.name));
                            } else if (v instanceof IComponent) {
                                ((IComponent) v).setData(componGlob.getParamValue(sv.name));
                            }
                            break;
                        case LOCALE:
                            if (v instanceof TextView) {
                                ((TextView) v).setText(preferences.getLocale());
                            } else if (v instanceof IComponent) {
                                ((IComponent) v).setData(preferences.getLocale());
                            }
//                            ((TextView) v).setText(preferences.getLocale());
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

    public void setLocale() {
        String loc = preferences.getLocale();
        if (loc.length() == 0) {
            loc = "uk";
        }
        if (loc.equals(Locale.getDefault().getLanguage())) return;
        Locale myLocale = new Locale(loc);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    @Override
    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    public String getNameScreen() {
        return null;
    }

    public Screen getScreen() {
        return null;
    }

    public int getLayoutId() {
        return 0;
    }

    public void initView() {

    }

    public void addPermissionsResult(int requestCode, PermissionsResult permissionsResult) {
        if (permissionsResultList == null) {
            permissionsResultList = new ArrayList<>();
        }
        permissionsResultList.add(new RequestPermissionsResult(requestCode, permissionsResult));
    }

    public int addForResult(ActionsAfterResponse afterResponse, ActivityResult activityResult) {
        int rc = 0;
        if (activityResultList != null) {
            rc = activityResultList.size();
        }
        addForResult(rc, afterResponse, activityResult);
        return rc;
    }

    public void addForResult(int requestCode, ActionsAfterResponse afterResponse, ActivityResult activityResult) {
        if (activityResultList == null) {
            activityResultList = new ArrayList<>();
        }
        activityResultList.add(new RequestActivityResult(requestCode, afterResponse, activityResult));
    }

    public void addForResult(int requestCode, ActivityResult activityResult) {
        if (activityResultList == null) {
            activityResultList = new ArrayList<>();
        }
        activityResultList.add(new RequestActivityResult(requestCode, activityResult));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CALL_PHONE_REQUEST) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCallPhone();
                }
            }
        } else {
            if (permissionsResultList != null) {
                int ik = permissionsResultList.size();
                int j = -1;
                for (int i = 0; i < ik; i++) {
                    RequestPermissionsResult rpr = permissionsResultList.get(i);
                    if (requestCode == rpr.request) {
                        rpr.permissionsResult.onPermissionsResult(requestCode, permissions, grantResults);
                        j = i;
                        break;
                    }
                    if (j > -1) {
                        permissionsResultList.remove(j);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityResultList != null) {
            int ik = activityResultList.size();
            int j = -1;
            for (int i = 0; i < ik; i++) {
                RequestActivityResult rar = activityResultList.get(i);
                if (requestCode == rar.request) {
                    j = i;
                    rar.activityResult.onActivityResult(requestCode, resultCode, data, rar.afterResponse);
                    break;
                }
            }
            if (j > -1) {
                RequestActivityResult rar = activityResultList.get(j);
                rar.request = -1;
                rar.activityResult = null;
                rar.afterResponse = null;
            }
        }
    }

    ActivityResult activityResult  = new ActivityResult() {
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data, ActionsAfterResponse afterResponse) {
            if (resultCode == RESULT_OK) {
                for (ViewHandler vh : afterResponse.viewHandlers) {
                    switch (vh.type) {
                        case UPDATE_DATA:
                            mComponent.getComponent(vh.viewId).updateData(vh.paramModel);
                            break;
                    }
                }
            }
        }
    };

    public View.OnClickListener navigatorClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            for (ViewHandler vh : mComponent.navigator.viewHandlers) {
                if (vh.viewId == id) {
                    switch (vh.type) {
                        case NAME_FRAGMENT:
                            int requestCode = -1;
                            if (vh.afterResponse != null) {
                                requestCode = addForResult(vh.afterResponse, activityResult);
                            }
                            switch (vh.paramForScreen) {
                                case RECORD:
                                    startScreen(vh.screen, false, paramScreenRecord, requestCode);
                                    break;
                                case RECORD_COMPONENT:
                                    BaseComponent bc = mComponent.getComponent(vh.componId);
                                    if (bc != null) {
                                        componGlob.setParam(bc.recordComponent);
                                        startScreen(vh.screen, false, bc.recordComponent, requestCode);
                                    }
                                    break;
                                default:
                                    startScreen(vh.screen, false, null, requestCode);
                                    break;
                            }
                            break;
                        case BACK:
                            onBackPressed();
                            break;
                        case CLICK_VIEW:
                            if (mComponent.iCustom != null) {
                                mComponent.iCustom.clickView(view, parentLayout, null, null, -1);
                            } else if (mComponent.moreWork != null) {
                                mComponent.moreWork.clickView(view, parentLayout, null, null, -1);
                            }
                            break;
                        case SHOW:
                            View showView = parentLayout.findViewById(vh.showViewId);
                            if (showView != null) {
                                if (showView instanceof AnimatePanel) {
                                    ((AnimatePanel) showView).show(BaseActivity.this);
                                } else {
                                    showView.setVisibility(View.VISIBLE);
                                }
                            }
                            break;
                        case SET_VALUE:
                            View showV = parentLayout.findViewById(vh.showViewId);
                            if (showV != null) {
                                if (showV instanceof TextView) {
                                    ((TextView) showV).setText(getString(vh.idString));
                                }
                            }
                            break;
                        case CALL_UP:
                            if (view instanceof TextView) {
                                String st = ((TextView) view).getText().toString();
                                if (st != null && st.length() > 0) {
                                    callUp(st);
                                }
                            }
                            break;
                        case DIAL_UP:
                            if (view instanceof TextView) {
                                String st = ((TextView) view).getText().toString();
                                if (st != null && st.length() > 0) {
                                    startDialPhone(st);
                                }
                            }
                            break;
                        case RESULT_RECORD :
                            if (vh.nameFieldWithValue != null && vh.nameFieldWithValue.length() > 0) {
                                Record record = workWithRecordsAndViews.ViewToRecord(parentLayout, vh.nameFieldWithValue);
                                Intent intent = new Intent();
                                intent.putExtra(Constants.RECORD, record.toString());
                                setResult(RESULT_OK, intent);
                            }
                            finishActivity();
                            break;
                        case SET_LOCALE:
                            preferences.setLocale(componGlob.getParamValue(componGlob.appParams.nameLanguageInParam));
                            recreate();
                            break;
                        case SET_GLOBAL:
                            BaseComponent bc = mComponent.getComponent(vh.componId);
                            bc.setGlobalData(vh.nameFieldWithValue);
                            break;
                        case RECEIVER:
                            LocalBroadcastManager.getInstance(BaseActivity.this).registerReceiver(broadcastReceiver,
                                    new IntentFilter(vh.nameFieldWithValue));
                            break;
                        case RESULT_PARAM :
                            Record record = workWithRecordsAndViews.ViewToRecord(parentLayout, vh.nameFieldWithValue);
                            if (record != null) {
                                componGlob.setParam(record);
                            }
                            setResult(RESULT_OK);
                            finishActivity();
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
            LocalBroadcastManager.getInstance(BaseActivity.this).unregisterReceiver(broadcastReceiver);
        }
    };

    public Screen getComponent(String name) {
        return componGlob.MapScreen.get(name);
    }

    @Override
    public void setFragmentsContainerId(int id) {
        containerFragmentId = id;
    }

    @Override
    protected void onStop() {
        if (listInternetProvider != null) {
            for (BaseInternetProvider provider : listInternetProvider) {
                provider.cancel();
            }
        }
        super.onStop();
    }

    @Override
    public void setResumePause(OnResumePause resumePause) {
        if (resumePauseList == null) {
            resumePauseList = new ArrayList<>();
        }
        resumePauseList.add(resumePause);
    }

    @Override
    public void onResume() {
        super.onResume();
        isActive = true;
        int statusBarColor = preferences.getStatusBarColor();
        if (statusBarColor != 0) {
            setStatusBarColor(statusBarColor);
        }
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onResume();
            }
        }
        if (countProgressStart <= 0 && progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onPause() {
        if (resumePauseList != null) {
            for (OnResumePause rp : resumePauseList) {
                rp.onPause();
            }
        }
        isActive = false;
        super.onPause();
    }

    @Override
    public void log(String msg) {
        Log.i(TAG, msg);
    }

    public void setStatusColor(int color) {
        preferences.setStatusBarColor(color);
    }

    @Override
    protected void onDestroy() {
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
    public Field getProfile() {
        return componGlob.profile;
    }

    @Override
    public void backPressed() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if ( ! canBackPressed()) {
            return;
        }
        FragmentManager fm = getSupportFragmentManager();
        int countFragment = fm.getBackStackEntryCount();
        if (countFragment > 0) {
            List<Fragment> fragmentList = fm.getFragments();
            Fragment fragment = topFragment(fm);
            if (fragment != null && fragment instanceof BaseFragment) {
                if (((BaseFragment) fragment).canBackPressed()) {
                    if (((BaseFragment) fragment).noKeyBack()) {
                        if (countFragment == 1) {
                            finish();
                        } else {
                            super.onBackPressed();
                        }
                    }
                }
            } else {
                if (countFragment == 1) {
                    if (canBackPressed()) {
                        finishActivity();
                    }
                } else {
                    super.onBackPressed();
                }
            }
        } else {
            finishActivity();
        }
    }

    @Override
    public void finish() {
        if (nameGlobalData.size() > 0) {
            for (String name : nameGlobalData) {
                componGlob.delGlobalData(name);
            }
        }
        super.finish();
    }

    public void finishActivity() {
        finish();
        if (mComponent.animateScreen != null) {
            switch (mComponent.animateScreen) {
                case TB :
                    overridePendingTransition(R.anim.bt_in, R.anim.bt_out);
                    break;
                case BT :
                    overridePendingTransition(R.anim.tb_in, R.anim.tb_out);
                    break;
                case LR :
                    overridePendingTransition(R.anim.rl_in, R.anim.rl_out);
                    break;
                case RL :
                    overridePendingTransition(R.anim.lr_in, R.anim.lr_out);
                    break;
            }
        }
    }

    private Fragment topFragment(FragmentManager fm) {
        List<Fragment> fragmentList = fm.getFragments();
        Fragment fragment = null;
        for (Fragment fragm : fragmentList) {
            if (fragm != null) {
                fragment = fragm;
            }
        }
        return fragment;
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

    public boolean canBackPressed() {
        return isHideAnimatePanel();
    }

    @Override
    public BaseFragment getBaseFragment() {
        return null;
    }

    @Override
    public boolean isViewActive() {
        return isActive;
    }

//    @Override

    public void startActivitySimple(String nameMVP, Object object) {
        Screen mc = mapFragment.get(nameMVP);
        if (mc != null) {
            startActivitySimple(nameMVP, mc, object, -1);
        } else {
            log("Нет Screens с именем " + nameMVP);
        }
    }

    public void startActivitySimple(String nameMVP, Screen mc, Object object, int forResult) {
        Intent intent;
        if (mc.customFragment == null) {
            intent = new Intent(this, ComponBaseStartActivity.class);
        } else {
            intent = new Intent(this, mc.customFragment);
        }
        intent.putExtra(Constants.NAME_MVP, nameMVP);
        if (object != null) {
            SimpleRecordToJson recordToJson = new SimpleRecordToJson();
            Field f = new Field();
            f.value = object;
            if (object instanceof Record) {
                f.type = Field.TYPE_RECORD;
                intent.putExtra(Constants.NAME_PARAM_FOR_SCREEN, recordToJson.modelToJson(f));
            } else if (object instanceof ListRecords) {
                f.type = Field.TYPE_LIST_RECORD;
                intent.putExtra(Constants.NAME_PARAM_FOR_SCREEN, recordToJson.modelToJson(f));
            }
        }
        if (forResult > -1) {
            startActivityForResult(intent, forResult);
        } else {
            startActivity(intent);
        }
        if (mc.animateScreen != null) {
            switch (mc.animateScreen) {
                case TB :
                    overridePendingTransition(R.anim.tb_in, R.anim.tb_out);
                    break;
                case BT :
                    overridePendingTransition(R.anim.bt_in, R.anim.bt_out);
                    break;
                case LR :
                    overridePendingTransition(R.anim.lr_in, R.anim.lr_out);
                    break;
                case RL :
                    overridePendingTransition(R.anim.rl_in, R.anim.rl_out);
                    break;
            }
        }
    }

    public void closeDrawer() {
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public void openDrawer() {
        if (drawer != null) {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public ParentModel getParentModel(String name) {
        if (parentModelList.size() > 0) {
            for (ParentModel pm : parentModelList) {
                if (pm.nameParentModel.equals(name)) {
                    return pm;
                }
            }
        }
        ParentModel pm = new ParentModel(name);
        parentModelList.add(pm);
        return pm;
    }

    public void showDialog(String title, String message, View.OnClickListener click) {
        int id = componGlob.appParams.errorDialogViewId;
//        Log.d("QWERT","showDialog IIIIIIDDDDD="+id+" message="+message);
        if (id != 0) {
            Record rec = new Record();
            rec.add(new Field("title", Field.TYPE_STRING, title));
            rec.add(new Field("body", Field.TYPE_STRING, message));
            View viewErrorDialog = parentLayout.findViewById(id);
            if (viewErrorDialog instanceof AnimatePanel) {
                ((AnimatePanel) viewErrorDialog).show(this);
                workWithRecordsAndViews.RecordToView(rec, viewErrorDialog);
            }
        } else {
            DialogTools.showDialog(this, title, message, click);
        }
    }

    @Override
    public void showDialog(int statusCode, String message, View.OnClickListener click) {
        showDialog(componGlob.formErrorRecord(this, statusCode, message), message, click);
    }

    public void showDialog(Record rec, String message, View.OnClickListener click) {
        int id = componGlob.appParams.errorDialogViewId;
        if (id != 0) {
            View viewErrorDialog = parentLayout.findViewById(id);
            if (viewErrorDialog instanceof AnimatePanel) {
                ((AnimatePanel) viewErrorDialog).show(this);
                workWithRecordsAndViews.RecordToView(rec, viewErrorDialog);
            } else {
                viewErrorDialog.setVisibility(View.VISIBLE);
                workWithRecordsAndViews.RecordToView(rec, viewErrorDialog);
            }
        } else {
            DialogTools.showDialog(this, "", message, click);
        }
    }

    @Override
    public void progressStart() {
        if (componGlob.appParams.classProgress != null) {
            if (progressDialog == null) {
                try {
                    progressDialog = (DialogFragment) componGlob.appParams.classProgress.newInstance();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (countProgressStart == 0) {
                progressDialog.show(getFragmentManager(), "MyProgressDialog");
            }
            countProgressStart++;
        } else if (componGlob.appParams.progressViewId != 0) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog();
            }
            if (countProgressStart == 0) {
                progressDialog.show(getFragmentManager(), "MyProgressDialog");
            }
            countProgressStart++;
        }
    }

    @Override
    public void progressStop() {
        countProgressStart--;
        if (countProgressStart <= 0 && progressDialog != null && isActive) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void resetProgress() {
        countProgressStart = 0;
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void startDrawerFragment(String nameMVP, int containerFragmentId) {
        Screen model = mapFragment.get(nameMVP);
        BaseFragment fragment = new BaseFragment();
        fragment.setModel(model);
        Bundle bundle =new Bundle();
        bundle.putString(Constants.NAME_MVP, model.nameComponent);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(containerFragmentId, fragment, model.nameComponent);
        transaction.commit();
    }

    @Override
    public void startScreen(String screen, boolean startFlag) {
        startScreen(screen, startFlag, null, -1);
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    @Override
    public void startScreen(String screen, boolean startFlag, Object object) {
        startScreen(screen, startFlag, object, -1);
    }

    @Override
    public void startScreen(String nameMVP, boolean startFlag, Object object, int forResult) {
        startScreen(nameMVP, startFlag, object, forResult, false);
    }

    @Override
    public void startScreen(String nameMVP, boolean startFlag, Object object, int forResult, boolean addFragment) {
        Screen mComponent = mapFragment.get(nameMVP);
//        Log.d("QWERT","startScreen mComponent="+mComponent);
//        String nameMVP = mComponent.nameComponent;
        if (mComponent == null || mComponent.typeView == null) {
            log("Нет Screens с именем " + nameMVP);
            return;
        }
        switch (mComponent.typeView) {
            case ACTIVITY:
                startActivitySimple(nameMVP, mComponent, object, forResult);
                break;
            case CUSTOM_ACTIVITY:
                startActivitySimple(nameMVP, mComponent, object, forResult);
                break;
            case FRAGMENT:
                startFragment(nameMVP, mComponent, startFlag, object, forResult, addFragment);
                break;
            case CUSTOM_FRAGMENT:
                startCustomFragment(nameMVP, mComponent, startFlag, object, forResult, addFragment);
                break;
        }
    }

    public void startCustomFragment(String nameMVP, Screen mComponent, boolean startFlag, Object object, int forResult, boolean addFragment) {
        BaseFragment fr = (BaseFragment) getSupportFragmentManager().findFragmentByTag(nameMVP);
        int count = (fr == null) ? 0 : 1;
        if (startFlag) {
            clearBackStack(count);
        }
        BaseFragment fragment = null; // (fr != null) ? fr : new ComponentsFragment();
        if (fr != null) {
            fragment = fr;
        } else {
            try {
                fragment = (BaseFragment) mComponent.customFragment.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (fragment != null) {
            Bundle bundle = null;
            if (object != null) {
                if (object instanceof Bundle) {
                    bundle= (Bundle) object;
                }
            }
            if (bundle == null){
                bundle = new Bundle();
            }
            bundle.putString(Constants.NAME_MVP, nameMVP);
            if (object != null) {
                if (object instanceof Record || object instanceof ListRecords) {
                    SimpleRecordToJson recordToJson = new SimpleRecordToJson();
                    Field f = new Field();
                    f.value = object;
                    if (object instanceof Record) {
                        f.type = Field.TYPE_RECORD;
                    } else {
                        f.type = Field.TYPE_LIST_RECORD;
                    }
                    bundle.putString(Constants.NAME_PARAM_FOR_SCREEN, recordToJson.modelToJson(f));
                } else {
                    fragment.setObject(object);
                }
            }
            fragment.setArguments(bundle);
            fragment.setModel(mComponent);
            startNewFragment(fragment, nameMVP, mComponent, addFragment);
        }
    }

    public void startFragment(String nameMVP, Screen mComponent, boolean startFlag, Object object, int forResult, boolean addFragment) {
        BaseFragment fr = (BaseFragment) getSupportFragmentManager().findFragmentByTag(nameMVP);
        int count = (fr == null) ? 0 : 1;
        if (startFlag) {
            clearBackStack(count);
        }
        BaseFragment fragment = (fr != null) ? fr : new BaseFragment();
        Bundle bundle =new Bundle();
        bundle.putString(Constants.NAME_MVP, nameMVP);
        if (object != null) {
            SimpleRecordToJson recordToJson = new SimpleRecordToJson();
            Field f = new Field();
            f.value = object;
            if (object instanceof Record) {
                f.type = Field.TYPE_RECORD;
                bundle.putString(Constants.NAME_PARAM_FOR_SCREEN, recordToJson.modelToJson(f));
            } else if (object instanceof ListRecords) {
                f.type = Field.TYPE_LIST_RECORD;
                bundle.putString(Constants.NAME_PARAM_FOR_SCREEN, recordToJson.modelToJson(f));
            }
        }
        fragment.setArguments(bundle);
        fragment.setModel(mComponent);
        startNewFragment(fragment, nameMVP, mComponent, addFragment);
    }

    private void startNewFragment(BaseFragment fragment, String nameMVP, Screen mComponent, boolean addFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mComponent.animateScreen != null) {
            switch (mComponent.animateScreen) {
                case RL:
                    transaction.setCustomAnimations(R.anim.rl_in, R.anim.rl_out,
                            R.anim.lr_in, R.anim.lr_out);
                    break;
                case LR :
                    transaction.setCustomAnimations(R.anim.lr_in, R.anim.lr_out,
                            R.anim.rl_in, R.anim.rl_out);
                    break;
                case TB :
                    transaction.setCustomAnimations(R.anim.tb_in, R.anim.tb_out,
                            R.anim.bt_in, R.anim.bt_out);
                    break;
                case BT :
                    transaction.setCustomAnimations(R.anim.bt_in, R.anim.bt_out,
                            R.anim.tb_in, R.anim.tb_out);
                    break;
            }
        }
        if (addFragment) {
            transaction.add(containerFragmentId, fragment, nameMVP);
        } else {
            transaction.replace(containerFragmentId, fragment, nameMVP);
        }
        resetProgress();
        transaction.addToBackStack(nameMVP)
                .commit();
    }

    public void clearBackStack(int count) {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > count) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(count);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void addParamValue(String name, String value) {
        componGlob.addParamValue(name, value);
    }


    @Override
    public BaseActivity getBaseActivity() {
        return this;
    }

    @Override
    public void addInternetProvider(BaseInternetProvider internetProvider) {
        listInternetProvider.add(internetProvider);
    }

    @Override
    public View getParentLayout() {
        return parentLayout;
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

    public String setFormatParam(String[] args) {
        String st = "";
        List<Param> paramValues = componGlob.paramValues;
        String sep = "";
        for (String arg : args) {
            for (Param paramV : paramValues) {
                if (arg.equals(paramV.name)) {
                    st = sep + paramV.value;
                    sep = ",";
                    break;
                }
            }
        }
        return st;
    }

    public void showSheetBottom(int id, Record rec, BaseComponent bc, View.OnClickListener clickView) {
        View clickInfoWindow = parentLayout.findViewById(id);
        if (clickInfoWindow == null) {
            log("Не найден clickInfoWindow в " + mComponent.nameComponent);
        } else {
            if (clickInfoWindow instanceof AnimatePanel) {
                ((AnimatePanel) clickInfoWindow).show(this);
            } else {
                clickInfoWindow.setVisibility(View.VISIBLE);
            }
            if (rec != null) {
                workWithRecordsAndViews.RecordToView(rec, clickInfoWindow, bc, clickView);
            }
        }
    }

    public void setGlobalData(String name, int type, Object data) {
        if (name != null && name.length() > 0) {
            nameGlobalData.add(name);
            Field ff = componGlob.globalData.getField(name);
            if (ff == null) {
                componGlob.globalData.add(new Field(name, type, data));
            } else {
                ff.type = type;
                ff.value = data;
            }
        }
    }

    public void setMenu() {
        MenuBottomComponent menu = (MenuBottomComponent) mComponent.getComponent(ParamComponent.TC.MENU_BOTTOM);
        if (menu != null) {
            menu.setItem();
        }
    }

    public void setMenu(int position) {
        MenuBottomComponent menu = (MenuBottomComponent) mComponent.getComponent(ParamComponent.TC.MENU_BOTTOM);
        if (menu != null) {
            menu.setItem(position);
        }
    }

    public void callUp(String phoneDial) {
        this.phoneDial = phoneDial;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    CALL_PHONE_REQUEST);
        } else {
            startCallPhone();
        }
    }

    private void startCallPhone() {
        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneDial));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(dialIntent);
    }

    public void startDialPhone(String phone) {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        startActivity(dialIntent);
    }

    // Прибирати клавіатуру при кліку за межами EditText
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            final View view = getCurrentFocus();
            if (view != null) {
                final boolean consumed = super.dispatchTouchEvent(ev);
                final View viewTmp = getCurrentFocus();
                final View viewNew = viewTmp != null ? viewTmp : view;
                if (viewNew.equals(view)) {
                    final Rect rect = new Rect();
                    final int[] coordinates = new int[2];
                    view.getLocationOnScreen(coordinates);
                    rect.set(coordinates[0], coordinates[1], coordinates[0] + view.getWidth(), coordinates[1] + view.getHeight());
                    final int x = (int) ev.getX();
                    final int y = (int) ev.getY();
                    if (rect.contains(x, y)) {
                        return consumed;
                    }
                } else if (viewNew instanceof EditText) {
                    return consumed;
                }
                final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(viewNew.getWindowToken(), 0);
                viewNew.clearFocus();
                return consumed;
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
