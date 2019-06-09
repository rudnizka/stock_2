package com.dpcsa.compon.single;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.dpcsa.compon.base.BaseInternetProvider;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.Param;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.param.AppParams;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.FieldBroadcaster;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.tools.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponGlob {
    public FieldBroadcaster profile;
    public Context context;
    public Map<String, Screen> MapScreen;
    public AppParams appParams;
    public List<Param> paramValues = new ArrayList<>();
    public String token;
    public Record globalData;
    public JsonSimple jsonSimple = new JsonSimple();
//    public String language;

    public ComponGlob(Context context) {
        this.context = context;
        token = "";
//        language = "uk";
        MapScreen = new HashMap<String, Screen>();
        globalData = new Record();
        profile = new FieldBroadcaster("profile", Field.TYPE_RECORD, null);
    }

    public void setParam(Record fields) {
        int ik = paramValues.size();
        boolean isParam;
        for (Field f: fields) {
            String name = f.name;
            if (f.value != null) {
                isParam = false;
                for (int i = 0; i < ik; i++) {
                    Param param = paramValues.get(i);
                    if (param.name.equals(name)) {
                        isParam = true;
                        setParamValue(param, f);
//                        switch (f.type) {
//                            case Field.TYPE_STRING:
//                                param.value = new String((String) f.value);
//                                break;
//                            case Field.TYPE_INTEGER:
//                                param.value = String.valueOf((Integer) f.value);
//                                break;
//                            case Field.TYPE_LONG:
//                                param.value = String.valueOf(f.value);
//                                break;
//                            case Field.TYPE_FLOAT:
//                                param.value = String.valueOf((Float) f.value);
//                                break;
//                            case Field.TYPE_DOUBLE:
//                                param.value = String.valueOf((Double) f.value);
//                                break;
//                            case Field.TYPE_BOOLEAN:
//                                param.value = String.valueOf((Boolean) f.value);
//                                break;
//                        }
                        break;
                    }
                }
                if ( ! isParam) {
                    Param nParam = new Param(name, "");
                    setParamValue(nParam, f);
                    paramValues.add(nParam);
                }
            }
        }
    }

    private void setParamValue(Param param, Field f) {
        switch (f.type) {
            case Field.TYPE_STRING:
                param.value = new String((String) f.value);
                break;
            case Field.TYPE_INTEGER:
                param.value = String.valueOf((Integer) f.value);
                break;
            case Field.TYPE_LONG:
                param.value = String.valueOf(f.value);
                break;
            case Field.TYPE_FLOAT:
                param.value = String.valueOf((Float) f.value);
                break;
            case Field.TYPE_DOUBLE:
                param.value = String.valueOf((Double) f.value);
                break;
            case Field.TYPE_BOOLEAN:
                param.value = String.valueOf((Boolean) f.value);
                break;
        }
    }

    public void addParam(String paramName) {
        for (Param param : paramValues) {
            if (paramName.equals(param.name)) {
                return;
            }
        }
        paramValues.add(new Param(paramName, ""));
    }

    public void addParamValue(String paramName, String paramValue) {
        for (Param param : paramValues) {
            if (paramName.equals(param.name)) {
                return;
            }
        }
        paramValues.add(new Param(paramName, paramValue));
    }

    public void setParamValue(String paramName, String paramValue) {
        for (Param param : paramValues) {
            if (paramName.equals(param.name)) {
                param.value = paramValue;
                return;
            }
        }
        paramValues.add(new Param(paramName, paramValue));
    }

    public String installParamName(String param, String url) {
        String st = "";
        if (param != null && param.length() > 0) {
            if (url.contains("?")) {
                st = "&";
            } else {
                st = "?";
            }
            String[] paramArray = param.split(Constants.SEPARATOR_LIST);
            String sep = "";
            for (String paramOne : paramArray) {
                for (Param paramV : paramValues) {
                    if (param.equals(paramV.name)) {
                        String valuePar = paramV.value;
                        if (valuePar != null && valuePar.length() > 0) {
                            st = st + sep + paramOne + "=" + paramV.value;
                            sep = "&";
                        }
                        break;
                    }
                }
            }
        }
        if (st.length() == 1) {
            st = "";
        }
        return st;
    }

    public String installParamSlash(String param) {
        String st = "";
        if (param != null && param.length() > 0) {
            String[] paramArray = param.split(Constants.SEPARATOR_LIST);
            for (String par : paramArray) {
                for (Param paramV : paramValues) {
                    if (param.equals(paramV.name)) {
                        if (paramV.value != null && paramV.value.length() > 0) {
                            st = st + "/" + paramV.value;
                        }
                        break;
                    }
                }
            }
        }
        return st;
    }

    public String getParamValue(String nameParam) {
        for (Param paramV : paramValues) {
            if (nameParam.equals(paramV.name)) {
                return paramV.value;
            }
        }
        return "";
    }

    public String installParam(String param, ParamModel.TypeParam typeParam, String url) {
        switch (typeParam) {
            case NAME: return installParamName(param, url);
            case SLASH: return installParamSlash(param);
            default: return "";
        }
    }

    public void delGlobalData(String name) {
        globalData.deleteField(name);
    }

    public View findViewByName(View v, String name) {
        View vS = null;
        ViewGroup vg;
        int id;
        String nameS = "";
        if (v instanceof ViewGroup) {
            vg = (ViewGroup) v;
            int countChild = vg.getChildCount();
            id = v.getId();
            if (id != -1) {
                try {
                    nameS = v.getContext().getResources().getResourceEntryName(id);
                } catch (Resources.NotFoundException e) {
                    nameS = "";
                }
                if (name.equals(nameS)) {
                    return v;
                }
            }
            for (int i = 0; i < countChild; i++) {
                vS = findViewByName(vg.getChildAt(i), name);
                if (vS != null) {
                    return vS;
                }
            }
        } else {
            id = v.getId();
            if (id != -1) {
                nameS = v.getContext().getResources().getResourceEntryName(id);
                if (nameS != null) {
                    if (name.equals(nameS)) return v;
                }
            }
        }
        return vS;
    }

    public Calendar stringToDate(String st) {
        Calendar c;
        String dd = "";
        if (st.indexOf("T") > 0) {
            dd = st.split("T")[0];
        } else {
            dd = st;
        }
        String[] d = dd.split("-");
        c = new GregorianCalendar(Integer.valueOf(d[0]),
                Integer.valueOf(d[1]) - 1,
                Integer.valueOf(d[2]));
        return c;
    }

    public String TextForNumbet(int num, String t1, String t2_4, String t5_9) {
        int n1 = num % 100;
        if (n1 < 21 && n1 > 4) {
            return t5_9;
        }
        n1 = num % 10;
        if (n1 == 1) {
            return t1;
        }
        if (n1 > 1 && n1 < 5) {
            return t2_4;
        }
        return t5_9;
    }

    public Record formErrorRecord(IBase iBase, int statusCode, String message) {
        Record result = new Record();
        if (statusCode < 700) {
            if (message != null && message.length() > 0) {
                    Field f = null;
                    try {
                        f = jsonSimple.jsonToModel(message);
                    } catch (JsonSyntaxException e) {
                        iBase.log(e.getMessage());
                        result.add(new Field(Constants.TITLE, Field.TYPE_STRING, context.getString(appParams.idStringDefaultErrorTitle)));
                        result.add(new Field(Constants.MESSAGE, Field.TYPE_STRING, context.getString(appParams.idStringJSONSYNTAXERROR)));
                        e.printStackTrace();
                    }
                    if (f != null && f.value != null) {
                        Field ff = ((Record) f.value).get(0);
                        result = (Record) ff.value;
                    }
            }
        } else {
            String stMes = "";
//            Log.d("QWERT","showDialog 111111 statusCode="+statusCode+" message="+message);
            String title = "";
            if (appParams.idStringDefaultErrorTitle == 0) {
                title = "StatusCode=" + statusCode;
            } else {
                title = context.getString(appParams.idStringDefaultErrorTitle);
            }
            switch (statusCode) {
                case BaseInternetProvider.ERRORINMESSAGE:
                    stMes = context.getString(appParams.idStringERRORINMESSAGE);
                    break;
                case BaseInternetProvider.NOCONNECTIONERROR:
                    stMes = context.getString(appParams.idStringNOCONNECTIONERROR);
                    if (appParams.idStringNOCONNECTION_TITLE != 0) {
                        title = context.getString(appParams.idStringNOCONNECTION_TITLE);
                    }
                    break;
                case BaseInternetProvider.TIMEOUT:
                    stMes = context.getString(appParams.idStringTIMEOUT);
                    break;
                case BaseInternetProvider.SERVERERROR:
                    stMes = context.getString(appParams.idStringSERVERERROR);
                    break;
                case BaseInternetProvider.JSONSYNTAXERROR:
                    stMes = context.getString(appParams.idStringJSONSYNTAXERROR);
                    break;
            }
            result.add(new Field(Constants.TITLE, Field.TYPE_STRING, title));
            result.add(new Field(Constants.MESSAGE, Field.TYPE_STRING, stMes));
//            showDialog(title, stMes, click);
        }

        return result;
    }

    public Record paramToRecord(String param) {
        Record rec = new Record();
        String[] par = param.split(",");
        if (par.length > 0) {
            for (String nameField : par) {
                String value = getParamValue(nameField);
                if (value.length() > 0) {
                    rec.add(new Field(nameField, Field.TYPE_STRING, value));
                }
            }
        }
        return rec;
    }

}
