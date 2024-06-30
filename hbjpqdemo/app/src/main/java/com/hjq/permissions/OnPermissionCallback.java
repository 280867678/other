package com.hjq.permissions;

import java.util.List;

/* loaded from: classes2.dex */
public interface OnPermissionCallback {

    /* renamed from: com.hjq.permissions.OnPermissionCallback$-CC  reason: invalid class name */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static void $default$onDenied(OnPermissionCallback _this, List list, boolean z) {
        }
    }

    void onDenied(List<String> list, boolean z);

    void onGranted(List<String> list, boolean z);
}
