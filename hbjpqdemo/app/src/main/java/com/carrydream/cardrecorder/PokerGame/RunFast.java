package com.carrydream.cardrecorder.PokerGame;

import androidx.exifinterface.media.ExifInterface;

/* loaded from: classes.dex */
public class RunFast extends Poker {
    @Override // com.carrydream.cardrecorder.PokerGame.Poker
    public Poker init() {
        String[] strArr = {ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS};
        for (int i = 0; i < 13; i++) {
            String str = strArr[i];
            str.hashCode();
            if (str.equals(ExifInterface.GPS_MEASUREMENT_2D)) {
                this.data.put(str, 1);
            } else if (str.equals(ExifInterface.GPS_MEASUREMENT_IN_PROGRESS)) {
                this.data.put(str, 3);
            } else {
                this.data.put(str, 4);
            }
        }
        return this;
    }
}
