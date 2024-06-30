package com.carrydream.cardrecorder.PokerGame;

import androidx.exifinterface.media.ExifInterface;

/* loaded from: classes.dex */
public class PokerTool extends Poker {
    @Override // com.carrydream.cardrecorder.PokerGame.Poker
    public Poker init() {
        String[] strArr = {"king", ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", ExifInterface.GPS_MEASUREMENT_IN_PROGRESS};
        for (int i = 0; i < 14; i++) {
            String str = strArr[i];
            this.initData.put(str, Integer.valueOf(str.equals("king") ? 2 : 4));
        }
        return this;
    }
}
