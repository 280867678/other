package com.mxtech;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/* loaded from: classes2.dex */
public class DrawUtils {
    public static final float DEFAULT_PATTERN_SIZE_DP = 5.0f;
    private static final Paint _whitePaint = new Paint();
    private static final Paint _grayPaint = new Paint();
    private static final RectF r = new RectF();

    static {
        _whitePaint.setColor(-1);
        _grayPaint.setColor(Color.rgb(203, 203, 203));
    }

    public static void drawAlphaPattern(Canvas canvas, RectF rect) {
        drawAlphaPattern(canvas, rect, DeviceUtils.DIPToPixel(5.0f));
    }

    public static void drawAlphaPattern(Canvas canvas, RectF rect, float rectSize) {
        boolean whiteLine = true;
        r.top = rect.top;
        while (r.top < rect.bottom) {
            r.bottom = r.top + rectSize;
            if (r.bottom > rect.bottom) {
                r.bottom = rect.bottom;
            }
            boolean whiteCol = whiteLine;
            r.left = rect.left;
            while (r.left < rect.right) {
                r.right = r.left + rectSize;
                if (r.right > rect.right) {
                    r.right = rect.right;
                }
                canvas.drawRect(r, whiteCol ? _whitePaint : _grayPaint);
                whiteCol = !whiteCol;
                r.left = r.right;
            }
            whiteLine = !whiteLine;
            r.top = r.bottom;
        }
    }

    public static void drawAlphaPattern(Canvas canvas) {
        drawAlphaPattern(canvas, DeviceUtils.DIPToPixel(5.0f));
    }

    public static void drawAlphaPattern(Canvas canvas, float rectSize) {
        boolean whiteLine = true;
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        r.top = 0.0f;
        while (r.top < height) {
            r.bottom = r.top + rectSize;
            if (r.bottom > height) {
                r.bottom = height;
            }
            boolean whiteCol = whiteLine;
            r.left = 0.0f;
            while (r.left < width) {
                r.right = r.left + rectSize;
                if (r.right > width) {
                    r.right = width;
                }
                canvas.drawRect(r, whiteCol ? _whitePaint : _grayPaint);
                whiteCol = !whiteCol;
                r.left = r.right;
            }
            whiteLine = !whiteLine;
            r.top = r.bottom;
        }
    }
}
