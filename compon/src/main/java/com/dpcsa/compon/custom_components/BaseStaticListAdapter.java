package com.dpcsa.compon.custom_components;

import android.view.View;

public class BaseStaticListAdapter {
    public BaseStaticList baseStaticList;

    public int getCount(){
        return 0;
    }
    public View getView(int position) {
        return null;
    }
    public void onClickView(View view, View viewElrment, int position) {

    }

    public void notifyDataSetChanged() {
        baseStaticList.formView();
    }
}
