package com.dpcsa.compon.interfaces_classes;

import java.util.ArrayList;
import java.util.List;

public class Navigator {
    public List<ViewHandler> viewHandlers = new ArrayList<>();

    public Navigator(ViewHandler ... handlers) {
        for (ViewHandler vh : handlers) {
            viewHandlers.add(vh);
        }
    }

    public Navigator add(ViewHandler handler) {
        viewHandlers.add(handler);
        return this;
    }

//    public Navigator add(String fieldNameFragment) {
//        viewHandlers.add(new ViewHandler(fieldNameFragment));
//        return this;
//    }
//
//    public Navigator add(int viewId, Screen screen) {
//        viewHandlers.add(new ViewHandler(viewId, screen));
//        return this;
//    }
//
//    public Navigator add(int viewId, Screen screen, ActionsAfterResponse afterResponse) {
//        viewHandlers.add(new ViewHandler(viewId, screen, afterResponse));
//        return this;
//    }
//
//    public Navigator add(int viewId, Screen screen, ViewHandler.TYPE_PARAM_FOR_SCREEN paramForScreen) {
//        viewHandlers.add(new ViewHandler(viewId, screen, paramForScreen));
//        return this;
//    }
//
//    public Navigator add(int viewId, ParamModel paramModel) {
//        viewHandlers.add(new ViewHandler(viewId, paramModel));
//        return this;
//    }
//
//    public Navigator add(ParamModel paramModel) {
//        viewHandlers.add(new ViewHandler(0, paramModel));
//        return this;
//    }
//
//    public Navigator add(int viewId, ViewHandler.TYPE type, ParamModel paramModel) {
//        viewHandlers.add(new ViewHandler(viewId, type, paramModel));
//        return this;
//    }
//
//    public Navigator add(int viewId, ViewHandler.TYPE type, ParamModel paramModel, Screen screen) {
//        viewHandlers.add(new ViewHandler(viewId, type, paramModel, screen));
//        return this;
//    }
//
//    public Navigator add(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
//                         Screen screen, boolean changeEnabled, int... mustValid) {
//        viewHandlers.add(new ViewHandler(viewId, type, paramModel, screen, changeEnabled, mustValid));
//        return this;
//    }
//
//    public Navigator add(int viewId, ViewHandler.TYPE type, ParamModel paramModel,
//                         ActionsAfterResponse afterResponse, boolean changeEnabled, int... mustValid) {
//        viewHandlers.add(new ViewHandler(viewId, type, paramModel, afterResponse, changeEnabled, mustValid));
//        return this;
//    }
//
//    public Navigator add(int viewId, ExecMethod execMethod) {
//        viewHandlers.add(new ViewHandler(viewId, execMethod));
//        return this;
//    }
//
////    public static ActionsAfterResponse actionsAfterResponse() {
////        return new ActionsAfterResponse();
////    }
//
//    public Navigator add(int viewId, String namePreference, boolean value) {
//        viewHandlers.add(new ViewHandler(viewId, namePreference, value));
//        return this;
//    }
//
//    public Navigator add(int viewId, String namePreference, String value) {
//        viewHandlers.add(new ViewHandler(viewId, namePreference, value));
//        return this;
//    }
//
//    public Navigator add(int viewId, ViewHandler.TYPE type) {
//        viewHandlers.add(new ViewHandler(viewId, type));
//        return this;
//    }
//
//    public Navigator add(ViewHandler.TYPE type) {
//        viewHandlers.add(new ViewHandler(type));
//        return this;
//    }
//
//    public Navigator add(int viewId, ViewHandler.TYPE type, String nameFieldWithValue) {
//        viewHandlers.add(new ViewHandler(viewId, type, nameFieldWithValue));
//        return this;
//    }
//
//    public Navigator add(int viewId, SendAndUpdate sendAndUpdate) {
//        viewHandlers.add(new ViewHandler(viewId, sendAndUpdate));
//        return this;
//    }
//
//    public Navigator showView(int viewId, int showViewId) {
//        viewHandlers.add(new ViewHandler(viewId, ViewHandler.TYPE.SHOW, showViewId));
//        return this;
//    }
//
//    public Navigator addAll(ViewHandler[] viewHandlers) {
//        for (ViewHandler vh : viewHandlers) {
//            this.viewHandlers.add(vh);
//        }
//        return this;
//    }
}
