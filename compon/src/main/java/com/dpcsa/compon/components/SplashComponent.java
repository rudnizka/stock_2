package com.dpcsa.compon.components;

import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.base.Screen;
import com.dpcsa.compon.interfaces_classes.IBase;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.param.ParamComponent;

public class SplashComponent extends BaseComponent {
    public SplashComponent(IBase iBase, ParamComponent paramMV, Screen multiComponent) {
        super(iBase, paramMV, multiComponent);
    }

    @Override
    public void initView() {
        if (paramMV.intro != null && ! preferences.getTutorial()) {
            iBase.startScreen(paramMV.intro, false);
        } else {
            if (paramMV.auth != null && preferences.getSessionToken().length() == 0) {
                iBase.startScreen(paramMV.auth, false);
            } else {
                iBase.startScreen(paramMV.main, false);
            }
        }
        iBase.backPressed();
    }

    @Override
    public void changeData(Field field) {

    }
}
