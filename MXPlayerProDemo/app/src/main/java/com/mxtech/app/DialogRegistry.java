package com.mxtech.app;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import com.mxtech.preference.MXPreferenceActivity;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public final class DialogRegistry implements DialogInterface.OnDismissListener {
    private static final String TAG = "MX.DialogRegistry";
    @Nullable
    private ArrayList<DialogRegistry> _children;
    private final ArrayList<DialogInterface> _list;
    @Nullable
    private ArrayList<Listener> _listeners;
    @Nullable
    private final DialogRegistry _parent;

    /* loaded from: classes.dex */
    public interface Listener {
        void onDialogRegistered(DialogRegistry dialogRegistry, DialogInterface dialogInterface);

        void onDialogUnregistered(DialogRegistry dialogRegistry, DialogInterface dialogInterface);
    }

    public DialogRegistry() {
        this(null);
    }

    public DialogRegistry(DialogRegistry parent) {
        this._list = new ArrayList<>();
        this._parent = parent;
    }

    public void addListener(Listener listener) {
        if (this._listeners == null) {
            this._listeners = new ArrayList<>();
        }
        if (!this._listeners.contains(listener)) {
            this._listeners.add(listener);
        }
    }

    public void removeListener(Listener listener) {
        if (this._listeners != null) {
            this._listeners.remove(listener);
        }
    }

    public void register(DialogInterface dlg) {
        this._list.add(dlg);
        onDialogRegistered(dlg);
    }

    public void unregister(DialogInterface dlg) {
        this._list.remove(dlg);
        onDialogUnregistered(dlg);
    }

    private void onDialogRegistered(DialogInterface dialog) {
        if (this._listeners != null) {
            Iterator<Listener> it = this._listeners.iterator();
            while (it.hasNext()) {
                Listener l = it.next();
                l.onDialogRegistered(this, dialog);
            }
        }
        if (this._parent != null) {
            this._parent.onDialogRegistered(dialog);
        }
    }

    private void onDialogUnregistered(DialogInterface dialog) {
        if (this._listeners != null) {
            Iterator<Listener> it = this._listeners.iterator();
            while (it.hasNext()) {
                Listener l = it.next();
                l.onDialogUnregistered(this, dialog);
            }
        }
        if (this._parent != null) {
            this._parent.onDialogUnregistered(dialog);
        }
    }

    public DialogRegistry newSubRegistry() {
        if (this._children == null) {
            this._children = new ArrayList<>();
        }
        DialogRegistry child = new DialogRegistry(this);
        this._children.add(child);
        return child;
    }

    public boolean hasDialogAfter(DialogInterface dialog) {
        boolean found = false;
        Iterator<DialogInterface> it = this._list.iterator();
        while (it.hasNext()) {
            DialogInterface dlg = it.next();
            if (dlg == dialog) {
                found = true;
            } else if (found) {
                return true;
            }
        }
        return false;
    }

    public void dismissAfter(DialogInterface dialog) {
        boolean found = false;
        Iterator<DialogInterface> it = this._list.iterator();
        while (it.hasNext()) {
            DialogInterface dlg = it.next();
            if (dlg == dialog) {
                found = true;
            } else if (found) {
                dlg.dismiss();
            }
        }
    }

    public void dismissAll() {
        if (this._children != null) {
            Iterator<DialogRegistry> it = this._children.iterator();
            while (it.hasNext()) {
                DialogRegistry child = it.next();
                child.dismissAll();
            }
        }
        Iterator<DialogInterface> it2 = this._list.iterator();
        while (it2.hasNext()) {
            DialogInterface dlg = it2.next();
            dlg.dismiss();
        }
    }

    public int size() {
        int size = this._list.size();
        if (this._children != null) {
            Iterator<DialogRegistry> it = this._children.iterator();
            while (it.hasNext()) {
                DialogRegistry child = it.next();
                size += child.size();
            }
        }
        return size;
    }

    public boolean contains(DialogInterface dialog) {
        if (this._list.contains(dialog)) {
            return true;
        }
        if (this._children != null) {
            Iterator<DialogRegistry> it = this._children.iterator();
            while (it.hasNext()) {
                DialogRegistry child = it.next();
                if (child.contains(dialog)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsInstanceOf(Class<? extends DialogInterface> type) {
        Iterator<DialogInterface> it = this._list.iterator();
        while (it.hasNext()) {
            DialogInterface dlg = it.next();
            if (type.isInstance(dlg)) {
                return true;
            }
        }
        if (this._children != null) {
            Iterator<DialogRegistry> it2 = this._children.iterator();
            while (it2.hasNext()) {
                DialogRegistry child = it2.next();
                if (child.containsInstanceOf(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public <T extends DialogInterface> T findByType(Class<T> type) {
        Iterator<DialogInterface> it = this._list.iterator();
        while (it.hasNext()) {
            T t = (T) it.next();
            if (type.isInstance(t)) {
                return t;
            }
        }
        if (this._children != null) {
            Iterator<DialogRegistry> it2 = this._children.iterator();
            while (it2.hasNext()) {
                DialogRegistry child = it2.next();
                T dialog = (T) child.findByType(type);
                if (dialog != null) {
                    return dialog;
                }
            }
        }
        return null;
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        unregister(dialog);
    }

    public static DialogRegistry registryOf(Context context) {
        if (context instanceof MXAppCompatActivity) {
            return ((MXAppCompatActivity) context).dialogRegistry;
        }
        if (context instanceof MXPreferenceActivity) {
            return ((MXPreferenceActivity) context).dialogRegistry;
        }
        return null;
    }
}
