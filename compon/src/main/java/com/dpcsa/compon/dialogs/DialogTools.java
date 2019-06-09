package com.dpcsa.compon.dialogs;

import android.app.Activity;
import android.app.DialogFragment;
import android.view.View;

import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.interfaces_classes.IErrorDialog;
import com.dpcsa.compon.single.Injector;

public class DialogTools {

    public static void  showDialog(Activity activity, String title, String msg,
                                   View.OnClickListener clickPositive) {
        ComponGlob componGlob = Injector.getComponGlob();
        if (componGlob.appParams.classErrorDialog != null) {
            DialogFragment errorDialog = null;
            try {
                errorDialog = (DialogFragment) componGlob.appParams.classErrorDialog.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (errorDialog != null) {
                ((IErrorDialog) errorDialog).setTitle(title);
                ((IErrorDialog) errorDialog).setMessage(msg);
                ((IErrorDialog) errorDialog).setOnClickListener(clickPositive);
                errorDialog.show(activity.getFragmentManager(), "dialog");
            }
        }
    }

//    public static void  showDialog(Activity activity, int statusCode, String msg,
//                                   View.OnClickListener clickPositive) {
//        ComponGlob componGlob = Injector.getComponGlob();
//        if (componGlob.appParams.classErrorDialog != null) {
//            DialogFragment errorDialog = null;
//            try {
//                errorDialog = (DialogFragment) componGlob.appParams.classErrorDialog.newInstance();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            if (errorDialog != null) {
////                String title = "Ошибка statusCode="+statusCode;
////                String description = null;
////                switch (statusCode) {
////                    case ERRORINMESSAGE :
////                        Log.d("QWERT","showDialog msg="+msg);
////                        JsonSimple jsonSimple = new JsonSimple();
////                        Field f = jsonSimple.jsonToModel(msg);
////                        Record record = (Record) f.value;
////                        title = record.getString("title");
////                        description = record.getString("message");
////                        break;
////                    case NOCONNECTIONERROR :
////                        description = "NOCONNECTIONERROR";
////                        break;
////                    case TIMEOUT :
////                        description = "TIMEOUT";
////                        break;
////                    case SERVERERROR :
////                        description = "SERVERERROR";
////                        break;
////                    case AUTHFAILURE :
////                        description = "AUTHFAILURE";
////                        break;
////                }
////                ((IErrorDialog) errorDialog).setTitle(title);
////                ((IErrorDialog) errorDialog).setMessage(description);
//                ((IErrorDialog) errorDialog).setParam(statusCode, null, msg);
//                ((IErrorDialog) errorDialog).setOnClickListener(clickPositive);
//                errorDialog.show(activity.getFragmentManager(), "dialog");
//            }
//        }
//    }
}
