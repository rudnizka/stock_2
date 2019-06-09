package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.IValidate;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.param.AppParams;

public class ComponEditText extends AppCompatEditText implements IComponent, IValidate {

    protected int typeValidate;
    protected final int FILLED = 0, EMAIL = 1, LENGTH = 2, DIAPASON = 3, MIN_LENGTH = 4;
    private int fieldLength;
    private int minLength = -1;
    private int maxLength = Integer.MAX_VALUE;
    private String alias;
    private OnChangeStatusListener statusListener;
    protected String textError = "";
    protected TextInputLayout textInputLayout;
    private String minValueText, maxValueText;
    private double minValue, maxValue;
    private long minValueLong, maxValueLong;
    private boolean typeClassNumber;
    private boolean isValid, isVerify;
    private boolean onlyLetters;
    private OnFocusChangeListener focusChangeListenerInheritor = null;
    private String filter = "[a-zA-ZёЁїЇіІ а-яА-Я-]+";
    private int selectPos;
    private String oldString;

    public ComponEditText(Context context) {
        super(context);
        init(context, null);
    }

    public ComponEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ComponEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);
        try {
            typeValidate = a.getInt(R.styleable.Simple_typeValidate, -1);
            textError = a.getString(R.styleable.Simple_textError);
            if (textError == null) {
                textError = "";
            }
            onlyLetters = a.getBoolean(R.styleable.Simple_onlyLetters, false);
            minValueText = a.getString(R.styleable.Simple_minValue);
            maxLength = a.getInt(R.styleable.Simple_maxLength, Integer.MAX_VALUE);
            maxValueText = a.getString(R.styleable.Simple_maxValue);
            minLength = a.getInt(R.styleable.Simple_minLength, -1);
            fieldLength = a.getInt(R.styleable.Simple_fieldLength, -1);
        } finally {
            a.recycle();
        }
        isValid = false;
        isVerify = false;
        if (minLength > -1) {
            typeValidate = MIN_LENGTH;
        } else {
            if (fieldLength > -1) {
                typeValidate = LENGTH;
            }
        }

        if (getInputType() == InputType.TYPE_CLASS_NUMBER) {
            typeClassNumber = true;
            maxValueLong = Long.MAX_VALUE;
            minValueLong = Long.MIN_VALUE;
            if (minValueText != null) {
                try {
                    minValueLong = Long.valueOf(minValueText);
                } catch (NumberFormatException e) {
                    minValueLong = Long.MIN_VALUE;
                    errorParam("minValue");
                }
            }
            if (maxValueText != null) {
                try {
                    maxValueLong = Long.valueOf(maxValueText);
                } catch (NumberFormatException e) {
                    maxValueLong = Long.MAX_VALUE;
                    errorParam("maxValue");
                }
            }
            if (minValueLong != Long.MIN_VALUE || maxValueLong != Long.MAX_VALUE) {
                typeValidate = DIAPASON;
                isVerify = true;
//                addTextChangedListener(new EditTextWatcher());
            }
        } else {
            typeClassNumber = false;
            maxValue = Double.MAX_VALUE;
            minValue = Double.MIN_VALUE;
            if (minValueText != null) {
                try {
                    minValue = Double.valueOf(minValueText);
                } catch (NumberFormatException e) {
                    minValue = Double.MIN_VALUE;
                    errorParam("minValue");
                }
            }
            if (maxValueText != null) {
                try {
                    maxValue = Double.valueOf(maxValueText);
                } catch (NumberFormatException e) {
                    maxValue = Double.MAX_VALUE;
                    errorParam("maxValue");
                }
            }
            if (minValue != Double.MIN_VALUE || maxValue != Double.MAX_VALUE) {
                typeValidate = DIAPASON;
                isVerify = true;
//                addTextChangedListener(new EditTextWatcher());
            }
        }
