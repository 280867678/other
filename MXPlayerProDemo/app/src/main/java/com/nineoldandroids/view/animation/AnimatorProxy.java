package com.nineoldandroids.view.animation;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/* loaded from: classes2.dex */
public final class AnimatorProxy extends Animation {
    public static final boolean NEEDS_PROXY;
    private static final WeakHashMap<View, AnimatorProxy> PROXIES;
    private boolean mHasPivot;
    private float mPivotX;
    private float mPivotY;
    private float mRotationX;
    private float mRotationY;
    private float mRotationZ;
    private float mTranslationX;
    private float mTranslationY;
    private final WeakReference<View> mView;
    private final Camera mCamera = new Camera();
    private float mAlpha = 1.0f;
    private float mScaleX = 1.0f;
    private float mScaleY = 1.0f;
    private final RectF mBefore = new RectF();
    private final RectF mAfter = new RectF();
    private final Matrix mTempMatrix = new Matrix();

    static {
        NEEDS_PROXY = Integer.valueOf(Build.VERSION.SDK).intValue() < 11;
        PROXIES = new WeakHashMap<>();
    }

    public static AnimatorProxy wrap(View view) {
        AnimatorProxy proxy = PROXIES.get(view);
        if (proxy == null || proxy != view.getAnimation()) {
            AnimatorProxy proxy2 = new AnimatorProxy(view);
            PROXIES.put(view, proxy2);
            return proxy2;
        }
        return proxy;
    }

