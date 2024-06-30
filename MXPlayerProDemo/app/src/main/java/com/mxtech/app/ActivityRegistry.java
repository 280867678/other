package com.mxtech.app;

import android.app.Activity;
import android.os.Build;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/* loaded from: classes.dex */
public final class ActivityRegistry {
    private static int _foregroundCount;
    private static HashSet<StateChangeListener> _stateListeners;
    private static HashSet<TerminationListener> _termListeners;
    private static int _visibleCount;
    static final WeakHashMap<Activity, Integer> activities = new WeakHashMap<>();

    /* loaded from: classes.dex */
    public interface StateChangeListener {
        void onActivityCreated(Activity activity);

        void onActivityRemoved(Activity activity);

        void onActivityStateChanged(Activity activity, int i);
    }

    /* loaded from: classes.dex */
    public interface TerminationListener {
        void onProcessMayTerminating();
    }

    public static Integer get(Activity activity) {
        return activities.get(activity);
    }

    public static boolean hasForegroundActivity() {
        return _foregroundCount > 0;
    }

    public static boolean hasVisibleActivity() {
        return _visibleCount > 0;
    }

    public static boolean hasActivity() {
        return activities.size() > 0;
    }

    public static <T extends Activity> boolean hasVisibleActivity(Class<T> type) {
        for (Map.Entry<Activity, Integer> entry : activities.entrySet()) {
            if (type.isInstance(entry.getKey()) && (entry.getValue().intValue() & 1) != 0) {
                return true;
            }
        }
        return false;
    }

    public static Activity getForegroundActivity() {
        for (Map.Entry<Activity, Integer> a : activities.entrySet()) {
            if ((a.getValue().intValue() & 2) != 0) {
                return a.getKey();
            }
        }
        return null;
    }

    public static Set<Activity> getAll() {
        return activities.keySet();
    }

    public static <T extends Activity> List<T> findByClass(Class<T> type) {
        ArrayList arrayList = new ArrayList();
        for (Activity a : activities.keySet()) {
            if (type.isInstance(a)) {
                arrayList.add(a);
            }
        }
        return arrayList;
    }

    public static void onCreated(Activity activity) {
        activities.put(activity, 0);
        if (_stateListeners != null) {
            Iterator<StateChangeListener> it = _stateListeners.iterator();
            while (it.hasNext()) {
                StateChangeListener listener = it.next();
                listener.onActivityCreated(activity);
            }
        }
    }

    public static boolean isVisible(Activity activity) {
        Integer state = activities.get(activity);
        return (state == null || (state.intValue() & 1) == 0) ? false : true;
    }

    public static boolean isInForeground(Activity activity) {
        Integer state = activities.get(activity);
        return (state == null || (state.intValue() & 2) == 0) ? false : true;
    }

    private static void changeState(Activity activity, int state) {
        activities.put(activity, Integer.valueOf(state));
        if (_stateListeners != null) {
            Iterator<StateChangeListener> it = _stateListeners.iterator();
            while (it.hasNext()) {
                StateChangeListener listener = it.next();
                listener.onActivityStateChanged(activity, state);
            }
        }
    }

    public static void onStarted(Activity activity) {
        _visibleCount++;
        changeState(activity, 1);
    }

    public static void onResumed(Activity activity) {
        _foregroundCount++;
        changeState(activity, 3);
    }

    public static void onPaused(Activity activity) {
        _foregroundCount--;
        changeState(activity, 17);
        if (Build.VERSION.SDK_INT < 11 && !activity.isFinishing()) {
            checkTermination();
        }
    }

    public static void onStopped(Activity activity) {
        _visibleCount--;
        changeState(activity, 16);
        if (!activity.isFinishing()) {
            checkTermination();
        }
    }

    public static void onDestroyed(Activity activity) {
        activities.remove(activity);
        if (_stateListeners != null) {
            Iterator<StateChangeListener> it = _stateListeners.iterator();
            while (it.hasNext()) {
                StateChangeListener listener = it.next();
                listener.onActivityRemoved(activity);
            }
        }
        checkTermination();
    }

    private static void checkTermination() {
        if (_foregroundCount <= 0 && _termListeners != null) {
            for (Integer num : activities.values()) {
                int state = num.intValue();
                if ((state & 16) == 0) {
                    return;
                }
            }
            Iterator<TerminationListener> it = _termListeners.iterator();
            while (it.hasNext()) {
                TerminationListener listener = it.next();
                listener.onProcessMayTerminating();
            }
        }
    }

    public static void finishAllActivities() {
        for (Activity a : activities.keySet()) {
            a.finish();
        }
    }

    public static void registerStateListener(StateChangeListener listener) {
        if (_stateListeners == null) {
            _stateListeners = new HashSet<>();
        }
        _stateListeners.add(listener);
    }

    public static void unregisterStateListener(StateChangeListener listener) {
        if (_stateListeners != null) {
            _stateListeners.remove(listener);
        }
    }

    public static void registerTerminationListener(TerminationListener listener) {
        if (_termListeners == null) {
            _termListeners = new HashSet<>();
        }
        _termListeners.add(listener);
    }

    public static void unregisterTerminationListener(TerminationListener listener) {
        if (_termListeners != null) {
            _termListeners.remove(listener);
        }
    }
}
