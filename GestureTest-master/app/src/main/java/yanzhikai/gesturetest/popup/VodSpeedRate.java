package yanzhikai.gesturetest.popup;

import yanzhikai.gesturetest.R;

public enum VodSpeedRate {
    RATE_4_POINT(4.0f, R.drawable.audio_play_speed_4),
    RATE_3_POINT(3.0f, R.drawable.audio_play_speed_3),
    RATE_2_POINT(2.0f, R.drawable.audio_play_speed_2),
    RATE_1_POINT_5(1.5f, R.drawable.audio_play_speed_15),
    RATE_1_POINT_25(1.25f, R.drawable.audio_play_speed_125),
    RATE_1_POINT_0(1.0f, R.drawable.audio_play_speed_1),
    RATE_0_POINT_5(0.5f, R.drawable.audio_play_speed_05);

    private int icon;
    private float value;

    VodSpeedRate(float f, int i) {
        this.value = f;
        this.icon = i;
    }

    public float getRateValue() {
        return this.value;
    }

    public String getRateDescription() {
        return this.value + "X";
    }

    public int getRateIcon() {
        return this.icon;
    }

    public static boolean isVipRate(VodSpeedRate vodSpeedRate) {
        return vodSpeedRate == RATE_2_POINT;
    }

    public static boolean isSVipRate(VodSpeedRate vodSpeedRate) {
        return vodSpeedRate == RATE_3_POINT;
    }

    public static boolean isYearSVipRate(VodSpeedRate vodSpeedRate) {
        return  vodSpeedRate == RATE_4_POINT;
    }

    public static VodSpeedRate getDefaultRate() {
        return RATE_1_POINT_0;
    }

    public static VodSpeedRate getVodSpeedRate(float f) {
        VodSpeedRate[] values;
        for (VodSpeedRate vodSpeedRate : values()) {
            if (vodSpeedRate.getRateValue() == f) {
                return vodSpeedRate;
            }
        }
        return RATE_1_POINT_0;
    }
}

