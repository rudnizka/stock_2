package com.dpcsa.compon.custom_components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dpcsa.compon.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class DateDiapason extends RelativeLayout {
    Context context;
    CalendarView calendarView;
    ArrowRight arrowRight;
    ArrowLeft arrowLeft;
    int HeightTitle = 40;
    float DENSITY = getResources().getDisplayMetrics().density;
    int HeightTitleDens = (int) (HeightTitle * DENSITY);
    int arrowH = (int) (24 * DENSITY);
    int arrowPadding = (int) (10 * DENSITY);
    String textOk, textCancel;
    TextView viewFrom, viewBefore;
    View thisView;
    int viewFromId, viewBeforeId;
    int type;
    dateC dateLast, dateBeforeLast, newDateC;
    String textFrom, textBefore;

    public DateDiapason(Context context) {
        this(context, null);
    }

    public DateDiapason(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateDiapason(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs){
        thisView = this;
        newDateC = new dateC();
        dateLast = new dateC();
        dateLast.ymd = - 1;
        dateBeforeLast = new dateC();
        type = 0;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
        textOk = a.getString(R.styleable.CalendarView_textOk);
        textCancel = a.getString(R.styleable.CalendarView_textCancel);
        viewFromId = a.getResourceId(R.styleable.CalendarView_viewFrom, 0);
        viewBeforeId = a.getResourceId(R.styleable.CalendarView_viewBefore, 0);
        if (textOk == null) {
            textOk = "oK";
        }
        if (textCancel == null) {
            textCancel = "Cancel";
        }
        a.recycle();

        int calendarId = generateViewId();
        calendarView = new CalendarView(context, attrs);
        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT, 500);
        calendarView.setId(calendarId);
        calendarView.setVisibility(VISIBLE);
        calendarView.setLayoutParams(lp);
        calendarView.setCountMonth(12,11);
        addView(calendarView);
        calendarView.setCalendarCallBack(callBack);
        calendarView.setAdapter(null);

        LinearLayout managem = new LinearLayout(context);
        LayoutParams lManag = new LayoutParams(MATCH_PARENT, HeightTitleDens);
        lManag.addRule(RelativeLayout.BELOW, calendarView.getId());
        managem.setGravity(Gravity.RIGHT);
        managem.setLayoutParams(lManag);
        addView(managem);
            TextView cancel = new TextView(context);
            LayoutParams lcancel = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, MATCH_PARENT);
            cancel.setLayoutParams(lcancel);
            cancel.setPadding(arrowH, 0, arrowH, 0);
            cancel.setGravity(Gravity.CENTER);
            cancel.setText(textCancel.toUpperCase());
            cancel.setTextSize(16);
            cancel.setTextColor(0xff000000);
            managem.addView(cancel);
            cancel.setOnClickListener(clickCancel);

            TextView ok = new TextView(context);
            LayoutParams lOk = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, MATCH_PARENT);
            ok.setLayoutParams(lOk);
            ok.setPadding(arrowH, 0, arrowH, 0);
            ok.setGravity(Gravity.CENTER);
            ok.setText(textOk.toUpperCase());
            ok.setTextSize(16);
            ok.setTextColor(0xff000000);
            managem.addView(ok);
            ok.setOnClickListener(clickOk);
        RelativeLayout botR = new RelativeLayout(context);
        LayoutParams lBotR = new LayoutParams(HeightTitleDens * 2, HeightTitleDens);
        lBotR.addRule(ALIGN_PARENT_RIGHT);
        botR.setOnClickListener(clickR);
        botR.setLayoutParams(lBotR);
        addView(botR);
            arrowRight = new ArrowRight(context);
            LayoutParams lArr = new LayoutParams(arrowH, arrowH);
            lArr.addRule(ALIGN_PARENT_RIGHT);
            lArr.addRule(CENTER_VERTICAL);
            arrowRight.setLayoutParams(lArr);
            botR.addView(arrowRight);

        RelativeLayout botL = new RelativeLayout(context);
        LayoutParams lBotL = new LayoutParams(HeightTitleDens * 2, HeightTitleDens);
        botL.setLayoutParams(lBotL);
        botL.setOnClickListener(clickL);
        addView(botL);
            arrowLeft = new ArrowLeft(context);
            LayoutParams lArrL = new LayoutParams(arrowH, arrowH);
            lArrL.addRule(CENTER_VERTICAL);
            arrowLeft.setLayoutParams(lArrL);
        botL.addView(arrowLeft);
        if (viewFromId != 0 || viewBeforeId != 0) {
            isParent.run();
        }
    }

    OnClickListener clickFrom = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setVisibility(VISIBLE);
            if (viewFrom != null) {
                textFrom = viewFrom.getText().toString();
            }
            if (viewBefore != null) {
                textBefore = viewBefore.getText().toString();
            }
        }
    };

    Handler handler = new Handler();

    Runnable isParent = new Runnable() {
        @Override
        public void run() {
            if (thisView.getParent() == null) {
                handler.postDelayed(isParent, 20);
            } else {
                setDateViews();
            }
        }
    };

    private void setDateViews() {
        ViewParent viewRoot = this;
        ViewParent view2 = viewRoot;
        ViewParent v = viewRoot.getParent();
        while (v != null) {
            view2 = viewRoot;
            viewRoot = v;
            v = viewRoot.getParent();
        }
        View vr = (View) view2;
        if (viewFromId != 0) {
            viewFrom = vr.findViewById(viewFromId);
            if (viewFrom != null) {
                type = 1;
            }
        }
        if (viewBeforeId != 0) {
            viewBefore = vr.findViewById(viewBeforeId);
            if (viewBefore != null) {
                if (type > 0) {
                    type = 2;
                } else {
                    viewFrom = viewBefore;
                }
            }
        }
        viewFrom.setOnClickListener(clickFrom);
    };

    OnClickListener clickCancel = new OnClickListener() {
        @Override
        public void onClick(View v) {
            viewFrom.setText(textFrom);
            if (type == 2) {
                viewBefore.setText(textBefore);
            }
            setVisibility(GONE);
        }
    };

    OnClickListener clickOk = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setVisibility(GONE);
        }
    };

    OnClickListener clickR = new OnClickListener() {
        @Override
        public void onClick(View v) {
            calendarView.monthPlus(1);
        }
    };

    OnClickListener clickL = new OnClickListener() {
        @Override
        public void onClick(View v) {
            calendarView.monthPlus(-1);
        }
    };

    CalendarView.CalendarCallBack callBack = new CalendarView.CalendarCallBack() {

        @Override
        public void onChangeDay(View v, int year, int month, int number, int weekday) {
            newDateC.setDat(year, month, number);
            if (newDateC.ymd != dateLast.ymd) {
                switch (type) {
                    case 1:
                        viewFrom.setText(number + "." + (month + 1) + "." + year);
                        dateLast.setDat(year, month, number);
                        break;
                    case 2:
                        dateBeforeLast.setDat(dateLast);
                        dateLast.setDat(newDateC);
                        if (dateLast.ymd > dateBeforeLast.ymd) {
                            viewFrom.setText(dateBeforeLast.d + "." + (dateBeforeLast.m + 1) + "." + dateBeforeLast.y);
                            viewBefore.setText(dateLast.d + "." + (dateLast.m + 1) + "." + dateLast.y);
                        } else {
                            viewBefore.setText(dateBeforeLast.d + "." + (dateBeforeLast.m + 1) + "." + dateBeforeLast.y);
                            viewFrom.setText(dateLast.d + "." + (dateLast.m + 1) + "." + dateLast.y);
                        }
                        break;
                }
            }
        }

        @Override
        public void onChangeMonth(int year, int month, int number) {

        }

        @Override
        public void setCurrentDate(int year, int month, int number, int weekDay) {
            dateLast.setDat(year, month, number);
        }
    };

    private class ArrowRight extends LinearLayout{
        int colorLine = 0xFF000000;
        float DENSITY = getResources().getDisplayMetrics().density;
        float strokeWidth = DENSITY * 2;
        protected int canvasW, canvasH;

        public ArrowRight(Context context) {
            this(context, null);
        }

        public ArrowRight(Context context, @Nullable AttributeSet attrs) {
            this(context, null, 0);
        }

        public ArrowRight(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setBackgroundColor(0x00000000);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            canvasW = w;
            canvasH = h;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(colorLine);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawLine(0, canvasH, canvasW / 2, canvasH / 2, paint);
            canvas.drawLine(canvasW / 2, canvasH / 2, 0, 0, paint);
        }
    }

    private class ArrowLeft extends LinearLayout{

        int colorLine = 0xFF000000;
        float DENSITY = getResources().getDisplayMetrics().density;
        float strokeWidth = DENSITY * 2;
        protected int canvasW, canvasH;

        public ArrowLeft(Context context) {
            this(context, null);
        }

        public ArrowLeft(Context context, @Nullable AttributeSet attrs) {
            this(context, null, 0);
        }

        public ArrowLeft(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            setBackgroundColor(0x00000000);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            canvasW = w;
            canvasH = h;
            super.onSizeChanged(w, h, oldw, oldh);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(colorLine);
            paint.setStrokeWidth(strokeWidth);
            canvas.drawLine(canvasW, canvasH, canvasW / 2, canvasH / 2, paint);
            canvas.drawLine(canvasW / 2, canvasH / 2, canvasW, 0, paint);
        }
    }

    private class dateC {
        int ymd, y, m, d;
        public void setDat(int year, int month, int day) {
            y = year;
            m = month;
            d = day;
            ymd = (year * 12 + month) * 100 + day;
        }

        public void setDat(dateC dat) {
            y = dat.y;
            m = dat.m;
            d = dat.d;
            ymd = (dat.y * 12 + dat.m) * 100 + dat.d;
        }
    }
}
