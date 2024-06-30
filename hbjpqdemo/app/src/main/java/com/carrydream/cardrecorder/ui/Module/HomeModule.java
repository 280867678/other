package com.carrydream.cardrecorder.ui.Module;

import com.carrydream.cardrecorder.ui.contacts.HomeContacts;
import dagger.Module;
import dagger.Provides;

@Module
/* loaded from: classes.dex */
public class HomeModule {
    private HomeContacts.View View;

    public HomeModule(HomeContacts.View view) {
        this.View = view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    public HomeContacts.View provideHomeView() {
        return this.View;
    }
}
