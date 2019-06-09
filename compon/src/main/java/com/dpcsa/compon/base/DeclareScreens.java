package com.dpcsa.compon.base;

import android.util.Log;

import com.dpcsa.compon.interfaces_classes.DataFieldGet;
import com.dpcsa.compon.interfaces_classes.ExecMethod;
import com.dpcsa.compon.interfaces_classes.FilterParam;
import com.dpcsa.compon.interfaces_classes.ItemSetValue;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.SendAndUpdate;
import com.dpcsa.compon.param.ParamView;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.interfaces_classes.ActionsAfterResponse;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.interfaces_classes.Visibility;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.single.Injector;
import com.dpcsa.compon.tools.Constants;

import java.util.Map;

public abstract class DeclareScreens<T>{
    protected ParamComponent.TC TC;
    protected Constants.AnimateScreen AS;
    protected ViewHandler.TYPE VH;
    protected ItemSetValue.TYPE_SOURCE TS;
    protected FilterParam.Operation FO;
    protected ViewHandler.TYPE_PARAM_FOR_SCREEN PS;
    public abstract void declare();
    protected int GET = ParamModel.GET, POST = ParamModel.POST, JSON = ParamModel.JSON,
            GET_DB = ParamModel.GET_DB, POST_DB = ParamModel.POST_DB, UPDATE_DB = ParamModel.UPDATE_DB,
            INSERT_DB = ParamModel.INSERT_DB, DEL_DB = ParamModel.DEL_DB, PARENT = ParamModel.PARENT,
            FIELD = ParamModel.FIELD, ARGUMENTS = ParamModel.ARGUMENTS, COUNTRY_CODE = ParamModel.COUNTRY_CODE,
            STRINGARRAY = ParamModel.STRINGARRAY, DATAFIELD = ParamModel.DATAFIELD, GLOBAL = ParamModel.GLOBAL;

    private Map<String, Screen> MapScreen;
    protected ComponGlob componGlob;

    public DeclareScreens() {
        componGlob = Injector.getComponGlob();
        MapScreen = componGlob.MapScreen;
    }

    protected String getString(int stringId) {
        return componGlob.context.getResources().getString(stringId);
    }

    public void initScreen() {
        declare();
        for (Screen value : MapScreen.values()) {
            String par = value.getParamModel();
            if (par != null && par.length() > 0) {
                String[] param = par.split(Constants.SEPARATOR_LIST);
                int ik = param.length;
                for (int i = 0; i < ik; i++) {
                    componGlob.addParam(param[i]);
                }
            }
        }
    }

    public Field getProfile() {
        return Injector.getComponGlob().profile;
    }