//        if (onlyLetters) {
//            setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxLength),
//                    new InputFilter() {
//                        @Override
//                        public CharSequence filter(CharSequence source, int start,
//                                                   int end, Spanned dest, int dstart, int dend) {
//                            if(source.equals("")){ // for backspace
//                                return source;
//                            }
//                            if(source.toString().matches("[a-zA-ZёЁїЇіІ а-яА-Я-]+")){
//                                return source;
//                            }
//                            return "";
//                        }
//                    }
//            });
//        }

        oldString = "";
        selectPos = 0;
        addTextChangedListener(new EditTextWatcher());
//        setFocusable(true);
//        setFocusableInTouchMode(true);
        getTextInputLayout();
        setOnFocusChangeListener(noFocus);
    }

    public void setFocusChangeListenerInheritor(OnFocusChangeListener listener) {
        focusChangeListenerInheritor = listener;
    }

    private void errorParam(String st) {
        int i = getId();
        String name = getResources().getResourceEntryName(i);
        Log.i(AppParams.NAME_LOG_APP, "error in attribute "+st+" for elemet "+name);
    }

    private OnFocusChangeListener noFocus = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                isValid();
            } else {
                setErrorValid("");
            }
            if (focusChangeListenerInheritor != null) {
                focusChangeListenerInheritor.onFocusChange(v, hasFocus);
            }
        }
    };

    @Override
    public void setData(Object data) {
        setText((String) data);
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Object getData() {
        String st = getText().toString();
        return st;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {
        this.statusListener = statusListener;
    }

    @Override
    public String getString() {
        return getText().toString();
    }

    @Override
    public boolean isValid() {
        String st = getText().toString().trim();
        boolean result = false;
        switch (typeValidate) {
            case -1: return true;
            case FILLED :
                result = st != null && st.length() > 0;
                break;
            case LENGTH :
                result = fieldLength == st.length();
                break;
            case EMAIL :
                result = android.util.Patterns.EMAIL_ADDRESS.matcher(st).matches();
                break;
            case MIN_LENGTH :
                result = st.length() >= minLength;
                break;
            case DIAPASON :
                double val = 0d;
                if (st != null) {
                    try {
                        val = Double.valueOf(st);
                    } catch (NumberFormatException e) {
                        result = false;
                        errorParam("value");
                    }
                }
                result = maxValue > val && val > minValue;
                break;
        }
        if (result) {
            setErrorValid("");
        } else {
            setErrorValid(textError);
        }
        return result;
    }

    private class EditTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String st = s.toString();
            if ( onlyLetters && st.length() > 0 && ! st.matches(filter)) {
                setText(oldString);
                setSelection(oldString.length());
                return;
            }
            if (isVerify) {
                if (typeClassNumber) {
                    long v = Long.valueOf(st);
                    if (v < minValueLong) {
                        st = String.valueOf(minValueLong);
                        setText(st);
                        setSelection(st.length());
                    }
                    if (v > maxValueLong) {
                        st = String.valueOf(maxValueLong);
                        setText(st);
                        setSelection(st.length());
                    }
                } else {
                    double v = Double.valueOf(st);
                    if (v < minValue) {
                        st = String.valueOf(minValue);
                        setText(st);
                        setSelection(st.length());
                    }
                    if (v > maxValue) {
                        st = String.valueOf(maxValue);
                        setText(st);
                        setSelection(st.length());
                    }
                }
            }
            if (isValid()) {
                if ( ! isValid) {
                    isValid = true;
                    setEvent(3);
                }
            } else {
                if (isValid) {
                    isValid = false;
                    setEvent(2);
                }
            }
            selectPos = getSelectionEnd();
            oldString = st;
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void setEvent(int stat) {
        if (statusListener != null) {
            statusListener.changeStatus(this, stat);
        }
    }

    private void getTextInputLayout() {
        ViewParent viewParent = getParent();
        textInputLayout = null;
        if (viewParent instanceof TextInputLayout) {
            textInputLayout = (TextInputLayout) viewParent;
        } else if (viewParent != null) {
            ViewParent vp = viewParent.getParent();
            if (vp instanceof TextInputLayout) {
                textInputLayout = (TextInputLayout) vp;
            }
        }
    }

    public void setErrorValid(String textError) {
        if (textInputLayout == null) {
            getTextInputLayout();
        }
        if (textInputLayout != null) {
            textInputLayout.setError(textError);
        }
    }
}
