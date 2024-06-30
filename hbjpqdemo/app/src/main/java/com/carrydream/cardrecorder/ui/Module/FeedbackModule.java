package com.carrydream.cardrecorder.ui.Module;

import com.carrydream.cardrecorder.ui.contacts.FeedbackContacts;
import dagger.Module;
import dagger.Provides;

@Module
/* loaded from: classes.dex */
public class FeedbackModule {
    private FeedbackContacts.View View;

    public FeedbackModule(FeedbackContacts.View view) {
        this.View = view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Provides
    public FeedbackContacts.View provideFeedbackView() {
        return this.View;
    }
}
