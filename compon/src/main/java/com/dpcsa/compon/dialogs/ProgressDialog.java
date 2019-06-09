package com.dpcsa.compon.dialogs;

import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dpcsa.compon.single.Injector;

public class ProgressDialog extends DialogFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        int id = Injector.getComponGlob().appParams.progressViewId;
        View view = null;
        if (id != 0) {
            view = inflater.inflate(id, container, false);
        }
//        View view = inflater.inflate(R.layout.dialog_progress, container, false);
        return view;
    }
}
