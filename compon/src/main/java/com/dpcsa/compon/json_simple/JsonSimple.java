package com.dpcsa.compon.json_simple;

import android.util.Log;

import java.util.Date;

public class JsonSimple {
    private int ind, indMax;
    private String json;
    private String separators = " ,\n";
    private final String quote = "\"";
    private final String quoteComa = quote + ",";
    private final String a = "\\";
    private final String quoteEcran = a + quote;
    private String currentSymbol;
    private String digits = "1234567890.+-";
    int ii = 0;
    SimpleRecordToJson recordToJson = new SimpleRecordToJson();

    public Field jsonToModel(String st) throws JsonSyntaxException {
        if (st == null) return null;
        Field res = null;
        json = st;
        indMax = st.length();
        ind = -1;
        if (firstSymbol()) {
            res = new Field();
            res.name = "";
            switch (currentSymbol) {
                case "[" :
                    res.value = getList();
                    if (res.value instanceof ListRecords) {
                        res.type = Field.TYPE_LIST_RECORD;
                    } else {
                        res.type = Field.TYPE_LIST_FIELD;
                    }
                    return res;
                case "{" :
                    res.type = Field.TYPE_CLASS;
                    res.value = getClazz();
                    return res;
                case quote :
                    res.type = Field.TYPE_STRING;
                    res.value = getStringValue();
                    return res;
                default:
                    throw new JsonSyntaxException("Does not start with [ or { : " + textForException());
            }
        }
        return res;
    }

    public String ModelToJson(Record model) {
        if (model != null) {
            return recordToJson.recordToJson(model);
//            StringBuilder st = new StringBuilder(512);
//            st.append("{");
//            String sep = "";
//            for (Field f : model) {
//                if (f.value != null) {
//                    if (f.type == Field.TYPE_STRING) {
//                        String stQ = (String) f.value;
//                        String stRes = "";
//                        int j = 0;
//                        int i;
//                        int iL = stQ.length();
//                        do {
//                            i = stQ.indexOf(quote, j);
//                            if (i > 0) {
//                                stRes += stQ.substring(j, i) + a + quote;
//                                j = i + 1;
//                            } else {
//                                stRes += stQ.substring(j, iL);
//                            }
//                        } while (i > 0);
//                        st.append(sep + "\"" + f.name + "\":\"" + stRes + "\"");
//                    } else {
//                        st.append(sep + "\"" + f.name + "\":\"" + (String) f.value + "\"");
//                    }
//                    sep = ",";
//                }
//            }
//            st.append("}");
//            return st.toString();
        } else {
            return null;
        }
    }

    private Object getList() throws JsonSyntaxException {
        if (firstSymbol()) {
            if (currentSymbol.equals("{") || currentSymbol.equals("]")) {
                ListRecords list = new ListRecords();
                while (!currentSymbol.equals("]")) {
                    if (currentSymbol.equals("{")) {
                        list.add(getClazz());
                        if (!firstSymbol()) {
                            throw new JsonSyntaxException("No ] " + textForException());
                        }
                    } else {
                        throw new JsonSyntaxException("No { " + textForException());
                    }
                }
                return list;
            } else {
                ListFields listF = new ListFields();
                while (!currentSymbol.equals("]")) {
                    listF.add(getField());
                    if (!firstSymbol()) {
                        throw new JsonSyntaxException("No ] " + textForException());
                    }
                }
                return listF;
            }
        }
        return new ListRecords();
    }

    private String textForException() {
        int in = ind - 200;
        if (in < 0) {
            in = 0;
        }
        int ik = ind + 60;
        if (ik > indMax) {
            ik = indMax;
        }
        return "near position: " + ind + " text >>" + json.substring(in, ik) + "<<";
    }

    private Field getField() throws JsonSyntaxException {
        Field item = new Field();
        item.name = "";
        switch (currentSymbol) {
            case quote : // String
                Field fs = getStringValue();
                item.type = fs.type;
                item.value = fs.value;
                break;
            case "n" :   // null
                item.type = Field.TYPE_NULL;
                item.value = getNullValue();
                break;
            case "f" :   // boolean
            case "t" :
                item.type = Field.TYPE_BOOLEAN;
                item.value = getBooleanValue();
                break;
            default:
                if (digits.contains(currentSymbol)) {    // digit
//                                item.type = Field.TYPE_INTEGER;
                    Field f = getDigitalValue(item.name);
                    item.value = f.value;
                    item.type = f.type;
                } else {
                }
        }
        return item;
    }

    private Record getClazz() throws JsonSyntaxException {
        Record list = new Record();
        if (firstSymbol()) {
            while ( ! currentSymbol.equals("}")) {
                 if (currentSymbol.equals(quote)) {
                     Field item = getValue();
                     if (item == null) {
                         return list;
                     }
                     list.add(item);
                     if ( ! firstSymbol()) {
                         throw new JsonSyntaxException("No } " + textForException());
                     }
                } else {
                    if (ind < indMax) {
                        firstSymbol();
                    } else {
                        throw new JsonSyntaxException("No } " + textForException());
                    }
                }
            }
        }
        return list;
    }

