package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.dpcsa.compon.R;
import com.dpcsa.compon.components.YouTubePlayerComponent;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.OnChangeStatusListener;

public class SimpleYouTubePlayer extends FrameLayout implements IComponent {

    private Context context;
    private String alias;
    private YouTubePlayerComponent youCompon;

    public SimpleYouTubePlayer(@NonNull Context context) {
        this(context, null);
    }

    public SimpleYouTubePlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleYouTubePlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setAttribute(context, attrs);
    }

    private void setAttribute(Context context, AttributeSet attrs) {
        context = context;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Simple,
                0, 0);
        try {
            alias = a.getString(R.styleable.Simple_alias);
        } finally {
            a.recycle();
        }
    }

    public void setYoutubeComponent(YouTubePlayerComponent youComponent) {
        youCompon = youComponent;
    }

    @Override
    public void setData(Object data) {
        if (youCompon != null) {
            youCompon.startVideo((String) data);
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
        return null;
    }
}
