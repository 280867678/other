package com.mxtech.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.mxtech.graphics.GraphicUtils;
import com.mxtech.videoplayer.pro.R;

/* loaded from: classes.dex */
public class AppCompatUtils {
    public static final String TAG = "MX.AppCompat";

    public static ColorFilter colorizeDrawable(Context themedContext, Drawable drawable, ColorFilter colorFilter) {
        if (colorFilter == null) {
            try {
                TypedArray a = themedContext.obtainStyledAttributes(new int[]{R.attr.colorControlNormal});
                int color = a.getColor(0, 0);
                if (color != 0) {
                    colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
                a.recycle();
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "", e);
            }
            if (colorFilter == null) {
                return null;
            }
        }
        GraphicUtils.safeMutate(drawable).setColorFilter(colorFilter);
        return colorFilter;
    }

    public static void colorizeMenuIcons(Context themedContext, Menu menu) {
        ColorFilter colorFilter = null;
        int numItems = menu.size();
        for (int i = 0; i < numItems; i++) {
            Drawable icon = menu.getItem(i).getIcon();
            if (icon != null && (colorFilter = colorizeDrawable(themedContext, icon, colorFilter)) == null) {
                return;
            }
        }
    }

    public static void colorizeDrawables(View view, ColorFilter colorFilter) {
        if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            Drawable d = iv.getDrawable();
            if (d != null) {
                GraphicUtils.safeMutate(d).setColorFilter(colorFilter);
            }
        } else if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int numChildren = group.getChildCount();
            for (int i = 0; i < numChildren; i++) {
                colorizeDrawables(group.getChildAt(i), colorFilter);
            }
        }
    }

    public static void colorizeDrawables(Context themedContext, View view) {
        try {
            TypedArray a = themedContext.obtainStyledAttributes(new int[]{R.attr.colorControlNormal});
            int color = a.getColor(0, 0);
            if (color != 0) {
                ColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
                colorizeDrawables(view, colorFilter);
            }
            a.recycle();
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "", e);
        }
    }

    public static Toolbar resetToolbar(Activity activity, int toolbarResId) {
        boolean reset;
        ViewGroup root = (ViewGroup) activity.findViewById(16908290);
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        if (toolbar == null) {
            View content = root.getChildAt(0);
            if (content == null) {
                return null;
            }
            root.removeAllViews();
            TypedArray a = activity.obtainStyledAttributes(R.styleable.AppCompatOverlayMode);
            int layoutResId = a.getBoolean(R.styleable.AppCompatOverlayMode_windowActionBarOverlay, false) ? R.layout.toolbar_activity_windowoverlay : R.layout.toolbar_activity;
            a.recycle();
            activity.getLayoutInflater().inflate(layoutResId, root);
            toolbar = (Toolbar) root.findViewById(R.id.toolbar);
            ViewGroup contentHolder = (ViewGroup) root.findViewById(R.id.content_holder);
            contentHolder.addView(content);
            reset = true;
        } else {
            reset = false;
        }
        if (!reset || toolbarResId != 0) {
            ViewGroup toolbarParent = (ViewGroup) toolbar.getParent();
            int childCount = toolbarParent.getChildCount();
            if (toolbarResId == 0) {
                TypedArray a2 = activity.obtainStyledAttributes(R.styleable.ToolbarActivity);
                toolbarResId = a2.getResourceId(R.styleable.ToolbarActivity_toolbar_layout, R.layout.toolbar);
                a2.recycle();
            }
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                } else if (toolbarParent.getChildAt(i) != toolbar) {
                    i++;
                } else {
                    toolbar.removeAllViews();
                    toolbarParent.removeViewAt(i);
                    toolbar = (Toolbar) activity.getLayoutInflater().inflate(toolbarResId, toolbarParent, false);
                    toolbarParent.addView(toolbar, i);
                    break;
                }
            }
        }
        return toolbar;
    }

    public static Toolbar resetSplitToolbar(Activity activity, int resId) {
        ViewGroup root = (ViewGroup) activity.findViewById(16908290);
        Toolbar splitbar = (Toolbar) root.findViewById(R.id.splitbar);
        if (splitbar == null) {
            ViewGroup layout = (ViewGroup) root.findViewById(R.id.toolbar_activity_layout);
            if (layout == null) {
                throw new IllegalStateException("Content is not set.");
            }
            Toolbar splitbar2 = (Toolbar) activity.getLayoutInflater().inflate(resId, layout, false);
            adjustSplitbarLayout(splitbar2);
            layout.addView(splitbar2, layout.getChildCount());
            return splitbar2;
        }
        ViewGroup parent = (ViewGroup) splitbar.getParent();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (parent.getChildAt(i) == splitbar) {
                parent.removeViewAt(i);
                Toolbar splitbar3 = (Toolbar) activity.getLayoutInflater().inflate(resId, parent, false);
                adjustSplitbarLayout(splitbar3);
                parent.addView(splitbar3, i);
                return splitbar3;
            }
        }
        return splitbar;
    }

    private static void adjustSplitbarLayout(Toolbar splitbar) {
        splitbar.setId(R.id.splitbar);
        ViewGroup.LayoutParams l = splitbar.getLayoutParams();
        if (l instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams rel = (RelativeLayout.LayoutParams) l;
            rel.addRule(12);
        }
    }

    public static int applyToolbarDisplayOptions(Toolbar toolbar) {
        int displayOptions = 0;
        Context context = toolbar.getContext();
        TypedArray a = context.obtainStyledAttributes(new int[]{R.attr.actionBarStyle});
        try {
            int actionBarStyleResId = a.getResourceId(0, 0);
            if (actionBarStyleResId != 0) {
                a = context.obtainStyledAttributes(actionBarStyleResId, new int[]{R.attr.displayOptions, R.attr.homeAsUpIndicator});
                try {
                    displayOptions = a.getInt(0, 0);
                    if ((displayOptions & 4) != 0) {
                        toolbar.setNavigationIcon(a.getResourceId(1, 0));
                    }
                } finally {
                }
            }
            return displayOptions;
        } finally {
        }
    }

    public static void applyOverscanMargin(Activity activity) {
        Resources res = activity.getResources();
        int horzMargin = res.getDimensionPixelSize(R.dimen.overscanMarginHorizontal);
        int vertMargin = res.getDimensionPixelSize(R.dimen.overscanMarginVertical);
        if (horzMargin != 0 || vertMargin != 0) {
            TypedArray a = activity.obtainStyledAttributes(R.styleable.AppCompatOverlayMode);
            boolean overlayMode = a.getBoolean(R.styleable.AppCompatOverlayMode_windowActionBarOverlay, false);
            a.recycle();
            if (!overlayMode) {
                View root = activity.findViewById(R.id.action_bar_root);
                if (root != null || (root = activity.findViewById(R.id.toolbar_activity_layout)) != null) {
                    ViewGroup.LayoutParams l = root.getLayoutParams();
                    if (l instanceof ViewGroup.MarginLayoutParams) {
                        ViewGroup.MarginLayoutParams marginParam = (ViewGroup.MarginLayoutParams) l;
                        marginParam.rightMargin = horzMargin;
                        marginParam.leftMargin = horzMargin;
                        marginParam.bottomMargin = vertMargin;
                        marginParam.topMargin = vertMargin;
                        root.requestLayout();
                    }
                }
            }
        }
    }
}
