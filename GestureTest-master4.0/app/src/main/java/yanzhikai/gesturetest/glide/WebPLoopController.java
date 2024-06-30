package yanzhikai.gesturetest.glide;

import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

public class WebPLoopController implements RequestListener {

    /* renamed from: a */
    private int f75456a;

    /* renamed from: b */
    private Animatable2Compat.AnimationCallback f75457b;

    /* renamed from: c */
    private WebpDrawable f75458c;

    @Override // com.bumptech.glide.request.RequestListener
    public boolean onLoadFailed(@Nullable GlideException glideException, Object obj, Target target, boolean z) {
        return false;
    }

    public WebPLoopController() {
        this(1);
    }

    public WebPLoopController(int i) {
        this.f75456a = i;
    }

    @Override // com.bumptech.glide.request.RequestListener
    public boolean onResourceReady(Object obj, Object obj2, Target target, DataSource dataSource, boolean z) {
        if (obj instanceof WebpDrawable) {
            WebpDrawable webpDrawable = (WebpDrawable) obj;
            this.f75458c = webpDrawable;
            m13356a(webpDrawable);
            return false;
        }
        return false;
    }

    /* renamed from: a */
    private void m13356a(WebpDrawable webpDrawable) {
        webpDrawable.setLoopCount(this.f75456a);
        Animatable2Compat.AnimationCallback animationCallback = this.f75457b;
        if (animationCallback != null) {
            webpDrawable.registerAnimationCallback(animationCallback);
            this.f75457b.onAnimationStart(webpDrawable);
        }
    }
}
