package com.carrydream.cardrecorder.ui.Module;

import com.carrydream.cardrecorder.ui.contacts.LauncherContacts;
import dagger.Module;
import dagger.Provides;

@Module
/* loaded from: classes.dex */
public class LauncherModule {
    private LauncherContacts.View View;

    public LauncherModule(LauncherContacts.View view) {
        this.View = view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    public LauncherContacts.View provideLauncherView() {
        return this.View;
    }
}
