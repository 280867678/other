package afzkl.development.mColorPicker.drawables;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
public class AlphaPatternDrawable extends Drawable {
    private Bitmap mBitmap;
    private int mRectangleSize;
    private int numRectanglesHorizontal;
    private int numRectanglesVertical;
    private Paint mPaint = new Paint();
    private Paint mPaintWhite = new Paint();
    private Paint mPaintGray = new Paint();

    public AlphaPatternDrawable(int rectangleSize) {
        this.mRectangleSize = 10;
        this.mRectangleSize = rectangleSize;
        this.mPaintWhite.setColor(-1);
        this.mPaintGray.setColor(-3421237);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mBitmap != null) {
            canvas.drawBitmap(this.mBitmap, (Rect) null, getBounds(), this.mPaint);
        }
    }

    @SuppressLint("WrongConstant")
    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        throw new UnsupportedOperationException("Alpha is not supported by this drawwable.");
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter cf) {
        throw new UnsupportedOperationException("ColorFilter is not supported by this drawwable.");
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        int height = bounds.height();
        int width = bounds.width();
        this.numRectanglesHorizontal = (int) Math.ceil(width / this.mRectangleSize);
        this.numRectanglesVertical = (int) Math.ceil(height / this.mRectangleSize);
        generatePatternBitmap();
    }

    private void generatePatternBitmap() {
        if (getBounds().width() > 0 && getBounds().height() > 0) {
            this.mBitmap = Bitmap.createBitmap(getBounds().width(), getBounds().height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(this.mBitmap);
            Rect r = new Rect();
            boolean verticalStartWhite = true;
            for (int i = 0; i <= this.numRectanglesVertical; i++) {
                boolean isWhite = verticalStartWhite;
                for (int j = 0; j <= this.numRectanglesHorizontal; j++) {
                    r.top = this.mRectangleSize * i;
                    r.left = this.mRectangleSize * j;
                    r.bottom = r.top + this.mRectangleSize;
                    r.right = r.left + this.mRectangleSize;
                    canvas.drawRect(r, isWhite ? this.mPaintWhite : this.mPaintGray);
                    isWhite = !isWhite;
                }
                verticalStartWhite = !verticalStartWhite;
            }
        }
    }
}