    private Field getValue() throws JsonSyntaxException {
        Field item = new Field();
        item.name = getName(quote);
        if (item.name != null && firstSymbol()) {
            if (currentSymbol.equals(":")) {
                if (firstSymbol()) {
                    switch (currentSymbol) {
                        case quote : // String
                            Field fs = getStringValue();
                            item.type = fs.type;
                            item.value = fs.value;
                            break;
                        case "n" :   // null
                            item.type = Field.TYPE_NULL;
                            item.value = getNullValue();
                            break;
                        case "f" :   // boolean
                        case "t" :
                            item.type = Field.TYPE_BOOLEAN;
                            item.value = getBooleanValue();
                            break;
                        case "[" :   // List
                            item.value = getList();
                            if (item.value instanceof ListRecords) {
                                item.type = Field.TYPE_LIST_RECORD;
                            } else {
                                item.type = Field.TYPE_LIST_FIELD;
                            }
                            break;
                        case "{" :   // Class
                            item.type = Field.TYPE_CLASS;
                            item.value = getClazz();
                            break;
                        default:
                            if (digits.contains(currentSymbol)) {    // digit
                                Field f = getDigitalValue(item.name);
                                item.value = f.value;
                                item.type = f.type;
//                                item.value = getIntegerValue();
                            } else {
                            }
                    }
                } else {
                    throw new JsonSyntaxException("No value " + textForException());
                }
            } else {
                throw new JsonSyntaxException("No : " + textForException());
            }
        } else {
            throw new JsonSyntaxException("No : " + textForException());
        }
        return item;
    }

    private Object getNullValue() throws JsonSyntaxException {
        String st = json.substring(ind, ind + 4);
        if (st.toUpperCase().equals("NULL")) {
            ind+=3;
        } else {
            throw new JsonSyntaxException("No NULL " + textForException());
        }
        return null;
    }

    private Boolean getBooleanValue() {
        String st;
        switch (currentSymbol) {
            case "f" :
                st = json.substring(ind, ind + 5);
                if (st.toUpperCase().equals("FALSE")) {
                    ind+=4;
                    return new Boolean(false);
                } else {
                    return null;
                }
            case "t" :
                st = json.substring(ind, ind + 4);
                if (st.toUpperCase().equals("TRUE")) {
                    ind+=3;
                    return new Boolean(true);
                } else {
                    return null;
                }
            default:
            return null;
        }
    }

    private Field getStringValue() throws JsonSyntaxException {
        int i = ind, j;
        do {
            j = i + 1;
            i = json.indexOf(quote, j);
            if (i < 0) {
                throw new JsonSyntaxException("Not " + quote +" " + textForException());
            }
        } while (json.substring(i - 1, i).equals("\\"));
        String st = json.substring(ind + 1, i);
        st = delSlash(st);
        ind = i;
        Field field = new Field();
        field.name = "";
        if (st.startsWith("/D")) {
            String t = st.substring(6, 18);
            Date d = new Date(Long.valueOf(t));
            field.type = Field.TYPE_DATE;
            field.value = d;
        } else {
            field.type = Field.TYPE_STRING;
            field.value = st;
        }
        return field;
    }

    private String delSlash(String st) {
        char[] c = st.toCharArray();
        StringBuilder builder = new StringBuilder();
        int i1;
        int ik = c.length;
//        boolean isYetSlash = false;
        for (int i = 0; i < ik; i++) {
            if (c[i] == '\\') {
                i1 = i + 1;
                if (i1 < ik ) {
                    char c1 = c[i1];
                    if (c1 == 'u') {
                        int iu = i + 5;
                        if (iu < ik) {
                            char cu = (char) Integer.parseInt(new String(new char[]{c[i + 2], c[i + 3], c[i + 4], c[i + 5]}), 16);
                            builder.append(cu);
                            i = iu;
                        }
                    } else if (c1 == 'r' || c1 == 'n' || c1 == 't') {
                        i++;
                    }
                }
            } else {
                builder.append(c[i]);
            }
        }
        String result = builder.toString();
        return result;
    }

    private Integer getIntegerValue() {
        int j = -1;
        int l = json.length();
        for (int i = ind; i < l; i++) {
            if ( ! digits.contains(json.substring(i, i + 1))) {
                j = i;
                break;
            }
        }
        if (j == -1) {
            return null;
        } else {
            String st = json.substring(ind, j);
            ind = j - 1;
            return Integer.valueOf(st);
        }
    }

    private Field getDigitalValue(String name) {
        Field f = new Field();
        f.name = name;
        int j = -1;
        int l = json.length();
        for (int i = ind; i < l; i++) {
            if ( ! digits.contains(json.substring(i, i + 1))) {
                j = i;
                break;
            }
        }
        if (j == -1) {
            return null;
        } else {
            String st = json.substring(ind, j);
            ind = j - 1;
            if (st.contains(".")) {
                Double d = Double.valueOf(st);
                f.type = Field.TYPE_DOUBLE;
                f.value = d;
                return f;
            } else {
                f.type = Field.TYPE_LONG;
                f.value = Long.valueOf(st);
                return f;
            }
        }
    }

    private int indexOf(String separ, int begin) {
        int l = json.length();
        for (int i = begin; i < l; i++) {
            if (separ.contains(json.substring(i, i + 1))) {
                return i;
            }
        }
        return -1;
    }

    private String getName(String separ) throws JsonSyntaxException {
        String st = "";
        int i = json.indexOf(quote, ind + 1);
        if (i > -1) {
            st = json.substring(ind + 1, i);
            ind = i;
        } else {
            throw new JsonSyntaxException("Not name " + textForException());
        }
        return st;
    }

    private boolean firstSymbol() {
        ind++;
        currentSymbol = json.substring(ind, ind + 1);
        while (separators.contains(currentSymbol)) {
            ind++;
            if (ind < indMax) {
                currentSymbol = json.substring(ind, ind + 1);
            } else {
                break;
            }
        }
        return ind < indMax;
    }
}
