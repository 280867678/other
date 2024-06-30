package com.mxtech.videoplayer.subtitle.service;

/* loaded from: classes2.dex */
public final class MovieCandidate {
    final int episode;
    final long id;
    final int season;
    final String title;
    final int year;

    public MovieCandidate(long id, String title, int year, int season, int episode) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.season = season;
        this.episode = episode;
    }

    public boolean equals(Object o) {
        return (o instanceof MovieCandidate) && ((MovieCandidate) o).id == this.id;
    }

    public int hashCode() {
        return (int) (this.id ^ (this.id >>> 32));
    }
}
