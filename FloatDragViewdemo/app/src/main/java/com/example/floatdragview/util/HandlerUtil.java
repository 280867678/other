package com.example.floatdragview.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

public class HandlerUtil {
    private static int mId = 16777216;

    /* loaded from: classes.dex */
    public interface MessageListener {
        void handleMessage(Message message);
    }

    public static final int generateId() {
        int i = mId + 1;
        mId = i;
        return i;
    }

    /* loaded from: classes.dex */
    public static class StaticHandler extends Handler {
        WeakReference<MessageListener> mListener;

        public StaticHandler(MessageListener messageListener) {
            this.mListener = new WeakReference<>(messageListener);
        }

        public StaticHandler(Looper looper, MessageListener messageListener) {
            super(looper);
            this.mListener = new WeakReference<>(messageListener);
        }

        public StaticHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            MessageListener messageListener;
            if (this.mListener == null || (messageListener = this.mListener.get()) == null) {
                return;
            }
            messageListener.handleMessage(message);
        }

        public void removeListener() {
            this.mListener = null;
        }
    }
}
