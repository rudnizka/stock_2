package com.dpcsa.compon.components;

import android.widget.EditText;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class PlusMinusComponent extends BaseComponent {

    public EditText editPlusMinus;

    public PlusMinusComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
//        editPlusMinus = (EditText) parentLayout.findViewById(paramMV.paramView.viewId);
//        editPlusMinus.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (iCustom != null) {
//                    iCustom.changeValue(paramMV.paramView.viewId, null);
//                }
//            }
//        });
//        // PLUS
//        parentLayout.findViewById(paramMV.paramView.layoutTypeId[0]).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int i = 0;
//                String st = editPlusMinus.getText().toString();
//                if (st != null && st.length() > 0) {
//                    i = Integer.valueOf(st);
//                }
//                i++;
//                st = String.valueOf(i);
//                editPlusMinus.setText(st);
//                editPlusMinus.setSelection(st.length());
//            }
//        });
//// MINUS
//        parentLayout.findViewById(paramMV.paramView.layoutFurtherTypeId[0]).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int i = 0;
//                String st = editPlusMinus.getText().toString();
//                if (st != null && st.length() > 0) {
//                    i = Integer.valueOf(st);
//                }
//                i--;
//                st = String.valueOf(i);
//                editPlusMinus.setText(st);
//                editPlusMinus.setSelection(st.length());
//            }
//        });
    }

//    public void changeCount(int count) {
//        if (navigator != null) {
//            for (ViewHandler vh : navigator.viewHandlers) {
//                switch (vh.type) {
//                    case UPDATE_DATA:
//                        ParamModel pm = vh.paramModel;
//                        if (pm.method == UPDATE_DB) {
//                            ContentValues cv = new ContentValues();
//                            cv.put(pm.updateSet, count);
//                            ComponGlob.getInstance().baseDB.updateRecord(iBase, pm, setParam(pm.param, record));
//                        }
//                        break;
//                }
//            }
//        }
//    }

    @Override
    public void changeData(Field field) {

    }
}
