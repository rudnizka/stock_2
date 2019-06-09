package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import com.dpcsa.compon.R;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;
import com.dpcsa.compon.json_simple.Field;
import com.dpcsa.compon.json_simple.WorkWithRecordsAndViews;

public class Gallery extends ViewPager implements IComponent {

    private Context context;
    private List<Field> listData;
    private int INDICATOR, placeholder;
    private String alias;
    private PagerIndicator indicator;

    public Gallery(@NonNull Context context) {
       this(context, null);
    }

    public Gallery(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAttribute(attrs);
    }

    private void setAttribute(AttributeSet attrs) {
        context = getContext();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);
        try {
            alias = a.getString(R.styleable.Simple_alias);
            INDICATOR = a.getResourceId(R.styleable.Simple_indicator, 0);
            placeholder = a.getResourceId(R.styleable.Simple_placeholder, 0);
        } finally {
            a.recycle();
        }
    }

    PagerAdapter adapter = new PagerAdapter() {
        WorkWithRecordsAndViews modelToView = new WorkWithRecordsAndViews();
        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int position) {
            ImageView v = new ImageView(context);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            v.setLayoutParams(lp);

            Field f2 = listData.get(position);
            if (placeholder == 0) {
                Glide.with(context)
                        .load((String) f2.value)
                        .into(v);
            } else {
                Glide.with(context)
                        .load((String) f2.value)
                        .apply(new RequestOptions().placeholder(placeholder))
//                        .placeholder(placeholder)
                        .into(v);
            }
            viewGroup.addView(v);
            return v;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int position, Object view) {
            viewGroup.removeView((View) view);
        }
    };

    @Override
    public void setData(Object data) {
        if (data instanceof List) {
            listData = (List<Field>) data;
            if (INDICATOR != 0) {
                View v = (View) getParent();
                indicator = (PagerIndicator) v.findViewById(INDICATOR);
                indicator.setCount(listData.size());
            }
            setOnPageChangeListener(new OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (INDICATOR != 0) {
                        indicator.setSelect(position);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            setAdapter(adapter);
        }
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public Object getData() {
        return listData;
    }

    @Override
    public void setOnChangeStatusListener(OnChangeStatusListener statusListener) {

    }

    @Override
    public String getString() {
        return null;
    }
}