    protected Screen fragment(String name, int layoutId, String title, String... args) {
        Screen mc = new Screen(name, layoutId, title, args);
        mc.typeView = Screen.TYPE_VIEW.FRAGMENT;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen fragment(String name, int layoutId, Class<T> additionalWork) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.FRAGMENT;
        mc.additionalWork = additionalWork;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen fragment(String name, int layoutId) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.FRAGMENT;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen fragment(String name, Class customFragment) {
        Screen mc = new Screen(name, customFragment);
        mc.typeView = Screen.TYPE_VIEW.CUSTOM_FRAGMENT;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen activity(String name, Class customActivity) {
        Screen mc = new Screen(name, customActivity);
        mc.typeView = Screen.TYPE_VIEW.CUSTOM_ACTIVITY;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen activity(String name, int layoutId, String title, String... args) {
        Screen mc = new Screen(name, layoutId, title, args);
        mc.typeView = Screen.TYPE_VIEW.ACTIVITY;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen activity(String name, int layoutId) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.ACTIVITY;
        MapScreen.put(name, mc);
        return mc;
    }

    protected Screen activity(String name, int layoutId, Class<T> additionalWork) {
        Screen mc = new Screen(name, layoutId);
        mc.typeView = Screen.TYPE_VIEW.ACTIVITY;
        mc.additionalWork = additionalWork;
        MapScreen.put(name, mc);
        return mc;
    }

    public ActionsAfterResponse after(ViewHandler ... handlers) {
        return new ActionsAfterResponse(handlers);
    }

//    public static ActionsAfterResponse actionsAfterResponse() {
//        return new ActionsAfterResponse();
//    }

    public static Visibility[] showManager(Visibility ... args) {
        return args;
    }

    public static Visibility visibility(int viewId, String nameField) {
        return new Visibility(0, viewId, nameField);
    }

    public static Visibility enabled(int viewId, String nameField) {
        return new Visibility(1, viewId, nameField);
    }

    public ParamModel model() {
        return new ParamModel();
    }

    public ParamModel model(int method) {
        return new ParamModel(method);
    }

    public ParamModel model(String url) {
        return new ParamModel(url);
    }

    public ParamModel model(int method, String urlOrNameParent) {
        return new ParamModel(method, urlOrNameParent);
    }

    public ParamModel model(int method, String[] urlArray, String param) {
        return new ParamModel(method, urlArray, param);
    }

    public ParamModel model(Field field) {
        return new ParamModel(field);
    }

    public ParamModel model(DataFieldGet dataFieldGet) {
        return new ParamModel(dataFieldGet);
    }

    public ParamModel model(String url, String param) {
        return new ParamModel(url, param);
    }

    public ParamModel model(int method, String table, String where, String param) {
        return new ParamModel(method, table, where, param);
    }
    public ParamModel model(int method, String table, String set, String where, String param) {
        return new ParamModel(method, table, set, where, param);
    }

    public ParamModel model(String url, String param, long duration) {
        return new ParamModel(url, param, duration);
    }

    public ParamModel model(String url, long duration) {
        return new ParamModel(url, "", duration);
    }

    public ParamModel model(int method, String url, String param, long duration) {
        return new ParamModel(method, url, param, duration);
    }

    public ParamModel model(int method, String urlOrNameParent, String paramOrField) {
        return new ParamModel(method, urlOrNameParent, paramOrField);
    }

    public ParamView view(int viewId) {
        return new ParamView(viewId);
    }

    public ParamView view(int viewId, int layoutItemId) {
        return new ParamView(viewId, layoutItemId);
    }

    public ParamView view(int viewId, int layoutItemId, int layoutFurtherId) {
        return new ParamView(viewId, layoutItemId, layoutFurtherId);
    }

    public ParamView view(int viewId, int[] layoutTypeId) {
        return new ParamView(viewId, layoutTypeId);
    }

    public ParamView view(int viewId, String fieldType, int[] layoutTypeId) {
        return new ParamView(viewId, fieldType, layoutTypeId);
    }

    public ParamView view(int viewId, String fieldType, int style) {
        return new ParamView(viewId, fieldType, style);
    }

    public ParamView view(int viewId, String [] screens) {
        return new ParamView(viewId, screens);
    }

    public ParamView view(int viewId, String[] screens, int[] containerId) {
        return new ParamView(viewId, screens, containerId);
    }

    public ParamView view(int viewId, String fieldType, int[] layoutTypeId, int[] layoutFurtherTypeId) {
        return new ParamView(viewId, fieldType, layoutTypeId, layoutFurtherTypeId);
    }

    public int[] images(int ... args) {
        return args;
    }

    public Navigator navigator() {
        return new Navigator();
    }

    public Navigator navigator(ViewHandler ... handlers) {
        return new Navigator(handlers);
    }

    public ViewHandler handler(String fieldNameFragment) {
        return new ViewHandler(fieldNameFragment);
    }

    public ViewHandler start(int viewId, String screen) {
        return new ViewHandler(viewId, screen);
    }

    public ViewHandler start(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen) {
        return new ViewHandler(viewId, screen, paramForScreen);
    }

    public ViewHandler start(String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen) {
        return new ViewHandler(0, screen, paramForScreen);
    }

    public ViewHandler start(String screen) {
        return new ViewHandler(0, screen);
    }

    public ViewHandler start(int viewId, String screen, boolean blocked) {
        ViewHandler vh = new ViewHandler(viewId, screen);
        vh.blocked = blocked;
        return vh;
    }

    public ViewHandler addScreen(int viewId, String screen) {
        ViewHandler vh = new ViewHandler(viewId, screen);
        vh.addFragment = true;
        return vh;
    }

    public ViewHandler setMenu(int viewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SET_MENU);
    }

    public ViewHandler setMenu(int viewId, String screen) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SET_MENU, screen);
    }

