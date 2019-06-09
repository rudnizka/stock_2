package com.dpcsa.compon.param;

public abstract class AppParams<T> {
    public int youtubeApiKey = 0;
    public String baseUrl;
    public int paginationPerPage = 20;
    public String paginationNameParamPerPage = "";
    public String paginationNameParamNumberPage = "";
    public int NETWORK_TIMEOUT_LIMIT = 30000; // milliseconds
    public int RETRY_COUNT = 0;
    public int LOG_LEVEL = 3;    // 0 - not, 1 - ERROR, 2 - URL, 3 - URL + jsonResponse
    public static String NAME_LOG_NET = "SMPL_NET";
    public static String NAME_LOG_APP = "SMPL_APP";
    public Class<T>  classProgress;
    public Class<T>  classErrorDialog;
    public String nameTokenInHeader = "";

    public String nameLanguageInHeader = "";
    public String nameLanguageInParam = "";
    public String initialLanguage = "";

    public boolean nameLanguageInURL = false;

    public int errorDialogViewId = 0;
    public int progressViewId = 0;
    public int idStringERRORINMESSAGE = 0;
    public int idStringDefaultErrorTitle = 0;
    public int idStringNOCONNECTION_TITLE = 0;
    public int idStringNOCONNECTIONERROR = 0;
    public int idStringTIMEOUT = 0;
    public int idStringSERVERERROR = 0;
    public int idStringJSONSYNTAXERROR = 0;
    public int defaultMethod = ParamModel.GET;

    public abstract void setParams();
    public AppParams() {
        setParams();
    }
}
