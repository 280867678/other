package com.carrydream.cardrecorder.ui.Module;

import com.carrydream.cardrecorder.ui.contacts.AccountContacts;
import dagger.Module;
import dagger.Provides;

@Module
/* loaded from: classes.dex */
public class AccountModule {
    private AccountContacts.View View;

    public AccountModule(AccountContacts.View view) {
        this.View = view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    public AccountContacts.View provideAccountView() {
        return this.View;
    }
}
