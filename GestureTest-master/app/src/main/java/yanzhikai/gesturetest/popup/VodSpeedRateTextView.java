package yanzhikai.gesturetest.popup;

import android.content.Context;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;


/* loaded from: classes3.dex */
public class VodSpeedRateTextView extends AppCompatTextView {
    public VodSpeedRateTextView(Context context) {
        this(context, null, 0);
    }

    public VodSpeedRateTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public VodSpeedRateTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setRate(VodSpeedRate vodSpeedRate) {
        if (vodSpeedRate == null) {
            setText("倍速");
        } else {
            setText(vodSpeedRate.getRateDescription());
        }
    }
}
