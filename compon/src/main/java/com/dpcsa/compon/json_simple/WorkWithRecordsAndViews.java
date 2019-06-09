package com.dpcsa.compon.json_simple;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
//import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;
import com.dpcsa.compon.base.BaseComponent;
import com.dpcsa.compon.custom_components.PlusMinus;
import com.dpcsa.compon.custom_components.SimpleImageView;
import com.dpcsa.compon.custom_components.SimpleTextView;
import com.dpcsa.compon.glide.GlideApp;
import com.dpcsa.compon.glide.GlideRequest;
import com.dpcsa.compon.interfaces_classes.IBaseComponent;
import com.dpcsa.compon.interfaces_classes.IComponent;
import com.dpcsa.compon.interfaces_classes.Navigator;
import com.dpcsa.compon.interfaces_classes.ViewHandler;
import com.dpcsa.compon.interfaces_classes.Visibility;
import com.dpcsa.compon.single.Injector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static com.bumptech.glide.request.RequestOptions.circleCropTransform;
import static com.bumptech.glide.request.RequestOptions.placeholderOf;
import static com.dpcsa.compon.json_simple.Field.TYPE_INTEGER;
import static com.dpcsa.compon.json_simple.Field.TYPE_STRING;

public class WorkWithRecordsAndViews {
    protected Record model;
    protected View view;
    protected Navigator navigator;
    protected View.OnClickListener clickView;
    protected Context context;
    protected String[] param;
    protected Record recordResult;
    protected BaseComponent baseComponent;
    private boolean setParam;
    private Visibility[] visibilityManager;
//    private boolean isSwipe;
    private int swipeId, rightId, leftId;
//    private SwipeLayout swipeLayout;

    public void RecordToView(Record model, View view) {
//        RecordToView(model, view, null, null, null);
        RecordToView(model, view, null, null);
    }

//    public void RecordToView(Record model, View view, Navigator navigator,
//                             View.OnClickListener clickView, Visibility[] visibilityManager) {
    public void RecordToView(Record model, View view, BaseComponent bc,
                         View.OnClickListener clickView) {
        this.model = model;
        this.view = view;
        if (bc != null) {
            baseComponent = bc;
            navigator = bc.navigator;
            visibilityManager = bc.paramMV.paramView.visibilityArray;
        }
//        this.navigator = navigator;
        this.clickView = clickView;
//        if (view instanceof SwipeLayout) {
//            isSwipe = true;
//            swipeLayout = (SwipeLayout) view;
////            swipeId = swipeLayout.getSwipeId();
////            rightId = swipeLayout.getRightId();
////            leftId = swipeLayout.getLeftId();
//        } else {
//            isSwipe = false;
//        }
        context = view.getContext();
        setParam = false;
//        this.visibilityManager = visibilityManager;
        enumViewChild(view);
    }

    public Record ViewToRecord(View view, String par) {
        recordResult = new Record();
        param = par.split(",");
        for (String st : param) {
            recordResult.add(new Field(st, TYPE_STRING, null));
        }
        setParam = true;
        enumViewChild(view);
        return recordResult;
    }

    private void enumViewChild(View v) {
        ViewGroup vg;
        int id;
        if (v instanceof ViewGroup) {
            vg = (ViewGroup) v;
            int countChild = vg.getChildCount();
            id = v.getId();
            if (id > -1) {
                setValue(v);
            }
            for (int i = 0; i < countChild; i++) {
                enumViewChild(vg.getChildAt(i));
            }
        } else {
            if (v != null) {
                id = v.getId();
                if (id != -1) {
                    setValue(v);
                }
            }
        }
    }

    private void setRecordField(View v, String name) {
        for (Field f : recordResult) {
            if (f.name.equals(name)) {
                if (v instanceof IComponent) {
                    f.value = ((IComponent) v).getString();
                    break;
                }
                if (v instanceof TextView) {
                    f.value = ((TextView) v).getText().toString();
                    break;
                }
            }
        }
    }

