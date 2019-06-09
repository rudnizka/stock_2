package com.dpcsa.compon.param;

import com.dpcsa.compon.interfaces_classes.FilterParam;
import com.dpcsa.compon.single.ComponGlob;
import com.dpcsa.compon.interfaces_classes.DataFieldGet;
import com.dpcsa.compon.interfaces_classes.Filters;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.single.Injector;

import java.util.ArrayList;
import java.util.List;

public class ParamModel <T> {
    public int method;
    public String url;
    public String param;
    public long duration;
    public static String PARENT_MODEL = "PARENT_MODEL";
    public static int GET = 0;
    public static int POST = 1;
    public static final int GET_DB = 10;
    public static int POST_DB = 11;
    public static int INSERT_DB = 12;
    public static int DEL_DB = 13;
    public static int UPDATE_DB = 14;
    public static final int PARENT = 100;
    public static final int FIELD = 101;
    public static final int ARGUMENTS = 102;
    public static final int STRINGARRAY = 103;
    public static final int DATAFIELD = 104;
    public static final int GLOBAL = 105;
    public static final int COUNTRY_CODE = 106;
    public static final int JSON = 107;
    public static int defaultMethod = GET;
    public String nameField, nameFieldTo;
    public DataFieldGet dataFieldGet;
    public String nameTakeField;
    public List<Record> addRecordBegining;
    public Field field;
    public Class<T>  internetProvider;
    public enum TypeParam {MAP, NAME, SLASH};
    public TypeParam typeParam = TypeParam.NAME;
    public boolean isPagination;
    public int paginationPerPage;
    public String rowName, nameAddField;
    public int typeAddField, valueIntAddField;;
    public int stringArray;
    public String paginationNameParamPerPage;
    public String paginationNameParamNumberPage;
    public String updateTable, updateUrl, updateAlias, updateSet;
    public String[] urlArray;
    public int urlArrayIndex = -1;
    private ComponGlob componGlob;
    public String sortParam;
    public int errorShowView;
    public Filters filters;

    public static void setDefaultMethod(int method) {
        defaultMethod = method;
    }

    public ParamModel() {
        this(PARENT, PARENT_MODEL, "", -1);
    }

    public ParamModel(int method) {
        this.method = method;
        param = "";
    }
    public ParamModel(String url) {
        this(url, "", -1);
    }

    public ParamModel(int method, String urlOrNameParent) {
        this(method, urlOrNameParent, "", -1);
    }

    public ParamModel(int method, String[] urlArray, String param) {
        this.method = method;
        this.param = param;
        this.urlArray = urlArray;
        this.duration = -1;
        nameField = null;
        nameTakeField = null;
        internetProvider = null;
    }

    public ParamModel(Field field) {
        this(FIELD, "", "", -1);
        this.field = field;
    }

    public ParamModel(DataFieldGet dataFieldGet) {
        this(DATAFIELD, "", "", -1);
        this.dataFieldGet = dataFieldGet;
    }

    public ParamModel(String url, String param) {
        this(url, param, -1);
    }

    public ParamModel(int method, String urlOrNameParent, String paramOrField) {
        this(method, urlOrNameParent, paramOrField, -1);
    }

    public ParamModel(int method, String table, String where, String param) {
        this.method = method;
        updateTable = table;
        updateUrl = where;
        this.param = param;
    }

    public ParamModel(int method, String table, String set, String where, String param) {
        this.method = method;
        updateTable = table;
        updateUrl = where;
        updateSet = set;
        this.param = param;
    }

    public ParamModel(String url, String param, long duration) {
        this(defaultMethod, url, param, duration);
    }

    public ParamModel(int method, String url, String param, long duration) {
        this.method = method;
        if (method == PARENT) {
                if ((url == null || url.length() == 0)) {
                    this.url = PARENT_MODEL;
                }
        } else {
            this.url = url;
        }
        this.param = param;
        this.duration = duration;
        nameField = null;
        urlArray = null;
        nameTakeField = null;
        internetProvider = null;
    }

    public ParamModel updateDB(String table, String url, long duration) {
        return updateDB(table, url, duration, null);
    }

    public ParamModel updateDB(String table, String url, long duration, String nameAlias) {
        updateTable = table;
        updateUrl = url;
        this.duration = duration;
        updateAlias = nameAlias;
        return this;
    }

    public ParamModel row(String nameRowField) {
        rowName = nameRowField;
        return this;
    }

    public ParamModel addField(String nameField, int type, int value) {
        nameAddField = nameField;
        typeAddField = type;
        valueIntAddField = value;
        return this;
    }

    public ParamModel internetProvider(Class<T> internetProvider) {
        this.internetProvider = internetProvider;
        return this;
    }

    public ParamModel changeNameField(String nameField, String nameFieldTo) {
        this.nameField = nameField;
        this.nameFieldTo = nameFieldTo;
        return this;
    }

    public ParamModel addToBeginning(Record record) {
        if (addRecordBegining == null) {
            addRecordBegining = new ArrayList<>();
        }
        addRecordBegining.add(record);
        return this;
    }

    public ParamModel addToBeginning(String name, int value) {
        Record record = new Record();
        Field field = new Field(name, Field.TYPE_INTEGER, value);
        record.add(field);
        return addToBeginning(record);
    }

    // Реализация ParamModel обеспечивает получение данных из заданных источников в форме Field.
    // Если данные являются Record и задано nameTakeField, то результатом реализации ParamModel
    // будет Field с именем nameTakeField из этого Record
    public ParamModel takeField(String name) {
        nameTakeField = name;
        return this;
    }

    public ParamModel typeParam(TypeParam typeParam) {
        this.typeParam = typeParam;
        return this;
    }

    public ParamModel pagination() {
        isPagination = true;
        componGlob = Injector.getComponGlob();
        paginationPerPage = componGlob.appParams.paginationPerPage;
        paginationNameParamPerPage = componGlob.appParams.paginationNameParamPerPage;
        paginationNameParamNumberPage = componGlob.appParams.paginationNameParamNumberPage;
        return this;
    }

    public ParamModel pagination(int paginationPerPage, String paginationNameParamPerPage,
                                 String paginationNameParamNumberPage) {
        isPagination = true;
        this.paginationPerPage = paginationPerPage;
        this.paginationNameParamPerPage = paginationNameParamPerPage;
        this.paginationNameParamNumberPage = paginationNameParamNumberPage;
        return this;
    }

    public ParamModel filters(int maxSize, FilterParam... item) {
        this.filters = new Filters(maxSize, item);
        return this;
    }

    public ParamModel errorShowView(int viewId) {
        errorShowView = viewId;
        return this;
    }

    public ParamModel sort(String sortParam) {
        this.sortParam = sortParam;
        return this;
    }

}
