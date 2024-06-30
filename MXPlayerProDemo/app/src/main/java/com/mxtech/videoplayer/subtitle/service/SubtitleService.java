package com.mxtech.videoplayer.subtitle.service;

import android.content.Context;
import android.support.annotation.Nullable;
import com.mxtech.widget.DecorEditText;
import java.io.File;
import java.util.List;
import java.util.Locale;

/* loaded from: classes2.dex */
public abstract class SubtitleService {
    public static final int FEATURE_ACCOUNT_NEED_EMAIL_ADDRESS = 32;
    public static final int FEATURE_ACCOUNT_NEED_EMAIL_CONFIRM = 64;
    public static final int FEATURE_ACCOUNT_NEED_USERNAME = 16;
    public static final int FEATURE_INFO_LINK = 4;
    public static final int FEATURE_REGISTER_DIRECT = 1;
    public static final int FEATURE_REGISTER_LINK = 2;
    public static final int FEATURE_UPLOADING = 128;
    public static final int FEATURE_VOTING_ANONYMOUS = 512;
    public static final int FEATURE_VOTING_NAMED = 256;
    protected static final int MAX_PASSWORD_LENGTH = 64;
    protected static final int MAX_USERNAME_LENGTH = 64;

    /* loaded from: classes2.dex */
    public static class Constraint {
        public int maxCharacters;
        public int minCharacters;
        public String pattern;
        public String patternFailMessage;
    }

    /* loaded from: classes2.dex */
    public static class DownloadLimitReachedException extends ServerException {
    }

    /* loaded from: classes2.dex */
    public static class EmailAlreadyUsedException extends ServerException {
    }

    /* loaded from: classes2.dex */
    public static class NotImplemtedException extends SubtitleServiceException {
    }

    /* loaded from: classes2.dex */
    public static class SubtitleAlreadyExistException extends ServerException {
    }

    /* loaded from: classes2.dex */
    public static class SubtitleFileEmptyException extends LocalException {
    }

    /* loaded from: classes2.dex */
    public static class SubtitleFileTooLargeException extends LocalException {
    }

    /* loaded from: classes2.dex */
    public static class SubtitleFormatUnrecognized extends ServerException {
    }

    /* loaded from: classes2.dex */
    public static class SubtitleNotFoundException extends ServerException {
    }

    /* loaded from: classes2.dex */
    public static class UnauthorizedException extends ServerException {
    }

    /* loaded from: classes2.dex */
    public static class UsernameExistException extends ServerException {
    }

    public abstract void get(File file, Object obj) throws SubtitleServiceException;

    public abstract int getFeatures();

    public abstract boolean isSupported(String str);

    public abstract String name();

    public abstract SubtitleEntry[] search(Media[] mediaArr, Locale[] localeArr, String str) throws SubtitleServiceException;

    public abstract void setupInputs(Context context, @Nullable DecorEditText decorEditText, DecorEditText decorEditText2);

    /* loaded from: classes2.dex */
    public static class SubtitleServiceException extends Exception {
        /* JADX INFO: Access modifiers changed from: package-private */
        public SubtitleServiceException() {
        }

        SubtitleServiceException(String detailMessage) {
            super(detailMessage);
        }

        SubtitleServiceException(Throwable cause) {
            super(cause);
        }

        SubtitleServiceException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    /* loaded from: classes2.dex */
    public static class LocalException extends SubtitleServiceException {
        LocalException() {
        }

        LocalException(String detailMessage) {
            super(detailMessage);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public LocalException(Throwable cause) {
            super(cause);
        }

        LocalException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    /* loaded from: classes2.dex */
    public static class ServerException extends SubtitleServiceException {
        /* JADX INFO: Access modifiers changed from: package-private */
        public ServerException() {
        }

        ServerException(String detailMessage) {
            super(detailMessage);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ServerException(Throwable cause) {
            super(cause);
        }

        ServerException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    /* loaded from: classes2.dex */
    public static class NetworkException extends SubtitleServiceException {
        /* JADX INFO: Access modifiers changed from: package-private */
        public NetworkException() {
        }

        NetworkException(String detailMessage) {
            super(detailMessage);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public NetworkException(Throwable cause) {
            super(cause);
        }

        NetworkException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    /* loaded from: classes2.dex */
    public static class MediaFileReadException extends LocalException {
        MediaFileReadException() {
        }

        MediaFileReadException(String detailMessage) {
            super(detailMessage);
        }

        MediaFileReadException(Throwable cause) {
            super(cause);
        }

        MediaFileReadException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    /* loaded from: classes2.dex */
    public static class SubtitleFileReadException extends LocalException {
        /* JADX INFO: Access modifiers changed from: package-private */
        public SubtitleFileReadException() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SubtitleFileReadException(String detailMessage) {
            super(detailMessage);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SubtitleFileReadException(Throwable cause) {
            super(cause);
        }

        SubtitleFileReadException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    /* loaded from: classes2.dex */
    public static class SubtitleFileWriteException extends LocalException {
        SubtitleFileWriteException() {
        }

        SubtitleFileWriteException(String detailMessage) {
            super(detailMessage);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public SubtitleFileWriteException(Throwable cause) {
            super(cause);
        }

        SubtitleFileWriteException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    /* loaded from: classes2.dex */
    public static class ServerDataFormatException extends ServerException {
        ServerDataFormatException() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ServerDataFormatException(String detailMessage) {
            super(detailMessage);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public ServerDataFormatException(Throwable cause) {
            super(cause);
        }

        ServerDataFormatException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }
    }

    public void createAccount(@Nullable String emailAddress, @Nullable String username, String password) throws SubtitleServiceException {
        throw new NotImplemtedException();
    }

    public String getRegisterUrl() {
        return null;
    }

    public String getInfoUrl() {
        return null;
    }

    public void rate(Media media, Subtitle subtitle, int rating, @Nullable String comment) throws SubtitleServiceException {
        throw new NotImplemtedException();
    }

    public List<MovieCandidate> searchMovies(Media media) throws SubtitleServiceException {
        throw new NotImplemtedException();
    }

    public List<MovieCandidate> searchMovies(String title) throws SubtitleServiceException {
        throw new NotImplemtedException();
    }

    public List<String> searchMoviesStartsWith(String text) throws SubtitleServiceException {
        throw new NotImplemtedException();
    }

    public long registerMovie(Media media, String title, int year, int season, int episode) throws SubtitleServiceException {
        throw new NotImplemtedException();
    }

    public void upload(long movieId, Media media, Subtitle subtitle, @Nullable Locale subtitleLocale) throws SubtitleServiceException {
        throw new NotImplemtedException();
    }

    public boolean exist(Media media, Subtitle subtitle) throws SubtitleServiceException {
        throw new NotImplemtedException();
    }
}