    private void setValue(View v) {
        int id = v.getId();
        String st;
        String name = v.getContext().getResources().getResourceEntryName(id);
        if (v instanceof IComponent) {
            st = ((IComponent) v).getAlias();
            if (st != null && st.length() > 0) {
                name = st;
            }
        }
        if (setParam) {
            setRecordField(v, name);
            return;
        }
        if (navigator != null) {
            for (ViewHandler vh : navigator.viewHandlers) {
                if (id == vh.viewId) {
                    v.setOnClickListener(clickView);
                    break;
                }
            }
        }
        if (visibilityManager != null && visibilityManager.length > 0) {
            for (Visibility vis : visibilityManager) {
                if (vis.viewId == id) {
                    if (model.getBooleanVisibility(vis.nameField)) {
                        switch (vis.typeShow) {
                            case 0 :
                                v.setVisibility(View.VISIBLE);
                                break;
                            case 1 :
                                v.setEnabled(true);
                                break;
                        }
                    } else {
                        switch (vis.typeShow) {
                            case 0 :
                                v.setVisibility(View.GONE);
                                break;
                            case 1 :
                                v.setEnabled(false);
                                break;
                        }
                    }
                    break;
                }
            }
        }
        if (model == null) {
            return;
        }
        Field field = model.getField(name);
        if (field != null) {
            if (v instanceof IComponent) {
                if (v instanceof IBaseComponent) {
                    ((IBaseComponent) v).setData(baseComponent.iBase, field.value);
                } else {
                    ((IComponent) v).setData(field.value);
                }
                return;
            }
            if (v instanceof TextView) {
                if (field.value instanceof String) {
                    ((TextView) v).setText((String )field.value);
                    if (v instanceof PlusMinus) {
                        ((PlusMinus) v).setParam(view, model, baseComponent);
                    }
                    return;
                }
                if (field.value instanceof Number) {
                    if (v instanceof SimpleTextView) {
                        st = ((SimpleTextView) v).getNumberFormat();
                        if (st != null) {
                            ((SimpleTextView) v).setText(new Formatter().format(st, field.value).toString());
                        } else {
                            ((SimpleTextView) v).setText(field.value.toString());
                        }
                    } else {
                        ((TextView) v).setText(field.value.toString());
                    }
                    if (v instanceof PlusMinus) {
                        ((PlusMinus) v).setParam(view, model, baseComponent);
                    }
                    return;
                }
                if(field.value instanceof Date) {
                    SimpleDateFormat format;
                    if (v instanceof SimpleTextView) {
                        st = ((SimpleTextView) v).getDateFormat();
                        if (st != null) {
                            ((SimpleTextView) v).setText(new Formatter().format(st, field.value).toString());
                        } else {
                            format = new SimpleDateFormat();
                            ((TextView) v).setText(format.format((Date) field.value));
                        }
                    } else {
                        format = new SimpleDateFormat();
                        ((TextView) v).setText(format.format((Date) field.value));
                    }
                    return;
                }
                return;
            }
        }
        if (v instanceof ImageView) {
            if (field == null) return;
            if (field.type == TYPE_STRING) {
                st = (String) field.value;
                if (st == null) {
                    st = "";
                }
                if (st.length() == 0) return;
//                if (st.contains("content")) {
//                    Glide.with(view.getContext())
//                            .load(st)
//                            .into((ImageView) v);
//                } else {
                    if (st.contains("/")) {
                        if (!st.contains("http")) {
                            st = Injector.getComponGlob().appParams.baseUrl + st;
                        }
//                        if (v instanceof SimpleImageView) {
//                            int placeholder = ((SimpleImageView) v).getPlaceholder();
//                            if (placeholder == 0) {
//                                Glide.with(view.getContext())
//                                        .load(st)
//                                        .into((ImageView) v);
//                            } else {
//                                Glide.with(view.getContext())
//                                        .load(st)
//                                        .apply(new RequestOptions().placeholder(placeholder))
////                                        .placeholder(placeholder)
//                                        .into((ImageView) v);
//                            }
//                        } else {
//                            Glide.with(view.getContext())
//                                    .load(st)
//                                    .into((ImageView) v);
//                        }
                        GlideRequest gr = GlideApp.with(view.getContext()).load(st);
                        if (v instanceof SimpleImageView) {
                            SimpleImageView simg = (SimpleImageView) v;
                            if (simg.getBlur() > 0) {
                                gr.apply(bitmapTransform(new BlurTransformation(simg.getBlur())));
                            }
                            if (simg.getPlaceholder() > 0) {
                                gr.apply(placeholderOf(simg.getPlaceholder()));
                            }
                            if (simg.isOval()) {
                                gr.apply(circleCropTransform());
                            }
                        }
                        gr.into((ImageView) v);
                    } else {
                        if (v instanceof SimpleImageView) {
                            ((ImageView) v).setImageDrawable(view.getContext()
                                    .getResources().getDrawable(((SimpleImageView) v).getPlaceholder()));
                        } else {
                            ((ImageView) v).setImageResource(view.getContext().getResources()
                                    .getIdentifier(st, "drawable", view.getContext().getPackageName()));
                        }
                    }
//                }
            } else {
                if (field.type == TYPE_INTEGER) {
                    ((ImageView) v).setImageResource((Integer) field.value);
                }
            }
        }
    }

//    private SwipeLayout getRootParent(View v) {
//        if (v == null) return null;
//        ViewParent vp = v.getParent();
//        while (vp != null && ! (vp instanceof SwipeLayout)) {
//            vp = vp.getParent();
//        }
//        return (SwipeLayout)vp;
//    }
//
//    private int typeSwipe(View v) {
//        View vv = v;
//        do {
//            int id = vv.getId();
//            if (id == swipeId) {
//                return 1;
//            } else if (id == rightId) {
//                return 2;
//            } else if (id == leftId) {
//                return 3;
//            }
//            vv = (ViewGroup) vv.getParent();
//        } while (vv != null);
//        return 0;
//    }
//
//    View.OnClickListener clickRight = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (swipeLayout.isSwipeRight()) {
//                Log.d("QWERT","clickRight clickRight clickRight");
//                clickView.onClick(v);
//            }
//        }
//    };
//
//    View.OnClickListener clickLeft = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (swipeLayout.isSwipeLeft()) {
//                clickView.onClick(v);
//            }
//        }
//    };

}
