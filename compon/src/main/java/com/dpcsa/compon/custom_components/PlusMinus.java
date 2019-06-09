package com.dpcsa.compon.custom_components;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.dpcsa.compon.R;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.components.PlusMinusComponent;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.ICustom;
import com.dpcsa.compon.interfaces_classes.Multiply;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.Record;
import com.dpcsa.compon.param.ParamComponent;
import com.dpcsa.compon.param.ParamModel;
import com.dpcsa.compon.single.Injector;

import static com.dpcsa.compon.param.ParamModel.UPDATE_DB;

public class PlusMinus extends AppCompatEditText {

    private Context context;
    private int plusId, minusId;
    public ICustom iCustom;
    public ParamComponent paramMV;
    private View parentView;
    private Record record;
    private Field field;
    private String thisName;
    private BaseComponent component;
    private int minValueInt,maxValueInt;
    private String minValue,maxValue;
    private PlusMinusComponent plusMinusComponent;
    private IBase iBase;
    private boolean noEdit;

    public PlusMinus(Context context) {
        this(context, null);
    }

    public PlusMinus(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
            try {
                plusId = a.getResourceId(R.styleable.Simple_plusViewId, 0);
                minusId = a.getResourceId(R.styleable.Simple_minusViewId, 0);
                minValue = a.getString(R.styleable.Simple_minValue);
                maxValue = a.getString(R.styleable.Simple_maxValue);
                noEdit = a.getBoolean(R.styleable.Simple_noEdit, false);
            } finally {
                a.recycle();
            }
            maxValueInt = Integer.MAX_VALUE;
            minValueInt = Integer.MIN_VALUE;
            if (maxValue != null && maxValue.length() > 0) {
                try {
                    maxValueInt = Integer.parseInt(maxValue);
                } catch (NumberFormatException e) {}
            }
            if (minValue != null && minValue.length() > 0) {
                try {
                    minValueInt = Integer.parseInt(minValue);
                } catch (NumberFormatException e) {}
            }
        }
        if (noEdit) {
            setEnabled(false);
            setCursorVisible(false);
            setBackgroundColor(0x00000000);
            setKeyListener(null);
        }
        thisName = getContext().getResources().getResourceEntryName(getId());
    }

    public void setParam(View parentView, Record rec, BaseComponent component) {
        this.parentView = parentView;
        this.record = rec;
        iCustom = component.iCustom;
        field = rec.getField(thisName);
        iBase = component.iBase;
        paramMV = component.paramMV;
        this.component = component;
        BaseComponent bc = component.multiComponent.getComponent(getId());
        if (bc instanceof PlusMinusComponent) {
            plusMinusComponent = (PlusMinusComponent) bc;

            if ( ! noEdit) {
                addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (plusMinusComponent.paramMV.multiplies != null) {
                            int i = 0;
                            String st = getText().toString();
                            if (st != null && st.length() > 0) {
                                i = Integer.valueOf(st);
                            }
                            if (i < minValueInt) {
                                i = minValueInt;
                                String st1 = String.valueOf(i);
                                setText(st1);
                                setSelection(st1.length());
                            }
                            setValue(i);
                        }
                    }
                });
            }
        }

        if (plusMinusComponent != null) {
            plusId = plusMinusComponent.paramMV.paramView.layoutTypeId[0];
            minusId = plusMinusComponent.paramMV.paramView.layoutFurtherTypeId[0];
        }

// PLUS
        if (plusId != 0) {
            View vPlus = parentView.findViewById(plusId);
            if (vPlus != null) {
                vPlus.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = 0;
                        String st = getText().toString();
                        if (st != null && st.length() > 0) {
                            i = Integer.valueOf(st);
                        }
                        if (i < maxValueInt) {
                            i++;
                            st = String.valueOf(i);
                            setText(st);
                            setSelection(st.length());
                            setValue(i);
                        }
                    }
                });
            }
        }
// MINUS
        if (minusId != 0) {
            View vMinus = parentView.findViewById(minusId);
            if (vMinus != null) {
                vMinus.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = 0;
                        String st = getText().toString();
                        if (st != null && st.length() > 0) {
                            i = Integer.valueOf(st);
                        }
                        if (i > minValueInt) {
                            i--;
                            st = String.valueOf(i);
                            setText(st);
                            setSelection(st.length());
                            setValue(i);
                        }
                    }
                });
            }
        }
    }

    private void setValue(int count) {
        if (field != null) {
            if (field.type == Field.TYPE_LONG) {
                field.value = new Long(count);
            } else {
                field.value = count;
            }
        }
        execNavigator(count);
        for (Multiply m : plusMinusComponent.paramMV.multiplies) {
            Float mult = record.getFloat(m.nameField);
            if (mult != null) {
                TextView tv = parentView.findViewById(m.viewId);
                Float ff = mult * count;
                if (tv != null) {
                    if (tv instanceof IComponent) {
                        ((IComponent) tv).setData(ff);
                    } else {
                        tv.setText(String.valueOf(ff));
                    }
                }
                if (m.nameFieldRes != null && m.nameFieldRes.length() > 0) {
                    Field field = record.getField(m.nameFieldRes);
                    if (field != null) {
                        switch (field.type) {
                            case Field.TYPE_DOUBLE:
                                Double d = Double.valueOf(ff);
                                field.value = d;
                                break;
                            case Field.TYPE_FLOAT:
                                field.value = ff;
                                break;
                            case Field.TYPE_INTEGER:
                                float ii = ff;
                                Integer in = Integer.valueOf((int) ii);
                                field.value = in;
                                break;
                            case Field.TYPE_LONG:
                                float iL = ff;
                                Long lon = Long.valueOf((long) iL);
                                field.value = lon;
                                break;
                        }
                    } else {
                        record.add(new Field(m.nameFieldRes, Field.TYPE_FLOAT, ff));
                    }
                }
            }
        }
        iBase.sendEvent(plusMinusComponent.paramMV.paramView.viewId);
        iCustom = PlusMinus.this.component.iCustom;
        if (iCustom != null) {
            iCustom.changeValue(getId(), null);
        }
    }

    private void execNavigator(int count) {
        if (plusMinusComponent.navigator != null) {
            for (ViewHandler vh : plusMinusComponent.navigator.viewHandlers) {
                switch (vh.type) {
                    case MODEL_PARAM:
                        ParamModel pm = vh.paramModel;
                        if (pm.method == UPDATE_DB) {
                            ContentValues cv = new ContentValues();
                            cv.put(pm.updateSet, count);
                            Injector.getBaseDB().updateRecord(iBase, pm, cv, plusMinusComponent.setParam(pm.param, record));
                        }
                        break;
                }
            }
        }
    }
}
