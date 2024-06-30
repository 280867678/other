package com.hjq.permissions;

import android.app.Activity;
import java.util.List;

/* loaded from: classes2.dex */
public interface IPermissionInterceptor {
    void deniedPermissions(Activity activity, OnPermissionCallback onPermissionCallback, List<String> list, boolean z);

    void grantedPermissions(Activity activity, OnPermissionCallback onPermissionCallback, List<String> list, boolean z);

    void requestPermissions(Activity activity, OnPermissionCallback onPermissionCallback, List<String> list);

    /* renamed from: com.hjq.permissions.IPermissionInterceptor$-CC  reason: invalid class name */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
    }
}
