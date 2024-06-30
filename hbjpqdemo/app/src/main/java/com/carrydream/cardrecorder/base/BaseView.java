package com.carrydream.cardrecorder.base;

//import com.android.tools.r8.annotations.SynthesizedClass;
import com.carrydream.cardrecorder.base.BasePresenter;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;

/* loaded from: classes.dex */
public interface BaseView<T extends BasePresenter, R> {

//    @SynthesizedClass(kind = "$-CC")
    /* renamed from: com.carrydream.cardrecorder.base.BaseView$-CC  reason: invalid class name */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC<T extends BasePresenter, R> {
        public static LifecycleTransformer $default$bindUntilEvent(BaseView _this, FragmentEvent fragmentEvent) {
            return null;
        }
    }

    <T> LifecycleTransformer<T> bindUntilEvent(FragmentEvent fragmentEvent);
}
