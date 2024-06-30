package com.mxtech;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.mxtech.app.AppUtils;
import com.mxtech.videoplayer.pro.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/* loaded from: classes.dex */
public final class ViewUtils {

    /* loaded from: classes2.dex */
    public interface IViewTraverse {
        void visit(View view);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static <T extends View> T findViewByClass(View root, Class<T> classToFind) {
        if (!classToFind.isInstance(root)) {
            if (root instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) root;
                int childCount = group.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    T found = (T) findViewByClass(group.getChildAt(i), classToFind);
                    if (found != null) {
                        return found;
                    }
                }
            }
            return null;
        }
        return root;
    }

    public static void getViewsWithTags(View root, Collection<View> views, Object... tags) {
        Object thisTag = root.getTag();
        int i = 0;
        while (true) {
            if (i < tags.length) {
                if (!tags[i].equals(thisTag)) {
                    i++;
                } else {
                    views.add(root);
                    break;
                }
            } else {
                break;
            }
        }
        if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            int childCount = group.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                getViewsWithTags(group.getChildAt(i2), views, tags);
            }
        }
    }

    public static Collection<View> getViewsWithTags(View root, Object... tags) {
        ArrayList<View> views = new ArrayList<>();
        getViewsWithTags(root, views, tags);
        return views;
    }

    public static void traverse(ViewGroup parent, IViewTraverse onViewTraverse) {
        for (int i = parent.getChildCount() - 1; i >= 0; i--) {
            View child = parent.getChildAt(i);
            onViewTraverse.visit(child);
            if (child instanceof ViewGroup) {
                traverse((ViewGroup) child, onViewTraverse);
            }
        }
    }

    public static void removeFromParent(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(view);
        }
    }

    public static CharSequence toSystemUiString(int flags) {
        StringBuilder sb = new StringBuilder();
        if ((flags & 0) != 0) {
            sb.append("VISIBLE ");
        }
        if ((flags & 4) != 0) {
            sb.append("FULLSCREEN ");
        }
        if ((flags & 1) != 0) {
            sb.append("LOW_PROFILE ");
        }
        if ((flags & 2) != 0) {
            sb.append("HIDE_NAVIGATION ");
        }
        if ((flags & 2048) != 0) {
            sb.append("IMMERSIVE ");
        }
        if ((flags & 4096) != 0) {
            sb.append("IMMERSIVE_STICKY ");
        }
        if ((flags & 1024) != 0) {
            sb.append("LAYOUT_FULLSCREEN ");
        }
        if ((flags & 512) != 0) {
            sb.append("LAYOUT_HIDE_NAVIGATION ");
        }
        if ((flags & 256) != 0) {
            sb.append("LAYOUT_STABLE ");
        }
        return sb;
    }

    public static Activity getActivityFrom(View view) {
        return AppUtils.getActivityFrom(view.getContext());
    }

    public static void positionToast(Toast toast, View view) {
        positionToast(toast, view, 0, 0);
    }

    public static void positionToast(Toast toast, View view, int offsetX, int offsetY) {
        Activity activity = getActivityFrom(view);
        if (activity != null) {
            positionToast(toast, activity, view, offsetX, offsetY);
        }
    }

    public static void positionToast(Toast toast, Activity activity, View view) {
        positionToast(toast, activity, view, 0, 0);
    }

    public static void positionToast(Toast toast, Activity activity, View view, int offsetX, int offsetY) {
        int toastY;
        Rect rect = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        int[] viewLocation = new int[2];
        view.getLocationInWindow(viewLocation);
        int viewLeft = viewLocation[0] - rect.left;
        int viewTop = viewLocation[1] - rect.top;
        DisplayMetrics metrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, 0);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, 0);
        toast.getView().measure(widthMeasureSpec, heightMeasureSpec);
        int toastWidth = toast.getView().getMeasuredWidth();
        int toastHeight = toast.getView().getMeasuredHeight();
        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();
        int toastX = ((viewWidth - toastWidth) / 2) + viewLeft + offsetX;
        if (viewTop + viewHeight + offsetY + toastHeight < (metrics.heightPixels * 4) / 5) {
            toastY = viewTop + viewHeight + offsetY;
        } else {
            toastY = (viewTop - offsetY) - toastHeight;
        }
        toast.setGravity(51, toastX, toastY);
    }

    @SuppressLint({"NewApi"})
    public static boolean hasCheckedItems(ListView listView) {
        if (Build.VERSION.SDK_INT >= 11) {
            return listView.getCheckedItemCount() > 0;
        }
        SparseBooleanArray positions = listView.getCheckedItemPositions();
        int numPositions = positions.size();
        for (int i = 0; i < numPositions; i++) {
            if (positions.valueAt(i)) {
                return true;
            }
        }
        return false;
    }

    public static List<Integer> getCheckedItemPositions(ListView listView) {
        SparseBooleanArray positions = listView.getCheckedItemPositions();
        int numPositions = positions.size();
        ArrayList<Integer> checked = new ArrayList<>(numPositions);
        for (int i = 0; i < numPositions; i++) {
            if (positions.valueAt(i)) {
                checked.add(Integer.valueOf(positions.keyAt(i)));
            }
        }
        return checked;
    }

    public static void trimTextView(TextView view) {
        String text = view.getText().toString();
        String newText = text.trim();
        if (!newText.equals(text)) {
            view.setText(newText);
        }
    }

    public static void setSpinnerEntries(Spinner spinner, CharSequence[] entries) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(spinner.getContext(), 17367048, entries);
        adapter.setDropDownViewResource(Build.VERSION.SDK_INT < 11 ? R.layout.select_dialog_singlechoice_material : 17367049);
        spinner.setAdapter((SpinnerAdapter) adapter);
    }
}
