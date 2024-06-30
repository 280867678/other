package com.mxtech.videoplayer.subtitle.service;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;

/* loaded from: classes.dex */
public abstract class Subtitle {
    public final Uri sourceUri;

    public abstract InputStream content() throws IOException;

    public abstract String directory();

    public abstract boolean exists();

    public abstract String filename();

    public abstract int size();

    public Subtitle(Uri sourceUri) {
        this.sourceUri = sourceUri;
    }

    public String toString() {
        return this.sourceUri.toString();
    }

    public int hashCode() {
        return this.sourceUri.hashCode();
    }

    public boolean equals(Object o) {
        if (o instanceof Subtitle) {
            return this.sourceUri.equals(((Subtitle) o).sourceUri);
        }
        return false;
    }
}
