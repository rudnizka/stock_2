package com.dpcsa.compon.components;

import android.util.Log;
import android.view.View;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.IValidate;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class EnabledComponent extends BaseComponent {

    public boolean[] validArray;
    private boolean isValid;

    public EnabledComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.paramView != null || paramMV.paramView.viewId != 0) {
            viewComponent = parentLayout.findViewById(paramMV.paramView.viewId);
        }
        if (viewComponent == null) {
            iBase.log("Не найден View для EnabledComponent в " + multiComponent.nameComponent);
            return;
        }
        if (paramMV.mustValid != null) {
            int ik = paramMV.mustValid.length;
            validArray = new boolean[ik];
            isValid = true;
            for (int i = 0; i < ik; i++) {
                View v = parentLayout.findViewById(paramMV.mustValid[i]);
                if (v != null && v instanceof IComponent) {
                    ((IComponent) v).setOnChangeStatusListener(statusListener);
                    if (v instanceof IValidate) {
                        boolean vv = ((IValidate) v).isValid();
                        if ( ! vv) {
                            isValid = false;
                        }
                        validArray[i] = vv;
                    } else {
                        validArray[i] = false;
                        isValid = false;
                    }
                } else {
                    validArray[i] = true;
                }
            }
        }
        viewComponent.setEnabled(isValid);
    }


    @Override
    public void changeData(Field field) {

    }

    OnChangeStatusListener statusListener = new OnChangeStatusListener() {
        @Override
        public void changeStatus(View view, Object status) {
            int stat = (Integer) status;
            if (stat == 2 || stat == 3) {
                int viewId = view.getId();
                int ik = paramMV.mustValid.length;
                for (int i = 0; i < ik; i++) {
                    if (paramMV.mustValid[i] == viewId) {
                        validArray[i] = stat == 3;
                        break;
                    }
                }
                isValid = true;
                for (boolean bb : validArray) {
                    if ( ! bb) {
                        isValid = false;
                        break;
                    }
                }
                viewComponent.setEnabled(isValid);
            }
        }
    };
}
