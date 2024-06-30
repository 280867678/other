package yanzhikai.gesturetest.pop;

import android.content.Context;
import android.media.AudioManager;

public class Utility {


    public static int getMaxVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        int i = 3;
        if (audioManager.getMode() == 3) {
            if (!audioManager.isBluetoothA2dpOn() && !audioManager.isBluetoothScoOn()) {
                i = 0;
            } else {
                i = 6;
            }
        }
        return audioManager.getStreamMaxVolume(i);
    }


    public static int getCurrentVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService("audio");
        int i = 3;
        if (audioManager.getMode() == 3) {
            if (!audioManager.isBluetoothA2dpOn() && !audioManager.isBluetoothScoOn()) {
                i = 0;
            } else {
                i = 6;
            }
        }
        return audioManager.getStreamVolume(i);
    }

    public static void setVolume(Context context, int i) {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService("audio");
            int i2 = 3;
            if (audioManager.getMode() == 3) {
                if (!audioManager.isBluetoothA2dpOn() && !audioManager.isBluetoothScoOn()) {
                    i2 = 0;
                }
                i2 = 6;
            }
            audioManager.setStreamVolume(i2, i, 0);
        } catch (Exception unused) {
        }
    }

}
