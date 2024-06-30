package com.mxtech.videoplayer;

import android.net.Uri;

import com.mxtech.videoplayer.subtitle.SubView;

public class MediaDatabase {


    public static class State {
        public static final short NO_AUDIO_STREAM = -100;
        public byte audioDecoder;
        public int audioOffset;
        public byte decoder;
        public int decodingOption;
        public float horzRatio;
        public short panX;
        public short panY;
        public int position;
        public int process;
        public int subtitleOffset;
        public Subtitle[] subtitles;
        public float vertRatio;
        public short zoomHeight;
        public short zoomWidth;
        public short audioStream = -1;
        public double subtitleSpeed = 1.0d;
        public double playbackSpeed = 0.0d;
        public int repeatA = -1;
        public int repeatB = -1;

        /* loaded from: classes.dex */
        public static class Subtitle {
            public boolean enabled;
            public String name;
            public String typename;
            public Uri uri;

            public Subtitle(Uri uri, String name, String typename, boolean enabled) {
                this.uri = uri;
                this.name = name;
                this.typename = typename;
                this.enabled = enabled;
            }

            public Subtitle() {
            }
        }

        public boolean hasSubtitles() {
            return this.subtitles != null && this.subtitles.length > 0;
        }

        public boolean containsEnabledSubtitle() {
            Subtitle[] subtitleArr;
            if (this.subtitles != null) {
                for (Subtitle sub : this.subtitles) {
                    if (sub.enabled) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }

        public boolean canResume() {
            return this.position > 0;
        }

        public void startOver() {
            this.position = 0;
            this.repeatA = -1;
            this.repeatB = -1;
        }

        public String toString() {
            return super.toString() + " [Position=" + this.position + " decoder=" + ((int) this.decoder) + " decodeOption=" + this.decodingOption + " audioDecoder=" + ((int) this.audioDecoder) + " audioStream=" + ((int) this.audioStream) + " audioOffset=" + this.audioOffset + " subtitle-offset=" + this.subtitleOffset + " subtitle-speed=" + this.subtitleSpeed + " playback-speed=" + this.playbackSpeed + " ratio=" + this.horzRatio + ":" + this.vertRatio + " zoom=" + ((int) this.zoomWidth) + 'x' + ((int) this.zoomHeight) + " pan=(" + ((int) this.panX) + ", " + ((int) this.panY) + ") process=" + this.process + " subtitle-count=" + (this.subtitles != null ? this.subtitles.length : 0) + " repeat=" + this.repeatA + " ~ " + this.repeatB + ']';
        }
    }


}
