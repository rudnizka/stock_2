package com.dpcsa.compon.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.dpcsa.compon.base.BaseDB;
import com.dpcsa.compon.interfaces_classes.DescriptTableDB;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IDbListener;
import com.dpcsa.compon.interfaces_classes.IPresenterListener;
import com.dpcsa.compon.interfaces_classes.ParamDB;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.ListRecords;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamModel;

public class DatabaseManager extends BaseDB {

    private Context context;
    public ParamDB paramDB;
    public DBHelper dbHelper;
    private int mOpenCounter;
    public SQLiteDatabase mDatabase;

    public DatabaseManager(Context context, ParamDB paramDB) {
        this.context = context;
        this.paramDB = paramDB;
        dbHelper = new DBHelper(context);
    }

    @Override
    public void remoteToLocale(IBase iBase, String url, String table, String nameAlias) {
        new RemoteToLocale(iBase, url, table, nameAlias, dbListener);
    }

    IDbListener dbListener = new IDbListener() {
        @Override
        public void onResponse(IBase iBase, ListRecords listRecords, String table, String nameAlias) {
            insertListRecord(iBase, table, listRecords, nameAlias);
        }
    };

//    @Override
//    public void insertListRecord(IBase iBase, String table, ListRecords listRecords) {
//        insertListRecord(iBase, table, listRecords, null);
//    }

