package com.mxtech;

import android.widget.CompoundButton;

/* loaded from: classes2.dex */
public class ExclusiveOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
    private CompoundButton[] _buttons;

    public ExclusiveOnCheckedChangeListener(CompoundButton... buttons) {
        this._buttons = buttons;
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CompoundButton[] compoundButtonArr;
        if (isChecked) {
            for (CompoundButton button : this._buttons) {
                if (button != buttonView) {
                    button.setChecked(false);
                }
            }
        }
    }
}
