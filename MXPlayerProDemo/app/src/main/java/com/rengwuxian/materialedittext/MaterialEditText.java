package com.rengwuxian.materialedittext;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.rengwuxian.materialedittext.validation.METLengthChecker;
import com.rengwuxian.materialedittext.validation.METValidator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class MaterialEditText extends AppCompatEditText {
    public static final int FLOATING_LABEL_HIGHLIGHT = 2;
    public static final int FLOATING_LABEL_NONE = 0;
    public static final int FLOATING_LABEL_NORMAL = 1;
    private Typeface accentTypeface;
    private boolean autoValidate;
    private int baseColor;
    private int bottomEllipsisSize;
    private float bottomLines;
    ObjectAnimator bottomLinesAnimator;
    private int bottomSpacing;
    private int bottomTextSize;
    private boolean charactersCountValid;
    private boolean checkCharactersCountAtBeginning;
    private Bitmap[] clearButtonBitmaps;
    private boolean clearButtonClicking;
    private boolean clearButtonTouched;
    private float currentBottomLines;
    private int errorColor;
    private int extraPaddingBottom;
    private int extraPaddingLeft;
    private int extraPaddingRight;
    private int extraPaddingTop;
    private boolean firstShown;
    private boolean floatingLabelAlwaysShown;
    private boolean floatingLabelAnimating;
    private boolean floatingLabelEnabled;
    private float floatingLabelFraction;
    private int floatingLabelPadding;
    private boolean floatingLabelShown;
    private CharSequence floatingLabelText;
    private int floatingLabelTextColor;
    private int floatingLabelTextSize;
    private ArgbEvaluator focusEvaluator;
    private float focusFraction;
    private String helperText;
    private boolean helperTextAlwaysShown;
    private int helperTextColor;
    private boolean hideUnderline;
    private boolean highlightFloatingLabel;
    private Bitmap[] iconLeftBitmaps;
    private int iconOuterHeight;
    private int iconOuterWidth;
    private int iconPadding;
    private Bitmap[] iconRightBitmaps;
    private int iconSize;
    View.OnFocusChangeListener innerFocusChangeListener;
    private int innerPaddingBottom;
    private int innerPaddingLeft;
    private int innerPaddingRight;
    private int innerPaddingTop;
    ObjectAnimator labelAnimator;
    ObjectAnimator labelFocusAnimator;
    private METLengthChecker lengthChecker;
    private int maxCharacters;
    private int minBottomLines;
    private int minBottomTextLines;
    private int minCharacters;
    View.OnFocusChangeListener outerFocusChangeListener;
    Paint paint;
    private int primaryColor;
    private boolean showClearButton;
    private boolean singleLineEllipsis;
    private String tempErrorText;
    private ColorStateList textColorHintStateList;
    private ColorStateList textColorStateList;
    StaticLayout textLayout;
    TextPaint textPaint;
    private Typeface typeface;
    private int underlineColor;
    private boolean validateOnFocusLost;
    private List<METValidator> validators;

    /* loaded from: classes.dex */
    public @interface FloatingLabelType {
    }

    public MaterialEditText(Context context) {
        super(context);
        this.helperTextColor = -1;
        this.focusEvaluator = new ArgbEvaluator();
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        init(context, null);
    }

    public MaterialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.helperTextColor = -1;
        this.focusEvaluator = new ArgbEvaluator();
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        init(context, attrs);
    }

    @TargetApi(21)
    public MaterialEditText(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        this.helperTextColor = -1;
        this.focusEvaluator = new ArgbEvaluator();
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        int defaultPrimaryColor;
        this.iconSize = getPixel(32);
        this.iconOuterWidth = getPixel(48);
        this.iconOuterHeight = getPixel(32);
        this.bottomSpacing = getResources().getDimensionPixelSize(R.dimen.inner_components_spacing);
        this.bottomEllipsisSize = getResources().getDimensionPixelSize(R.dimen.bottom_ellipsis_height);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialEditText);
        this.textColorStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColor);
        this.textColorHintStateList = typedArray.getColorStateList(R.styleable.MaterialEditText_met_textColorHint);
        this.baseColor = typedArray.getColor(R.styleable.MaterialEditText_met_baseColor, ViewCompat.MEASURED_STATE_MASK);
        TypedValue primaryColorTypedValue = new TypedValue();
        try {
        } catch (Exception e) {
            try {
                int colorPrimaryId = getResources().getIdentifier("colorPrimary", "attr", getContext().getPackageName());
                if (colorPrimaryId != 0) {
                    context.getTheme().resolveAttribute(colorPrimaryId, primaryColorTypedValue, true);
                    defaultPrimaryColor = primaryColorTypedValue.data;
                } else {
                    throw new RuntimeException("colorPrimary not found");
                }
            } catch (Exception e2) {
                defaultPrimaryColor = this.baseColor;
            }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            context.getTheme().resolveAttribute(16843827, primaryColorTypedValue, true);
            defaultPrimaryColor = primaryColorTypedValue.data;
            this.primaryColor = typedArray.getColor(R.styleable.MaterialEditText_met_primaryColor, defaultPrimaryColor);
            setFloatingLabelInternal(typedArray.getInt(R.styleable.MaterialEditText_met_floatingLabel, 0));
            this.errorColor = typedArray.getColor(R.styleable.MaterialEditText_met_errorColor, Color.parseColor("#e7492E"));
            this.minCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_minCharacters, 0);
            this.maxCharacters = typedArray.getInt(R.styleable.MaterialEditText_met_maxCharacters, 0);
            this.singleLineEllipsis = typedArray.getBoolean(R.styleable.MaterialEditText_met_singleLineEllipsis, false);
            this.helperText = typedArray.getString(R.styleable.MaterialEditText_met_helperText);
            this.helperTextColor = typedArray.getColor(R.styleable.MaterialEditText_met_helperTextColor, -1);
            this.minBottomTextLines = typedArray.getInt(R.styleable.MaterialEditText_met_minBottomTextLines, 0);
            String fontPathForAccent = typedArray.getString(R.styleable.MaterialEditText_met_accentTypeface);
            if (fontPathForAccent != null && !isInEditMode()) {
                this.accentTypeface = getCustomTypeface(fontPathForAccent);
                this.textPaint.setTypeface(this.accentTypeface);
            }
            String fontPathForView = typedArray.getString(R.styleable.MaterialEditText_met_typeface);
            if (fontPathForView != null && !isInEditMode()) {
                this.typeface = getCustomTypeface(fontPathForView);
                setTypeface(this.typeface);
            }
            this.floatingLabelText = typedArray.getString(R.styleable.MaterialEditText_met_floatingLabelText);
            if (this.floatingLabelText == null) {
                this.floatingLabelText = getHint();
            }
            this.floatingLabelPadding = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_floatingLabelPadding, this.bottomSpacing);
            this.floatingLabelTextSize = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_floatingLabelTextSize, getResources().getDimensionPixelSize(R.dimen.floating_label_text_size));
            this.floatingLabelTextColor = typedArray.getColor(R.styleable.MaterialEditText_met_floatingLabelTextColor, -1);
            this.floatingLabelAnimating = typedArray.getBoolean(R.styleable.MaterialEditText_met_floatingLabelAnimating, true);
            this.bottomTextSize = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_bottomTextSize, getResources().getDimensionPixelSize(R.dimen.bottom_text_size));
            this.hideUnderline = typedArray.getBoolean(R.styleable.MaterialEditText_met_hideUnderline, false);
            this.underlineColor = typedArray.getColor(R.styleable.MaterialEditText_met_underlineColor, -1);
            this.autoValidate = typedArray.getBoolean(R.styleable.MaterialEditText_met_autoValidate, false);
            this.iconLeftBitmaps = generateIconBitmaps(typedArray.getResourceId(R.styleable.MaterialEditText_met_iconLeft, -1));
            this.iconRightBitmaps = generateIconBitmaps(typedArray.getResourceId(R.styleable.MaterialEditText_met_iconRight, -1));
            this.showClearButton = typedArray.getBoolean(R.styleable.MaterialEditText_met_clearButton, false);
            this.clearButtonBitmaps = generateIconBitmaps(R.drawable.met_ic_clear);
            this.iconPadding = typedArray.getDimensionPixelSize(R.styleable.MaterialEditText_met_iconPadding, getPixel(16));
            this.floatingLabelAlwaysShown = typedArray.getBoolean(R.styleable.MaterialEditText_met_floatingLabelAlwaysShown, false);
            this.helperTextAlwaysShown = typedArray.getBoolean(R.styleable.MaterialEditText_met_helperTextAlwaysShown, false);
            this.validateOnFocusLost = typedArray.getBoolean(R.styleable.MaterialEditText_met_validateOnFocusLost, false);
            this.checkCharactersCountAtBeginning = typedArray.getBoolean(R.styleable.MaterialEditText_met_checkCharactersCountAtBeginning, true);
            typedArray.recycle();
            int[] paddings = {16842965, 16842966, 16842967, 16842968, 16842969};
            TypedArray paddingsTypedArray = context.obtainStyledAttributes(attrs, paddings);
            int padding = paddingsTypedArray.getDimensionPixelSize(0, 0);
            this.innerPaddingLeft = paddingsTypedArray.getDimensionPixelSize(1, padding);
            this.innerPaddingTop = paddingsTypedArray.getDimensionPixelSize(2, padding);
            this.innerPaddingRight = paddingsTypedArray.getDimensionPixelSize(3, padding);
            this.innerPaddingBottom = paddingsTypedArray.getDimensionPixelSize(4, padding);
            paddingsTypedArray.recycle();
            if (Build.VERSION.SDK_INT >= 16) {
                setBackground(null);
            } else {
                setBackgroundDrawable(null);
            }
            if (this.singleLineEllipsis) {
                TransformationMethod transformationMethod = getTransformationMethod();
                setSingleLine();
                setTransformationMethod(transformationMethod);
            }
            initMinBottomLines();
            initPadding();
            initText();
            initFloatingLabel();
            initTextWatcher();
            checkCharactersCount();
            return;
        }
        throw new RuntimeException("SDK_INT less than LOLLIPOP");
    }

    private void initText() {
        if (!TextUtils.isEmpty(getText())) {
            CharSequence text = getText();
            setText((CharSequence) null);
            resetHintTextColor();
            setText(text);
            setSelection(text.length());
            this.floatingLabelFraction = 1.0f;
            this.floatingLabelShown = true;
        } else {
            resetHintTextColor();
        }
        resetTextColor();
    }

    private void initTextWatcher() {
        addTextChangedListener(new TextWatcher() { // from class: com.rengwuxian.materialedittext.MaterialEditText.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                MaterialEditText.this.checkCharactersCount();
                if (MaterialEditText.this.autoValidate) {
                    MaterialEditText.this.validate();
                } else {
                    MaterialEditText.this.setError(null);
                }
                MaterialEditText.this.postInvalidate();
            }
        });
    }

    private Typeface getCustomTypeface(@NonNull String fontPath) {
        return Typeface.createFromAsset(getContext().getAssets(), fontPath);
    }

    public void setIconLeft(@DrawableRes int res) {
        this.iconLeftBitmaps = generateIconBitmaps(res);
        initPadding();
    }

    public void setIconLeft(Drawable drawable) {
        this.iconLeftBitmaps = generateIconBitmaps(drawable);
        initPadding();
    }

    public void setIconLeft(Bitmap bitmap) {
        this.iconLeftBitmaps = generateIconBitmaps(bitmap);
        initPadding();
    }

    public void setIconRight(@DrawableRes int res) {
        this.iconRightBitmaps = generateIconBitmaps(res);
        initPadding();
    }

    public void setIconRight(Drawable drawable) {
        this.iconRightBitmaps = generateIconBitmaps(drawable);
        initPadding();
    }

    public void setIconRight(Bitmap bitmap) {
        this.iconRightBitmaps = generateIconBitmaps(bitmap);
        initPadding();
    }

    public boolean isShowClearButton() {
        return this.showClearButton;
    }

    public void setShowClearButton(boolean show) {
        this.showClearButton = show;
        correctPaddings();
    }

    private Bitmap[] generateIconBitmaps(@DrawableRes int origin) {
        if (origin == -1) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), origin, options);
        int size = Math.max(options.outWidth, options.outHeight);
        options.inSampleSize = size > this.iconSize ? size / this.iconSize : 1;
        options.inJustDecodeBounds = false;
        return generateIconBitmaps(BitmapFactory.decodeResource(getResources(), origin, options));
    }

    private Bitmap[] generateIconBitmaps(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return generateIconBitmaps(Bitmap.createScaledBitmap(bitmap, this.iconSize, this.iconSize, false));
    }

    private Bitmap[] generateIconBitmaps(Bitmap origin) {
        if (origin == null) {
            return null;
        }
        Bitmap[] iconBitmaps = new Bitmap[4];
        Bitmap origin2 = scaleIcon(origin);
        iconBitmaps[0] = origin2.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(iconBitmaps[0]);
        canvas.drawColor((Colors.isLight(this.baseColor) ? ViewCompat.MEASURED_STATE_MASK : -1979711488) | (this.baseColor & ViewCompat.MEASURED_SIZE_MASK), PorterDuff.Mode.SRC_IN);
        iconBitmaps[1] = origin2.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas2 = new Canvas(iconBitmaps[1]);
        canvas2.drawColor(this.primaryColor, PorterDuff.Mode.SRC_IN);
        iconBitmaps[2] = origin2.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas3 = new Canvas(iconBitmaps[2]);
        canvas3.drawColor((Colors.isLight(this.baseColor) ? 1275068416 : 1107296256) | (this.baseColor & ViewCompat.MEASURED_SIZE_MASK), PorterDuff.Mode.SRC_IN);
        iconBitmaps[3] = origin2.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas4 = new Canvas(iconBitmaps[3]);
        canvas4.drawColor(this.errorColor, PorterDuff.Mode.SRC_IN);
        return iconBitmaps;
    }

    private Bitmap scaleIcon(Bitmap origin) {
        int scaledHeight;
        int scaledWidth;
        int width = origin.getWidth();
        int height = origin.getHeight();
        int size = Math.max(width, height);
        if (size != this.iconSize && size > this.iconSize) {
            if (width > this.iconSize) {
                scaledWidth = this.iconSize;
                scaledHeight = (int) (this.iconSize * (height / width));
            } else {
                scaledHeight = this.iconSize;
                scaledWidth = (int) (this.iconSize * (width / height));
            }
            return Bitmap.createScaledBitmap(origin, scaledWidth, scaledHeight, false);
        }
        return origin;
    }

    public float getFloatingLabelFraction() {
        return this.floatingLabelFraction;
    }

    public void setFloatingLabelFraction(float floatingLabelFraction) {
        this.floatingLabelFraction = floatingLabelFraction;
        invalidate();
    }

    public float getFocusFraction() {
        return this.focusFraction;
    }

    public void setFocusFraction(float focusFraction) {
        this.focusFraction = focusFraction;
        invalidate();
    }

    public float getCurrentBottomLines() {
        return this.currentBottomLines;
    }

    public void setCurrentBottomLines(float currentBottomLines) {
        this.currentBottomLines = currentBottomLines;
        initPadding();
    }

    public boolean isFloatingLabelAlwaysShown() {
        return this.floatingLabelAlwaysShown;
    }

    public void setFloatingLabelAlwaysShown(boolean floatingLabelAlwaysShown) {
        this.floatingLabelAlwaysShown = floatingLabelAlwaysShown;
        invalidate();
    }

    public boolean isHelperTextAlwaysShown() {
        return this.helperTextAlwaysShown;
    }

    public void setHelperTextAlwaysShown(boolean helperTextAlwaysShown) {
        this.helperTextAlwaysShown = helperTextAlwaysShown;
        invalidate();
    }

    @Nullable
    public Typeface getAccentTypeface() {
        return this.accentTypeface;
    }

    public void setAccentTypeface(Typeface accentTypeface) {
        this.accentTypeface = accentTypeface;
        this.textPaint.setTypeface(accentTypeface);
        postInvalidate();
    }

    public boolean isHideUnderline() {
        return this.hideUnderline;
    }

    public void setHideUnderline(boolean hideUnderline) {
        this.hideUnderline = hideUnderline;
        initPadding();
        postInvalidate();
    }

    public int getUnderlineColor() {
        return this.underlineColor;
    }

    public void setUnderlineColor(int color) {
        this.underlineColor = color;
        postInvalidate();
    }

    public CharSequence getFloatingLabelText() {
        return this.floatingLabelText;
    }

    public void setFloatingLabelText(@Nullable CharSequence floatingLabelText) {
        if (floatingLabelText == null) {
            floatingLabelText = getHint();
        }
        this.floatingLabelText = floatingLabelText;
        postInvalidate();
    }

    public int getFloatingLabelTextSize() {
        return this.floatingLabelTextSize;
    }

    public void setFloatingLabelTextSize(int size) {
        this.floatingLabelTextSize = size;
        initPadding();
    }

    public int getFloatingLabelTextColor() {
        return this.floatingLabelTextColor;
    }

    public void setFloatingLabelTextColor(int color) {
        this.floatingLabelTextColor = color;
        postInvalidate();
    }

    public int getBottomTextSize() {
        return this.bottomTextSize;
    }

    public void setBottomTextSize(int size) {
        this.bottomTextSize = size;
        initPadding();
    }

    private int getPixel(int dp) {
        return Density.dp2px(getContext(), dp);
    }

    private void initPadding() {
        this.extraPaddingTop = this.floatingLabelEnabled ? this.floatingLabelTextSize + this.floatingLabelPadding : this.floatingLabelPadding;
        this.textPaint.setTextSize(this.bottomTextSize);
        Paint.FontMetrics textMetrics = this.textPaint.getFontMetrics();
        this.extraPaddingBottom = (this.hideUnderline ? this.bottomSpacing : this.bottomSpacing * 2) + ((int) ((textMetrics.descent - textMetrics.ascent) * this.currentBottomLines));
        this.extraPaddingLeft = this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding;
        this.extraPaddingRight = this.iconRightBitmaps != null ? this.iconPadding + this.iconOuterWidth : 0;
        correctPaddings();
    }

    private void initMinBottomLines() {
        int i = 1;
        boolean extendBottom = this.minCharacters > 0 || this.maxCharacters > 0 || this.singleLineEllipsis || this.tempErrorText != null || this.helperText != null;
        if (this.minBottomTextLines > 0) {
            i = this.minBottomTextLines;
        } else if (!extendBottom) {
            i = 0;
        }
        this.minBottomLines = i;
        this.currentBottomLines = i;
    }

    @Override // android.widget.TextView, android.view.View
    @Deprecated
    public final void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
    }

    public void setPaddings(int left, int top, int right, int bottom) {
        this.innerPaddingTop = top;
        this.innerPaddingBottom = bottom;
        this.innerPaddingLeft = left;
        this.innerPaddingRight = right;
        correctPaddings();
    }

    private void correctPaddings() {
        int buttonsWidthLeft = 0;
        int buttonsWidthRight = 0;
        int buttonsWidth = this.iconOuterWidth * getButtonsCount();
        if (isRTL()) {
            buttonsWidthLeft = buttonsWidth;
        } else {
            buttonsWidthRight = buttonsWidth;
        }
        super.setPadding(this.innerPaddingLeft + this.extraPaddingLeft + buttonsWidthLeft, this.innerPaddingTop + this.extraPaddingTop, this.innerPaddingRight + this.extraPaddingRight + buttonsWidthRight, this.innerPaddingBottom + this.extraPaddingBottom);
    }

    private int getButtonsCount() {
        return isShowClearButton() ? 1 : 0;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.firstShown) {
            this.firstShown = true;
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            adjustBottomLines();
        }
    }

    private boolean adjustBottomLines() {
        Layout.Alignment alignment;
        int destBottomLines;
        if (getWidth() == 0) {
            return false;
        }
        this.textPaint.setTextSize(this.bottomTextSize);
        if (this.tempErrorText != null || this.helperText != null) {
            if ((getGravity() & 5) == 5 || isRTL()) {
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
            } else {
                alignment = (getGravity() & 3) == 3 ? Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_CENTER;
            }
            this.textLayout = new StaticLayout(this.tempErrorText != null ? this.tempErrorText : this.helperText, this.textPaint, (((getWidth() - getBottomTextLeftOffset()) - getBottomTextRightOffset()) - getPaddingLeft()) - getPaddingRight(), alignment, 1.0f, 0.0f, true);
            destBottomLines = Math.max(this.textLayout.getLineCount(), this.minBottomTextLines);
        } else {
            destBottomLines = this.minBottomLines;
        }
        if (this.bottomLines != destBottomLines) {
            getBottomLinesAnimator(destBottomLines).start();
        }
        this.bottomLines = destBottomLines;
        return true;
    }

    public int getInnerPaddingTop() {
        return this.innerPaddingTop;
    }

    public int getInnerPaddingBottom() {
        return this.innerPaddingBottom;
    }

    public int getInnerPaddingLeft() {
        return this.innerPaddingLeft;
    }

    public int getInnerPaddingRight() {
        return this.innerPaddingRight;
    }

    private void initFloatingLabel() {
        addTextChangedListener(new TextWatcher() { // from class: com.rengwuxian.materialedittext.MaterialEditText.2
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                if (MaterialEditText.this.floatingLabelEnabled) {
                    if (s.length() == 0) {
                        if (MaterialEditText.this.floatingLabelShown) {
                            MaterialEditText.this.floatingLabelShown = false;
                            MaterialEditText.this.getLabelAnimator().reverse();
                        }
                    } else if (!MaterialEditText.this.floatingLabelShown) {
                        MaterialEditText.this.floatingLabelShown = true;
                        MaterialEditText.this.getLabelAnimator().start();
                    }
                }
            }
        });
        this.innerFocusChangeListener = new View.OnFocusChangeListener() { // from class: com.rengwuxian.materialedittext.MaterialEditText.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View v, boolean hasFocus) {
                if (MaterialEditText.this.floatingLabelEnabled && MaterialEditText.this.highlightFloatingLabel) {
                    if (hasFocus) {
                        MaterialEditText.this.getLabelFocusAnimator().start();
                    } else {
                        MaterialEditText.this.getLabelFocusAnimator().reverse();
                    }
                }
                if (MaterialEditText.this.validateOnFocusLost && !hasFocus) {
                    MaterialEditText.this.validate();
                }
                if (MaterialEditText.this.outerFocusChangeListener != null) {
                    MaterialEditText.this.outerFocusChangeListener.onFocusChange(v, hasFocus);
                }
            }
        };
        super.setOnFocusChangeListener(this.innerFocusChangeListener);
    }

    public boolean isValidateOnFocusLost() {
        return this.validateOnFocusLost;
    }

    public void setValidateOnFocusLost(boolean validate) {
        this.validateOnFocusLost = validate;
    }

    public void setBaseColor(int color) {
        if (this.baseColor != color) {
            this.baseColor = color;
        }
        initText();
        postInvalidate();
    }

    public void setPrimaryColor(int color) {
        this.primaryColor = color;
        postInvalidate();
    }

    public void setMetTextColor(int color) {
        this.textColorStateList = ColorStateList.valueOf(color);
        resetTextColor();
    }

    public void setMetTextColor(ColorStateList colors) {
        this.textColorStateList = colors;
        resetTextColor();
    }

    private void resetTextColor() {
        if (this.textColorStateList == null) {
            this.textColorStateList = new ColorStateList(new int[][]{new int[]{16842910}, EMPTY_STATE_SET}, new int[]{(this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | (-553648128), (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688});
            setTextColor(this.textColorStateList);
            return;
        }
        setTextColor(this.textColorStateList);
    }

    public void setMetHintTextColor(int color) {
        this.textColorHintStateList = ColorStateList.valueOf(color);
        resetHintTextColor();
    }

    public void setMetHintTextColor(ColorStateList colors) {
        this.textColorHintStateList = colors;
        resetHintTextColor();
    }

    private void resetHintTextColor() {
        if (this.textColorHintStateList == null) {
            setHintTextColor((this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688);
        } else {
            setHintTextColor(this.textColorHintStateList);
        }
    }

    private void setFloatingLabelInternal(int mode) {
        switch (mode) {
            case 1:
                this.floatingLabelEnabled = true;
                this.highlightFloatingLabel = false;
                return;
            case 2:
                this.floatingLabelEnabled = true;
                this.highlightFloatingLabel = true;
                return;
            default:
                this.floatingLabelEnabled = false;
                this.highlightFloatingLabel = false;
                return;
        }
    }

    public void setFloatingLabel(@FloatingLabelType int mode) {
        setFloatingLabelInternal(mode);
        initPadding();
    }

    public int getFloatingLabelPadding() {
        return this.floatingLabelPadding;
    }

    public void setFloatingLabelPadding(int padding) {
        this.floatingLabelPadding = padding;
        postInvalidate();
    }

    public boolean isFloatingLabelAnimating() {
        return this.floatingLabelAnimating;
    }

    public void setFloatingLabelAnimating(boolean animating) {
        this.floatingLabelAnimating = animating;
    }

    public void setSingleLineEllipsis() {
        setSingleLineEllipsis(true);
    }

    public void setSingleLineEllipsis(boolean enabled) {
        this.singleLineEllipsis = enabled;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public int getMaxCharacters() {
        return this.maxCharacters;
    }

    public void setMaxCharacters(int max) {
        this.maxCharacters = max;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public int getMinCharacters() {
        return this.minCharacters;
    }

    public void setMinCharacters(int min) {
        this.minCharacters = min;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public int getMinBottomTextLines() {
        return this.minBottomTextLines;
    }

    public void setMinBottomTextLines(int lines) {
        this.minBottomTextLines = lines;
        initMinBottomLines();
        initPadding();
        postInvalidate();
    }

    public boolean isAutoValidate() {
        return this.autoValidate;
    }

    public void setAutoValidate(boolean autoValidate) {
        this.autoValidate = autoValidate;
        if (autoValidate) {
            validate();
        }
    }

    public int getErrorColor() {
        return this.errorColor;
    }

    public void setErrorColor(int color) {
        this.errorColor = color;
        postInvalidate();
    }

    public void setHelperText(CharSequence helperText) {
        this.helperText = helperText == null ? null : helperText.toString();
        if (adjustBottomLines()) {
            postInvalidate();
        }
    }

    public String getHelperText() {
        return this.helperText;
    }

    public int getHelperTextColor() {
        return this.helperTextColor;
    }

    public void setHelperTextColor(int color) {
        this.helperTextColor = color;
        postInvalidate();
    }

    @Override // android.widget.TextView
    public void setError(CharSequence errorText) {
        this.tempErrorText = errorText == null ? null : errorText.toString();
        if (adjustBottomLines()) {
            postInvalidate();
        }
    }

    @Override // android.widget.TextView
    public CharSequence getError() {
        return this.tempErrorText;
    }

    private boolean isInternalValid() {
        return this.tempErrorText == null && isCharactersCountValid();
    }

    @Deprecated
    public boolean isValid(String regex) {
        if (regex == null) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(getText());
        return matcher.matches();
    }

    @Deprecated
    public boolean validate(String regex, CharSequence errorText) {
        boolean isValid = isValid(regex);
        if (!isValid) {
            setError(errorText);
        }
        postInvalidate();
        return isValid;
    }

    public boolean validateWith(@NonNull METValidator validator) {
        CharSequence text = getText();
        boolean isValid = validator.isValid(text, text.length() == 0);
        if (!isValid) {
            setError(validator.getErrorMessage());
        }
        postInvalidate();
        return isValid;
    }

    public boolean validate() {
        if (this.validators == null || this.validators.isEmpty()) {
            return true;
        }
        CharSequence text = getText();
        boolean isEmpty = text.length() == 0;
        boolean isValid = true;
        Iterator<METValidator> it = this.validators.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            METValidator validator = it.next();
            if (isValid && validator.isValid(text, isEmpty)) {
                isValid = true;
                continue;
            } else {
                isValid = false;
                continue;
            }
            if (!isValid) {
                setError(validator.getErrorMessage());
                break;
            }
        }
        if (isValid) {
            setError(null);
        }
        postInvalidate();
        return isValid;
    }

    public boolean hasValidators() {
        return (this.validators == null || this.validators.isEmpty()) ? false : true;
    }

    public MaterialEditText addValidator(METValidator validator) {
        if (this.validators == null) {
            this.validators = new ArrayList();
        }
        this.validators.add(validator);
        return this;
    }

    public void clearValidators() {
        if (this.validators != null) {
            this.validators.clear();
        }
    }

    @Nullable
    public List<METValidator> getValidators() {
        return this.validators;
    }

    public void setLengthChecker(METLengthChecker lengthChecker) {
        this.lengthChecker = lengthChecker;
    }

    @Override // android.view.View
    public void setOnFocusChangeListener(View.OnFocusChangeListener listener) {
        if (this.innerFocusChangeListener == null) {
            super.setOnFocusChangeListener(listener);
        } else {
            this.outerFocusChangeListener = listener;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ObjectAnimator getLabelAnimator() {
        if (this.labelAnimator == null) {
            this.labelAnimator = ObjectAnimator.ofFloat(this, "floatingLabelFraction", 0.0f, 1.0f);
        }
        this.labelAnimator.setDuration(this.floatingLabelAnimating ? 300L : 0L);
        return this.labelAnimator;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ObjectAnimator getLabelFocusAnimator() {
        if (this.labelFocusAnimator == null) {
            this.labelFocusAnimator = ObjectAnimator.ofFloat(this, "focusFraction", 0.0f, 1.0f);
        }
        return this.labelFocusAnimator;
    }

    private ObjectAnimator getBottomLinesAnimator(float destBottomLines) {
        if (this.bottomLinesAnimator == null) {
            this.bottomLinesAnimator = ObjectAnimator.ofFloat(this, "currentBottomLines", destBottomLines);
        } else {
            this.bottomLinesAnimator.cancel();
            this.bottomLinesAnimator.setFloatValues(destBottomLines);
        }
        return this.bottomLinesAnimator;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onDraw(@NonNull Canvas canvas) {
        int ellipsisStartX;
        int floatingLabelStartX;
        int i;
        int buttonLeft;
        char c;
        char c2;
        int startX = getScrollX() + (this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding);
        int endX = getScrollX() + (this.iconRightBitmaps == null ? getWidth() : (getWidth() - this.iconOuterWidth) - this.iconPadding);
        int lineStartY = (getScrollY() + getHeight()) - getPaddingBottom();
        this.paint.setAlpha(255);
        if (this.iconLeftBitmaps != null) {
            Bitmap[] bitmapArr = this.iconLeftBitmaps;
            if (isInternalValid()) {
                c2 = !isEnabled() ? (char) 2 : hasFocus() ? (char) 1 : (char) 0;
            } else {
                c2 = 3;
            }
            Bitmap icon = bitmapArr[c2];
            int iconLeft = ((startX - this.iconPadding) - this.iconOuterWidth) + ((this.iconOuterWidth - icon.getWidth()) / 2);
            int iconTop = ((this.bottomSpacing + lineStartY) - this.iconOuterHeight) + ((this.iconOuterHeight - icon.getHeight()) / 2);
            canvas.drawBitmap(icon, iconLeft, iconTop, this.paint);
        }
        if (this.iconRightBitmaps != null) {
            Bitmap[] bitmapArr2 = this.iconRightBitmaps;
            if (isInternalValid()) {
                c = !isEnabled() ? (char) 2 : hasFocus() ? (char) 1 : (char) 0;
            } else {
                c = 3;
            }
            Bitmap icon2 = bitmapArr2[c];
            int iconRight = this.iconPadding + endX + ((this.iconOuterWidth - icon2.getWidth()) / 2);
            int iconTop2 = ((this.bottomSpacing + lineStartY) - this.iconOuterHeight) + ((this.iconOuterHeight - icon2.getHeight()) / 2);
            canvas.drawBitmap(icon2, iconRight, iconTop2, this.paint);
        }
        if (hasFocus() && this.showClearButton && !TextUtils.isEmpty(getText())) {
            this.paint.setAlpha(255);
            if (isRTL()) {
                buttonLeft = startX;
            } else {
                buttonLeft = endX - this.iconOuterWidth;
            }
            Bitmap clearButtonBitmap = this.clearButtonBitmaps[0];
            int buttonLeft2 = buttonLeft + ((this.iconOuterWidth - clearButtonBitmap.getWidth()) / 2);
            int iconTop3 = ((this.bottomSpacing + lineStartY) - this.iconOuterHeight) + ((this.iconOuterHeight - clearButtonBitmap.getHeight()) / 2);
            canvas.drawBitmap(clearButtonBitmap, buttonLeft2, iconTop3, this.paint);
        }
        if (!this.hideUnderline) {
            lineStartY += this.bottomSpacing;
            if (!isInternalValid()) {
                this.paint.setColor(this.errorColor);
                canvas.drawRect(startX, lineStartY, endX, getPixel(2) + lineStartY, this.paint);
            } else if (!isEnabled()) {
                this.paint.setColor(this.underlineColor != -1 ? this.underlineColor : (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688);
                float interval = getPixel(1);
                for (float xOffset = 0.0f; xOffset < getWidth(); xOffset += 3.0f * interval) {
                    canvas.drawRect(startX + xOffset, lineStartY, startX + xOffset + interval, getPixel(1) + lineStartY, this.paint);
                }
            } else if (hasFocus()) {
                this.paint.setColor(this.primaryColor);
                canvas.drawRect(startX, lineStartY, endX, getPixel(2) + lineStartY, this.paint);
            } else {
                this.paint.setColor(this.underlineColor != -1 ? this.underlineColor : (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 503316480);
                canvas.drawRect(startX, lineStartY, endX, getPixel(1) + lineStartY, this.paint);
            }
        }
        this.textPaint.setTextSize(this.bottomTextSize);
        Paint.FontMetrics textMetrics = this.textPaint.getFontMetrics();
        float relativeHeight = (-textMetrics.ascent) - textMetrics.descent;
        float bottomTextPadding = this.bottomTextSize + textMetrics.ascent + textMetrics.descent;
        if ((hasFocus() && hasCharactersCounter()) || !isCharactersCountValid()) {
            this.textPaint.setColor(isCharactersCountValid() ? (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688 : this.errorColor);
            String charactersCounterText = getCharactersCounterText();
            canvas.drawText(charactersCounterText, isRTL() ? startX : endX - this.textPaint.measureText(charactersCounterText), this.bottomSpacing + lineStartY + relativeHeight, this.textPaint);
        }
        if (this.textLayout != null && (this.tempErrorText != null || ((this.helperTextAlwaysShown || hasFocus()) && !TextUtils.isEmpty(this.helperText)))) {
            TextPaint textPaint = this.textPaint;
            if (this.tempErrorText != null) {
                i = this.errorColor;
            } else {
                i = this.helperTextColor != -1 ? this.helperTextColor : (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688;
            }
            textPaint.setColor(i);
            canvas.save();
            if (isRTL()) {
                canvas.translate(endX - this.textLayout.getWidth(), (this.bottomSpacing + lineStartY) - bottomTextPadding);
            } else {
                canvas.translate(getBottomTextLeftOffset() + startX, (this.bottomSpacing + lineStartY) - bottomTextPadding);
            }
            this.textLayout.draw(canvas);
            canvas.restore();
        }
        if (this.floatingLabelEnabled && !TextUtils.isEmpty(this.floatingLabelText)) {
            this.textPaint.setTextSize(this.floatingLabelTextSize);
            this.textPaint.setColor(((Integer) this.focusEvaluator.evaluate(this.focusFraction, Integer.valueOf(this.floatingLabelTextColor != -1 ? this.floatingLabelTextColor : (this.baseColor & ViewCompat.MEASURED_SIZE_MASK) | 1140850688), Integer.valueOf(this.primaryColor))).intValue());
            float floatingLabelWidth = this.textPaint.measureText(this.floatingLabelText.toString());
            if ((getGravity() & 5) == 5 || isRTL()) {
                floatingLabelStartX = (int) (endX - floatingLabelWidth);
            } else if ((getGravity() & 3) == 3) {
                floatingLabelStartX = startX;
            } else {
                floatingLabelStartX = startX + ((int) (getInnerPaddingLeft() + ((((getWidth() - getInnerPaddingLeft()) - getInnerPaddingRight()) - floatingLabelWidth) / 2.0f)));
            }
            int distance = this.floatingLabelPadding;
            int floatingLabelStartY = (int) ((((this.innerPaddingTop + this.floatingLabelTextSize) + this.floatingLabelPadding) - ((this.floatingLabelAlwaysShown ? 1.0f : this.floatingLabelFraction) * distance)) + getScrollY());
            int alpha = (int) ((this.floatingLabelTextColor != -1 ? 1.0f : Color.alpha(this.floatingLabelTextColor) / 256.0f) * ((0.74f * this.focusFraction) + 0.26f) * (this.floatingLabelAlwaysShown ? 1.0f : this.floatingLabelFraction) * 255.0f);
            this.textPaint.setAlpha(alpha);
            canvas.drawText(this.floatingLabelText.toString(), floatingLabelStartX, floatingLabelStartY, this.textPaint);
        }
        if (hasFocus() && this.singleLineEllipsis && getScrollX() != 0) {
            this.paint.setColor(isInternalValid() ? this.primaryColor : this.errorColor);
            float startY = this.bottomSpacing + lineStartY;
            if (isRTL()) {
                ellipsisStartX = endX;
            } else {
                ellipsisStartX = startX;
            }
            int signum = isRTL() ? -1 : 1;
            canvas.drawCircle(((this.bottomEllipsisSize * signum) / 2) + ellipsisStartX, (this.bottomEllipsisSize / 2) + startY, this.bottomEllipsisSize / 2, this.paint);
            canvas.drawCircle((((this.bottomEllipsisSize * signum) * 5) / 2) + ellipsisStartX, (this.bottomEllipsisSize / 2) + startY, this.bottomEllipsisSize / 2, this.paint);
            canvas.drawCircle((((this.bottomEllipsisSize * signum) * 9) / 2) + ellipsisStartX, (this.bottomEllipsisSize / 2) + startY, this.bottomEllipsisSize / 2, this.paint);
        }
        super.onDraw(canvas);
    }

    @TargetApi(17)
    private boolean isRTL() {
        if (Build.VERSION.SDK_INT < 17) {
            return false;
        }
        Configuration config = getResources().getConfiguration();
        return config.getLayoutDirection() == 1;
    }

    private int getBottomTextLeftOffset() {
        return isRTL() ? getCharactersCounterWidth() : getBottomEllipsisWidth();
    }

    private int getBottomTextRightOffset() {
        return isRTL() ? getBottomEllipsisWidth() : getCharactersCounterWidth();
    }

    private int getCharactersCounterWidth() {
        if (hasCharactersCounter()) {
            return (int) this.textPaint.measureText(getCharactersCounterText());
        }
        return 0;
    }

    private int getBottomEllipsisWidth() {
        if (this.singleLineEllipsis) {
            return (this.bottomEllipsisSize * 5) + getPixel(4);
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkCharactersCount() {
        boolean z = false;
        if ((!this.firstShown && !this.checkCharactersCountAtBeginning) || !hasCharactersCounter()) {
            this.charactersCountValid = true;
            return;
        }
        CharSequence text = getText();
        int count = text == null ? 0 : checkLength(text);
        if (count >= this.minCharacters && (this.maxCharacters <= 0 || count <= this.maxCharacters)) {
            z = true;
        }
        this.charactersCountValid = z;
    }

    public boolean isCharactersCountValid() {
        return this.charactersCountValid;
    }

    private boolean hasCharactersCounter() {
        return this.minCharacters > 0 || this.maxCharacters > 0;
    }

    private String getCharactersCounterText() {
        if (this.minCharacters <= 0) {
            if (!isRTL()) {
                String text = checkLength(getText()) + " / " + this.maxCharacters;
                return text;
            }
            String text2 = this.maxCharacters + " / " + checkLength(getText());
            return text2;
        } else if (this.maxCharacters <= 0) {
            if (isRTL()) {
                String text3 = "+" + this.minCharacters + " / " + checkLength(getText());
                return text3;
            }
            String text4 = checkLength(getText()) + " / " + this.minCharacters + "+";
            return text4;
        } else if (isRTL()) {
            String text5 = this.maxCharacters + "-" + this.minCharacters + " / " + checkLength(getText());
            return text5;
        } else {
            String text6 = checkLength(getText()) + " / " + this.minCharacters + "-" + this.maxCharacters;
            return text6;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x007a  */
    @Override // android.widget.TextView, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean onTouchEvent(MotionEvent event) {
        if (this.singleLineEllipsis && getScrollX() > 0 && event.getAction() == 0 && event.getX() < getPixel(20) && event.getY() > (getHeight() - this.extraPaddingBottom) - this.innerPaddingBottom && event.getY() < getHeight() - this.innerPaddingBottom) {
            setSelection(0);
            return false;
        }
        if (hasFocus() && this.showClearButton) {
            switch (event.getAction()) {
                case 0:
                    if (insideClearButton(event)) {
                        this.clearButtonTouched = true;
                        this.clearButtonClicking = true;
                        return true;
                    }
                    if (this.clearButtonClicking && !insideClearButton(event)) {
                        this.clearButtonClicking = false;
                    }
                    if (this.clearButtonTouched) {
                        return true;
                    }
                    break;
                case 1:
                    if (this.clearButtonClicking) {
                        if (!TextUtils.isEmpty(getText())) {
                            setText((CharSequence) null);
                        }
                        this.clearButtonClicking = false;
                    }
                    if (this.clearButtonTouched) {
                        this.clearButtonTouched = false;
                        return true;
                    }
                    this.clearButtonTouched = false;
                    break;
                case 2:
                    if (this.clearButtonClicking) {
                        this.clearButtonClicking = false;
                        break;
                    }
                    if (this.clearButtonTouched) {
                    }
                    break;
                case 3:
                    this.clearButtonTouched = false;
                    this.clearButtonClicking = false;
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private boolean insideClearButton(MotionEvent event) {
        int buttonLeft;
        float x = event.getX();
        float y = event.getY();
        int startX = getScrollX() + (this.iconLeftBitmaps == null ? 0 : this.iconOuterWidth + this.iconPadding);
        int endX = getScrollX() + (this.iconRightBitmaps == null ? getWidth() : (getWidth() - this.iconOuterWidth) - this.iconPadding);
        if (isRTL()) {
            buttonLeft = startX;
        } else {
            buttonLeft = endX - this.iconOuterWidth;
        }
        int buttonTop = (((getScrollY() + getHeight()) - getPaddingBottom()) + this.bottomSpacing) - this.iconOuterHeight;
        return x >= ((float) buttonLeft) && x < ((float) (this.iconOuterWidth + buttonLeft)) && y >= ((float) buttonTop) && y < ((float) (this.iconOuterHeight + buttonTop));
    }

    private int checkLength(CharSequence text) {
        return this.lengthChecker == null ? text.length() : this.lengthChecker.getLength(text);
    }
}
