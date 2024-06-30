package yanzhikai.gesturetest.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.RawRes;

import com.bumptech.glide.integration.webp.decoder.WebpDrawable;
import com.bumptech.glide.integration.webp.decoder.WebpDrawableTransformation;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
//import com.bumptech.glide.request.p195a.ViewTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.github.penfeizhou.animation.glide.AnimationDecoderOption;
//import com.xunlei.common.GlideApp;
//import com.xunlei.common.GlideRequest;
//import kotlin.Metadata;
//import kotlin.jvm.internal.Intrinsics;


public final class WebPLoadHelper {
    /* renamed from: a */
    public final ViewTarget<ImageView, Drawable> m13357a(Context context, @RawRes int i, BitmapTransformation bitmapTransformation, ImageView view, int i2, int i3) {
//        Intrinsics.checkNotNullParameter(context, "context");
//        Intrinsics.checkNotNullParameter(view, "view");
        ViewTarget<ImageView, Drawable> a = m13358a(context, i, bitmapTransformation, i2, i3).into(view);
//        Intrinsics.checkNotNullExpressionValue(a, "loadRequest(context, raw…Id, loopCount).into(view)");
        return a;
    }

    /* renamed from: a */
    public final GlideRequest<Drawable> m13358a(Context context, @RawRes int i, BitmapTransformation bitmapTransformation, int i2, int i3) {
        GlideRequest<Drawable> mo43249a;
//        Intrinsics.checkNotNullParameter(context, "context");
        GlideRequest<Drawable> mo43189o = GlideApp.m43317a(context).mo43182a(Integer.valueOf(i)).m43233b( AnimationDecoderOption.DISABLE_ANIMATION_WEBP_DECODER, true).mo43228b(new WebPLoopController(i3)).mo43189o();
        if (bitmapTransformation != null && (mo43249a = mo43189o.mo43249a(WebpDrawable.class, new WebpDrawableTransformation(bitmapTransformation))) != null) {
            mo43189o = mo43249a;
        }
        GlideRequest<Drawable> mo43256a = mo43189o.mo43266a(i2).mo43218c(i2).mo43256a(DiskCacheStrategy.ALL);
//        Intrinsics.checkNotNullExpressionValue(mo43256a, "with(context)\n          …gy(DiskCacheStrategy.ALL)");
        return mo43256a;
    }
}
