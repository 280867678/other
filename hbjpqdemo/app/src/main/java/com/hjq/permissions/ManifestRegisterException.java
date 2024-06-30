package com.hjq.permissions;

/* loaded from: classes2.dex */
final class ManifestRegisterException extends RuntimeException {
    /* JADX INFO: Access modifiers changed from: package-private */
    public ManifestRegisterException() {
        super("No permissions are registered in the manifest file");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ManifestRegisterException(String str) {
        super(str + ": Permissions are not registered in the manifest file");
    }
}
