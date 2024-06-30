package afzkl.development.mColorPicker.views;

import afzkl.development.mColorPicker.drawables.AlphaPatternDrawable;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import com.mxtech.DeviceUtils;
import com.mxtech.videoplayer.pro.R;


/* loaded from: classes.dex */
public class ColorPanelView extends View {
    public static final float DEFAULT_BORDER_SIZE_DIP = 1.0f;
    private static final int[] DEFAULT_COLOR = {0, 0};
    private int _borderColor;
    private ColorStateList _borderColors;
    private int _borderWidth;
    private int[] _color;
    private AlphaPatternDrawable mAlphaPattern;
    private final Paint mBorderPaint;
    private final Paint mColorPaint;
    private final Rect mColorRect;
    private final Rect mDrawingRect;

    public ColorPanelView(Context context) {
        this(context, null);
    }

    public ColorPanelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPanelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this._color = DEFAULT_COLOR;
        this.mBorderPaint = new Paint();
        this.mColorPaint = new Paint();
        this.mDrawingRect = new Rect();
        this.mColorRect = new Rect();
        init(context, attrs, defStyle, 0);
    }

    @TargetApi(21)
    public ColorPanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this._color = DEFAULT_COLOR;
        this.mBorderPaint = new Paint();
        this.mColorPaint = new Paint();
        this.mDrawingRect = new Rect();
        this.mColorRect = new Rect();
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this._borderWidth = (int) (1.0f * DeviceUtils.density);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPanelView, defStyleAttr, defStyleRes);
        setFocusable(a.getBoolean(R.styleable.ColorPanelView_android_focusable, true));
        this._borderColors = a.getColorStateList(R.styleable.ColorPanelView_colorPanelViewBorderColor);
        if (this._borderColors == null) {
            this._borderColors = getResources().getColorStateList(R.color.color_pane_view_default_border_color);
        }
        a.recycle();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Rect rect = this.mColorRect;
        if (this._borderWidth > 0) {
            this.mBorderPaint.setColor(this._borderColor);
            canvas.drawRect(this.mDrawingRect, this.mBorderPaint);
        }
        if (this.mAlphaPattern != null) {
            this.mAlphaPattern.draw(canvas);
        }
        if (this._color[1] != 0 && this._color[0] != this._color[1]) {
            int orgTop = rect.top;
            int orgBottom = rect.bottom;
            rect.bottom = rect.top + ((rect.bottom - rect.top) / 2);
            this.mColorPaint.setColor(this._color[0]);
            canvas.drawRect(rect, this.mColorPaint);
            rect.top = rect.bottom;
            rect.bottom = orgBottom;
            this.mColorPaint.setColor(this._color[1]);
            canvas.drawRect(rect, this.mColorPaint);
            rect.top = orgTop;
            return;
        }
        this.mColorPaint.setColor(this._color[0]);
        canvas.drawRect(rect, this.mColorPaint);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mDrawingRect.left = getPaddingLeft();
        this.mDrawingRect.right = w - getPaddingRight();
        this.mDrawingRect.top = getPaddingTop();
        this.mDrawingRect.bottom = h - getPaddingBottom();
        recalc();
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int newBorderColor = this._borderColors.getColorForState(getDrawableState(), 0);
        if (newBorderColor != this._borderColor) {
            this._borderColor = newBorderColor;
            invalidate();
        }
    }

    private void recalc() {
        this.mColorRect.left = this.mDrawingRect.left + this._borderWidth;
        this.mColorRect.top = this.mDrawingRect.top + this._borderWidth;
        this.mColorRect.bottom = this.mDrawingRect.bottom - this._borderWidth;
        this.mColorRect.right = this.mDrawingRect.right - this._borderWidth;
        if (Color.alpha(this._color[0]) != 255 || (this._color[1] != 0 && Color.alpha(this._color[1]) != 255)) {
            if (this.mAlphaPattern == null) {
                this.mAlphaPattern = new AlphaPatternDrawable((int) (5.0f * DeviceUtils.density));
            }
            this.mAlphaPattern.setBounds(this.mColorRect);
            return;
        }
        this.mAlphaPattern = null;
    }

    public void setBorderWidth(float dip) {
        this._borderWidth = (int) (DeviceUtils.density * dip);
        recalc();
        invalidate();
    }

    public final void setColor(int color) {
        setColor(new int[]{color, 0});
    }

    public void setColor(int[] color) {
        this._color = color;
        recalc();
        invalidate();
    }

    public int[] getColors() {
        return this._color;
    }

    public int getColor() {
        return this._color[0];
    }

    public void setBorderColor(int color) {
        setBorderColor(ColorStateList.valueOf(color));
    }

    public void setBorderColor(ColorStateList colors) {
        this._borderColors = colors;
        int newBorderColor = this._borderColors.getColorForState(getDrawableState(), 0);
        if (newBorderColor != this._borderColor) {
            this._borderColor = newBorderColor;
            invalidate();
        }
    }
}