    private AnimatorProxy(View view) {
        setDuration(0L);
        setFillAfter(true);
        view.setAnimation(this);
        this.mView = new WeakReference<>(view);
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public void setAlpha(float alpha) {
        if (this.mAlpha != alpha) {
            this.mAlpha = alpha;
            View view = this.mView.get();
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public float getPivotX() {
        return this.mPivotX;
    }

    public void setPivotX(float pivotX) {
        if (!this.mHasPivot || this.mPivotX != pivotX) {
            prepareForUpdate();
            this.mHasPivot = true;
            this.mPivotX = pivotX;
            invalidateAfterUpdate();
        }
    }

    public float getPivotY() {
        return this.mPivotY;
    }

    public void setPivotY(float pivotY) {
        if (!this.mHasPivot || this.mPivotY != pivotY) {
            prepareForUpdate();
            this.mHasPivot = true;
            this.mPivotY = pivotY;
            invalidateAfterUpdate();
        }
    }

    public float getRotation() {
        return this.mRotationZ;
    }

    public void setRotation(float rotation) {
        if (this.mRotationZ != rotation) {
            prepareForUpdate();
            this.mRotationZ = rotation;
            invalidateAfterUpdate();
        }
    }

    public float getRotationX() {
        return this.mRotationX;
    }

    public void setRotationX(float rotationX) {
        if (this.mRotationX != rotationX) {
            prepareForUpdate();
            this.mRotationX = rotationX;
            invalidateAfterUpdate();
        }
    }

    public float getRotationY() {
        return this.mRotationY;
    }

    public void setRotationY(float rotationY) {
        if (this.mRotationY != rotationY) {
            prepareForUpdate();
            this.mRotationY = rotationY;
            invalidateAfterUpdate();
        }
    }

    public float getScaleX() {
        return this.mScaleX;
    }

    public void setScaleX(float scaleX) {
        if (this.mScaleX != scaleX) {
            prepareForUpdate();
            this.mScaleX = scaleX;
            invalidateAfterUpdate();
        }
    }

    public float getScaleY() {
        return this.mScaleY;
    }

    public void setScaleY(float scaleY) {
        if (this.mScaleY != scaleY) {
            prepareForUpdate();
            this.mScaleY = scaleY;
            invalidateAfterUpdate();
        }
    }

    public int getScrollX() {
        View view = this.mView.get();
        if (view == null) {
            return 0;
        }
        return view.getScrollX();
    }

    public void setScrollX(int value) {
        View view = this.mView.get();
        if (view != null) {
            view.scrollTo(value, view.getScrollY());
        }
    }

    public int getScrollY() {
        View view = this.mView.get();
        if (view == null) {
            return 0;
        }
        return view.getScrollY();
    }

    public void setScrollY(int value) {
        View view = this.mView.get();
        if (view != null) {
            view.scrollTo(view.getScrollX(), value);
        }
    }

    public float getTranslationX() {
        return this.mTranslationX;
    }

    public void setTranslationX(float translationX) {
        if (this.mTranslationX != translationX) {
            prepareForUpdate();
            this.mTranslationX = translationX;
            invalidateAfterUpdate();
        }
    }

    public float getTranslationY() {
        return this.mTranslationY;
    }

    public void setTranslationY(float translationY) {
        if (this.mTranslationY != translationY) {
            prepareForUpdate();
            this.mTranslationY = translationY;
            invalidateAfterUpdate();
        }
    }

    public float getX() {
        View view = this.mView.get();
        if (view == null) {
            return 0.0f;
        }
        return view.getLeft() + this.mTranslationX;
    }

    public void setX(float x) {
        View view = this.mView.get();
        if (view != null) {
            setTranslationX(x - view.getLeft());
        }
    }

    public float getY() {
        View view = this.mView.get();
        if (view == null) {
            return 0.0f;
        }
        return view.getTop() + this.mTranslationY;
    }

    public void setY(float y) {
        View view = this.mView.get();
        if (view != null) {
            setTranslationY(y - view.getTop());
        }
    }

    private void prepareForUpdate() {
        View view = this.mView.get();
        if (view != null) {
            computeRect(this.mBefore, view);
        }
    }

    private void invalidateAfterUpdate() {
        View view = this.mView.get();
        if (view != null && view.getParent() != null) {
            RectF after = this.mAfter;
            computeRect(after, view);
            after.union(this.mBefore);
            ((View) view.getParent()).invalidate((int) Math.floor(after.left), (int) Math.floor(after.top), (int) Math.ceil(after.right), (int) Math.ceil(after.bottom));
        }
    }

    private void computeRect(RectF r, View view) {
        float w = view.getWidth();
        float h = view.getHeight();
        r.set(0.0f, 0.0f, w, h);
        Matrix m = this.mTempMatrix;
        m.reset();
        transformMatrix(m, view);
        this.mTempMatrix.mapRect(r);
        r.offset(view.getLeft(), view.getTop());
        if (r.right < r.left) {
            float f = r.right;
            r.right = r.left;
            r.left = f;
        }
        if (r.bottom < r.top) {
            float f2 = r.top;
            r.top = r.bottom;
            r.bottom = f2;
        }
    }

    private void transformMatrix(Matrix m, View view) {
        float w = view.getWidth();
        float h = view.getHeight();
        boolean hasPivot = this.mHasPivot;
        float pX = hasPivot ? this.mPivotX : w / 2.0f;
        float pY = hasPivot ? this.mPivotY : h / 2.0f;
        float rX = this.mRotationX;
        float rY = this.mRotationY;
        float rZ = this.mRotationZ;
        if (rX != 0.0f || rY != 0.0f || rZ != 0.0f) {
            Camera camera = this.mCamera;
            camera.save();
            camera.rotateX(rX);
            camera.rotateY(rY);
            camera.rotateZ(-rZ);
            camera.getMatrix(m);
            camera.restore();
            m.preTranslate(-pX, -pY);
            m.postTranslate(pX, pY);
        }
        float sX = this.mScaleX;
        float sY = this.mScaleY;
        if (sX != 1.0f || sY != 1.0f) {
            m.postScale(sX, sY);
            float sPX = (-(pX / w)) * ((sX * w) - w);
            float sPY = (-(pY / h)) * ((sY * h) - h);
            m.postTranslate(sPX, sPY);
        }
        m.postTranslate(this.mTranslationX, this.mTranslationY);
    }

    @Override // android.view.animation.Animation
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        View view = this.mView.get();
        if (view != null) {
            t.setAlpha(this.mAlpha);
            transformMatrix(t.getMatrix(), view);
        }
    }
}
