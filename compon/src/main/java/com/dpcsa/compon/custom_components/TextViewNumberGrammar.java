package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.single.Injector;

public class TextViewNumberGrammar extends android.support.v7.widget.AppCompatTextView
        implements IComponent {
    private Context context;
    private int stringArray;
    private String [] textArray;
    private String alias;
    private Object data;

    public TextViewNumberGrammar(Context context) {
        super(context);
        this.context = context;
    }

    public TextViewNumberGrammar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setAttrs(attrs);
    }

    public TextViewNumberGrammar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setAttrs(attrs);
    }

    private void setAttrs(AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Simple);
        alias = a.getString(R.styleable.Simple_alias);
        stringArray = a.getResourceId(R.styleable.Simple_stringArray, 0);
        a.recycle();
        textArray = null;
        if (stringArray != 0) {
            textArray = getResources().getStringArray(stringArray);
        }
    }

    @Override
    public void setData(Object data) {
        if (textArray != null) {
            int num = 0;
            if (data instanceof String) {
                try {
                    num = Integer.valueOf((String) data);
                } catch (NumberFormatException e) {
                }
            } else {
                if (data instanceof Long) {
                    long lon = (Long) data;
                    num = (int) lon;
                } else if (data instanceof Integer) {
                    num = (Integer) data;
                } else {
                    return;
                }
            }
            String st = Injector.getComponGlob().TextForNumbet(num, textArray[0],
                    textArray[1], textArray[2]);
            setText(num + " " + st);
        }
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return getText().toString();
    }
}