    public ViewHandler setMenu(int viewId, int position) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SET_MENU, position);
    }

    public ViewHandler handler(int viewId, String screen, ActionsAfterResponse afterResponse) {
        return new ViewHandler(viewId, screen, afterResponse);
    }

    public ViewHandler handler(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen) {
        return new ViewHandler(viewId, screen, paramForScreen);
    }

    public ViewHandler handler(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen, ActionsAfterResponse afterResponse) {
        return new ViewHandler(viewId, screen, paramForScreen, afterResponse);
    }

    public ViewHandler handler(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen,
                               String param, ActionsAfterResponse afterResponse) {
        return new ViewHandler(viewId, screen, paramForScreen, param, afterResponse);
    }

    public ViewHandler handler(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen,
                               String param) {
        return new ViewHandler(viewId, screen, paramForScreen, param);
    }

    public ViewHandler handler(int viewId, String screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen, int componId) {
        return new ViewHandler(viewId, screen, paramForScreen, componId);
    }

    public ViewHandler handler(int viewId, ParamModel paramModel) {
        return new ViewHandler(viewId, paramModel);
    }

    public ViewHandler handler(ParamModel paramModel) {
        return new ViewHandler(0, paramModel);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel) {
        return new ViewHandler(viewId, type, paramModel);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel, String screen) {
        return new ViewHandler(viewId, type, paramModel, screen);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
                               String screen, boolean changeEnabled, int... mustValid) {
        return new ViewHandler(viewId, type, paramModel, screen, changeEnabled, mustValid);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
                               ActionsAfterResponse afterResponse) {
        return new ViewHandler(viewId, type, paramModel, afterResponse, false, null);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
                         ActionsAfterResponse afterResponse, boolean changeEnabled, int... mustValid) {
        return new ViewHandler(viewId, type, paramModel, afterResponse, changeEnabled, mustValid);
    }

    public ViewHandler handler(int viewId, ExecMethod execMethod) {
        return new ViewHandler(viewId, execMethod);
    }

    public ViewHandler handler(int viewId, String namePreference, boolean value) {
        return new ViewHandler(viewId, namePreference, value);
    }

    public ViewHandler handler(int viewId, String namePreference, String value) {
        return new ViewHandler(viewId, namePreference, value);
    }

    public ViewHandler back(int viewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.BACK);
    }

    public ViewHandler keyBack(int viewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.KEY_BACK);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type) {
        return new ViewHandler(viewId, type);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, String nameParam, String valueParam) {
        return new ViewHandler(viewId, type, nameParam, valueParam);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, int idCompon) {
        return new ViewHandler(viewId, type, idCompon, "");
    }

    public ViewHandler handler(ViewHandler.TYPE type) {
        return new ViewHandler(type);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, String nameFieldWithValue) {
        return new ViewHandler(viewId, type, nameFieldWithValue);
    }

    public ViewHandler assignValue(int viewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.ASSIGN_VALUE);
    }

//    public ViewHandler startScreen(String screen) {
//        return new ViewHandler(0, screen);
////        viewHandlers.add(new ViewHandler(0, screen));
////        return this;
//    }

    public ViewHandler showComponent(int viewId) {
        return new ViewHandler(0, ViewHandler.TYPE.SHOW, viewId);
    }

    public ViewHandler handler(int viewId, SendAndUpdate sendAndUpdate) {
        return new ViewHandler(viewId, sendAndUpdate);
    }

    public ViewHandler handler(int viewId, int showViewId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SHOW, showViewId);
    }

    public ViewHandler show(int viewId, int showViewId, boolean onActivity) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SHOW, showViewId, onActivity);
    }

    public ViewHandler showHide(int viewId, int showViewId, int textShowId, int textHideId) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SHOW_HIDE, showViewId, textShowId, textHideId);
    }

    public ViewHandler handler(int viewId, int idTextV, int idString) {
        return new ViewHandler(viewId, ViewHandler.TYPE.SET_VALUE, idTextV, idString);
    }

    public ViewHandler handler(int viewId, ViewHandler.TYPE type, int idCompon, String name) {
        return new ViewHandler(viewId, type, idCompon, name);
    }

    public ItemSetValue item(int viewId, ItemSetValue.TYPE_SOURCE source) {
        return new ItemSetValue(viewId, source);
    }

    public ItemSetValue item(int viewId, ItemSetValue.TYPE_SOURCE source, String name) {
        return new ItemSetValue(viewId, source, name);
    }

    public ItemSetValue item(int viewId, ItemSetValue.TYPE_SOURCE source, int componId) {
        return new ItemSetValue(viewId, source, componId);
    }

    public FilterParam filter(String nameField, FilterParam.Operation oper, Object value) {
        return new FilterParam(nameField, oper, value);
    }

}