    @Override
    public void insertListRecord(IBase iBase, String table, ListRecords listRecords, String nameAlias) {
//        Map<String, String> mapField = new HashMap<>();
        iBase.log("DatabaseManager insertListRecord TABLE="+table+" SIZE="+listRecords.size());
//        Log.d("QWERT","DatabaseManager insertListRecord TABLE="+table+"<<< SIZE="+listRecords.size());
        String[] columnNames = null;
        String[] aliasNames = null;
        openDatabase();
        mDatabase.beginTransaction();
        int[] columnType;
        Cursor cc = mDatabase.rawQuery("PRAGMA table_info(" + table + ");", null);
        int count = cc.getCount();
        columnNames = new String[count];
        columnType = new int[count];
        int ind = 0;
        if (cc.moveToFirst()) {
            do {
                columnNames[ind] = cc.getString(1);
                int type = 0;
                switch (cc.getString(2).toLowerCase()) {
                    case "integer" :
                        type = Cursor.FIELD_TYPE_INTEGER;
                        break;
                    case "text" :
                        type = Cursor.FIELD_TYPE_STRING;
                        break;
                    case "real" :
                        type = Cursor.FIELD_TYPE_FLOAT;
                        break;
                    case "blob" :
                        type = Cursor.FIELD_TYPE_BLOB;
                        break;
                }
                columnType[ind] = type;
                ind++;
            } while (cc.moveToNext());
        }
        cc.close();

        if (columnNames != null) {
            int jk = columnNames.length;
            aliasNames = new String[jk];
            for (int i = 0; i < jk; i++) {
                aliasNames[i] = columnNames[i];
            }
            if (nameAlias != null && nameAlias.length() > 0) {
                String[] na = nameAlias.split(";");
                for (int i = 0; i < na.length; i++) {
                    String[] na1 = na[i].split(",");
                    String name = na1[0].trim();
                    for (int j = 0; j < jk; j++) {
                        if (aliasNames[j].equals(name)) {
                            aliasNames[j] = na1[1].trim();
                            break;
                        }
                    }
                }
            }
            int ii = 0;
            mDatabase.delete(table, null, null);
//            Log.d("QWERT","insertListRecord table="+table+" size="+listRecords.size());
            for (Record record : listRecords) {
                ContentValues cv = new ContentValues();
//                String stt = "";
                for (int j = 0; j < jk; j++) {
                    Field f = record.getField(aliasNames[j]);
                    if (f != null) {
//                        stt += columnNames[j] + "=";
                        switch (columnType[j]) {
                            case Cursor.FIELD_TYPE_INTEGER :

                                cv.put(columnNames[j], record.getLongField(f));
//                                stt+= record.getLongField(f) + ";";
                                break;
                            case Cursor.FIELD_TYPE_FLOAT :
                                cv.put(columnNames[j], record.getFloatField(f));
//                                stt+= record.getFloatField(f) + ";";
                                break;
                            case Cursor.FIELD_TYPE_STRING :
                                cv.put(columnNames[j], (String) f.value);
//                                stt+= (String) f.value + ";";
                                break;
                        }
                    }
                }
//                Log.d("QWERT","insertListRecord record="+record.toString());
                long rowID = mDatabase.replace(table, null, cv);
            }
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
        closeDatabase();
    }

    @Override
    public void deleteRecord(IBase iBase, ParamModel paramModel, String[] param) {
        openDatabase();
        if (paramModel.method == ParamModel.DEL_DB) {
            int i = mDatabase.delete(paramModel.updateTable, paramModel.updateUrl, param);
        }
        closeDatabase();
    }

    @Override
    public void updateRecord(IBase iBase, ParamModel paramModel, ContentValues cv, String[] param) {
        openDatabase();
        if (paramModel.method == ParamModel.UPDATE_DB) {
            int i = mDatabase.update(paramModel.updateTable, cv, paramModel.updateUrl, param);
        }
        closeDatabase();
    }

    @Override
    public void insertRecord(String sql, Record record) {
        openDatabase();
        ContentValues cv = new ContentValues();
        for (Field f : record) {
            cv.put(f.name, (String) f.value);
        }
        long rowID = mDatabase.insert(sql, null, cv);
        closeDatabase();
    }

    @Override
    public void insertCV(String nameTable, ContentValues cv) {
        openDatabase();
        long rowID = mDatabase.insert(nameTable, null, cv);
        closeDatabase();
    }

    @Override
    public void get(IBase iBase, ParamModel paramModel, String[] param, IPresenterListener listener) {
//        Log.d("QWERT","DatabaseManager SQL="+paramModel.url);
        new GetDbPresenter(iBase, paramModel, param, listener);
    }

    @Override
    public Field get(IBase iBase, ParamModel paramModel, String sql, String[] param) {
        if (param != null) {
            String st = "";
            for (String sti : param) {
                st += sti + ",";
            }
            iBase.log("DatabaseManager GET SQL=" + sql + "<< param=" + st);
//            Log.d("QWERT", "DatabaseManager GET SQL=" + sql + "<< param=" + st);
        } else {
            iBase.log("DatabaseManager GET SQL=" + sql + "<<");
//            Log.d("QWERT", "DatabaseManager GET SQL=" + sql + "<<");
        }
        openDatabase();
        Cursor c = null;
        try {
            c = mDatabase.rawQuery(sql, param);
        } catch (SQLiteException e) {
            iBase.log(e.getMessage());
        }
        ListRecords listRecords = new ListRecords();
        if (c != null) {
            if (c.moveToFirst()) {
                int countCol = c.getColumnCount();
                String[] nameColumn = c.getColumnNames();
//                listRecords = new ListRecords();
                Record record;
                int row = 0;
                do {
                    record = new Record();
                    for (int i = 0; i < countCol; i++) {
                        switch (c.getType(i)) {
                            case Cursor.FIELD_TYPE_INTEGER:
                                record.add(new Field(nameColumn[i], Field.TYPE_LONG, new Long(c.getLong(i))));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                record.add(new Field(nameColumn[i], Field.TYPE_DOUBLE, c.getDouble(i)));
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                record.add(new Field(nameColumn[i], Field.TYPE_STRING, c.getString(i)));
                                break;
                            default:
                                record.add(new Field(nameColumn[i], Field.TYPE_STRING, c.getString(i)));
                                break;
                        }
                    }
                    if (paramModel.rowName != null && paramModel.rowName.length() > 0) {
                        row++;
                        record.add(new Field(paramModel.rowName, Field.TYPE_LONG, row));
                    }
//                    iBase.log("DatabaseManager RECORD=" + record.toString());
                    listRecords.add(record);
                } while (c.moveToNext());
            } else {
                iBase.log("DatabaseManager get 0 rows");
//                Log.d("QWERT", "DatabaseManager get 0 rows");
            }
            c.close();
        }
        closeDatabase();
        return new Field("", Field.TYPE_LIST_RECORD, listRecords);
    }

    public synchronized SQLiteDatabase openDatabase() {
        mOpenCounter++;
        if(mOpenCounter == 1) {
            mDatabase = dbHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        mOpenCounter--;
        if(mOpenCounter == 0) {
            if (mDatabase != null && mDatabase.isOpen()) {
                mDatabase.close();
            }
        }
    }

    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, paramDB.nameDB, null, paramDB.versionDB);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for (DescriptTableDB dt : paramDB.listTables) {
                db.execSQL("DROP TABLE IF EXISTS " + dt.nameTable + ";");
                db.execSQL("CREATE TABLE " + dt.nameTable + " (" + dt.descriptTable + ");");
                if (dt.indexName != null && dt.indexName.length() > 0) {
                    db.execSQL("DROP INDEX IF EXISTS " + dt.indexName + ";");
                    db.execSQL("CREATE INDEX IF NOT EXISTS " + dt.indexName + " ON " + dt.nameTable + " (" + dt.indexColumn + ");");
                }
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

}
