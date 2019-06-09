package com.dpcsa.compon.base;

import android.text.Html;
import android.util.Log;

import com.dpcsa.compon.network.CacheWork;
import com.dpcsa.compon.param.AppParams;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.JsonSimple;
import com.dpcsa.compon.json_simple.JsonSyntaxException;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.providers.VolleyInternetProvider;
import com.dpcsa.compon.single.ComponPrefTool;
import com.dpcsa.compon.single.Injector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasePresenter implements BaseInternetProvider.InternetProviderListener {
    private IBase iBase;
    private ParamModel paramModel;
    private Record data;
    Map<String, String> headers;
    private IPresenterListener listener;
    private boolean isCanceled;
    private BaseInternetProvider internetProvider;
    protected JsonSimple jsonSimple = new JsonSimple();
    protected String nameJson, json, url;
    protected int method;
    private ComponGlob componGlob;
    private ComponPrefTool preferences;
    private CacheWork cacheWork;
    private String urlFull;
    private boolean onProgress;
    
    public BasePresenter(IBase iBase, ParamModel paramModel,
                         Map<String, String> headersPar, Record data, IPresenterListener listener) {
        this.iBase = iBase;
        this.paramModel = paramModel;
        this.data = data;
        this.listener = listener;
        this.headers = headersPar;
        if (headers == null) {
            headers = new HashMap<>();
        }
        onProgress = true;
        componGlob = Injector.getComponGlob();
        preferences = Injector.getPreferences();
        cacheWork = Injector.getCacheWork();
        String nameToken = componGlob.appParams.nameTokenInHeader;
        String token = preferences.getSessionToken();
        if (nameToken.length() > 0 && token.length() > 0) {
//            headers.put(nameToken, "bceee76d3c7d761c9ec92c286fb8bebcefb4225c311bb87e");
            headers.put(nameToken, token);
        }
        String nameLanguage = componGlob.appParams.nameLanguageInHeader;
        if (nameLanguage.length() > 0) {
            headers.put(nameLanguage, preferences.getLocale());
        }

        this.method = paramModel.method;
        long duration = paramModel.duration;
        String baseUrl = componGlob.appParams.baseUrl;
        if (componGlob.appParams.nameLanguageInURL) {
            String loc = Injector.getPreferences().getLocale();
            if (this.method == ParamModel.GET) {
                urlFull = baseUrl + loc + paramModel.url;
            } else {
                urlFull = baseUrl + paramModel.url;
            }
        } else {
            urlFull = baseUrl + paramModel.url;
        }
        if (method == ParamModel.GET) {
            String st = componGlob.installParam(paramModel.param, paramModel.typeParam, urlFull);
            url = urlFull + st;
        } else {
            url = urlFull;
        }
        if (duration > 0) {
            nameJson = url;
            json = cacheWork.getJson(nameJson);
            if (json == null) {
                startInternetProvider();
            } else {
                Field ff = null;
                try {
                    ff = jsonSimple.jsonToModel(Html.fromHtml(json).toString());
//                    listener.onResponse(jsonSimple.jsonToModel(Html.fromHtml(json).toString()));
                } catch (JsonSyntaxException e) {
                    iBase.log(e.getMessage());
                    e.printStackTrace();
                }
                if (ff != null) {
                    Field f = ff;
                    if (f.value instanceof Record) {
                        Record rec = (Record) f.value;
                        Object obj = rec.getValue("data");
                        if (obj != null) {
                            ff = (Field) ((Record)obj).get(0);
                        }
                    }
                    listener.onResponse(ff);
                    if (duration == 1) {
                        onProgress = false;
                        startInternetProvider();
                    }
                } else {
                    startInternetProvider();
                }
            }
        } else {
            startInternetProvider();
        }

    }

    public void startInternetProvider() {
        isCanceled = false;
        if (paramModel.internetProvider == null) {
            internetProvider = new VolleyInternetProvider();
            internetProvider.setParam(paramModel.method,
                    url, headers, jsonSimple.ModelToJson(data), this);
        } else {
            BaseInternetProvider bip = null;
            try {
                bip = (BaseInternetProvider) paramModel.internetProvider.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (bip != null) {
                internetProvider = bip.getThis();
                internetProvider.setParam(paramModel.method,
                        url, headers, jsonSimple.ModelToJson(data), this);
            } else {
                iBase.log("Ошибка создания internetProvider");
            }
        }
        iBase.addInternetProvider(internetProvider);
        if (onProgress) {
            iBase.progressStart();
        }
    }

    public void cancel() {
        isCanceled = true;
        if (internetProvider != null) {
            internetProvider.cancel();
        }
    }

    @Override
    public void response(String response) {
        if (onProgress) {
            iBase.progressStop();
        }
        if (response == null) {
            iBase.showDialog("", "no response", null);
        }
        if (paramModel.duration > 0) {
            cacheWork.addCasche(url,
                    paramModel.duration, response);
        }
        if ( ! isCanceled) {
            if (response.length() == 0) {
                listener.onResponse(new Field("", Field.TYPE_STRING, ""));
            } else {
                Field f = null;
                try {
                    f = jsonSimple.jsonToModel(response);
                } catch (JsonSyntaxException e) {
                    iBase.log(e.getMessage());
                    iBase.showDialog(BaseInternetProvider.JSONSYNTAXERROR, e.getMessage(), null);
                    e.printStackTrace();
                }
                if (f != null && f.value != null) {
                    Field ff = f;
                    if (f.value instanceof Record) {
                        Record rec = (Record) f.value;
                        Object obj = rec.getValue("data");
                        if (obj != null) {
                            ff = (Field) ((Record)obj).get(0);
                        }
                    }
                    listener.onResponse(ff);
                } else {
                    iBase.log("Ошибка данных");
                }
            }
        }
    }

    @Override
    public void error(int statusCode, String message) {
        if (onProgress) {
            iBase.progressStop();
        }
        if (paramModel.errorShowView == 0) {
            iBase.showDialog(statusCode, message, null);
        } else {
            listener.onError(statusCode, message, null);
        }
    }

}
